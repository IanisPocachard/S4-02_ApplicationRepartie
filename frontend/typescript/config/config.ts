export const BASE_VELO_API_INDEX_URL = "https://api.cyclocity.fr/contracts/nancy/gbfs/v2/gbfs.json";
export const VELO_API_INDEX_URL = "https://api.cyclocity.fr/contracts/nancy/gbfs/v2/gbfs.json";
export const VELO_API_STATION_INFORMATION_URL = "https://api.cyclocity.fr/contracts/nancy/gbfs/v2/station_information.json";
export const VELO_API_STATION_STATUS_URL = "https://api.cyclocity.fr/contracts/nancy/gbfs/v2/station_status.json";

export const INCIDENTS_API_URL = "https://carto.g-ny.eu/data/cifs/cifs_waze_v2.json";

export const PROXY_BASE_URL = "http://localhost:8080"; // TODO à remplir avec l'URL du proxy d'Ambroise au moment où il sera lancé sur un pc de la salle on ne sait pas l'IP à l'avance

export const PROXY_INCIDENTS_URL = PROXY_BASE_URL ? `${PROXY_BASE_URL}/api/incidents` : "";
export const PROXY_RESTAURANTS_URL = PROXY_BASE_URL ? `${PROXY_BASE_URL}/api/bd/restaurants` : "";
export const PROXY_RESERVATION_URL = PROXY_BASE_URL ? `${PROXY_BASE_URL}/api/bd/reserver` : "";

