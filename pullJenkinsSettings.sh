#!/bin/bash

# This script pulls maven settings into the jenkins job runner workspace
# It's main purpose is to provide the wma.maven.url to the project's pom.xml
# so that we don't expose the private chs artifactory url to the public

git config user.email "cida_javadev@usgs.gov"
git config user.name "CIDA-Jenkins"

cp $WMA_ARTIFACTORY_SETTINGS_XML $WORKSPACE/settings.xml

if test ${RELEASE_BUILD} == true; then

    docker run --rm \
        -v "$WORKSPACE":/usr/src/mymaven \
        -v "$WORKSPACE":/root/.m2 \
        -w /usr/src/mymaven \
        maven:3.6.0-jdk-11 mvn -Drevision=$BUILD_NUMBER -DskipTests=true -Ddocker.skip=true deploy scm:tag -P release;

else

    docker run --rm \
        -v "$WORKSPACE":/usr/src/mymaven \
        -v "$WORKSPACE":/root/.m2 \
        -w /usr/src/mymaven \
        maven:3.6.0-jdk-11 mvn -Drevision=$BUILD_NUMBER -DskipTests=true -Ddocker.skip=true deploy;

fi;
