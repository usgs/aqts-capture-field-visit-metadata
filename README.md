# Aquarius Timeseries (AQTS) Capture Field Visit Readings By Location

[![Build Status](https://travis-ci.com/usgs/aqts-capture-field-visit-metadata.svg?branch=master)](https://travis-ci.com/usgs/aqts-capture-field-visit-metadata)
[![codecov](https://codecov.io/gh/usgs/aqts-capture-field-visit-metadata/branch/master/graph/badge.svg)](https://codecov.io/gh/usgs/aqts-capture-field-visit-metadata)

This project transforms data from the Aquarius GetFieldVisitReadingsByLocation api call into intermediate tables meant to be used by the aqts-capture-etl
for further processing. The source is the json_data table.

## Testing
This project contains JUnit tests. Maven can be used to run them (in addition to the capabilities of your IDE).

### Docker Network
A named Docker Network is needed to run the automated tests via maven. The following is a sample command for creating your own local network. In this example the name is aqts and the ip addresses will be 172.25.0.x

```.sh
docker network create --subnet=172.25.0.0/16 aqts
```

### Unit Testing
To run the unit tests of the application use:

```.sh
mvn package
```

### Database Integration Testing with Maven
To additionally start up both the transform and observation Docker databases and run the integration tests of the application use:

```.sh
mvn verify \
    -DTRANSFORM_TESTING_DATABASE_PORT=5437 \
    -DLOCAL_TRANSFORM_TESTING_DATABASE_PORT=5437 \
    -DTRANSFORM_TESTING_DATABASE_ADDRESS=localhost \
    -DTESTING_DATABASE_NETWORK=aqts \
```

### Database Integration Testing with an IDE
To run tests against local transform and observation Docker databases use:

Transform database:
```.sh
docker run -p 127.0.0.1:5437:5432/tcp usgswma/aqts_capture_db:ci
```

Additionally, add an application.yml configuration file at the project root (the following is an example):
```.yaml
AQTS_DATABASE_ADDRESS: localhost
AQTS_DATABASE_PORT: 5437
AQTS_DATABASE_NAME: database_name
AQTS_SCHEMA_NAME: schema_name
AQTS_SCHEMA_OWNER_USERNAME: schema_owner
AQTS_SCHEMA_OWNER_PASSWORD: changeMe
```
