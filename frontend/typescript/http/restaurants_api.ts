/**
 * Module responsable des appels HTTP liés aux restaurants :
 * récupération des restaurants et envoi des demandes de réservation au proxy
 */

import { PROXY_RESTAURANTS_URL, PROXY_RESERVATION_URL } from "../config/config";
import type { Restaurant, Reservation, DetailsReservation } from "../types/restaurants";

export async function fetchRestaurants(): Promise<Restaurant[]> {
	if (!PROXY_RESTAURANTS_URL) throw new Error("Proxy non configuré, impossible d'accésder aux données des restaurants");

	console.log("[API RESTAURANTS] Récupération des restaurants");

	let reponse : Response;

	try {
		reponse = await fetch(PROXY_RESTAURANTS_URL);
	} catch {
		throw new Error("Impossible de joindre le proxy pour récupérer les restaurants");
	}

	console.log("[API RESTAURANTS] CODE HTTP : ", reponse.status);

	if (reponse.status === 200) {
		return await reponse.json() as Restaurant[];
	}

	const message = await reponse.text();

	if (reponse.status === 503 || reponse.status === 502) {
		throw new Error(message || "Le service de restaurants est indisponible");
	}

	if (reponse.status === 400) {
		throw new Error(message || "La requête pour récupérer les restaurants est invalide");
	}

	throw new Error(message || "Erreur lors de la récupération des restaurants : " + reponse.status);
}




export async function reserverRestaurant(reservation : Reservation) : Promise<DetailsReservation> {

	if (!PROXY_RESERVATION_URL) {
		throw new Error("Proxy non configuré, impossible de faire une réservation");
	}

	console.log("[API RESTAURANTS] Envoi de la demande de réservation suivante au proxy : " + reservation);

	let reponse : Response;

	try {
		reponse = await fetch(PROXY_RESERVATION_URL, {
			method: "POST",
			headers: {
				"Content-Type": "application/json"
			},
			body: JSON.stringify(reservation), // permet de convertir l'objet reservation en une chaîne JSON pour l'envoyer dans le corps de la requête HTTP
		});
	} catch {
		throw new Error("Impossible de joindre le proxy pour la réservation");
	}

	console.log("[API RESTAURANTS] CODE HTTP Réservation : " + reponse.status);


	if (reponse.status === 200 || reponse.status === 201) {
		return await reponse.json() as DetailsReservation;
	}

	const message = await reponse.text();

	if (reponse.status === 409) {
		throw new Error(message || "Aucune table disponible pour la réservation demandée");
	}

	if (reponse.status === 400) {
		throw new Error(message || "La demande de réservation est invalide");
	}

	if (reponse.status === 503 || reponse.status === 502) {
		throw new Error(message || "Le service de réservation est indisponible");
	}

	throw new Error(message || "Erreur lors de la réservation : " + reponse.status);

}
