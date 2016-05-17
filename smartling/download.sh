#!/bin/bash

rm -rf ./translations/

./core/download.sh -t published -l "zh-TW zh-CN el-GR es-US hu-HU ms-MY ro-Hu sv-SE" -W translations

cp -rf ./translations/zh-TW/ ./translations/zh-HK/
cp -rf ./translations/zh-CN/ ./translations/zh/
cp -rf ./translations/el-GR/ ./translations/el-CY/

cd translations
for f in */; do
  mv "$f" "values-${f/-/-r}"
done
