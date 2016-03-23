#!/bin/bash

string=""

stringsPathSocial="../app/src/main/res/values/strings.xml"
stringsPathDtl="../app/src/main/res/values/dtl_strings.xml"
stringsPathMessenger="../app/src/main/res/values/strings.xml"

if [[ $* == *"social"* ]];
then
    string+=" "$stringsPathSocial
fi

if [[ $* == *"dtl"* ]]
then
    string+=" "$stringsPathDtl
fi

if [[ $* == *"messenger"* ]]
then
    string+=" "$stringsPathMessenger
fi

if [[ -z "$string" ]];
then
    printf $*"\nThere are no suitable inputted params. Available params are: social, dtl, messenger"
else
    echo $string
    ./core/upload.sh -t android $string
fi