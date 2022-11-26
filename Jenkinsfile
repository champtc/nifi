@Library('utils@dev') _

node {
	def pomFile

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
			env.NIFI_BASE_DIR = "/build/nifi/${env.BUILD_NUMBER}"
			// configFileProvider([configFile(fileId: 'P2_MAVEN_SETTINGS', variable: 'MAVEN_SETTINGS_XML'),configFile(fileId: 'TOOLCHAINS', replaceTokens: true, variable: 'TOOLCHAINS_SETTINGS_XML')]) {
            // 	sh '$M2_HOME/bin/mvn -T2 -Dmaven.test.failure.ignore=true clean install -s $MAVEN_SETTINGS_XML -t $TOOLCHAINS_SETTINGS_XML --batch-mode --errors --fail-at-end --show-version -f ./pom.xml'
        	// }
		}

		stage('Build NiFi Docker Image') {
			configFileProvider([configFile(fileId: 'P2_MAVEN_SETTINGS', variable: 'MAVEN_SETTINGS_XML'),configFile(fileId: 'TOOLCHAINS', replaceTokens: true, variable: 'TOOLCHAINS_SETTINGS_XML')]) {
            	sh '$M2_HOME/bin/mvn package -DskipTests -ff -nsu -Pdocker -Ddocker.image.name=openjdk -Ddocker.image.tag=11.0.12-jre -s $MAVEN_SETTINGS_XML -t $TOOLCHAINS_SETTINGS_XML --batch-mode --errors --fail-at-end --show-version -f ./nifi-docker/dockermaven/pom.xml'
        	}
			docker.withRegistry("https://nexus.darklight.ai/repository/darklight-docker/", "NEXUS_DEPLOY_USER") {
				def dockerImage = docker.image("apache/nifi:${env.POM_VERSION}-dockermaven")
				dockerImage.push(env.POM_VERSION)
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