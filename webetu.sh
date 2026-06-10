#!/bin/bash

cd frontend/

nmp run build

zip -r -9 -e carte.zip  css/ pages/ dist/

echo "..." | scp carte.zip ...@webetu.iutnc.univ_lorraine.fr:/www

echo "..." | ssh  ...@webetu.iutnc.univ_lorraine.fr "cd /www && unzip carte.zip"
