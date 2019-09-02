# Contributing

We welcome contributions to this project by both internal and external parties

# How to contribute
1. Fork the repository into your own github account (external contributors) or create a new branch (internal contributions)
1. Make your code changes
1. Ensure you commit message is descriptive as it acts as the changelog.  Mark any breaking changes with `BREAKING`. Include a rectification strategy if you introduce a `BREAKING` change.
1. Ensure `README.md` is updated if needed. 
1. Submit a pull request back to `master` branch (or the branch you are contributing to)
1. Ensure TravisCI build passes
1. Await reviews
1. Once merged into `master` a `SNAPSHOT` build will be available for consumption immediately [here](https://oss.sonatype.org/content/repositories/snapshots/io/github/origin-energy/). Note that snapshots change regularly and cannot be relied upon.
1. Hard Releases will by made once enough features have been added. 

# Deploying to maven central
Gradle release plugin is not currently working so this is a manual process at the moment.

## Preparing
1. Create a branch `release/<VersionNumber>`
1. Update `gradle.properties` and remove `-SNAPSHOT` from the version number
1. Check this file into version control and push the branch to the remote
1. run `./gradlew clean build signArchives uploadArchives -PossrhUsername=${SONAR_USERNAME} -PossrhPassword=${SONAR_PASSWORD} -Psigning.keyId=${GPG_KEY_ID} -Psigning.password=${GPG_KEY_PASSPHRASE} -Psigning.secretKeyRingFile=${PATH_TO_SECRING_GPG}`

## Releasing
1. Login to SONAR (https://oss.sonatype.org)
1. Click 'Staging Repositories' and locate the 'iogithuborigin-energy' bundle
1. Review artifacts are correct in the 'Content' tab
1. Press the 'Close' and give a reason such as "Jack Matthews - Confirmed artifacts are OK"
1. Wait for about 1 min and press the 'Refresh button', if all sanity checks have passed the 'Release' button will be visible
1. Press the 'Release' button and give a reason for releasing
1. Objects should be available in about 10 min (Longer for search.maven.org)

## Cleanup
1. Checkout master branch
1. Increment version number in `gradle.properties`
1. Create pull request for merge