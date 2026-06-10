/**
 * Récupère des données depuis une API potentiellement bloquée par CORS.
 * Tente d'abord un fetch direct ; en cas d'échec (CORS notamment), contourne
 * via le proxy en lui envoyant l'URL cible dans le body. Le proxy effectue
 * alors la requête à notre place via le client HTTP (RMI) et nous renvoie le JSON.
 *
 * @param urlApi URL de l'API cible (potentiellement bloquée par CORS)
 * @param urlProxy URL du proxy qui effectue la requête à notre place
 * @throws {Error} Si la requête directe ET la requête proxy échouent
 * @returns Une promesse contenant les données typées
 */
export async function fetchAPIBloque<T>(urlApi: string, urlProxy: string): Promise<T> {
	try {
		const reponse = await fetch(urlApi);

		if (!reponse.ok) {
			throw new Error("Erreur HTTP : " + reponse.status);
		}

		return await reponse.json() as T;

	} catch (error) { // erreur CORS probable -> contournement via le proxy
		console.warn("Erreur lors de la récupération des données, tentative de contournement via le proxy : ", error);
		const reponseDuProxy = await fetch(urlProxy, {
			method: "POST",
			headers: {
				"Content-Type": "text/plain; charset=UTF-8",
			},
			body: urlApi, // on envoie l'URL de l'API au proxy pour qu'il fasse la requête à notre place
		});

		if (!reponseDuProxy.ok) {
			throw new Error("Erreur HTTP du PROXY ! : " + reponseDuProxy.status);
		}

		return await reponseDuProxy.json() as T;
	}
}