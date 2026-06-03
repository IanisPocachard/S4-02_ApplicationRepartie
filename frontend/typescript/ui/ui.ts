import L from "leaflet";
import { initMap } from "../map/map";
import { fetchStationInformation, fetchStationStatus } from "../http/velostanlib_api";
import type { VeloStationInformation, VeloStationStatus } from "../types/velo";

function couleur(velos: number): string {
	if (velos === 0) return "#ef4444";
	if (velos <= 3) return "#f59e0b";
	return "#22c55e";
}

function icone(velos: number): L.DivIcon {
	return L.divIcon({
		className: "",
		html: `<div style="width:12px;height:12px;background:${couleur(velos)};border:2px solid white;border-radius:50%;box-shadow:0 1px 4px rgba(0,0,0,0.3)"></div>`,
		iconSize: [12, 12],
		iconAnchor: [6, 6],
	});
}

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

function filtre(statut: VeloStationStatus): boolean {
	return statut.num_bikes_available === 0 && statut.num_docks_available === 0;
}

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
}