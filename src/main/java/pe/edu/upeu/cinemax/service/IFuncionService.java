package pe.edu.upeu.cinemax.service;

import pe.edu.upeu.cinemax.model.Funcion;

import java.util.List;

public interface IFuncionService {

    List<Funcion> findAll();

    Funcion save(Funcion funcion);

    Funcion update(Long id, Funcion funcion);

    void delete(Long id);
}
