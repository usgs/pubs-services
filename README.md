# pubs-services

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/fd42f9aae3a24a0199c7562f534333e4)](https://app.codacy.com/app/usgs_wma_dev/pubs-services?utm_source=github.com&utm_medium=referral&utm_content=usgs/pubs-services&utm_campaign=Badge_Grade_Settings)


## Development
This is a Spring Batch/Boot project. All of the normal caveats relating to a Spring Batch/Boot application apply.

### Dependencies
This application utilizes a PostgreSQL database.
[pubs-db](https://code.chs.usgs.gov/wma/iidd/pubs/pubs-db) contains everything you need to set up a development database environment. A Docker image is also available at [pubs-db](https://cloud.docker.com/u/usgswma/repository/docker/usgswma/pubs_db).

### Environment variables
To run the project you will need to create the file application.yml in the project's root directory and add the following:
```
nldiDbHost: hostNameOfDatabase
nldiDbPort: portNumberForDatabase
nldiDbUsername: dbUserName
nldiDbPassword: dbPassword

nldiProtocol: http
nldiHost: owi-test.usgs.gov:8080
nldiPath: /test-url

serverContextPath: /nldi
springFrameworkLogLevel: INFO
serverPort: 8080

spring.security.user.password: changeMe
```

### Testing
This project contains JUnit tests. Maven can be used to run them (in addition to the capabilities of your IDE).

To run the unit tests of the application use:

```shell
mvn package
```

To additionally start up a Docker database and run the integration tests of the application use:

```shell
mvn verify -DTESTING_DATABASE_PORT=5445 -DTESTING_DATABASE_ADDRESS=localhost -DTESTING_DATABASE_NETWORK=pubsServices -DTESTING_CROSSREF_USERNAME=changeMe -DTESTING_CROSSREF_PASSWORD=changeMe
```

