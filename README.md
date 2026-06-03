# S4-02_ApplicationRepartie

## Partie Frontend - Adrien et Louis

### Objectif du frontend
Le frontend servira à afficher l'application côté navigateur et à brancher progressivement les différents écrans et composants du projet.

### Installation
Création du dossier frontend et initialisation du projet npm avec esbuild, typescript et handlebars pour potentiellement faire du templating côté client.

Exécution de la commande :
```bash
npm install
```
avec les fichiers package.json et tsconfig.json déjà créés repris d'un projet fonctionnel pour éviter les problèmes de configuration.

Installation de leaflet pour utiliser une carte interactive :
```bash
npm install leaflet
```

### Structure actuelle
- `frontend/index.html` : page HTML d'entrée
- `frontend/typescript/index.ts` : point d'entrée TypeScript
- `frontend/typescript/ui/` : orchestration de l'affichage
- `frontend/typescript/map/` : logique de carte
- `frontend/typescript/http/` : requêtes vers les API
- `frontend/typescript/types/` : types partagés
- `frontend/typescript/config/` : configuration et constantes