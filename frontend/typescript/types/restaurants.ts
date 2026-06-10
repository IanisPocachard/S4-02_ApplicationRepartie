export interface RestaurantsResponse {
	restaurants: Restaurant[];
}


export interface Restaurant {
	id: string;
	name: string;
	lat: number;
	lon: number;
	address?: string;
	type?: string;
	description?: string;
}