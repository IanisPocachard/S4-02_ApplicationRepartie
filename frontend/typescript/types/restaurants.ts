export interface Restaurant {
	id: number;
	nom: string;
	adresse: string;
	latitude: number;
	longitude: number;
}

export interface Reservation { // objet qu'on envoie au backend pour faire une réservation
	idRestaurant: number;
	date: string;
	nbPersonnes: number;
	nom: string;
	prenom: string;
	telephone: string;
}

export interface DetailsReservation { // objet renvoyé par le backend (le service RMI de la base de données de Ianis) une fois qu'une réservation a fonctionnée
	id: number;
	nomClient: string;
	prenomClient: string;
	numeroTelephone: string;
	nbPersonnes: number;
	date: string;
	restaurant? : Restaurant;
	tableRestaurant? : TableRestaurant;
}

export interface TableRestaurant {
		id: number;
		capacite: number;
		restaurant?: Restaurant;
}