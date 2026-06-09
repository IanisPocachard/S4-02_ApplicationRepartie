export interface IncidentsResponse {
	incidents: Incident[];
}

export interface Incident {
	type: string;
	description: string;
	short_description: string;
	starttime: string;
	endtime: string;
	location: IncidentLocation;
	source: IncidentSource;
	updatetime: string;
	creationtime: string;
	id: string;
}

export interface IncidentLocation {
	street: string;
	polyline: string; // contient les coordonnées GPS de l'incident, latitude/longitude séparé par un espace
	location_description: string;
}

export interface IncidentSource {
	name: string;
	reference: string;
}