import { initMap, addStationMarkers } from "../map/map";
import { fetchStationInformation } from "../http/velostanlib_api";

export async function renderApp(): Promise<void> {
	const mapContainer = document.querySelector<HTMLElement>("#map");

	if (!mapContainer) {
		return;
	}

	const map = initMap(mapContainer);
	const stationInformationResponse = await fetchStationInformation();

	addStationMarkers(map, stationInformationResponse.data.stations);

}
