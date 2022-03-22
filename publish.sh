#!/bin/bash

readonly GPG_KEY_ID="52BA9EB387E196ED14A26683BD979BA91D9F6F2E"
readonly SONATYPE_USERNAME="vasya-polyansky"

readonly GPG_PRIVATE_KEY=$(gpg --export-secret-keys --armor ${GPG_KEY_ID})

read -rsp "GPG passphrase: " GPG_PRIVATE_PASSWORD
printf "\n"
read -rsp "Sonatype password: " SONATYPE_PASSWORD

./gradlew publish \
  -Pgpg.private.key="${GPG_PRIVATE_KEY}" \
  -Pgpg.private.password="${GPG_PRIVATE_PASSWORD}" \
  -Psonatype.username=${SONATYPE_USERNAME} \
  -Psonatype.password=${SONATYPE_PASSWORD}
