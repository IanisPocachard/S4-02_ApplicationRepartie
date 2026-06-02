import L from "leaflet";
import type { VeloStationInformation } from "../types/velo";

export function initMap(container: HTMLElement): any {
	container.style.height = "100vh";
	container.style.width = "100%";

	const map = L.map(container).setView([48.6921, 6.1844], 13);

	L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
		maxZoom: 19,
		attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>',
	}).addTo(map);

	return map;
}

export function addStationMarkers(map: any, stations: VeloStationInformation[]): void {
	stations.forEach((station) => {
		const marker = L.marker([station.lat, station.lon]).addTo(map);
		marker.bindPopup(`<strong>${station.name}</strong><br>${station.address}<br>Capacité : ${station.capacity}`);
	});
}