# Static Analysis Wrapper

An integration wrapper for the Veracode scan engine for use in CI/CD pipelines. This will allow for automated creation of an application in the Veracode platform, given an existing Business Unit and Team.

### Prerequisites

```
Business Unit and Team created in Veracode
Java 8 - if running CLI commands
Optional - Application profile created in Veracode
```

## Usage

To run the Veracode Wrapper jar enter the command Java –jar staticAnalysisWrapper.jar “appname” “team” “business unit” “artifact name”

For example:
```
Java -jar staticAnalysisWrapper “CARES_Hybris” “CARES_Hybris” “Cardinal Health at Home” “CARES_Hybris.zip” 
```

This wrapper will create the application if it does not exist. ***Be mindful of the application name as any difference in naming will create an additional application consuming a license(s).***

*The above issue can also occur when using the Veracode plugin in build pipeline tools such as Jenkins.*

## Built With

* [Gradle](https://gradle.org/)

## Contributing

Please read [CONTRIBUTING.md](https://gist.github.com/PurpleBooth/b24679402957c63ec426) for details on our code of conduct, and the process for submitting pull requests to us.

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://github.com/your/project/tags). 

## Authors
* **Josh Pobiega**
* **Billie Thompson** - *Readme template* - [PurpleBooth](https://github.com/PurpleBooth)

#FAQ

How do I find out my team and business unit?
1. Check with the Team tech lead
2. Logging into Veracode, finding the application in question, and going to the application’s profile page – the information will be listed under the ‘Organizational Information’ section
3. Send an email requesting the information to GMB-EIT-AppSec


How do I get access to Veracode?
1. Service Now Request for AD Group
2. Send email to **GMB-EIT-AppSec** if team lead or new team
3. Sec Arch team will create team in Veracode
4. Team lead will setup the rest of the access for team

More information can be found on the [AppSec Wiki Page](https://wiki.cardinalhealth.net/Application_Security)

## Acknowledgments

* Special thanks to the DevOps team - **Doug Hoke**, **Carlo Vedavato**, and **Jim Shingler** for serving as my Obi-Wan, Yoda, and Anakin. (In no particular order)
