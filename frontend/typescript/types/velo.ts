export interface GbfsIndexResponse {
	last_updated: number;
	ttl: number;
	version: string;
	data: {
		fr: {
			feeds: GbfsFeed[];
		};
	};
}

export interface GbfsFeed {
	name: string;
	url: string;
}

export interface VeloStationInformationResponse {
	last_updated: number;
	ttl: number;
	version: string;
	data: {
		stations: VeloStationInformation[];
	};
}

export interface VeloStationInformation {
	station_id: string;
	name: string;
	lat: number;
	lon: number;
	address: string;
	rental_methods?: string[];
	capacity: number;
}

export interface VeloStationStatusResponse {
	last_updated: number;
	ttl: number;
	version: string;
	data: {
		stations: VeloStationStatus[];
	};
}

export interface VeloStationStatus {
	station_id: string;
	num_bikes_available: number;
	vehicle_types_available: Array<{
		vehicle_type_id: string;
		count: number;
	}>;
	num_bikes_disabled: number;
	num_docks_available: number;
	num_docks_disabled: number;
	is_installed: boolean;
	is_renting: boolean;
	is_returning: boolean;
	last_reported: number;
}
