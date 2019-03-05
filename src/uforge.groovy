#!/usr/bin/env groovy

/**
 * Copyright (c) 2007-2019 UShareSoft, All rights reserved
 *
 * @author UShareSoft
 */

def uForgeUrl
def login
def password

/**
* install hammr with asked version
*/
def init(String uForgeUrl, String login, String password, String version) {
    this.uForgeUrl = uForgeUrl
    this.login = login
    this.password = password

    installHammr(version)
}

def installHammr(String version) {
    sh """
        virtualenv -p python2.7 $HAMMR_VENV
        . $HAMMR_VENV/bin/activate
        pip install hammr==$version
        deactivate
    """
}

/**
* generate a machine image with UForge
*/
def generate(String templatePath) {
    sh """
        . $HAMMR_VENV/bin/activate
        hammr template create --force --url $uForgeUrl -u $login -p $password --file $templatePath
        hammr template build --url $uForgeUrl -u $login -p $password --file $templatePath
        deactivate
    """
}

return this