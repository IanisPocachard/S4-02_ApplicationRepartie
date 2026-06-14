/**
 * Module responsable des appels HTTP liés à l'api velib
 */

import {VeloApiIndexResponse, VeloStationInformationResponse, VeloStationStatusResponse } from "../types/velo";
import { VELO_API_INDEX_URL, VELO_API_STATION_INFORMATION_URL, VELO_API_STATION_STATUS_URL } from "../config/config";

// Récupère les informations de l'annuaire de l'api velib
export async function fetchVeloApiIndex(): Promise<VeloApiIndexResponse> {
	let response : Response;

	try {
		response = await fetch(VELO_API_INDEX_URL);
	} catch (error) {
		console.error("[API_VELO] Impossible de contacter l'API Velostanlib pour récupérer les informations de l'index : ", error);
		throw new Error("Impossible de contacter l'API Velostanlib pour récupérer les informations de l'annuaire");
	}

	if (!response.ok) {
		throw new Error(`Erreur HTTP ${response.status} en essayant de fetch l'URL suivante : ${VELO_API_INDEX_URL}`);
	}

	return await response.json() as VeloApiIndexResponse;
}


// Récupère les informations statiques de toutes les stations (nom, position GPS, capacité)
export async function fetchStationInformation(): Promise<VeloStationInformationResponse> {
	let response : Response;

	try {
		response = await fetch(VELO_API_STATION_INFORMATION_URL);
	} catch (error) {
		console.error("[API_VELO] Impossible de contacter l'API Velostanlib pour récupérer les informations des stations : ", error);
		throw new Error("Impossible de contacter l'API Velostanlib pour récupérer les informations des stations");
	}

	if (!response.ok) {
		throw new Error(`Erreur HTTP ${response.status} en essayant de fetch l'URL suivante : ${VELO_API_STATION_INFORMATION_URL}`);
	}

	return await response.json() as VeloStationInformationResponse;
}

/**
 * Récupère les status des stations
 */
export async function fetchStationStatus(): Promise<VeloStationStatusResponse> {
	let response : Response;

	try {
		response = await fetch(VELO_API_STATION_STATUS_URL);
	} catch (error) {
		console.error("[API_VELO] Impossible de contacter l'API Velostanlib pour récupérer les status des stations : ", error);
		throw new Error("Impossible de contacter l'API Velostanlib pour récupérer les status des stations");
	}

	if (!response.ok) {
		throw new Error(`Erreur HTTP ${response.status} en essayant de fetch l'URL suivante : ${VELO_API_STATION_STATUS_URL}`);
	}

	return await response.json() as VeloStationStatusResponse;
}