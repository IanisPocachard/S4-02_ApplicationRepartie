/**
 * ATTENTION :
 * Module responsable de l'orchestration de l'interface de la page avec :
 * chargement des données, la liste latérale, les modales et interactions utilisateur
 */

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

async function ouvrirFormulaireReservation(restaurant: Restaurant): Promise<void> {

	const modale = document.createElement("div");
	modale.className = "modal-overlay";

	modale.innerHTML = `
		<form class="modal" id="formulaire-reservation">
			<h2>Réserver chez ${restaurant.nom}</h2>

			<label>Nom</label>
			<input name="nom" required placeholder="Votre nom svp">

			<label>Prénom</label>
			<input name="prenom" required placeholder="Maintenant votre prénom">

			<label>Téléphone</label>
			<input name="telephone" required placeholder="Et votre numéro de téléphone ?">

			<label>Nombre de personnes</label>
			<input name="nbPersonnes" type="number" min="1" required placeholder="Pour combien de personnes ?">

			<label>Date</label>
			<input name="date" type="datetime-local" required placeholder="Pour quelle date ?"> 

			<div class="modal-actions">
				<button type="button" id="annuler-reservation">Annuler</button>
				<button type="submit">Réserver</button>
			</div>
		</form>
	`; // le datetime-local permet de renvoyer directement une string sous la bonne forme pour le backend donc pas besoin de faire de conversion particulière côté proxy Ambroise

	document.body.appendChild(modale);

	document.getElementById("annuler-reservation")?.addEventListener("click", () => {
		modale.remove();
	});

	const formulaire = document.getElementById("formulaire-reservation") as HTMLFormElement;

	formulaire.addEventListener("submit", async (event) => {
		event.preventDefault(); // empêche le comportement par défaut du formulaire qui est de recharger la page lors de l'envoi sur submit, comme ça on peut envoyer avec notre fonction de fetch à nous

		const donneesDuFormulaire = new FormData(formulaire);

		const reservation: Reservation = {
			idRestaurant: restaurant.id,
			date: String(donneesDuFormulaire.get("date")),
			nbPersonnes: Number(donneesDuFormulaire.get("nbPersonnes")),
			nom: String(donneesDuFormulaire.get("nom")),
			prenom: String(donneesDuFormulaire.get("prenom")),
			telephone: String(donneesDuFormulaire.get("telephone")),
		};

		try {
			const reponse = await reserverRestaurant(reservation);

			modale.remove();
			afficherMessage("Votre réservation a bien été prise en compte", "Réservation confirmée : " + reponse.reservation?.nom + " " + reponse.reservation?.prenom + " pour " + reponse.reservation?.nbPersonnes + " personnes le " + reponse.reservation?.date);

		} catch (error) {
			console.error(error);
			afficherMessage("Réservation impossible", error instanceof Error ? error.message : "Impossible d'envoyer la réservation");
		}
	});
}

function afficherMessage(titre: string, message: string): void {
	const popup = document.createElement("div");
	popup.className = "modal-overlay";

	popup.innerHTML = `
		<div class="modal-message-informatif">
			<div class="modal-icon">!</div>
			<h3>${titre}</h3>
			<p>${message}</p>
			<div class="modal-actions">
				<button type="button" class="modal-button">OK</button>
			 </div>
		</div>
	`

	document.body.appendChild(popup); // ajouter le popup à la page

	const boutonFermer = popup.querySelector<HTMLButtonElement>(".modal-button"); // ici on met popup pour limiter la recherche du bouton à l'intérieur du popup qu'on vient de créer, parce que sinon dès qu'il y a plusieurs popups à l'écran ça peut poser problème pour trouver le bon bouton et fermer le bon popup
	boutonFermer?.addEventListener("click", () => {
		popup.remove();
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
	const carte = initMap(conteneurCarte) as L.Map;

	try {
		const [infoRes, statutRes] = await Promise.all([
			fetchStationInformation(),
			fetchStationStatus(),
		]);
		const stations = infoRes.data.stations;

		const statuts = new Map<string, VeloStationStatus>(
			statutRes.data.stations.map(s => [s.station_id, s])
		);

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

	} catch (error) {
		console.error("Erreur lors du chargement des données des stations : " + error);
		afficherMessage("Erreur", "Impossible de charger les stations vélostanlib");
	}




	try {
		const incidentsRep: IncidentsResponse = await fetchIncidents(INCIDENTS_API_URL, PROXY_INCIDENTS_URL);
		addIncidentMarkers(carte, incidentsRep.incidents);
	} catch (error) {
		console.error("Erreur lors du chargement des incidents" + error);
		afficherMessage("Erreur", "Impossible de charger les incidents");
	}

	try {
		const restaurants: Restaurant[] = await fetchRestaurants();
		addRestaurantMarkers(carte, restaurants, ouvrirFormulaireReservation);
	} catch (error) {
		console.error("Erreur lors du chargement des restaurants : " + error);
		afficherMessage("Erreur", "Impossible de charger les restaurants");
	}
}