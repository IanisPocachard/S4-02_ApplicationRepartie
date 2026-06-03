import type {
	VeloApiIndexResponse,
	VeloStationInformationResponse,
	VeloStationStatusResponse,
} from "../types/velo";

const VELO_API_INDEX_URL = "https://api.cyclocity.fr/contracts/nancy/gbfs/v2/gbfs.json";
const VELO_API_STATION_INFORMATION_URL = "https://api.cyclocity.fr/contracts/nancy/gbfs/v2/station_information.json";
const VELO_API_STATION_STATUS_URL = "https://api.cyclocity.fr/contracts/nancy/gbfs/v2/station_status.json";

export async function fetchVeloApiIndex(): Promise<VeloApiIndexResponse> {
	const response = await fetch(VELO_API_INDEX_URL);

	if (!response.ok) {
		throw new Error(`Erreur HTTP ${response.status} en essayant de fetch l'URL suivante : ${VELO_API_INDEX_URL}`);
	}

	return response.json() as Promise<VeloApiIndexResponse>;
}

export async function fetchStationInformation(): Promise<VeloStationInformationResponse> {
	const response = await fetch(VELO_API_STATION_INFORMATION_URL);

	if (!response.ok) {
		throw new Error(`Erreur HTTP ${response.status} en essayant de fetch l'URL suivante : ${VELO_API_STATION_INFORMATION_URL}`);
	}

	return response.json() as Promise<VeloStationInformationResponse>;
}

export async function fetchStationStatus(): Promise<VeloStationStatusResponse> {
	const response = await fetch(VELO_API_STATION_STATUS_URL);

	if (!response.ok) {
		throw new Error(`Erreur HTTP ${response.status} en essayant de fetch l'URL suivante : ${VELO_API_STATION_STATUS_URL}`);
	}

	return response.json() as Promise<VeloStationStatusResponse>;
}