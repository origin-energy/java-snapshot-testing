#!/usr/bin/env bash

# travis-ci.org maven publishing
# This will sign and deploy the artifacts to the maven central repository
# Environment variables are configured on the
#
if [ "$TRAVIS_BRANCH" = 'master' ] && [ "$TRAVIS_PULL_REQUEST" == 'false' ]; then
  openssl aes-256-cbc -K $encrypted_995a2870b5b0_key -iv $encrypted_995a2870b5b0_iv -in sonar.travis.gpg.enc -out sonar.travis.gpg -d
  echo 'Signing & Deploying to Maven Central'
  ./gradlew signArchives uploadArchives -PossrhUsername=${SONAR_USERNAME} -PossrhPassword=${SONAR_PASSWORD} -Psigning.keyId=${GPG_KEY_ID} -Psigning.password=${GPG_KEY_PASSPHRASE} -Psigning.secretKeyRingFile=../sonar.travis.gpg
fi