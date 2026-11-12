# Guía de exposición · CineMax

Guion corto para presentar el sistema en clase (10 a 12 minutos).
Las capturas están en `docs/capturas/`.

---

## 1. Presentación (1 minuto)

> "CineMax es un sistema de ventas de entradas y dulcería para una cadena de
> cines. Resuelve el problema del registro manual: controla el stock, calcula el
> precio de venta, el IGV y la utilidad, y guarda cada venta con su detalle.
> Está hecho en Java 21 con JavaFX y FXML, con arquitectura por capas y base de
> datos H2."

Mencionar los siete módulos: Películas, Salas, Funciones, Productos, Clientes,
Ventas e Historial.

---

## 2. Inicio de sesión (1 minuto)

1. Abrir la aplicación (clase `SysCineMax`).
2. Ingresar con **admin / admin123**.
3. Señalar que el login se valida contra la tabla `usuario` y su perfil:

   ```sql
   SELECT u.*, p.nombre
   FROM usuario u JOIN perfil p ON p.id_perfil = u.id_perfil
   WHERE u.usuario = ? AND u.clave = ? AND u.estado = 'Activo'
   ```

4. Probar una clave incorrecta para mostrar el mensaje de error.

---

## 3. Películas y salas (1 minuto)

1. En **Películas**, escribir un título, género, duración, clasificación y estado.
2. Guardar y mostrar cómo aparece en la tabla.
3. Seleccionar una fila: el formulario se llena para editar.
4. Intentar eliminar una película con funciones: el sistema lo impide.
5. En **Salas**, registrar por ejemplo `Sala 4`, tipo `VIP`, capacidad `30`.
6. Explicar la validación de nombre repetido.

---

## 4. Funciones (1 minuto)

1. Elegir película, sala, fecha y hora (`HH:mm`).
2. Guardar y señalar la columna Estado.
3. Intentar repetir la misma sala, fecha y hora: aparece
   *"La sala ya tiene una función en esa fecha y hora"*.

Esto demuestra la restricción `UNIQUE (id_sala, fecha, hora)`.

---

## 5. Productos (2 minutos)

1. Mostrar el catálogo: snacks, bebidas, combos y entradas.
2. Explicar las columnas **Costo**, **Utilidad**, **Precio** y **Stock**.
3. Recordar la fórmula:

   ```
   precio = costo × (1 + % utilidad)
   ```

   Ejemplo: canchita con costo S/ 6.00 y 50 % de utilidad → **S/ 9.00**.

4. Seleccionar la categoría **Entradas**: el sistema desactiva el stock porque
   las entradas no se inventarían.
5. Registrar un producto de dulcería con stock y mostrar su aparición en la tabla.

---

## 6. Clientes (1 minuto)

1. Registrar un cliente con DNI de 8 dígitos.
2. Provocar un error con un DNI de 7 dígitos para mostrar la validación.
3. Explicar que el DNI es la clave primaria y no se puede repetir.

---

## 7. Venta (3 minutos) — parte principal

1. Ir a **Ventas**.
2. Seleccionar un cliente y la fecha.
3. Agregar **2 entradas 2D**: al elegir una entrada se habilita el combo
   **Función**; seleccionar una función.
4. Agregar una **canchita** y una **gaseosa**.
5. Mostrar el detalle y los totales:

   | Concepto | Cálculo |
   |---|---|
   | Total | suma de importes |
   | Neto | total / 1.18 |
   | IGV | total − neto (18 %) |
   | Utilidad | (precio − costo) × cantidad |

6. Pulsar **Registrar venta** y leer el mensaje con el código generado.
7. Abrir **Productos** y mostrar que el stock de la canchita y la gaseosa bajó.

> Punto clave: la venta se guarda en **una sola transacción**. Si el stock es
> insuficiente, no se guarda ni la cabecera ni el detalle.

---

## 8. Historial (2 minutos)

1. Ir a **Historial**.
2. Buscar por cliente o código de venta.
3. Seleccionar una venta y mostrar su detalle (producto, función, cantidad,
   importe).
4. Señalar el resumen: **Total vendido S/ 86.50**, igual que el Excel del caso.
5. Eliminar una venta y volver a **Productos** para comprobar que el stock se
   devuelve.

---

## 9. Modelo de datos (2 minutos)

Mostrar el diagrama y explicar las relaciones principales:

```
perfil 1 ─── N usuario
usuario 1 ── N venta
cliente 1 ── N venta
venta 1 ──── N venta_detalle
producto 1 ─ N venta_detalle
funcion 1 ── N venta_detalle      (solo para entradas)
pelicula 1 ─ N funcion
sala 1 ───── N funcion
categoria 1 ─ N producto
unidad_medida 1 ─ N producto
```

Archivos:

- `database/CineMax_PowerArchitect.architect` → abrir con SQL Power Architect.
- `database/CineMax_PowerArchitect.sql` → DDL para PostgreSQL.
- `src/main/resources/schema.sql` → esquema que usa la aplicación.

---

## 10. Arquitectura (1 minuto)

```
FXML + CSS  →  Controlador  →  Servicio  →  Repositorio  →  H2 / PostgreSQL
```

- **Modelo**: clases con Lombok (`Pelicula`, `Producto`, `Venta`, ...).
- **Repositorio**: JDBC con `PreparedStatement` y transacciones.
- **Servicio**: valida reglas de negocio y calcula precios, IGV y utilidad.
- **Controlador**: escucha los eventos de la vista.
- **AppContext**: crea y conecta los objetos (sin frameworks).

---

## 11. Preguntas frecuentes del jurado

**¿Por qué las entradas no descuentan stock?**
Porque no son inventario: se venden contra la capacidad de una función. El campo
`controla_stock` permite distinguirlas de la dulcería.

**¿Dónde se calcula el precio de venta?**
En el modelo `Producto` (`getPrecioVenta()`) y se vuelve a validar en
`VentaServiceImp` antes de guardar, para que el precio no dependa de la interfaz.

**¿Qué pasa si se cae la venta a la mitad?**
Se ejecuta `rollback()` y no queda nada grabado. La cabecera, el detalle y el
descuento de stock se confirman juntos con `commit()`.

**¿Por qué H2 y no MySQL?**
H2 viene embebida: el proyecto se ejecuta sin instalar un servidor. El DDL de
`database/` está preparado para PostgreSQL y el código acepta ambos motores
cambiando `application.properties`.

**¿Cómo se evita vender más de lo que hay?**
Dos controles: la interfaz compara la cantidad con el stock y el repositorio
ejecuta

```sql
UPDATE producto SET stock = stock - ?
WHERE id_producto = ? AND stock >= ?
```

Si no se actualiza ninguna fila, lanza error y se revierte la transacción.

**¿Qué patrón de diseño se usó?**
Inyección de dependencias manual con `AppContext` (contenedor Singleton),
separación por capas, repositorio genérico (`AbstractJpaRepository`) y servicios
con interfaces.

---

## 12. Cierre

> "CineMax demuestra un CRUD completo con reglas de negocio reales: catálogo,
> stock, ventas con IGV y utilidad, y un modelo relacional de 11 tablas listo
> para SQL Power Architect."
