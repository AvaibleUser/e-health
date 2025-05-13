CREATE SCHEMA ward;
CREATE SCHEMA finance;
CREATE SCHEMA operating_room;

CREATE TABLE ward.patient
(
    id BIGSERIAL PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    cui VARCHAR(255) NOT NULL UNIQUE,
    birth_date DATE,
    phone VARCHAR(255),
    email VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE ward.room
(
    id BIGSERIAL PRIMARY KEY,
    number VARCHAR(255) NOT NULL UNIQUE,
    cost_per_day NUMERIC(10, 2),
    is_occupied BOOLEAN NOT NULL DEFAULT FALSE,
    under_maintenance BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TYPE ward.admission_status_enum AS ENUM (
    'ADMITTED',
    'DISCHARGED'
);

CREATE TABLE ward.admission
(
    id BIGSERIAL PRIMARY KEY,
    admission_date DATE NOT NULL,
    discharge_date DATE,
    status VARCHAR(255) NOT NULL DEFAULT 'ADMITTED',
    patient_id BIGINT NOT NULL REFERENCES ward.patient (id),
    room_id BIGINT NOT NULL REFERENCES ward.room (id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TYPE ward.assigned_employee_type_enum AS ENUM (
    'DOCTOR',
    'SPECIALIST',
    'NURSE'
);

CREATE TABLE ward.assigned_employee
(
    id BIGSERIAL PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    type VARCHAR(255) NOT NULL DEFAULT 'DOCTOR',
    admission_id BIGINT NOT NULL REFERENCES ward.admission (id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE finance.tariff
(
    id BIGSERIAL PRIMARY KEY,
    description VARCHAR(255) NOT NULL,
    hospital_cost NUMERIC(10, 2),
    specialist_fee NUMERIC(10, 2),
    price NUMERIC(10, 2),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE operating_room.surgery
(
    id BIGSERIAL PRIMARY KEY,
    performed_date DATE NOT NULL,
    description VARCHAR(255),
    patient_id BIGINT NOT NULL REFERENCES ward.patient (id),
    tariff_id BIGINT NOT NULL REFERENCES finance.tariff (id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TYPE operating_room.surgery_specialist_type_enum AS ENUM (
    'DOCTOR',
    'SPECIALIST',
    'NURSE'
);

CREATE TABLE operating_room.surgery_specialist
(
    id BIGSERIAL PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    type operating_room.surgery_specialist_type_enum NOT NULL DEFAULT 'DOCTOR',
    surgery_id BIGINT NOT NULL REFERENCES operating_room.surgery (id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE finance.bill
(
    id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL REFERENCES ward.patient (id),
    total NUMERIC(10, 2),
    is_closed BOOLEAN NOT NULL DEFAULT FALSE,
    is_paid BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TYPE finance.bill_item_type_enum AS ENUM (
    'HOSPITALIZED',
    'SURGERY',
    'CONSULTATION',
    'MEDICATION'
);

CREATE TABLE finance.bill_item
(
    id BIGSERIAL PRIMARY KEY,
    concept VARCHAR(255) NOT NULL,
    amount NUMERIC(10, 2),
    type finance.bill_item_type_enum NOT NULL DEFAULT 'CONSULTATION',
    sale_id NULL BIGINT,
    admission_id BIGINT NULL REFERENCES ward.admission (id),
    surgery_id BIGINT NULL REFERENCES operating_room.surgery (id),
    bill_id BIGINT NOT NULL REFERENCES finance.bill (id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);
