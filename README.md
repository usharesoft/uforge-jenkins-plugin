# UForge AppCenter Jenkins Plugin

This plugin integrates UForge AppCenter with Jenkins.

## Prerequisite

The plugin requires at least Jenkins version 2.150.2 to work.

## Usage
* `mvn install` builds the plugin. This will create the file `./target/uforge.hpi` that you can deploy to Jenkins.
* `mvn hpi:run` runs Jenkins with the current plugin project. You can access the running Jenkins instance at `localhost:8080/jenkins`

## UForge AppCenter Jenkins Library
The directory `library/` contains a groovy library that integrates UForge AppCenter with Jenkins.

## Release process
This section describes the process to release the UForge AppCenter Jenkins plugin

The release is automated by the Jenkins pipeline described in `ci/jenkins-plugin-release.goovy`.
It releases the plugin to a Nexus Repository using `maven-release-plugin`.

The job does:
 - run tests
 - commit and tag the release version
 - build and upload to Nexus
 - prepare the next development version

### Job parameters
This job requires parameters:
- `GIT_BRANCH`: The git branch to release
- `VERSION`: The official release version (example: 1.0.0)
- `NEXT_VERSION`: The next official release version, without qualifier (example: 1.0.1)
