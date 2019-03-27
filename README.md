# UForge AppCenter Jenkins Plugin
	
This plugin integrates UForge AppCenter with Jenkins.
	
## Prerequisite
	
The plugin requires at least Jenkins version 2.150.2 to work.
	
## Usage
* `mvn install` builds the plugin. This will create the file `./target/uforge.hpi` that you can deploy to Jenkins.
* `mvn hpi:run` runs Jenkins with the current plugin project. You can access the running Jenkins instance at `localhost:8080/jenkins`

## UForge AppCenter Jenkins Library
The directory `library/` contains a groovy library that integrates UForge AppCenter with Jenkins.
