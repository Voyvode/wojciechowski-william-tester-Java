/* Setting up prod database */
CREATE DATABASE prod;
USE prod;

CREATE TABLE parking
(
    parking_number INT PRIMARY KEY,
    available      BOOL        NOT NULL,
    type           VARCHAR(10) NOT NULL
);

CREATE TABLE ticket
(
    id                 INT PRIMARY KEY AUTO_INCREMENT,
    parking_number     INT         NOT NULL,
    vehicle_reg_number VARCHAR(10) NOT NULL,
    price              DOUBLE,
    in_time            DATETIME    NOT NULL,
    out_time           DATETIME,
    FOREIGN KEY (parking_number)
        REFERENCES parking (parking_number)
);

INSERT INTO parking(parking_number, available, type)
VALUES (1, TRUE, 'CAR');
INSERT INTO parking(parking_number, available, type)
VALUES (2, TRUE, 'CAR');
INSERT INTO parking(parking_number, available, type)
VALUES (3, TRUE, 'CAR');
INSERT INTO parking(parking_number, available, type)
VALUES (4, TRUE, 'BIKE');
INSERT INTO parking(parking_number, available, type)
VALUES (5, TRUE, 'BIKE');
COMMIT;

/* Setting up test database */
CREATE DATABASE test;
USE test;

CREATE TABLE parking
(
    parking_number INT PRIMARY KEY,
    available      BOOL        NOT NULL,
    type           VARCHAR(10) NOT NULL
);

CREATE TABLE ticket
(
    id                 INT PRIMARY KEY AUTO_INCREMENT,
    parking_number     INT         NOT NULL,
    vehicle_reg_number VARCHAR(10) NOT NULL,
    price              DOUBLE,
    in_time            DATETIME    NOT NULL,
    out_time           DATETIME,
    FOREIGN KEY (parking_number)
        REFERENCES parking (parking_number)
);

INSERT INTO parking(parking_number, available, type)
VALUES (1, TRUE, 'CAR');
INSERT INTO parking(parking_number, available, type)
VALUES (2, TRUE, 'CAR');
INSERT INTO parking(parking_number, available, type)
VALUES (3, TRUE, 'CAR');
INSERT INTO parking(parking_number, available, type)
VALUES (4, TRUE, 'BIKE');
INSERT INTO parking(parking_number, available, type)
VALUES (5, TRUE, 'BIKE');
COMMIT;
