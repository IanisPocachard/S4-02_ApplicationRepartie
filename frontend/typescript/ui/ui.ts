import L from "leaflet";
import { initMap, addIncidentMarkers, addRestaurantMarkers, addStationMarkers, icone, couleur } from "../map/map";
import { fetchStationInformation, fetchStationStatus } from "../http/velostanlib_api";
import type { VeloStationInformation, VeloStationStatus } from "../types/velo";
import { PROXY_INCIDENTS_URL, INCIDENTS_API_URL, PROXY_RESERVATION_URL } from "../config/config";
import { IncidentsResponse } from "../types/incidents";
import { fetchIncidents } from "../http/incidents_api";
import { fetchRestaurants, reserverRestaurant } from "../http/restaurants_api";
import type { Restaurant, Reservation, ReservationResponse } from "../types/restaurants";

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

function ouvrirFormulaireReservation(restaurant: Restaurant): void {
	const nom = prompt("Entrez votre nom pour la réservation au restaurant " + restaurant.nom);
	if (!nom) {
		alert("Le nom est requis pour la réservation.");
		return;
	}

	const prenom = prompt("De même pour votre prénom svp");
	if (!prenom) {
		alert("Le prénom est requis pour la réservation.");
		return;
	}

	const telephone = prompt("Et votre numéro de téléphone ?");
	if (!telephone) {
		alert("Le numéro de téléphone est requis pour la réservation.");
		return;
	}

	const nbPersonnesStr = prompt("Pour combien de personnes souhaitez-vous réserver ?");
	const nbPersonnes = nbPersonnesStr ? parseInt(nbPersonnesStr) : NaN;
	if (isNaN(nbPersonnes) || nbPersonnes <= 0) {
		alert("Petit rigolo ! Rentre un nombre correct maintenant.");
		return;
	}

	const date = prompt("Pour quelle date souhaitez-vous réserver ? (format YYYY-MM-DDTHH:mm)");
	if (!date || isNaN(Date.parse(date))) {
		alert("La date doit être au format YYYY-MM-DDTHH:mm je l'avais dit pourtant");
		return;
	}

	const reservation: Reservation = {
		idRestaurant: restaurant.id,
		date,
		nbPersonnes,
		nom,
		prenom,
		telephone
	};

	reserverRestaurant(reservation).then((reponseServiceRestaurant: ReservationResponse) => {
		if (reponseServiceRestaurant.status === "success") {
			alert("La réservation a bien été prise en compte ! \n En voici les détails : " + JSON.stringify(reponseServiceRestaurant.reservation));
		} else {
			alert("Erreur lors de la réservation : " + reponseServiceRestaurant.message); // TODO : voir demander à Ianis s'il renvoie bien tjrs un message d'erreur dans le cas où la réservation n'a pas fonctionné
		}
	}).catch((error) => {
		console.error("Erreur lors de la réservation : " + error);
		alert("Une erreur est survenue lors de la réservation");
	});
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
	const marqueurs = addStationMarkers(carte, stations, statuts, filtre);

	afficherListe(stations, statuts, marqueurs, carte);

	const searchInput = document.getElementById("search") as HTMLInputElement;

	if (searchInput) {
		searchInput.addEventListener("input", (event) => {
			// Récupérer le texte tapé et le mettre en minuscules
			const texteRecherche = (event.target as HTMLInputElement).value.toLowerCase();

			// Filtrer les stations dont le nom ou l'adresse contient le texte recherché
			const stationsFiltrees = stations.filter(station =>
				station.name.toLowerCase().includes(texteRecherche) ||
				station.address.toLowerCase().includes(texteRecherche)
			);

			// Mettre à jour l'affichage de la liste avec les résultats filtrés
			afficherListe(stationsFiltrees, statuts, marqueurs, carte);
		});
	}

	try {
		const incidentsRep : IncidentsResponse = await fetchIncidents(INCIDENTS_API_URL, PROXY_INCIDENTS_URL);
		addIncidentMarkers(carte, incidentsRep.incidents);
	} catch (error) {
		console.error("Erreur lors du chargement des incidents" + error);
	}

	try {
		const restaurants : Restaurant[] = await fetchRestaurants();
		addRestaurantMarkers(carte, restaurants, ouvrirFormulaireReservation);
	} catch (error) {
		console.error("Erreur lors du chargement des restaurants : " + error);
	}
}