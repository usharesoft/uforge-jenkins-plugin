# UForge AppCenter Jenkins Plugin
This plugin integrates UForge AppCenter with Jenkins.
It allows you to perform actions on a UForge server from a Jenkins pipeline or a Freestyle Job.
It uses [Hammr CLI](https://github.com/usharesoft/hammr) to create and generate UForge appliances.

## Prerequisite
- The plugin requires at least Jenkins version 2.150.2 to work.
- `pip`,`virtualenv` and `python2.7` need to be installed on the executor.
- You also need a running UForge AppCenter server (minimum version: 3.8.12).

## Build from source
* `mvn install` builds the plugin. This will create the file `./target/uforge.hpi` that you can deploy to Jenkins.
* `mvn hpi:run` runs Jenkins with the current plugin project. You can access the running Jenkins instance at `localhost:8080/jenkins`

## Usage
This plugin provides a Jenkins build step that takes a template file describing a machine image.
This image is built and generated onto a UForge AppCenter server.

__warnings__:
The Jenkins plugin currently has the following limitations, due to underlying Hammr issues:
- If an appliance with the same name and version already exists on the UForge AppCenter server, the job will not fail and will generate an image for the pre-existing appliance (and not for the one provided in the template file).
- If a generation for the same template and image format is ongoing when the job launches the generation, the generation fails but the job appears successful.

### Template file
The template file describes the machine image that the plugin will create and generate.
It is written in YAML or JSON format.

The template contains a `stack` section that describes the machine image.
See http://docs.usharesoft.com/projects/hammr/en/latest/pages/templates/template-create.html for more information.

It also contains a `builders` section that describes one or many targets for the generation.
See http://docs.usharesoft.com/projects/hammr/en/latest/pages/machine-images/builders/overview.html for more information.

You need to make this template file accessible for the UForge AppCenter Plugin build step.

### Freestyle Job
In Jenkins you can create a Freestyle Job with the build step "Build with UForge".
This build step takes parameters:

- `UForge API URL<String>`: URL of the UForge server
- `UForge version<String>`: version of the UForge server
- `UForge login<String>`: login of the UForge account
- `UForge password<Password>`: password of the UForge account
- `Template path<String>`: path to the YAML or JSON template

### Pipeline
The build step is also compatible with pipeline and is called like this:
```
uforge url: <UFORGE_URL>, version: <VERSION>, login: <LOGIN>, password: <PASSWORD>, templatePath: <TEMPLATE_PATH>
```

### Environment variables
The build step will fill these environment variables:
- `UFORGE_APPLIANCE_ID` : id of the created appliance
- `UFORGE_IMAGE_ID`: id of the generated image

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
