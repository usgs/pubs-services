# pubs-services

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/fd42f9aae3a24a0199c7562f534333e4)](https://app.codacy.com/app/usgs_wma_dev/pubs-services?utm_source=github.com&utm_medium=referral&utm_content=usgs/pubs-services&utm_campaign=Badge_Grade_Settings)
[![Build Status](https://travis-ci.org/usgs/pubs-services.svg?branch=master)](https://travis-ci.org/usgs/pubs-services)

## Development
This is a Spring Boot project. All of the normal caveats relating to a Spring Boot application apply.

### Dependencies
This application utilizes a PostgreSQL database.
[pubs-db](https://code.chs.usgs.gov/wma/iidd/pubs/pubs-db) contains everything you need to set up a development database environment. A Docker image is also available at [pubs-db](https://cloud.docker.com/u/usgswma/repository/docker/usgswma/pubs_db).

### Environment variables
To run the project you will need to create the file application.yml in the project's root directory and add the following:

``` yaml
QUEUE_URL: vm://localhost?broker.persistent=false
PUBS_DB_HOST: hostNameOfDatabase
PUBS_DB_PORT: portNumberForDatabase
PUBS_DB_NAME: dbName
PUBS_SCHEMA_NAME: dbSchema
PUBS_DB_READ_ONLY_USERNAME: dbUserName
PUBS_DB_READ_ONLY_PASSWORD: changeMe

SERVER_PORT: 8080
SERVER_CONTEXT_PATH: /pubs-services

CROSSREF_PROTOCOL: https
CROSSREF_HOST: test.crossref.org
CROSSREF_URL: /servlet/deposit
CROSSREF_PORT: -1
CROSSREF_USERNAME: crossrefUsername
CROSSREF_PASSWORD: changeMe
CROSSREF_DEPOSITOR_EMAIL: nobody@usgs.gov
CROSSREF_SCHEMA_URL: http://www.crossref.org/schema/deposit/crossref4.4.0.xsd

pubs.emailList: changeMe
pubs.mailHost: changeMe
PUBS_LOCK_TIMEOUT_HOURS: 1
PUBS_WAREHOUSE_ENPOINT: http://pubs.er.usgs.gov
PUBS_AUTHORIZED_GROUPS: group1, group2, spnGroup
PUBS_SPN_GROUPS: spnGroup

DISSEMINATION_SCHEDULE: <cron type syntax use just a single hyphen for not scheduled>
DISSEMINATION_LIST_URL: https://something.gov
DAYS_LAST_DISSEMINATED: 1
INFOPRODUCT_URL: https://something.gov

SWAGGER_DISPLAY_HOST: localhost:8080
SWAGGER_DISPLAY_PATH: /pubs-services
SWAGGER_DISPLAY_PROTOCOL: http

SECURITY_RESOURCE_ID: resourceId
SECURITY_KEYSET_URI: keysetURI

ROOT_LOG_LEVEL: INFO

spring.security.user.password: changeMe

logging.gelf.host: "tcp:localhost"
logging.gelf.port: 12201
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

It is also possible to build the project using the Maven Docker image. Commands to follow are in the .travis.yml file.
