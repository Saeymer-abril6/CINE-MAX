-- ============================================================
--  CINEMAX - SISTEMA DE VENTAS DE ENTRADAS Y DULCERÍA
--  Modelo físico para SQL Power Architect / PostgreSQL
--  Caso de estudio: CINE MAX
--  11 tablas · 10 relaciones
-- ============================================================
--  Orden de creación: primero las tablas maestras y luego las
--  tablas que contienen las claves foráneas.
-- ============================================================

-- ------------------------------------------------------------
-- 1. UNIDAD DE MEDIDA
-- ------------------------------------------------------------
CREATE TABLE unidad_medida (
    id_unidad_medida SERIAL       NOT NULL,
    nombre           VARCHAR(30)  NOT NULL,
    abreviatura      VARCHAR(10)  NOT NULL,
    CONSTRAINT unidad_medida_pkey PRIMARY KEY (id_unidad_medida),
    CONSTRAINT unidad_medida_nombre_key UNIQUE (nombre)
);

COMMENT ON TABLE  unidad_medida IS 'Unidades con las que se vende un producto: kilo, unidad, litros.';
COMMENT ON COLUMN unidad_medida.abreviatura IS 'Abreviatura que se muestra en la interfaz (kg, und, L).';

-- ------------------------------------------------------------
-- 2. CATEGORÍA
-- ------------------------------------------------------------
CREATE TABLE categoria (
    id_categoria SERIAL        NOT NULL,
    nombre       VARCHAR(30)   NOT NULL,
    descripcion  VARCHAR(100),
    CONSTRAINT categoria_pkey PRIMARY KEY (id_categoria),
    CONSTRAINT categoria_nombre_key UNIQUE (nombre)
);

COMMENT ON TABLE categoria IS 'Categorías comerciales: Snacks, Bebidas, Combos y Entradas.';

-- ------------------------------------------------------------
-- 3. PERFIL
-- ------------------------------------------------------------
CREATE TABLE perfil (
    id_perfil SERIAL      NOT NULL,
    nombre    VARCHAR(30) NOT NULL,
    CONSTRAINT perfil_pkey PRIMARY KEY (id_perfil),
    CONSTRAINT perfil_nombre_key UNIQUE (nombre)
);

COMMENT ON TABLE perfil IS 'Perfil de acceso del personal: Administrador o Vendedor.';

-- ------------------------------------------------------------
-- 4. USUARIO
-- ------------------------------------------------------------
CREATE TABLE usuario (
    id_usuario SERIAL       NOT NULL,
    usuario    VARCHAR(30)  NOT NULL,
    clave      VARCHAR(100) NOT NULL,
    nombre     VARCHAR(80)  NOT NULL,
    id_perfil  INTEGER      NOT NULL,
    estado     VARCHAR(15)  NOT NULL DEFAULT 'Activo',
    CONSTRAINT usuario_pkey PRIMARY KEY (id_usuario),
    CONSTRAINT usuario_usuario_key UNIQUE (usuario),
    CONSTRAINT usuario_id_perfil_fkey FOREIGN KEY (id_perfil)
        REFERENCES perfil (id_perfil)
        ON UPDATE CASCADE ON DELETE RESTRICT
);

COMMENT ON TABLE usuario IS 'Personal que inicia sesión en el sistema.';

-- ------------------------------------------------------------
-- 5. CLIENTE
-- ------------------------------------------------------------
CREATE TABLE cliente (
    dni       VARCHAR(8)   NOT NULL,
    nombre    VARCHAR(80)  NOT NULL,
    direccion VARCHAR(120),
    celular   VARCHAR(15),
    CONSTRAINT cliente_pkey PRIMARY KEY (dni)
);

COMMENT ON TABLE  cliente IS 'Clientes del cine identificados por su DNI.';
COMMENT ON COLUMN cliente.dni IS 'Documento Nacional de Identidad de 8 dígitos.';

-- ------------------------------------------------------------
-- 6. PELÍCULA
-- ------------------------------------------------------------
CREATE TABLE pelicula (
    id_pelicula   SERIAL       NOT NULL,
    titulo        VARCHAR(100) NOT NULL,
    genero        VARCHAR(40)  NOT NULL,
    duracion_min  INTEGER      NOT NULL,
    clasificacion VARCHAR(10)  NOT NULL,
    estado        VARCHAR(15)  NOT NULL DEFAULT 'En cartelera',
    CONSTRAINT pelicula_pkey PRIMARY KEY (id_pelicula),
    CONSTRAINT pelicula_titulo_key UNIQUE (titulo)
);

COMMENT ON TABLE pelicula IS 'Películas disponibles en la cartelera del cine.';

-- ------------------------------------------------------------
-- 7. SALA
-- ------------------------------------------------------------
CREATE TABLE sala (
    id_sala   SERIAL      NOT NULL,
    nombre    VARCHAR(30) NOT NULL,
    tipo      VARCHAR(10) NOT NULL,
    capacidad INTEGER     NOT NULL,
    CONSTRAINT sala_pkey PRIMARY KEY (id_sala),
    CONSTRAINT sala_nombre_key UNIQUE (nombre)
);

COMMENT ON TABLE  sala IS 'Salas de proyección del cine.';
COMMENT ON COLUMN sala.tipo IS 'Tipo de sala: 2D, 3D o VIP.';

-- ------------------------------------------------------------
-- 8. FUNCIÓN
-- ------------------------------------------------------------
CREATE TABLE funcion (
    id_funcion  SERIAL      NOT NULL,
    id_pelicula INTEGER     NOT NULL,
    id_sala     INTEGER     NOT NULL,
    fecha       DATE        NOT NULL,
    hora        TIME        NOT NULL,
    estado      VARCHAR(15) NOT NULL DEFAULT 'Programada',
    CONSTRAINT funcion_pkey PRIMARY KEY (id_funcion),
    CONSTRAINT funcion_sala_fecha_hora_key UNIQUE (id_sala, fecha, hora),
    CONSTRAINT funcion_id_pelicula_fkey FOREIGN KEY (id_pelicula)
        REFERENCES pelicula (id_pelicula)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT funcion_id_sala_fkey FOREIGN KEY (id_sala)
        REFERENCES sala (id_sala)
        ON UPDATE CASCADE ON DELETE RESTRICT
);

COMMENT ON TABLE funcion IS 'Proyección de una película en una sala, con fecha y hora.';
COMMENT ON COLUMN funcion.fecha IS 'La sala no puede repetir fecha y hora en dos funciones.';

-- ------------------------------------------------------------
-- 9. PRODUCTO
-- ------------------------------------------------------------
CREATE TABLE producto (
    id_producto      SERIAL        NOT NULL,
    nombre           VARCHAR(80)   NOT NULL,
    id_categoria     INTEGER       NOT NULL,
    id_unidad_medida INTEGER       NOT NULL,
    costo            NUMERIC(10,2) NOT NULL,
    porce_util       NUMERIC(5,2)  NOT NULL DEFAULT 0.30,
    stock            INTEGER       NOT NULL DEFAULT 0,
    controla_stock   BOOLEAN       NOT NULL DEFAULT TRUE,
    estado           VARCHAR(15)   NOT NULL DEFAULT 'Activo',
    CONSTRAINT producto_pkey PRIMARY KEY (id_producto),
    CONSTRAINT producto_nombre_key UNIQUE (nombre),
    CONSTRAINT producto_id_categoria_fkey FOREIGN KEY (id_categoria)
        REFERENCES categoria (id_categoria)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT producto_id_unidad_medida_fkey FOREIGN KEY (id_unidad_medida)
        REFERENCES unidad_medida (id_unidad_medida)
        ON UPDATE CASCADE ON DELETE RESTRICT
);

COMMENT ON TABLE  producto IS 'Productos de dulcería y entradas del cine.';
COMMENT ON COLUMN producto.costo IS 'Costo unitario del producto.';
COMMENT ON COLUMN producto.porce_util IS 'Porcentaje de utilidad expresado como fracción: 0.50 = 50 %.';
COMMENT ON COLUMN producto.controla_stock IS 'Verdadero para dulcería; falso para las entradas.';

-- ------------------------------------------------------------
-- 10. VENTA
-- ------------------------------------------------------------
CREATE TABLE venta (
    id_venta     SERIAL        NOT NULL,
    dni_cliente  VARCHAR(8)    NOT NULL,
    id_usuario   INTEGER       NOT NULL,
    fecha_venta  DATE          NOT NULL,
    neto_total   NUMERIC(10,2) NOT NULL,
    igv          NUMERIC(10,2) NOT NULL,
    precio_total NUMERIC(10,2) NOT NULL,
    CONSTRAINT venta_pkey PRIMARY KEY (id_venta),
    CONSTRAINT venta_dni_cliente_fkey FOREIGN KEY (dni_cliente)
        REFERENCES cliente (dni)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT venta_id_usuario_fkey FOREIGN KEY (id_usuario)
        REFERENCES usuario (id_usuario)
        ON UPDATE CASCADE ON DELETE RESTRICT
);

COMMENT ON TABLE  venta IS 'Cabecera de la venta: cliente, vendedor, fecha e importes.';
COMMENT ON COLUMN venta.neto_total IS 'Importe sin IGV: total / 1.18.';
COMMENT ON COLUMN venta.igv IS 'Impuesto General a las Ventas: neto × 0.18.';
COMMENT ON COLUMN venta.precio_total IS 'Suma de los importes del detalle.';

-- ------------------------------------------------------------
-- 11. VENTA DETALLE
-- ------------------------------------------------------------
CREATE TABLE venta_detalle (
    id_venta_detalle SERIAL        NOT NULL,
    id_venta         INTEGER       NOT NULL,
    id_producto      INTEGER       NOT NULL,
    id_funcion       INTEGER,
    precio_unit      NUMERIC(10,2) NOT NULL,
    porce_util       NUMERIC(5,2)  NOT NULL,
    cantidad         INTEGER       NOT NULL,
    neto_total       NUMERIC(10,2) NOT NULL,
    utilidad         NUMERIC(10,2) NOT NULL,
    CONSTRAINT venta_detalle_pkey PRIMARY KEY (id_venta_detalle),
    CONSTRAINT venta_detalle_id_venta_fkey FOREIGN KEY (id_venta)
        REFERENCES venta (id_venta)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT venta_detalle_id_producto_fkey FOREIGN KEY (id_producto)
        REFERENCES producto (id_producto)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT venta_detalle_id_funcion_fkey FOREIGN KEY (id_funcion)
        REFERENCES funcion (id_funcion)
        ON UPDATE CASCADE ON DELETE RESTRICT
);

COMMENT ON TABLE  venta_detalle IS 'Líneas de la venta: producto, cantidad, precio, importe y utilidad.';
COMMENT ON COLUMN venta_detalle.id_funcion IS 'Solo se llena cuando el producto es una entrada.';
COMMENT ON COLUMN venta_detalle.precio_unit IS 'Precio de venta: costo × (1 + % utilidad).';

-- ------------------------------------------------------------
-- ÍNDICES DE LAS CLAVES FORÁNEAS
-- ------------------------------------------------------------
CREATE INDEX idx_usuario_perfil       ON usuario (id_perfil);
CREATE INDEX idx_funcion_pelicula     ON funcion (id_pelicula);
CREATE INDEX idx_funcion_sala         ON funcion (id_sala);
CREATE INDEX idx_funcion_fecha        ON funcion (fecha);
CREATE INDEX idx_producto_categoria   ON producto (id_categoria);
CREATE INDEX idx_producto_unidad      ON producto (id_unidad_medida);
CREATE INDEX idx_venta_cliente        ON venta (dni_cliente);
CREATE INDEX idx_venta_usuario        ON venta (id_usuario);
CREATE INDEX idx_venta_fecha          ON venta (fecha_venta);
CREATE INDEX idx_detalle_venta        ON venta_detalle (id_venta);
CREATE INDEX idx_detalle_producto     ON venta_detalle (id_producto);
CREATE INDEX idx_detalle_funcion      ON venta_detalle (id_funcion);

-- ============================================================
--  DATOS DE EJEMPLO (los mismos del Excel del caso)
-- ============================================================

INSERT INTO unidad_medida (nombre, abreviatura) VALUES
    ('Kilo',   'kg'),
    ('Unidad', 'und'),
    ('Litros', 'L');

INSERT INTO categoria (nombre, descripcion) VALUES
    ('Snacks',  'Canchita, nachos, hot dog y gomitas'),
    ('Bebidas', 'Gaseosas, chicha morada y agua'),
    ('Combos',  'Combinaciones de snacks y bebidas'),
    ('Entradas','Entradas para las funciones del cine');

INSERT INTO perfil (nombre) VALUES
    ('Administrador'),
    ('Vendedor');

-- Claves de demostración para el aula
INSERT INTO usuario (usuario, clave, nombre, id_perfil, estado) VALUES
    ('admin',    'admin123',    'Administrador General', 1, 'Activo'),
    ('vendedor', 'vendedor123', 'Juan Ramón',            2, 'Activo'),
    ('maria',    'maria123',    'María Quispe',          2, 'Activo');

INSERT INTO cliente (dni, nombre, direccion, celular) VALUES
    ('43631917', 'Pedro Gonzales',  'Av. La Torre 120',      '951782520'),
    ('43631516', 'Pepe Gutiérrez',  'Jr. Lima 455',          '954454545'),
    ('70123456', 'Lucía Torres',    'Jr. Puno 210',          '987654321'),
    ('45881234', 'Carlos Mamani',   'Av. Circunvalación 350','912345678');

INSERT INTO pelicula (titulo, genero, duracion_min, clasificacion, estado) VALUES
    ('Aventura en los Andes', 'Aventura', 110, 'APT', 'En cartelera'),
    ('La Noche Eterna',       'Terror',     95, '+14', 'En cartelera'),
    ('Risa Total',            'Comedia',   100, 'APT', 'En cartelera'),
    ('Código Zero',           'Acción',    120, '+14', 'En cartelera'),
    ('El Último Viaje',       'Drama',     105, '+14', 'Retirada');

INSERT INTO sala (nombre, tipo, capacidad) VALUES
    ('Sala 1', '2D',  120),
    ('Sala 2', '3D',   90),
    ('Sala 3', 'VIP',  40);

INSERT INTO funcion (id_pelicula, id_sala, fecha, hora, estado) VALUES
    (1, 1, DATE '2026-11-10', TIME '18:00:00', 'Programada'),
    (2, 2, DATE '2026-11-10', TIME '20:30:00', 'Programada'),
    (3, 1, DATE '2026-11-11', TIME '16:00:00', 'Programada'),
    (4, 3, DATE '2026-11-11', TIME '21:00:00', 'Programada'),
    (1, 2, DATE '2026-11-12', TIME '19:30:00', 'Programada');

INSERT INTO producto (nombre, id_categoria, id_unidad_medida, costo, porce_util, stock, controla_stock, estado) VALUES
    ('Canchita grande',         1, 2,  6.00, 0.50,  80, TRUE,  'Activo'),
    ('Nachos con queso',        1, 2,  5.00, 0.40,  50, TRUE,  'Activo'),
    ('Hot dog',                 1, 2,  4.50, 0.40,  40, TRUE,  'Activo'),
    ('Gaseosa 500 ml',          2, 2,  3.50, 0.50, 120, TRUE,  'Activo'),
    ('Chicha morada (litro)',   2, 3,  8.00, 0.50,  25, TRUE,  'Activo'),
    ('Gomitas a granel (kilo)', 1, 1, 18.00, 0.30,  15, TRUE,  'Activo'),
    ('Combo pareja',            3, 2, 20.00, 0.40,  30, TRUE,  'Activo'),
    ('Entrada 2D',              4, 2,  8.00, 0.50,   0, FALSE, 'Activo'),
    ('Entrada 3D',              4, 2, 10.00, 0.50,   0, FALSE, 'Activo'),
    ('Entrada VIP',             4, 2, 15.00, 0.50,   0, FALSE, 'Activo');

INSERT INTO venta (dni_cliente, id_usuario, fecha_venta, neto_total, igv, precio_total) VALUES
    ('43631917', 1, DATE '2026-11-10', 36.86, 6.64, 43.50),
    ('43631516', 1, DATE '2026-11-10', 36.44, 6.56, 43.00);

INSERT INTO venta_detalle (id_venta, id_producto, id_funcion, precio_unit, porce_util, cantidad, neto_total, utilidad) VALUES
    (1, 8, 1,    12.00, 0.50, 2, 24.00, 8.00),
    (1, 1, NULL,  9.00, 0.50, 1,  9.00, 3.00),
    (1, 4, NULL,  5.25, 0.50, 2, 10.50, 3.50),
    (2, 9, 2,    15.00, 0.50, 1, 15.00, 5.00),
    (2, 7, NULL, 28.00, 0.40, 1, 28.00, 8.00);

-- ============================================================
--  CONSULTAS DE VERIFICACIÓN (opcionales)
-- ============================================================
-- Total vendido: debe dar S/ 86.50
-- SELECT SUM(precio_total) AS total_vendido FROM venta;
--
-- Utilidad total: debe dar S/ 27.50
-- SELECT SUM(utilidad) AS utilidad_total FROM venta_detalle;
--
-- Precio de venta calculado: costo × (1 + % utilidad)
-- SELECT nombre, costo, porce_util,
--        ROUND(costo * (1 + porce_util), 2) AS precio_venta
-- FROM producto ORDER BY nombre;
