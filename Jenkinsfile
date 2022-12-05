@Library('utils@dev') _

node {
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
			configFileProvider([configFile(fileId: 'P2_MAVEN_SETTINGS', variable: 'MAVEN_SETTINGS_XML'),configFile(fileId: 'TOOLCHAINS', replaceTokens: true, variable: 'TOOLCHAINS_SETTINGS_XML')]) {
             	sh '$M2_HOME/bin/mvn -T2 -Dmaven.test.failure.ignore=true clean install -s $MAVEN_SETTINGS_XML -t $TOOLCHAINS_SETTINGS_XML --batch-mode --errors --fail-at-end --show-version -f ./pom.xml'
        	}
		}

		stage('Build NiFi Docker Image') {
			configFileProvider([configFile(fileId: 'P2_MAVEN_SETTINGS', variable: 'MAVEN_SETTINGS_XML'),configFile(fileId: 'TOOLCHAINS', replaceTokens: true, variable: 'TOOLCHAINS_SETTINGS_XML')]) {
            	sh '$M2_HOME/bin/mvn package -DskipTests -ff -nsu -Pdocker -Ddocker.image.name=$JRE_NAME -Ddocker.image.tag=$JRE_TAG -s $MAVEN_SETTINGS_XML -t $TOOLCHAINS_SETTINGS_XML --batch-mode --errors --show-version -f ./nifi-docker/dockermaven/pom.xml'
        	}
			docker.withRegistry("${env.NEXUS_DOCKER_REGISTRY}", "NEXUS_DEPLOY_USER") {
				def image = docker.image("${env.NEXUS_DOCKER_REGISTRY}/nifi:${env.COMMON_BUILD_VERSION_SHORT}-dockermaven")
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