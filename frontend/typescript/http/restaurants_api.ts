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

	const reponse = await fetch(PROXY_RESERVATION_URL, {
		method: "POST",
		headers: {
			"Content-Type": "application/json"
		},
		body: JSON.stringify(reservation), // permet de convertir l'objet reservation en une chaîne JSON pour l'envoyer dans le corps de la requête HTTP
	});

	if (!reponse.ok) {
		throw new Error(`Erreur réservation ${reponse.status}`);
	}

	return await reponse.json() as ReservationResponse;
}