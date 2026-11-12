package pe.edu.upeu.cinemax.service;

import pe.edu.upeu.cinemax.model.Pelicula;

import java.util.List;

public interface IPeliculaService {

    List<Pelicula> findAll();

    Pelicula save(Pelicula pelicula);

    Pelicula update(Long id, Pelicula pelicula);

    void delete(Long id);
}
