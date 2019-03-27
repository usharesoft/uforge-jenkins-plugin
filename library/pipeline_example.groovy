/**
 * Copyright (c) 2007-2019 UShareSoft, All rights reserved
 *
 * @author UShareSoft
 */

pipeline {
    agent any
    environment {
        HAMMR_VENV = "$WORKSPACE/hammr"
    }
    stages {
        stage('Init') {
            steps {
                script {
                    def uForgeUtils = load 'src/uforge.groovy'
                    uForgeUtils.init("${params.UFORGE_URL}", "${params.LOGIN}", "${params.PASSWORD}", "${params.VERSION}")
                }
            }
        }
        stage('Generate') {
            steps {
                script {
                    def uForgeUtils = load 'src/uforge.groovy'
                    uForgeUtils.generate("${params.TEMPLATE_PATH}")
                }
            }
        }
    }
}