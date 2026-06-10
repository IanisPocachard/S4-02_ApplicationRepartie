import L from "leaflet";
import {
	initMap,
	createStationLayer,
	createIncidentLayer,
	createRestaurantLayer,
	couleur,
} from "../map/map";
import { fetchStationInformation, fetchStationStatus } from "../http/velostanlib_api";
import type { VeloStationInformation, VeloStationStatus } from "../types/velo";
import { PROXY_INCIDENTS_URL, INCIDENTS_API_URL } from "../config/config";
import type { Incident } from "../types/incidents";
import type { Restaurant } from "../types/restaurants";
import { fetchIncidents } from "../http/incidents_api";
import { fetchRestaurants } from "../http/restaurants_api";

type Onglet = "velos" | "incidents" | "restaurants";

const TITRES: Record<Onglet, string> = {
	velos: "Stations VéloStan Nancy",
	incidents: "Incidents de circulation",
	restaurants: "Restaurants",
};

/**
 * État applicatif partagé entre les différents onglets.
 * Regroupe les données chargées, les couches Leaflet et les marqueurs indexés.
 */
interface AppState {
	carte: L.Map;
	ongletActif: Onglet;
	recherche: string;
	velos: {
		stations: VeloStationInformation[];
		statuts: Map<string, VeloStationStatus>;
		layer: L.LayerGroup;
		marqueurs: Map<string, L.Marker>;
	};
	incidents: {
		liste: Incident[];
		layer: L.LayerGroup;
		marqueurs: Map<string, L.Marker>;
	};
	restaurants: {
		liste: Restaurant[];
		layer: L.LayerGroup;
		marqueurs: Map<string, L.Marker>;
	};
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
 * Recentre la carte sur une position et ouvre la pop-up du marqueur associé.
 */
function focusMarqueur(state: AppState, position: L.LatLngExpression, marqueur?: L.Marker): void {
	state.carte.setView(position, 16, { animate: true });
	marqueur?.openPopup();
}

/**
 * Affiche dans la liste latérale un message quand aucune entrée ne correspond.
 */
function afficherVide(liste: HTMLElement, message: string): void {
	const vide = document.createElement("div");
	vide.className = "list-empty";
	vide.textContent = message;
	liste.appendChild(vide);
}

/**
 * Rendu de la liste des stations vélos (filtrée par la recherche).
 */
function rendreVelos(state: AppState, liste: HTMLElement): void {
	const recherche = state.recherche.toLowerCase();
	let count = 0;

	for (const station of state.velos.stations) {
		const statut = state.velos.statuts.get(station.station_id);
		if (statut && filtre(statut)) continue;

		const texte = `${station.name} ${station.address}`.toLowerCase();
		if (recherche && !texte.includes(recherche)) continue;

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
			focusMarqueur(state, [station.lat, station.lon], state.velos.marqueurs.get(station.station_id));
		});
		liste.appendChild(element);
		count++;
	}

	if (count === 0) afficherVide(liste, "Aucune station ne correspond.");
}

/**
 * Rendu de la liste des incidents (filtrée par la recherche).
 */
function rendreIncidents(state: AppState, liste: HTMLElement): void {
	const recherche = state.recherche.toLowerCase();
	let count = 0;

	for (const incident of state.incidents.liste) {
		const texte = `${incident.type} ${incident.short_description} ${incident.location.location_description}`.toLowerCase();
		if (recherche && !texte.includes(recherche)) continue;

		const element = document.createElement("div");
		element.className = "list-item";
		element.innerHTML = `
			<div class="item-dot" style="background:#ef4444"></div>
			<div class="item-info">
				<div class="item-title">${incident.type}</div>
				<div class="item-sub">${incident.short_description || incident.location.location_description || ""}</div>
			</div>
		`;
		element.addEventListener("click", () => {
			const coords = incident.location.polyline.split(" ").map(Number);
			focusMarqueur(state, [coords[0], coords[1]], state.incidents.marqueurs.get(incident.id));
		});
		liste.appendChild(element);
		count++;
	}

	if (count === 0) afficherVide(liste, "Aucun incident à afficher.");
}

/**
 * Rendu de la liste des restaurants (filtrée par la recherche).
 */
function rendreRestaurants(state: AppState, liste: HTMLElement): void {
	const recherche = state.recherche.toLowerCase();
	let count = 0;

	for (const restaurant of state.restaurants.liste) {
		const texte = `${restaurant.nom} ${restaurant.adresse}`.toLowerCase();
		if (recherche && !texte.includes(recherche)) continue;

		const element = document.createElement("div");
		element.className = "list-item";
		element.innerHTML = `
			<div class="item-dot" style="background:#2563eb"></div>
			<div class="item-info">
				<div class="item-title">${restaurant.nom}</div>
				<div class="item-sub">${restaurant.adresse}</div>
			</div>
		`;
		element.addEventListener("click", () => {
			focusMarqueur(state, [restaurant.latitude, restaurant.longitude], state.restaurants.marqueurs.get(restaurant.id));
		});
		liste.appendChild(element);
		count++;
	}

	if (count === 0) afficherVide(liste, "Aucun restaurant à afficher.");
}

/**
 * Re-rend la liste latérale en fonction de l'onglet actif et de la recherche.
 */
function rendreListe(state: AppState): void {
	const liste = document.getElementById("station-list");
	if (!liste) return;
	liste.innerHTML = "";

	switch (state.ongletActif) {
		case "velos":       rendreVelos(state, liste); break;
		case "incidents":   rendreIncidents(state, liste); break;
		case "restaurants": rendreRestaurants(state, liste); break;
	}
}

/**
 * Active un onglet : met à jour la couche affichée sur la carte, le titre,
 * les attributs ARIA et re-rend la liste.
 */
function activerOnglet(state: AppState, onglet: Onglet): void {
	state.ongletActif = onglet;

	// Couches : on n'affiche que celle de l'onglet actif.
	state.velos.layer.remove();
	state.incidents.layer.remove();
	state.restaurants.layer.remove();
	state[onglet].layer.addTo(state.carte);

	// Titre de la sidebar.
	const titre = document.getElementById("sidebar-title");
	if (titre) titre.textContent = TITRES[onglet];

	// État ARIA des boutons d'onglet.
	document.querySelectorAll<HTMLButtonElement>(".tab").forEach((btn) => {
		btn.setAttribute("aria-selected", String(btn.dataset.tab === onglet));
	});

	rendreListe(state);
}

/**
 * Branche les écouteurs sur les boutons d'onglet et le champ de recherche.
 */
function brancherControles(state: AppState): void {
	document.querySelectorAll<HTMLButtonElement>(".tab").forEach((btn) => {
		btn.addEventListener("click", () => {
			const onglet = btn.dataset.tab as Onglet;
			if (onglet && onglet !== state.ongletActif) activerOnglet(state, onglet);
		});
	});

	const search = document.getElementById("search") as HTMLInputElement | null;
	search?.addEventListener("input", () => {
		state.recherche = search.value.trim();
		rendreListe(state);
	});
}

/**
 * Point d'entrée principal de l'app
 * 1. Récupère les données vélos puis initialise la carte.
 * 2. Construit les couches (vélos / incidents / restaurants) sans toutes les afficher.
 * 3. Branche les onglets et la recherche, puis affiche l'onglet vélos par défaut.
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

	const carte = initMap(conteneurCarte);
	const velosLayer = createStationLayer(stations, statuts, filtre);

	const state: AppState = {
		carte,
		ongletActif: "velos",
		recherche: "",
		velos: { stations, statuts, layer: velosLayer.layer, marqueurs: velosLayer.marqueurs },
		incidents: { liste: [], layer: L.layerGroup(), marqueurs: new Map() },
		restaurants: { liste: [], layer: L.layerGroup(), marqueurs: new Map() },
	};

	brancherControles(state);
	activerOnglet(state, "velos");

	// Chargement des incidents (avec fallback proxy en cas de CORS).
	try {
		const incidentsRep = await fetchIncidents(INCIDENTS_API_URL, PROXY_INCIDENTS_URL);
		state.incidents.liste = incidentsRep.incidents;
		const couche = createIncidentLayer(incidentsRep.incidents);
		state.incidents.layer = couche.layer;
		state.incidents.marqueurs = couche.marqueurs;
		if (state.ongletActif === "incidents") activerOnglet(state, "incidents");
	} catch (error) {
		console.error("Erreur lors du chargement des incidents : " + error);
	}

	// Chargement des restaurants (via proxy).
	try {
		const restaurants = await fetchRestaurants();
		state.restaurants.liste = restaurants;
		const couche = createRestaurantLayer(restaurants);
		state.restaurants.layer = couche.layer;
		state.restaurants.marqueurs = couche.marqueurs;
		if (state.ongletActif === "restaurants") activerOnglet(state, "restaurants");
	} catch (error) {
		console.error("Erreur lors du chargement des restaurants : " + error);
	}
}