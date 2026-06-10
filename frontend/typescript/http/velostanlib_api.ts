import {VeloApiIndexResponse, VeloStationInformationResponse, VeloStationStatusResponse } from "../types/velo";
import { VELO_API_INDEX_URL, VELO_API_STATION_INFORMATION_URL, VELO_API_STATION_STATUS_URL } from "../config/config";


/**
 * Récupère les informations de l'annuaire de l'api velib
 * *@throws {error} Si le requete http echoue
 * @returns Une promesse contenant les différents points d'accès de l'api
 */
export async function fetchVeloApiIndex(): Promise<VeloApiIndexResponse> {
	const response = await fetch(VELO_API_INDEX_URL);

	if (!response.ok) {
		throw new Error(`Erreur HTTP ${response.status} en essayant de fetch l'URL suivante : ${VELO_API_INDEX_URL}`);
	}

	return await response.json() as VeloApiIndexResponse;
}


/**
 * Récupère les informations statiques de toutes les stations (nom, position GPS, capacité).
 * *@throws {error} Si la requete HTTP échoue
 * @returns Une promesse contenant les données des stations
 */
export async function fetchStationInformation(): Promise<VeloStationInformationResponse> {
	const response = await fetch(VELO_API_STATION_INFORMATION_URL);

	if (!response.ok) {
		throw new Error(`Erreur HTTP ${response.status} en essayant de fetch l'URL suivante : ${VELO_API_STATION_INFORMATION_URL}`);
	}

	return await response.json() as VeloStationInformationResponse;
}

/**
 * Récupère les status des stations
 * *@throws {error} Si la requete http échoue
 * @returns Une promesse contenant les status des stations
 */
export async function fetchStationStatus(): Promise<VeloStationStatusResponse> {
	const response = await fetch(VELO_API_STATION_STATUS_URL);

	if (!response.ok) {
		throw new Error(`Erreur HTTP ${response.status} en essayant de fetch l'URL suivante : ${VELO_API_STATION_STATUS_URL}`);
	}

	return await response.json() as VeloStationStatusResponse;
}