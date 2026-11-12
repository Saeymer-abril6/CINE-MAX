package pe.edu.upeu.cinemax.service;

import pe.edu.upeu.cinemax.model.Sala;

import java.util.List;

public interface ISalaService {

    List<Sala> findAll();

    Sala save(Sala sala);

    Sala update(Long id, Sala sala);

    void delete(Long id);
}
