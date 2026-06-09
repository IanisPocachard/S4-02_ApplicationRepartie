import { renderApp } from "./ui/ui";

renderApp();

// fetch de test pour voir les fameuses données bloquées dont parle l'énoncé pour les problèmes de CORS
fetch("https://carto.g-ny.eu/data/cifs/cifs_waze_v2.json")
    .then(response => response.json())
    .then(data => {
        console.log("Données récupérées :", data);
    })
    .catch(error => {
        console.error("Erreur lors de la récupération des données :", error);
    });
