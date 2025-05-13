-- insert roles
INSERT INTO auth.role(name) VALUES ('Encargado de Farmacia'), ('Encargado de Empleados'),
                                   ('Encargado de Pacientes') , ('Administrador');

INSET INTO auth.user (email, cui, password)
VALUES ("hr@mail.com", "1111555551111", "e3d78166623f1225b72d50490495147a62058bb9c9354276f830ef4b57797921");
