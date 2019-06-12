/**
 * Copyright (c) 2007-2019 UShareSoft, All rights reserved
 *
 * @author UShareSoft
 */

pipeline {
    agent any
    stages {
        stage('Release Plugin') {
            steps {
                script {
                    sshagent(['github-usharesoft-ci']) {
                        sh 'mvn release:prepare release:perform -DreleaseVersion=${VERSION} -DdevelopmentVersion=${NEXT_VERSION}-SNAPSHOT -Dtag=${VERSION}'
                    }
                }
            }
            post {
                always {
                    script {
                        def buildPlugin = load 'ci/lib/build-plugin.groovy'
                        buildPlugin.archive()
                    }
                }
            }
        }
    }
}
