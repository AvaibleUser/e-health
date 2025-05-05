CREATE SCHEMA hr;


CREATE TABLE hr.area (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(255) NOT NULL UNIQUE,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP
);

CREATE TABLE hr.employee (
  id BIGSERIAL PRIMARY KEY,
  full_name VARCHAR(255) NOT NULL,
  cui VARCHAR(255) NOT NULL UNIQUE,
  phone VARCHAR(255),
  email VARCHAR(255),
  is_specialist BOOLEAN NOT NULL DEFAULT FALSE,
  area_id BIGINT NOT NULL REFERENCES hr.area (id),
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP
);

CREATE TABLE hr.contract (
  id BIGSERIAL PRIMARY KEY,
  salary NUMERIC(10,2) NOT NULL,
  igss_discount NUMERIC(10,2),
  irtra_discount NUMERIC(10,2),
  termination_reason VARCHAR,
  termination_description TEXT,
  start_date DATE NOT NULL,
  end_date DATE,
  employee_id BIGINT NOT NULL REFERENCES hr.employee (id),
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP
);

CREATE TABLE hr.vacation (
  id BIGSERIAL PRIMARY KEY,
  requested_date DATE NOT NULL,
  start_date DATE NOT NULL,
  end_date DATE NOT NULL,
  approved BOOLEAN NOT NULL DEFAULT FALSE,
  finalized BOOLEAN NOT NULL DEFAULT FALSE,
  employee_id BIGINT NOT NULL REFERENCES hr.employee (id),
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP
);

CREATE TABLE hr.specialist_payment (
  id BIGSERIAL PRIMARY KEY,
  surgery_id BIGINT NOT NULL REFERENCES hr.contract (id),
  amount NUMERIC(10,2) NOT NULL,
  paid_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  specialist_doctor_id BIGINT NOT NULL REFERENCES hr.employee (id),
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP
);
