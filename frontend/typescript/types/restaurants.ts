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

export interface ReservationResponse { // JSON renvoyé par le service RMI de Ianis aussi mais qui correspond à la réponse et qu'on va utiliser pour savoir si la réservation a fonctionné ou pas
	status: "success" | "error"; // TODO va surement changé en fonction de ce que Ianis va me renvoyer dorénavant
	message?: string; // Ianis renvoie le message d'erreur "no_table_available" dans le cas où la réservation n'a pas fonctionné
	reservation? : DetailsReservation;
}

export interface TableRestaurant {
		id: number;
		capacite: number;
		restaurant?: Restaurant;
}