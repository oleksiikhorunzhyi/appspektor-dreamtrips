#!/bin/bash

function validate_required_input {
    key=$1
    value=$2

    if [ -z "${value}" ] ; then
        echo "[!] Missing required input: ${key}"
        usage
        exit 1
    fi
}

function usage
{
    echo "usage: $0 [[[-u username ] [-p password] [-s scid]] [-d domain] (preprod | stage) | [-h]]"
}

username=68242071
password=Test1231!
scid=68242071
domain=stage

while [ "$1" != "" ]; do
    case $1 in
        -u | --username )       shift
                                username=$1
                                ;;
        -p | --password )        shift
                                password=$1
                                ;;
        -s | --scid )            shift
                                scid=$1
                                ;;
        -d | --domain )         shift
                                domain=$1
                                ;;
        -h | --help )           usage
                                exit
                                ;;
        * )                     usage
                                exit 1
    esac
    shift
done

echo username $username
echo password $password
echo scid $scid

validate_required_input "username" $username
validate_required_input "password" $password
validate_required_input "scid" $scid
validate_required_input "domain" $domain


domain_url=

if [ "$domain" = "stage" ]
    then
    domain_url="http://techery--dt--staging-techery-io-txbe74ea4jwh.runscope.net"
elif [ "$domain" = "preprod" ]
    then
    domain_url="http://techery-dt-preprod.techery.io"
else
    echo "unknown domain: $domain\n use (preprod | stage)"
    exit 1
fi

echo domain_url $domain_url

num=$(curl -sS -X "POST" "$domain_url/api/sessions" \
     -H "Content-Type: application/json; charset=utf-8" \
     -H "Accept: application/com.dreamtrips.api+json;version=2" \
     -d "{\"username\":\"$username\",\"password\":\"$password\"}" | /usr/bin/perl -pe 's/.*"token":"([a-z0-9]*)".*/\1/')
echo token $num

curl -X "DELETE" "$domain_url/api/smartcard/provisioning/card_user/$scid" \
     -H "Content-Type: application/json" \
     -H "User-Agent: DreamTrip/1.15.0 (iPhone; iOS 9.0; Scale/2.00)" \
     -H "Authorization: Token token=$num" \
     -H "Accept: application/com.dreamtrips.api+json;version=2" \
     -d "{\"device_id\":\"b93d0d4808af3a355615656875243ZX1G3263NH\"}"