# UForge AppCenter Jenkins Plugin
This plugin integrates UForge AppCenter with Jenkins.
It allows you to perform actions on a UForge server from a Jenkins pipeline or a Freestyle Job.
It uses [Hammr CLI](https://github.com/usharesoft/hammr) to create, generate and publish UForge appliances.

## Prerequisite
- The plugin requires at least Jenkins version 2.150.2 to work.
- `python2.7` needs to be installed on the executor.
- You also need a running UForge AppCenter server (minimum version: 3.8.13).

**Note**: If the "Installation Step" of the plugin fails, you may need to install some more dependencies on your Jenkins executor.
- On Debian based system: `python-dev, gcc, libxslt1-dev, which`
- On Red-hat based system: `gcc, python-devel, libxml2-devel, libxslt-devel, which, redhat-rpm-config`
- On Ubuntu system: `libz-dev`

## Installation
You can download `uforge-appcenter-X.X.X.hpi` file on `https://maven.usharesoft.com/nexus/content/repositories/official/com/usharesoft/jenkins/uforge-appcenter/` and install it manually in your Jenkins instance.

## Build from source
* `mvn install` builds the plugin. This will create the file `./target/uforge.hpi` that you can deploy to Jenkins.
* `mvn hpi:run` runs Jenkins with the current plugin project. You can access the running Jenkins instance at `localhost:8080/jenkins`

## Usage
This plugin provides a Jenkins build step that takes a template file describing a machine image.
This image is built and generated onto a UForge AppCenter server. Then it can be published to a chosen cloud environment or container.

### Template file
The template file describes the machine image that the plugin will create, generate and publish if needed.
It is written in YAML or JSON format.

The template contains a `stack` section that describes the machine image.
See http://docs.usharesoft.com/projects/hammr/en/latest/pages/templates/template-create.html for more information.

It also contains a `builders` section that describes one or many targets for the generation. If you want your image to be published, you need to add an `account` in the `builders` section and other information depending of the publish target.
See http://docs.usharesoft.com/projects/hammr/en/latest/pages/machine-images/builders/overview.html for more information.

You need to make this template file accessible for the UForge AppCenter Plugin build step.

### Freestyle Job
In Jenkins you can create a Freestyle Job with the build step "Build with UForge".
This build step takes parameters:

- `UForge API URL<String>`: URL of the UForge server
- `UForge version<String>`: version of the UForge server
- `UForge credentials<Username with password>`: credentials of the UForge account
- `Template path<String>`: path to the YAML or JSON template

### Pipeline
The build step is also compatible with pipeline and is called like this:
```
uforge url: <UFORGE_URL>, version: <VERSION>, credentialsId: <CREDENTIALS_ID>, templatePath: <TEMPLATE_PATH>
```

__Note__:`credentialsId` parameter is the id of a Jenkins credentials `Username with password`.

### Environment variables
The build step will fill these environment variables:
- `UFORGE_APPLIANCE_ID` : id of the created appliance
- `UFORGE_IMAGE_ID`: id of the generated image
- `UFORGE_IMAGE_REGISTERING_NAME`: registering name of the image in the internal registry (in case of Docker or OpenShift generation)

If the image is published, these variables will be set as well:
- `UFORGE_PUBLISH_ID`: id of the published image in the UForge server
- `UFORGE_CLOUD_ID`: id of the registered machine image in the cloud provider (if available)

## Release process
This section describes the process to release the UForge AppCenter Jenkins plugin

The release is automated by the Jenkins pipeline described in `ci/jenkins-plugin-release.goovy`.
It releases the plugin to a Nexus Repository using `maven-release-plugin`.

The job does the following:
 - runs tests
 - commits and tags the release version
 - builds and uploads to Nexus
 - prepares the next development version

### Job parameters
This job requires parameters:
- `GIT_BRANCH`: The git branch to release
- `VERSION`: The official release version (example: 1.0.0)
- `NEXT_VERSION`: The next official release version, without qualifier (example: 1.0.1)
