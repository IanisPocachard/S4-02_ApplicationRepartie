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

	if (reponse.status === 503 || reponse.status === 502) {
		throw new Error("Le service de restaurants est indisponible");
	}

	if (reponse.status === 400) {
		throw new Error("La requête pour récupérer les restaurants est invalide");
	}

	throw new Error("Erreur lors de la récupération des restaurants : " + reponse.status);
}




export async function reserverRestaurant(reservation : Reservation) : Promise<DetailsReservation> {

	if (!PROXY_RESERVATION_URL) {
		throw new Error("Proxy non configuré, impossible de faire une réservation");
	}

	console.log("[API RESTAURANTS] Envoi de la demande de réservation suivante au proxy : " + reservation.toString());

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


	switch (reponse.status) {
		case 200:
		case 201:
			return await reponse.json() as DetailsReservation;

		case 400: {
			const message = await reponse.text();
			console.warn("[restaurants_api] Erreur 400 :", message);
			throw new Error("La demande de réservation est invalide.");
		}

		case 409: {
			const message = await reponse.text();
			console.warn("[restaurants_api] Réservation refusée :" + message);
			throw new Error("Aucune table disponible pour cette date et ce nbr de personnes");
		}

		case 502: {
			const message = await reponse.text();
			console.warn("[restaurants_api] Erreur 502 - appel RMI échoué :", message);
			throw new Error("Le proxy n'a pas réussi à joindre le service de réservation.");
		}

		case 503: {
			const message = await reponse.text();
			console.warn("[restaurants_api] Erreur 503 :", message);
			throw new Error("Le service de réservation est actuellement indisponible.");
		}

		default: {
			const message = await reponse.text();
			console.warn("[restaurants_api] Erreur inconnue :", reponse.status, message);
			throw new Error("Une erreur est survenue pendant la réservation.");
		}
	}

}
