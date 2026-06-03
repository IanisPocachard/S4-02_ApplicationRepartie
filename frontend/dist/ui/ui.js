import L from "leaflet";
import { initMap } from "../map/map";
import { fetchStationInformation, fetchStationStatus } from "../http/velostanlib_api";
// couleurs pour l'application
function getColor(bikes) {
    if (bikes === 0)
        return "#ef4444";
    if (bikes <= 3)
        return "#f59e0b";
    return "#22c55e";
}
function makeIcon(bikes) {
    const color = getColor(bikes);
    return L.divIcon({
        className: "",
        html: `<div style="width:12px;height:12px;background:${color};border:2px solid white;border-radius:50%;box-shadow:0 1px 4px rgba(0,0,0,0.3)"></div>`,
        iconSize: [12, 12],
        iconAnchor: [6, 6],
    });
}
function makePopup(info, status) {
    const bikes = status?.num_bikes_available ?? "?";
    const docks = status?.num_docks_available ?? "?";
    const time = status
        ? new Date(status.last_reported * 1000).toLocaleTimeString("fr-FR", { hour: "2-digit", minute: "2-digit" })
        : "—";
    return `
        <strong>${info.name}</strong><br>
        ${info.address}<br>
        Vélos disponibles : <b>${bikes}</b><br>
        Places libres : <b>${docks}</b><br>
        Capacité : ${info.capacity}<br>
        Mis à jour : ${time}
    `;
}
function renderList(stations, statusMap, markers, map, filter = "") {
    const list = document.getElementById("station-list");
    const filtered = filter
        ? stations.filter(s => s.name.toLowerCase().includes(filter.toLowerCase()))
        : stations;
    list.innerHTML = "";
    for (const station of filtered) {
        const status = statusMap.get(station.station_id);
        const bikes = status?.num_bikes_available ?? 0;
        const item = document.createElement("div");
        item.className = "station-item";
        item.innerHTML = `
            <div class="station-dot" style="background:${getColor(bikes)}"></div>
            <div class="station-info">
                <div class="station-name">${station.name}</div>
                <div class="station-address">${station.address}</div>
            </div>
            <div class="station-bikes">${bikes} 🚲</div>
        `;
        item.addEventListener("click", () => {
            map.setView([station.lat, station.lon], 16, { animate: true });
            markers.get(station.station_id)?.openPopup();
        });
        list.appendChild(item);
    }
}
export async function renderApp() {
    const mapContainer = document.querySelector("#map");
    if (!mapContainer)
        return;
    const [infoRes, statusRes] = await Promise.all([
        fetchStationInformation(),
        fetchStationStatus(),
    ]);
    const stations = infoRes.data.stations;
    const statusMap = new Map(statusRes.data.stations.map(s => [s.station_id, s]));
    // Stats
    let totalBikes = 0, totalDocks = 0, emptyCount = 0;
    for (const s of stations) {
        const st = statusMap.get(s.station_id);
        if (st) {
            totalBikes += st.num_bikes_available;
            totalDocks += st.num_docks_available;
            if (st.num_bikes_available === 0)
                emptyCount++;
        }
    }
    document.getElementById("stat-stations").textContent = String(stations.length);
    document.getElementById("stat-bikes").textContent = String(totalBikes);
    document.getElementById("stat-docks").textContent = String(totalDocks);
    document.getElementById("stat-empty").textContent = String(emptyCount);
    // Carte
    const map = initMap(mapContainer);
    const markers = new Map();
    for (const station of stations) {
        const status = statusMap.get(station.station_id);
        const bikes = status?.num_bikes_available ?? 0;
        const marker = L.marker([station.lat, station.lon], { icon: makeIcon(bikes) })
            .addTo(map)
            .bindPopup(makePopup(station, status));
        markers.set(station.station_id, marker);
    }
    //
    renderList(stations, statusMap, markers, map);
    document.getElementById("search")?.addEventListener("input", e => {
        renderList(stations, statusMap, markers, map, e.target.value);
    });
}
