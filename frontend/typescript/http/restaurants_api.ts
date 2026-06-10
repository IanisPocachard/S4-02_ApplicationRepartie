import { RestaurantsResponse } from "../types/restaurants";

/**
 * Récupère la liste des restaurants.
 * Tente d'abord un fetch direct sur l'API ; en cas d'échec (CORS notamment),
 * contourne via le proxy en lui envoyant l'URL cible dans le body.
 * @param urlApi URL de l'API restaurants (bloquée par CORS)
 * @param urlProxy URL du proxy qui effectue la requête à notre place via le client HTTP (RMI)
 * @throws {Error} Si la requête directe ET la requête proxy échouent
 * @returns Une promesse contenant la liste des restaurants
 */
export async function fetchAPIRestaurants(urlApi: string, urlProxy: string): Promise<RestaurantsResponse> {
	try {
		const reponse = await fetch(urlApi);

		if (!reponse.ok) {
			throw new Error("Erreur HTTP : " + reponse.status);
		}

		return await reponse.json() as RestaurantsResponse;

	} catch (error) {
		console.warn("Erreur lors de la récupération des restaurants, tentative de contournement via le proxy : ", error);
		const reponseDuProxy = await fetch(urlProxy, {
			method: "POST",
			headers: {
				"Content-Type": "text/plain; charset=UTF-8",
			},
			body: urlApi,
		});

		if (!reponseDuProxy.ok) {
			throw new Error("Erreur HTTP du PROXY ! : " + reponseDuProxy.status);
		}

		return await reponseDuProxy.json() as RestaurantsResponse;
	}
}