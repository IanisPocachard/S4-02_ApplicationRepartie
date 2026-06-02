CREATE TABLE Restaurant (
    id NUMBER PRIMARY KEY,
    nom VARCHAR2(30) NOT NULL,
    adresse VARCHAR2(50),
    latitude NUMBER(8,6), -- -90 < latitude < 90
    longitude NUMBER(9,6) -- -180 < longitude < 180
);

CREATE TABLE Reservation (
    id NUMBER PRIMARY KEY,
    nom_client VARCHAR2(20) NOT NULL,
    prenom_client VARCHAR2(20) NOT NULL,
    numero_telephone VARCHAR2(20),
    nb_personnes NUMBER(2),
    id_restaurant NUMBER NOT NULL,
    date_reservation DATE NOT NULL,
    CONSTRAINT fk_reservation_restaurant
        FOREIGN KEY (id_restaurant)
            REFERENCES Restaurant(id)
);