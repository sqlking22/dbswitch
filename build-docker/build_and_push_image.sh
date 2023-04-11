#!/bin/sh

set -e 

DBSWITCH_VERSION=1.7.0
BUILD_DOCKER_DIR="$( cd "$( dirname "$0"  )" && pwd  )"
PROJECT_ROOT_DIR=$( dirname "$BUILD_DOCKER_DIR")
DOCKER_DBSWITCH_DIR=$BUILD_DOCKER_DIR/dbswitch

# build project
cd $PROJECT_ROOT_DIR && sh docker-maven-build.sh && cd -

# copy files
cd $BUILD_DOCKER_DIR \
 && tar zxvf $PROJECT_ROOT_DIR/target/dbswitch-release-${DBSWITCH_VERSION}.tar.gz -C /tmp \
 && cp /tmp/dbswitch-release-${DBSWITCH_VERSION}/lib/* ${BUILD_DOCKER_DIR}/dbswitch/dbswitch-release/lib/ \
 && cp -r /tmp/dbswitch-release-${DBSWITCH_VERSION}/drivers/* ${BUILD_DOCKER_DIR}/dbswitch/dbswitch-release/drivers/ \
 && rm -rf /tmp/dbswitch-release-*

# build image
cd ${DOCKER_DBSWITCH_DIR} \
  && tar zcvf dbswitch-release.tar.gz dbswitch-release/ \
  && docker build -t inrgihc/dbswitch:${DBSWITCH_VERSION} . \
  && rm -f dbswitch-release.tar.gz \
  && rm -f dbswitch-release/lib/*.jar \
  && rm -rf dbswitch-release/drivers/*

# clean project
cd $PROJECT_ROOT_DIR && sh docker-maven-clean.sh && cd -

# login and push docker image
docker login -u inrgihc
docker push inrgihc/dbswitch:${DBSWITCH_VERSION}

