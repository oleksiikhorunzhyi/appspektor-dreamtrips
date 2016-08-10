#!/usr/bin/env bash

# in case "permission denied: ./replace.sh"
# execute once: $ chmod +x replace.sh
# then script would stop fire exceptions

# ./replace.sh - replace all translations
# ./replace.sh [social dtl messenger] single or plenty - replace chosen translations

# ATTENTION! strings (1).xml would be replaced too. Just delete it

readonly socialFile="strings.xml"
readonly dtlFile="dtl_strings.xml"
readonly messengerFile="messenger_strings.xml"

readonly socialExclude=" --exclude=$socialFile"
readonly dtlExclude=" --exclude=$dtlFile"
readonly messengerExclude=" --exclude=$messengerFile"

readonly fullExcludeParams=" $socialExclude $dtlExclude $messengerExclude"

excludeParams=" $socialExclude $dtlExclude $messengerExclude"

if [[ $* == *" social"* ]];
then
    excludeParams=${excludeParams/$socialExclude/""}
fi

if [[ $* == *" dtl"* ]]
then
    excludeParams=${excludeParams/$dtlExclude/""}
fi

if [[ $* == *" messenger"* ]]
then
    excludeParams=${excludeParams/$messengerExclude/""}
fi

if [ "$fullExcludeParams" == "$excludeParams" ];
then
    excludeParams=""
    printf $*"\nThere are no inputted suitable params. Available params are: social, dtl, messenger. All translations would be replaced.\n\n"
fi

echo "Start replacing translations:"

smartlingRes=$( cd "$(dirname "${BASH_SOURCE}")" ; pwd -P )/translations/
projectRes=${smartlingRes%/*/*/*}/app/src/main/res

printf "FROM: %s\nTO: %s\n" "$smartlingRes" "$projectRes"

rsync -avh $excludeParams $smartlingRes $projectRes

printf "\nFinish replacing translations"