CREATE TABLE Restaurant (
    id NUMBER PRIMARY KEY,
    nom VARCHAR2(30) NOT NULL,
    adresse VARCHAR2(50),
    latitude NUMBER(8,6), -- -90 < latitude < 90
    longitude NUMBER(9,6) -- -180 < longitude < 180
);