CREATE TABLE Restaurant (
    id NUMBER PRIMARY KEY,
    nom VARCHAR2(30) NOT NULL,
    adresse VARCHAR2(50),
    latitude NUMBER(8,6), -- -90 < latitude < 90
    longitude NUMBER(9,6) -- -180 < longitude < 180
);

CREATE TABLE TableRestaurant (
     id NUMBER PRIMARY KEY,
     capacite NUMBER(2) NOT NULL,
     id_restaurant NUMBER NOT NULL,
     CONSTRAINT fk_table_restaurant
         FOREIGN KEY (id_restaurant)
             REFERENCES Restaurant(id)
);

CREATE TABLE Reservation (
    id NUMBER PRIMARY KEY,
    nom_client VARCHAR2(20) NOT NULL,
    prenom_client VARCHAR2(20) NOT NULL,
    numero_telephone VARCHAR2(20),
    nb_personnes NUMBER(2) CHECK (nb_personnes > 0),
    id_restaurant NUMBER NOT NULL,
    id_tableRestaurant NUMBER NOT NULL,
    date_reservation TIMESTAMP NOT NULL,
    CONSTRAINT fk_reservation_restaurant
        FOREIGN KEY (id_restaurant)
            REFERENCES Restaurant(id),
    CONSTRAINT fk_reservation_tableRestaurant
        FOREIGN KEY (id_tableRestaurant)
            REFERENCES TableRestaurant(id)
);


-- Dump de test
INSERT INTO Restaurant (id, nom, adresse, latitude, longitude) VALUES
    (1, 'Le Gourmet', '12 rue des Saveurs, Nancy', 48.6921, 6.1844);

INSERT INTO Restaurant (id, nom, adresse, latitude, longitude) VALUES
    (2, 'Pizza Roma', '5 place Stanislas, Nancy', 48.6936, 6.1834);

INSERT INTO Restaurant (id, nom, adresse, latitude, longitude) VALUES
    (3, 'Sushi Zen', '18 avenue Foch, Nancy', 48.6892, 6.1731);

-- Le Gourmet
INSERT INTO TableRestaurant (id, capacite, id_restaurant) VALUES (1, 2, 1);
INSERT INTO TableRestaurant (id, capacite, id_restaurant) VALUES (2, 4, 1);
INSERT INTO TableRestaurant (id, capacite, id_restaurant) VALUES (3, 6, 1);

-- Pizza Roma
INSERT INTO TableRestaurant (id, capacite, id_restaurant) VALUES (4, 2, 2);
INSERT INTO TableRestaurant (id, capacite, id_restaurant) VALUES (5, 4, 2);
INSERT INTO TableRestaurant (id, capacite, id_restaurant) VALUES (6, 8, 2);

-- Sushi Zen
INSERT INTO TableRestaurant (id, capacite, id_restaurant) VALUES (7, 2, 3);
INSERT INTO TableRestaurant (id, capacite, id_restaurant) VALUES (8, 4, 3);
INSERT INTO TableRestaurant (id, capacite, id_restaurant) VALUES (9, 10, 3);

-- réservation existante (collision test)
INSERT INTO Reservation (
    id, nom_client, prenom_client, numero_telephone,
    nb_personnes, id_restaurant, id_tableRestaurant, date_reservation
) VALUES (
             1001, 'Dupont', 'Jean', '0600000000',
             2, 1, 1, TO_TIMESTAMP('2026-06-10 19:30:00', 'YYYY-MM-DD HH24:MI:SS')
         );

-- autre réservation différente table
INSERT INTO Reservation (
    id, nom_client, prenom_client, numero_telephone,
    nb_personnes, id_restaurant, id_tableRestaurant, date_reservation
) VALUES (
             1002, 'Martin', 'Sophie', '0611111111',
             4, 1, 2, TO_TIMESTAMP('2026-06-10 19:30:00', 'YYYY-MM-DD HH24:MI:SS')
         );

-- réservation autre jour
INSERT INTO Reservation (
    id, nom_client, prenom_client, numero_telephone,
    nb_personnes, id_restaurant, id_tableRestaurant, date_reservation
) VALUES (
             1003, 'Bernard', 'Lucas', '0622222222',
             2, 2, 4, TO_TIMESTAMP('2026-06-11 20:00:00', 'YYYY-MM-DD HH24:MI:SS')
         );

COMMIT;


-- Supprimer la table
-- DROP TABLE Reservation;
-- DROP TABLE TableRestaurant;
-- DROP TABLE Restaurant;