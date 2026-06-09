import L from "leaflet";
import { VeloStationInformation } from "../types/velo";
import { IncidentsResponse } from "../types/incidents";

/**
 * Initialisation du conteneur de la carte
 * @param container éléments html qui contient cette carte
 * @returns La carte
 */
export function initMap(container: HTMLElement): any {
	container.style.height = "500px";
	container.style.width = "100%";

	const map = L.map(container).setView([48.6921, 6.1844], 13);

	L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
		maxZoom: 19,
		attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>',
	}).addTo(map);

	return map;
}

/**
 * Crée sur la carte les marqeurs à chaque station vélib
 * @param map La carte
 * @param stations Liste des stations avec toutes les infos
 */
export function addStationMarkers(map: any, stations: VeloStationInformation[]): void {
	stations.forEach((station) => {
		const marker = L.marker([station.lat, station.lon]).addTo(map);
		console.log(`Ajout du marqueur pour la station ${station.name} à la position (${station.lat}, ${station.lon})`);
		marker.bindPopup(`<strong>${station.name}</strong><br>${station.address}<br>Capacité : ${station.capacity}`);
	});
}

export function addIncidentMarkers(map: any, incidents: IncidentsResponse): void {
	incidents.incidents.forEach((incident) => {
		const coordonnees = incident.location.polyline.split(" ").map(Number); // on split la polyline pour récupérer les coordonnées GPS de l'incident donc on récupère un tableau [latitude, longitude]
		const marker = L.marker([coordonnees[0], coordonnees[1]]).addTo(map);
		console.log("Ajout du marqueur pour l'incident qui a la descritpion : " + incident.description + " à la position GPS : (" + coordonnees[0], coordonnees[1] + ")");
		marker.bindPopup(`<strong>${incident.type}</strong><br>${incident.short_description}<br>${incident.location.location_description}`);
	});
}