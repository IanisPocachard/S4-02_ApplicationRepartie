/**
 * Module responsable des appels HTTP liés aux restaurants :
 * récupération des restaurants et envoi des demandes de réservation au proxy
 */

import { PROXY_RESTAURANTS_URL, PROXY_RESERVATION_URL } from "../config/config";
import type { Restaurant, Reservation, ReservationResponse } from "../types/restaurants";

export async function fetchRestaurants(): Promise<Restaurant[]> {
	if (!PROXY_RESTAURANTS_URL) throw new Error("Proxy non configuré, impossible d'accésder aux données des restaurants");

	const reponse = await fetch(PROXY_RESTAURANTS_URL);
	if (!reponse.ok) {
        throw new Error("Erreur proxy restaurants : " + reponse.status);
    }

	return await reponse.json() as Restaurant[];
}


// TODO : A IMPLEMENTER / A VERIFIER
export async function reserverRestaurant(reservation : Reservation) : Promise<ReservationResponse> {

	if (!PROXY_RESERVATION_URL) {
		throw new Error("Proxy non configuré, impossible de faire une réservation");
	}

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


	if (!reponse.ok) {
		const message = await reponse.text();

		if (reponse.status === 409) {
			throw new Error(message || "Aucune table disponible pour la réservation demandée");
		}

		if (reponse.status === 503 || reponse.status === 502) {
			throw new Error(message || "Le service de réservation est indisponible");
		}

		throw new Error(message || "Erreur réservation : " + reponse.status);
	}

	// return await reponse.json() as ReservationResponse;
	return await reponse.json() as ReservationResponse;
}