import { PROXY_RESTAURANTS_URL, PROXY_RESERVATION_URL } from "../config/config";
import type { Restaurant } from "../types/restaurants";

export async function fetchRestaurants(): Promise<Restaurant[]> {
	if (!PROXY_RESTAURANTS_URL) throw new Error("Proxy non configuré, impossible d'accésder aux données des restaurants");

	const reponse = await fetch(PROXY_RESTAURANTS_URL);
	if (!reponse.ok) {
        throw new Error("Erreur proxy restaurants : " + reponse.status);
    }

	return await reponse.json() as Restaurant[];
}


// TODO : A IMPLEMENTER
// export async function reserverRestaurant():  {

// 	const reponse = await fetch(PROXY_RESERVATION_URL, {
// 		method: "POST",
// 		headers: { "Content-Type": "application/json" },
// 		body: JSON.stringify(),
// 	});

// 	if (!reponse.ok) throw new Error(`Erreur réservation ${reponse.status}`);

// 	return await reponse.json();
// }