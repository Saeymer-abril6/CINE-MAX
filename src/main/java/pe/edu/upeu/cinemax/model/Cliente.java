package pe.edu.upeu.cinemax.model;

public class Cliente {
    private int id;
    private String dni;
    private String nombre;
    private String telefono;

    public Cliente(int id, String dni, String nombre, String telefono) {
        this.id = id;
        this.dni = dni;
        this.nombre = nombre;
        this.telefono = telefono;
    }

    public Cliente(String dni, String nombre, String telefono) {
        this(0, dni, nombre, telefono);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    @Override
    public String toString() {
        return nombre + " - DNI: " + dni;
    }
}
