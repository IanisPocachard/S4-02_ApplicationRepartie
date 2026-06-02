import { initMap } from "../map/map";

export function renderApp(): void {
	const mapContainer = document.querySelector<HTMLElement>("#map");

	if (!mapContainer) {
		return;
	}

	initMap(mapContainer);
}
