package pe.edu.upeu.cinemax.service;

import pe.edu.upeu.cinemax.model.Pelicula;
import pe.edu.upeu.cinemax.repository.PeliculaRepository;

import java.util.List;

public class PeliculaService {
    private final PeliculaRepository repo = new PeliculaRepository();

    public List<Pelicula> listar() { return repo.listar(); }
    public Pelicula crear(String titulo, String genero, int duracion, String clasificacion) {
        return repo.guardar(new Pelicula(titulo, genero, duracion, clasificacion));
    }
    public void actualizar(Pelicula pelicula) { repo.actualizar(pelicula); }
    public void eliminar(int id) { repo.eliminar(id); }
}
