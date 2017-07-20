#!/bin/bash

string=""

stringsPathCommon="../app/src/main/res/values/strings.xml"
stringsPathSocial="../app/src/main/res/values/social_strings.xml"
stringsPathDtl="../app/src/main/res/values/dtl_strings.xml"
stringsPathMessenger="../app/src/main/res/values/messenger_strings.xml"
stringsPathWallet="../app/src/main/res/values/wallet_strings.xml"

if [[ $* == *"common"* ]];
then
    string+=" "$stringsPathCommon
fi

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

if [[ $* == *"wallet"* ]]
then
    string+=" "$stringsPathWallet
fi

if [[ -z "$string" ]];
then
    printf $*"\nThere are no suitable inputted params. Available params are: common, social, dtl, messenger, wallet"
else
    echo $string
    ./core/upload.sh -t android $string
fi
