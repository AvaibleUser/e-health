-- insert value areas
INSERT INTO hr.area(name) VALUES
                              ('Farmacia'),
                              ('Cocina'),
                              ('Seguridad'),
                              ('Medicos'),
                              ('Medicos Especialistas'),
                              ('Secretaria'),
                              ('Enfermeria'),
                              ('Administracion');

INSERT INTO hr.employee(full_name, cui, phone, email, area_id)
VALUES ('Moises Granados',  '3226295050801', '40930525', 'admin@gamil.com', 8);

INSERT INTO hr.contract(salary, igss_discount, irtra_discount, start_date, employee_id)
VALUES (12000.00, 8.2, 1.2, '2024-02-02', 1);

INSERT INTO hr.vacation(requested_date, start_date, end_date, approved, finalized, employee_id)
VALUES ('2024-02-02', '2025-02-02', '2025-03-02', true,true,1);