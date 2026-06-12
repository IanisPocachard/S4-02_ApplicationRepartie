/**
 * Module responsable des appels HTTP liés aux incidents
 * Le frontend tente d'abord de récupérer les données directement depuis l'API publique, et une fois qu'il reçoit l'erreur CORS il fait un fallback en demandant au proxy de faire la requête à sa place via le client HTTP
 */

import { IncidentsResponse } from "../types/incidents";

export async function fetchIncidents(urlApi : string, urlProxy : string) : Promise<IncidentsResponse> {
    try {

        const reponse = await fetch(urlApi);

        if (!reponse.ok) {
            throw new Error("Erreur HTTP : " + reponse.status);
        }

        return await reponse.json() as IncidentsResponse; // obligé de mettre un cast en IncidentsResponse pour que le type de retour de la fonction soit correct

    } catch(error) { // on rentre dans le catch si une erreur CORS, et c'est à ce moment la qu'on applique la solution de contournement en demandant au proxy d'effectuer la requête pour nous via le client HTTP qu'il peut appeler en RMI

        console.warn("Erreur lors de la récupération des incidents, tentative de contournement via le proxy : ", error);
        
        const reponseDuProxy = await fetch(urlProxy);

        if (!reponseDuProxy.ok) {
            console.warn("[API_INCIDENTS] Erreur lors de la récupération des incidents via le proxy : " + reponseDuProxy.status);
            throw new Error("Le service des incidents est indisponible");
        }

        return await reponseDuProxy.json() as IncidentsResponse;
    }
}