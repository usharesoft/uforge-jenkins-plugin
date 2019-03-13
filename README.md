# UForge Jenkins library

This is a groovy library that allows you to perform actions on a UForge server from a Jenkins pipeline. It uses [Hammr CLI](https://github.com/usharesoft/hammr) to create and generate UForge appliances.

## Prerequisite

To use this library you need a Jenkins instance with `pip`,`virtualenv` and `python2.7` installed on the executor.
You also need a running UForge server.

## Usage

You can find an example of usage in `pipeline_example.groovy`.

The `init()` method installs hammr CLI in the `virtualenv`.

The `generate()` method takes the path to a YAML or a JSON template file and creates the described appliance (see [documentation](http://docs.usharesoft.com/projects/hammr/en/latest/pages/templates/templates-spec/overview.html) for syntax). The appliance is then generated in the targeted formats (section `builders` of template).
__Warning__: If you already have an appliance with the same name in your UForge server, it will be replaced by the one described in the template file.

### Environment variables

The library requires the `HAMMR_VENV` environment variable. This is the path used by `virtualenv` to install Hammr and launch the scripts. In the example, `HAMMR_VENV` is defined globally to be shared between the different stages.

```
environment {
    HAMMR_VENV = '$WORKSPACE/hammr'
}
```

You can also define it only for one stage:

```
withEnv(['HAMMR_VENV=$WORKSPACE/hammr']) {
    sh "echo do some stuff"
}
```

### Parameters

The library requires these parameters:

- `UFORGE_URL<String>`: URL of the UForge instance
- `LOGIN<String>`: login of the UForge account
- `PASSWORD<Password>`: password of the UForge account
- `VERSION<String>`: version of UForge
- `TEMPLATE_PATH<String>`: path to the YAML or JSON template (can be an URL)

You can define them in the plugin configuration "Configure" -> "General", check "This project is parameterized" and add these parameters. Another way of doing it is to put the parameters in the Jenkinsfile as shown below :

```
parameters {
    string(name: 'UFORGE_URL', defaultValue: '<UFORGE_URL>', description: '')
    string(name: 'LOGIN', defaultValue: '<LOGIN>', description: '')
    password(name: 'PASSWORD', defaultValue: '<PASSWORD>', description: '')
    string(name: 'VERSION', defaultValue: '<VERSION>', description: '')
    string(name: 'TEMPLATE_PATH', defaultValue: '<TEMPLATE_PATH>', description: '')
}
```

## Test

The library can be tested using `pipeline_example.groovy` and `template_example.yml`. It creates a machine image and generates it on VirtualBox.