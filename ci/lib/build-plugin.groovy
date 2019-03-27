/**
 * Copyright (c) 2007-2019 UShareSoft, All rights reserved
 *
 * @author UShareSoft
 */

def build() {
    sh 'mvn clean install'
}

def archive() {
    archiveArtifacts '**/target/*.hpi'
}

return this
