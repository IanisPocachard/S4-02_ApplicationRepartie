import L from "leaflet";
export function initMap(container) {
    container.style.height = "500px";
    container.style.width = "100%";
    const map = L.map(container).setView([48.6921, 6.1844], 13);
    L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
        maxZoom: 19,
        attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>',
    }).addTo(map);
    return map;
}
export function addStationMarkers(map, stations) {
    stations.forEach((station) => {
        const marker = L.marker([station.lat, station.lon]).addTo(map);
        console.log(`Ajout du marqueur pour la station ${station.name} à la position (${station.lat}, ${station.lon})`);
        marker.bindPopup(`<strong>${station.name}</strong><br>${station.address}<br>Capacité : ${station.capacity}`);
    });
}
