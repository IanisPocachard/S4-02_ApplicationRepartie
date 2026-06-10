import L from "leaflet";
import { VeloStationInformation, VeloStationStatus } from "../types/velo";
import { Incident } from "../types/incidents";
import { Restaurant } from "../types/restaurants";

/**
 * Initialisation du conteneur de la carte
 * @param container éléments html qui contient cette carte
 * @returns La carte
 */
export function initMap(container: HTMLElement): any {
	const map = L.map(container).setView([48.6921, 6.1844], 13);

	L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
		maxZoom: 19,
		attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>',
	}).addTo(map);

	return map;
}

/**
 * Construction d'une pop-up de station de velib
 * @param info Les informations d'une station
 * @param statut Le statut de la station
 * @returns une fenetre pop-up détaillant la station
 */
export function popupContenu(info: VeloStationInformation, statut: VeloStationStatus | undefined): string {
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
 * Crée sur la carte un marqueur pour chaque station vélib opérationnelle,
 * avec une icône colorée selon le nombre de vélos et une pop-up détaillée.
 * @param map La carte
 * @param stations Liste des stations avec toutes les infos
 * @param statuts Les statuts des stations indexés par station_id
 * @param filtre Prédicat optionnel : si true pour un statut, la station est ignorée
 * @returns Une Map des marqueurs créés, indexés par station_id (pour les ouvrir depuis la liste)
 */
export function addStationMarkers(
	map: L.Map,
	stations: VeloStationInformation[],
	statuts: Map<string, VeloStationStatus>,
	filtre?: (statut: VeloStationStatus) => boolean,
): Map<string, L.Marker> {
	const marqueurs = new Map<string, L.Marker>();

	for (const station of stations) {
		const statut = statuts.get(station.station_id);
		if (statut && filtre?.(statut)) continue;

		const velos = statut?.num_bikes_available ?? 0;
		const marqueur = L.marker([station.lat, station.lon], { icon: icone(velos) })
			.addTo(map)
			.bindPopup(popupContenu(station, statut));
		marqueurs.set(station.station_id, marqueur);
	}

	return marqueurs;
}

export function addIncidentMarkers(map: any, incidents: Incident[]): void {
	incidents.forEach((incident) => {
		const coordonnees = incident.location.polyline.split(" ").map(Number); // on split la polyline pour récupérer les coordonnées GPS de l'incident donc on récupère un tableau [latitude, longitude]
		const marker = L.marker([coordonnees[0], coordonnees[1]]).addTo(map);
		console.log("Ajout du marqueur pour l'incident qui a la description : " + incident.description + " à la position GPS : (" + coordonnees[0] + ", " + coordonnees[1] + ")");
		marker.bindPopup(`<strong>${incident.type}</strong><br>${incident.short_description}<br>${incident.location.location_description}`);
	});
}

/**
 * Crée sur la carte un marqueur pour chaque restaurant.
 * @param map La carte
 * @param restaurants Liste des restaurants
 */
export function addRestaurantMarkers(map: L.Map, restaurants: Restaurant[]): void {
	restaurants.forEach((restaurant) => {
		const marker = L.marker([restaurant.latitude, restaurant.longitude]).addTo(map);
		console.log(`Ajout du marqueur pour le restaurant ${restaurant.nom} à la position (${restaurant.latitude}, ${restaurant.longitude})`);
		marker.bindPopup(`<strong>${restaurant.nom}</strong><br>${restaurant.adresse}`);
	});
}

/**
 * Gestion de la couleur d'affichage des balises sur la carte en fonction du nombre de vélos dispo
 * - Rouge : aucun vélo
 * - Orange : Moins de 3 vélos
 * - Vert : Plus de 3 vélos
 * @param velos - Le nombre de vélos dispo sur la station
 * @returns Le code couleur correspondant au statut.
 */
export function couleur(velos: number): string {
	if (velos === 0) return "#ef4444";
	if (velos <= 3) return "#f59e0b";
	return "#22c55e";
}

/**
 * Construction d'un icon
 * @param velos - le nombre de vélos dispo sur la station
 * @returns une icon d'une station de velib
 */
export function icone(velos: number): L.DivIcon {
	return L.divIcon({
		className: "",
		html: `<div style="width:12px;height:12px;background:${couleur(velos)};border:2px solid white;border-radius:50%;box-shadow:0 1px 4px rgba(0,0,0,0.3)"></div>`,
		iconSize: [12, 12],
		iconAnchor: [6, 6],
	});
}