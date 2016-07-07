#!/usr/bin/env bash

# in case "permission denied: ./dot.sh"
# execute once: $ chmod +x dot.sh
# then script would stop fire exceptions

OIFS="$IFS"
IFS=$'\n'

echo "Start replacing dots in smartling/translations:"

path=$( cd "$(dirname "${BASH_SOURCE}")" ; pwd -P )/translations
printf "smartling/translations path: $path \n\n"

for f in $(find $path /tmp -name '*.xml')
do
    echo "Processing $f"
    sed -i "" 's/\.\.\./…/g' $f
done

printf "\nFinish replacing dots in smartling/translations"