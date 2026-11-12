package pe.edu.upeu.cinemax.service.impl;

import pe.edu.upeu.cinemax.exception.BusinessException;
import pe.edu.upeu.cinemax.model.Funcion;
import pe.edu.upeu.cinemax.repository.FuncionRepository;
import pe.edu.upeu.cinemax.repository.PeliculaRepository;
import pe.edu.upeu.cinemax.repository.SalaRepository;
import pe.edu.upeu.cinemax.service.IFuncionService;
import pe.edu.upeu.cinemax.util.Validador;

import java.util.List;

public class FuncionServiceImp implements IFuncionService {

    private final FuncionRepository repository;
    private final PeliculaRepository peliculaRepository;
    private final SalaRepository salaRepository;

    public FuncionServiceImp(FuncionRepository repository,
                             PeliculaRepository peliculaRepository,
                             SalaRepository salaRepository) {
        this.repository = repository;
        this.peliculaRepository = peliculaRepository;
        this.salaRepository = salaRepository;
    }

    @Override
    public List<Funcion> findAll() {
        return repository.findAll();
    }

    @Override
    public Funcion save(Funcion funcion) {
        validar(funcion);
        validarCruce(funcion, null);
        return repository.save(funcion);
    }

    @Override
    public Funcion update(Long id, Funcion funcion) {
        if (repository.findById(id).isEmpty()) {
            throw new BusinessException("La función que intentas actualizar no existe.");
        }
        validar(funcion);
        validarCruce(funcion, id);
        funcion.setIdFuncion(id);
        return repository.update(funcion);
    }

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new BusinessException("La función que intentas eliminar no existe.");
        }
        if (repository.existsEnVentaDetalle(id)) {
            throw new BusinessException("No se puede eliminar la función porque tiene entradas vendidas.");
        }
        repository.deleteById(id);
    }

    private void validar(Funcion funcion) {
        Validador.validar(funcion);
        if (peliculaRepository.findById(funcion.getIdPelicula()).isEmpty()) {
            throw new BusinessException("La película seleccionada no existe.");
        }
        if (salaRepository.findById(funcion.getIdSala()).isEmpty()) {
            throw new BusinessException("La sala seleccionada no existe.");
        }
    }

    private void validarCruce(Funcion funcion, Long idActual) {
        repository.findBySalaFechaHora(funcion.getIdSala(), funcion.getFecha(), funcion.getHora())
                .ifPresent(f -> {
                    if (idActual == null || !f.getIdFuncion().equals(idActual)) {
                        throw new BusinessException("La sala ya tiene una función en esa fecha y hora.");
                    }
                });
    }
}
