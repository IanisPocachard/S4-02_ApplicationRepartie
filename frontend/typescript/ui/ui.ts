import L from "leaflet";
import { initMap, addIncidentMarkers, addRestaurantMarkers, icone, couleur } from "../map/map";
import { fetchStationInformation, fetchStationStatus } from "../http/velostanlib_api";
import type { VeloStationInformation, VeloStationStatus } from "../types/velo";
import { INCIDENTS_API_URL, RESTAURANTS_API_URL, PROXY_URL } from "../config/config";
import { fetchAPIBloque } from "../http/fetch_api_bloque";
import { IncidentsResponse } from "../types/incidents";
import { RestaurantsResponse } from "../types/restaurants";





/**
 * Construction d'une pop-up de station de velib
 * @param info Les informations d'une station
 * @param statut Le statut de la station
 * @returns une fenetre pop-up détaillant la station
 */
function popupContenu(info: VeloStationInformation, statut: VeloStationStatus | undefined): string {
	const velos = statut?.num_bikes_available ?? "?";
	const places = statut?.num_docks_available ?? "?";
	return `
		<strong>${info.name}</strong><br>
		${info.address}<br>
		Vélos disponibles : <b>${velos}</b><br>
		Places libres : <b>${places}</b><br>
		Capacité : ${info.capacity}<br>
	`;
}

/**
 * Méthode permettant de filtrer les stations non opérationnelles
 * @param statut Information sur une station
 * @returns un booleen true si la station est ko, false si elle est bonne
 */
function filtre(statut: VeloStationStatus): boolean {
	return statut.num_bikes_available === 0 && statut.num_docks_available === 0;
}

/**
 * 
 * @param stations La liste des stations
 * @param statuts Les status des stations
 * @param marqueurs Liste des marqueurs sur la carte des stations
 * @param carte Carte leaflet
 */
function afficherListe(stations: VeloStationInformation[], statuts: Map<string, VeloStationStatus>, marqueurs: Map<string, L.Marker>, carte: L.Map): void {
	const liste = document.getElementById("station-list")!;
	liste.innerHTML = "";

	for (const station of stations) {
		const statut = statuts.get(station.station_id);
		if (statut && filtre(statut)) continue;

		const velos = statut?.num_bikes_available ?? 0;
		const element = document.createElement("div");
		element.className = "station-item";
		element.innerHTML = `
			<div class="station-dot" style="background:${couleur(velos)}"></div>
			<div class="station-info">
				<div class="station-name">${station.name}</div>
				<div class="station-address">${station.address}</div>
			</div>
			<div class="station-bikes">${velos}</div>
		`;
		element.addEventListener("click", () => {
			carte.setView([station.lat, station.lon], 16, { animate: true });
			marqueurs.get(station.station_id)?.openPopup();
		});
		liste.appendChild(element);
	}
}

/**
 * Point d'entrée principal de l'app
 * 1. Récupère les données statiques et dynamiques de l'api
 * 2. Initatise la carte
 * 3. Fusione les données pour générer les marqueurs sur la carte et la liste latérale.
 */
export async function renderApp(): Promise<void> {
	const conteneurCarte = document.querySelector<HTMLElement>("#map");
	if (!conteneurCarte) return;

	const [infoRes, statutRes] = await Promise.all([
		fetchStationInformation(),
		fetchStationStatus(),
	]);

	const stations = infoRes.data.stations;
	const statuts = new Map<string, VeloStationStatus>(
		statutRes.data.stations.map(s => [s.station_id, s])
	);

	const carte = initMap(conteneurCarte) as L.Map;
	const marqueurs = new Map<string, L.Marker>();

	for (const station of stations) {
		const statut = statuts.get(station.station_id);
		if (statut && filtre(statut)) continue;

		const velos = statut?.num_bikes_available ?? 0;
		const marqueur = L.marker([station.lat, station.lon], { icon: icone(velos) })
			.addTo(carte)
			.bindPopup(popupContenu(station, statut));
		marqueurs.set(station.station_id, marqueur);
	}

	afficherListe(stations, statuts, marqueurs, carte);

	try {
		// Récupération des incidents et ajout des marqueurs sur la carte
		const incidentsRep = await fetchAPIBloque<IncidentsResponse>(INCIDENTS_API_URL, PROXY_URL);
		addIncidentMarkers(carte, incidentsRep.incidents);
	} catch(error) {
		console.error("Erreur lors du chargement des incidents" + error);
	}

	try {
		const restaurantsRep = await fetchAPIBloque<RestaurantsResponse>(RESTAURANTS_API_URL, PROXY_URL);
		addRestaurantMarkers(carte, restaurantsRep.restaurants);
	} catch(error) {
		console.error("Erreur lors du changement des restaurants : " + error);
	}

}