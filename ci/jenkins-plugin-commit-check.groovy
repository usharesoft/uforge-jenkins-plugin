/**
 * Copyright (c) 2007-2019 UShareSoft, All rights reserved
 *
 * @author UShareSoft
 */

pipeline {
    agent any
    stages {
        stage('Build Plugin') {
            steps {
                script {
                    def buildPlugin = load 'ci/lib/build-plugin.groovy'
                    buildPlugin.build()
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
