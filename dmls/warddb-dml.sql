INSERT INTO ward.room (number, cost_per_day)
VALUES ('A1', 100),
       ('A2', 100),
       ('A3', NULL),
       ('A4', NULL);

INSERT INTO finance.tariff (description, hospital_cost, specialist_fee, price)
VALUES ('Colecistectomia Laparoscópica', 200, 10, 500),
       ('Apendicectomia', 475, 10, 800),
       ('Hemorroidectomia', 900, 10, 1000),
       ('Biopsia', 50, 10, 150),
       ('Artroscopia', 1000, 10, 1100); 
