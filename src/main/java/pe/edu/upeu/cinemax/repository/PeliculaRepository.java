package pe.edu.upeu.cinemax.repository;

import pe.edu.upeu.cinemax.model.Pelicula;

import java.util.ArrayList;
import java.util.List;

public class PeliculaRepository {
    private final List<Pelicula> peliculas = new ArrayList<>();
    private int nextId = 1;

    public PeliculaRepository() {
        guardar(new Pelicula("Superman", "Acción", 130, "APT-12"));
        guardar(new Pelicula("Jurassic World", "Aventura", 133, "APT-12"));
        guardar(new Pelicula("Lilo & Stitch", "Familia", 108, "APT"));
        guardar(new Pelicula("Minecraft", "Aventura", 101, "APT"));
        guardar(new Pelicula("Cómo entrenar a tu dragón", "Fantasía", 125, "APT"));
        guardar(new Pelicula("Misión Imposible", "Acción", 169, "APT-14"));
        guardar(new Pelicula("El Conjuro", "Terror", 112, "MAY-14"));
        guardar(new Pelicula("F1", "Deportes", 155, "APT-12"));
    }

    public List<Pelicula> listar() { return new ArrayList<>(peliculas); }

    public Pelicula guardar(Pelicula pelicula) {
        pelicula.setId(nextId++);
        peliculas.add(pelicula);
        return pelicula;
    }

    public void actualizar(Pelicula pelicula) {
        for (int i = 0; i < peliculas.size(); i++) {
            if (peliculas.get(i).getId() == pelicula.getId()) {
                peliculas.set(i, pelicula);
                return;
            }
        }
    }

    public void eliminar(int id) {
        peliculas.removeIf(p -> p.getId() == id);
    }
}
