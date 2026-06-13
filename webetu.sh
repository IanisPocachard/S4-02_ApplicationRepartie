#!/bin/bash
set -e # stop le script entier si une seule commande échoue

USER_WEBETU="e71517u"
LIEN_WEBETU="webetu.iutnc.univ-lorraine.fr"
WEBETU_DIR="www/"
ARCHIVE="carte.zip"

cd "$(dirname "$0")/frontend" # se place à la racine du projet

npm run build  # compilation du typescript

rm -f "$ARCHIVE" # supprime l'ancienne archive au cas où elle soit restée à cause d'un crash du programme

zip -r -9 "$ARCHIVE" \
  index.html \
  config.json \
  css \
  pages \
  dist

scp "$ARCHIVE" "${USER_WEBETU}@${LIEN_WEBETU}:${WEBETU_DIR}/"

ssh "${USER_WEBETU}@${LIEN_WEBETU}" "
  cd ${WEBETU_DIR} &&
  rm -rf css pages dist index.html config.json &&
  unzip -o ${ARCHIVE} &&
  rm ${ARCHIVE}
"

rm -f "$ARCHIVE" # supprime l'archive

echo "Déploiement terminé."