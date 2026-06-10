import { IncidentsResponse } from "../types/incidents";
import { PROXY_INCIDENTS_URL } from "../config/config";

export async function fetchIncidents(urlApi : string, urlProxy : string) : Promise<IncidentsResponse> {
    try {
        const reponse = await fetch(urlApi);

        if (!reponse.ok) {
            throw new Error("Erreur HTTP : " + reponse.status);
        }

        return await reponse.json() as IncidentsResponse; // obligé de mettre un cast en IncidentsResponse pour que le type de retour de la fonction soit correct

    } catch(error) { // on rentre dans le catch si une erreur CORS, et c'est à ce moment la qu'on applique la solution de contournement en demandant au proxy d'effectuer la requête pour nous via le client HTTP qu'il peut appeler en RMI
        console.warn("Erreur lors de la récupération des incidents, tentative de contournement via le proxy : ", error);
        const reponseDuProxy = await fetch(PROXY_INCIDENTS_URL);

        if (!reponseDuProxy.ok) {
            throw new Error("Erreur HTTP du PROXY ! : " + reponseDuProxy.status);
        }

        return await reponseDuProxy.json() as IncidentsResponse;
    }
}