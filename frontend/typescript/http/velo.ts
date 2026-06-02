import type {
	GbfsIndexResponse,
	VeloStationInformationResponse,
	VeloStationStatusResponse,
} from "../types/velo";

const GBFS_INDEX_URL = "https://api.cyclocity.fr/contracts/nancy/gbfs/v2/gbfs.json";
const STATION_INFORMATION_URL = "https://api.cyclocity.fr/contracts/nancy/gbfs/v2/station_information.json";
const STATION_STATUS_URL = "https://api.cyclocity.fr/contracts/nancy/gbfs/v2/station_status.json";

async function requestJson(url: string): Promise<any> {
	const response = await fetch(url);

	if (!response.ok) {
		throw new Error(`HTTP ${response.status} while fetching ${url}`);
	}

	return response.json();
}

export async function fetchGbfsIndex(): Promise<GbfsIndexResponse> {
	return requestJson(GBFS_INDEX_URL) as Promise<GbfsIndexResponse>;
}

export async function fetchStationInformation(): Promise<VeloStationInformationResponse> {
	return requestJson(STATION_INFORMATION_URL) as Promise<VeloStationInformationResponse>;
}

export async function fetchStationStatus(): Promise<VeloStationStatusResponse> {
	return requestJson(STATION_STATUS_URL) as Promise<VeloStationStatusResponse>;
}