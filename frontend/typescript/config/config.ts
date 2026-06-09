export const BASE_VELO_API_INDEX_URL = "https://api.cyclocity.fr/contracts/nancy/gbfs/v2/gbfs.json";

export const VELO_API_FEEDS = {
	stationInformation: "https://api.cyclocity.fr/contracts/nancy/gbfs/v2/station_information.json",
	stationStatus: "https://api.cyclocity.fr/contracts/nancy/gbfs/v2/station_status.json",
	systemInformation: "https://api.cyclocity.fr/contracts/nancy/gbfs/v2/system_information.json",
} as const;

export const INCIDENTS_API_URL = "https://carto.g-ny.eu/data/cifs/cifs_waze_v2.json";

export const PROXY_URL = ""; // TODO à remplir avec l'URL du proxy d'Ambroise au moment où il sera lancé sur un pc de la salle on ne sait pas l'IP à l'avance