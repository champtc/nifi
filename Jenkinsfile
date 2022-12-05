@Library('utils@dev') _

node {
	env.DOCKER_REGISTRY = utils.NEXUS_DOCKER_REGISTRY
  	env.DOCKER_REGISTRY_CREDS = utils.NEXUS_DEPLOY_CREDS

	def pomFile
	env.JRE_TAG = "11.0.12-jre" //"11.0.17_8-jre"
	env.JRE_NAME = "openjdk" //"eclipse-temurin"
	def dockerImage = "UNKNOWN"

	try {
		// Checkout and notify start
		stage('Checkout') {
			checkout scm
			notify.notifyBuild('STARTED');
		}
		
		// Initialize variables, tools, and set version for build
		stage('Init') {	
			javaUtils.setupCommonToolsAndEnvVars()
			utils.getAndSetBuildVersion("${env.WORKSPACE}/version.properties")
			utils.setCommonBuildStepsAndProperties()
			pomFile = "${env.WORKSPACE}/pom.xml";
		}

		// Set build properties and set version in poms
		stage('Setup Build') {
		//	javaUtils.setupAndSetVersionMaven(pomFile)
		}
		
		stage('Build Nifi') {
			env.JAVA_HOME = tool 'OPEN_JDK_11'
			javaUtils.runMavenCommand("clean install -T2", pomFile)
//			configFileProvider([configFile(fileId: 'P2_MAVEN_SETTINGS', variable: 'MAVEN_SETTINGS_XML'),configFile(fileId: 'TOOLCHAINS', replaceTokens: true, variable: 'TOOLCHAINS_SETTINGS_XML')]) {
//             	sh '$M2_HOME/bin/mvn -T2 -Dmaven.test.failure.ignore=true clean install -s $MAVEN_SETTINGS_XML -t $TOOLCHAINS_SETTINGS_XML --batch-mode --errors --fail-at-end --show-version -f ./pom.xml'
//        	}
		}

		stage('Build NiFi Docker Image') {
			docker.withRegistry("https://${utils.NEXUS_MIRROR_REGISTRY}", "${utils.NEXUS_MIRROR_CREDS}") {
				javaUtils.runMavenCommand("package -DskipTests -ff -nsu -Pdocker -Ddocker.image.name=${env.JRE_NAME} -Ddocker.image.tag=${env.JRE_TAG}", pomFile)
				// configFileProvider([configFile(fileId: 'P2_MAVEN_SETTINGS', variable: 'MAVEN_SETTINGS_XML'),configFile(fileId: 'TOOLCHAINS', replaceTokens: true, variable: 'TOOLCHAINS_SETTINGS_XML')]) {
            	// 	sh '$M2_HOME/bin/mvn package -DskipTests -ff -nsu -Pdocker -Ddocker.image.name=$JRE_NAME -Ddocker.image.tag=$JRE_TAG -s $MAVEN_SETTINGS_XML -t $TOOLCHAINS_SETTINGS_XML --batch-mode --errors --show-version -f ./nifi-docker/dockermaven/pom.xml'
        		// }
			}
			docker.withRegistry("https://${env.DOCKER_REGISTRY}", "${env.DOCKER_REGISTRY_CREDS}") {
				def image = docker.image("${env.DOCKER_REGISTRY}/nifi:${env.COMMON_BUILD_VERSION_SHORT}-dockermaven")
				dockerImage = "${env.COMMON_BUILD_VERSION_SHORT}-jre-11.0-${env.COMMIT_ID}"
				image.push("${dockerImage}")
			}
		}

		stage('Code Analysis') {
			//	javaUtils.snykAnalysis("${env.WORKSPACE}")
			//	javaUtils.runCodeAnalysis("ai.darklight.nifi")
			//  publishCoverage adapters: [jacocoAdapter(mergeToOneReport: false, path: '**/target/site/jacoco/jacoco.xml')], sourceFileResolver: sourceFiles('STORE_LAST_BUILD')
			//	dependencyCheckPublisher pattern: ''
		}

		// Record results of build and junit tests
		stage('Record Results') {
			javaUtils.recordResultsForJavaMavenBuilds();
			currentBuild.description="Image: ${dockerImage}"
		}

		// Archive built jars and fingerprint them
		stage('Archive Artifacts') {
			javaUtils.archiveArtifactsJavaMavenBuilds('nifi-assembly/target/nifi-*-bin.zip');
			//if (branchname == "release") {
				// create a tag for git based on the version
			//}
		}
		
	} catch (Exception ex) {
		currentBuild.result = "FAILED"
		throw ex
		
	} finally {	
		notify.notifyBuild(currentBuild.result, env.POM_VERSION);
	}
}