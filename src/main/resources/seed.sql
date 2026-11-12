-- ============================================================
--  DATOS DE EJEMPLO DE CINEMAX
--  Basados en el modelo del caso de estudio
-- ============================================================

INSERT INTO unidad_medida (id_unidad_medida, nombre, abreviatura) VALUES
    (1, 'Kilo', 'kg'),
    (2, 'Unidad', 'und'),
    (3, 'Litros', 'L');

INSERT INTO categoria (id_categoria, nombre, descripcion) VALUES
    (1, 'Snacks', 'Canchita, nachos, hot dog y gomitas'),
    (2, 'Bebidas', 'Gaseosas, chicha morada y agua'),
    (3, 'Combos', 'Combinaciones de snacks y bebidas'),
    (4, 'Entradas', 'Entradas para las funciones del cine');

INSERT INTO perfil (id_perfil, nombre) VALUES
    (1, 'Administrador'),
    (2, 'Vendedor');

INSERT INTO usuario (id_usuario, usuario, clave, nombre, id_perfil, estado) VALUES
    (1, 'admin', 'admin123', 'Administrador General', 1, 'Activo'),
    (2, 'vendedor', 'vendedor123', 'Juan Ramón', 2, 'Activo'),
    (3, 'maria', 'maria123', 'María Quispe', 2, 'Activo');

INSERT INTO cliente (dni, nombre, direccion, celular) VALUES
    ('43631917', 'Pedro Gonzales', 'Av. La Torre 120', '951782520'),
    ('43631516', 'Pepe Gutiérrez', 'Jr. Lima 455', '954454545'),
    ('70123456', 'Lucía Torres', 'Jr. Puno 210', '987654321'),
    ('45881234', 'Carlos Mamani', 'Av. Circunvalación 350', '912345678');

INSERT INTO pelicula (id_pelicula, titulo, genero, duracion_min, clasificacion, estado) VALUES
    (1, 'Aventura en los Andes', 'Aventura', 110, 'APT', 'En cartelera'),
    (2, 'La Noche Eterna', 'Terror', 95, '+14', 'En cartelera'),
    (3, 'Risa Total', 'Comedia', 100, 'APT', 'En cartelera'),
    (4, 'Código Zero', 'Acción', 120, '+14', 'En cartelera'),
    (5, 'El Último Viaje', 'Drama', 105, '+14', 'Retirada');

INSERT INTO sala (id_sala, nombre, tipo, capacidad) VALUES
    (1, 'Sala 1', '2D', 120),
    (2, 'Sala 2', '3D', 90),
    (3, 'Sala 3', 'VIP', 40);

INSERT INTO funcion (id_funcion, id_pelicula, id_sala, fecha, hora, estado) VALUES
    (1, 1, 1, '2026-11-10', '18:00:00', 'Programada'),
    (2, 2, 2, '2026-11-10', '20:30:00', 'Programada'),
    (3, 3, 1, '2026-11-11', '16:00:00', 'Programada'),
    (4, 4, 3, '2026-11-11', '21:00:00', 'Programada'),
    (5, 1, 2, '2026-11-12', '19:30:00', 'Programada');

INSERT INTO producto (id_producto, nombre, id_categoria, id_unidad_medida, costo, porce_util, stock, controla_stock, estado) VALUES
    (1, 'Canchita grande', 1, 2, 6.00, 0.50, 80, TRUE, 'Activo'),
    (2, 'Nachos con queso', 1, 2, 5.00, 0.40, 50, TRUE, 'Activo'),
    (3, 'Hot dog', 1, 2, 4.50, 0.40, 40, TRUE, 'Activo'),
    (4, 'Gaseosa 500 ml', 2, 2, 3.50, 0.50, 120, TRUE, 'Activo'),
    (5, 'Chicha morada (litro)', 2, 3, 8.00, 0.50, 25, TRUE, 'Activo'),
    (6, 'Gomitas a granel (kilo)', 1, 1, 18.00, 0.30, 15, TRUE, 'Activo'),
    (7, 'Combo pareja', 3, 2, 20.00, 0.40, 30, TRUE, 'Activo'),
    (8, 'Entrada 2D', 4, 2, 8.00, 0.50, 0, FALSE, 'Activo'),
    (9, 'Entrada 3D', 4, 2, 10.00, 0.50, 0, FALSE, 'Activo'),
    (10, 'Entrada VIP', 4, 2, 15.00, 0.50, 0, FALSE, 'Activo');

INSERT INTO venta (id_venta, dni_cliente, id_usuario, fecha_venta, neto_total, igv, precio_total) VALUES
    (1, '43631917', 1, '2026-11-10', 36.86, 6.64, 43.50),
    (2, '43631516', 1, '2026-11-10', 36.44, 6.56, 43.00);

INSERT INTO venta_detalle (id_venta_detalle, id_venta, id_producto, id_funcion, precio_unit, porce_util, cantidad, neto_total, utilidad) VALUES
    (1, 1, 8, 1, 12.00, 0.50, 2, 24.00, 8.00),
    (2, 1, 1, NULL, 9.00, 0.50, 1, 9.00, 3.00),
    (3, 1, 4, NULL, 5.25, 0.50, 2, 10.50, 3.50),
    (4, 2, 9, 2, 15.00, 0.50, 1, 15.00, 5.00),
    (5, 2, 7, NULL, 28.00, 0.40, 1, 28.00, 8.00);

-- Reiniciar los contadores de las claves generadas
ALTER TABLE unidad_medida ALTER COLUMN id_unidad_medida RESTART WITH 4;
ALTER TABLE categoria ALTER COLUMN id_categoria RESTART WITH 5;
ALTER TABLE perfil ALTER COLUMN id_perfil RESTART WITH 3;
ALTER TABLE usuario ALTER COLUMN id_usuario RESTART WITH 4;
ALTER TABLE pelicula ALTER COLUMN id_pelicula RESTART WITH 6;
ALTER TABLE sala ALTER COLUMN id_sala RESTART WITH 4;
ALTER TABLE funcion ALTER COLUMN id_funcion RESTART WITH 6;
ALTER TABLE producto ALTER COLUMN id_producto RESTART WITH 11;
ALTER TABLE venta ALTER COLUMN id_venta RESTART WITH 3;
ALTER TABLE venta_detalle ALTER COLUMN id_venta_detalle RESTART WITH 6;
