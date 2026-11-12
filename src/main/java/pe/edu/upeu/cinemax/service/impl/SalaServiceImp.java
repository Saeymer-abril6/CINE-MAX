package pe.edu.upeu.cinemax.service.impl;

import pe.edu.upeu.cinemax.exception.BusinessException;
import pe.edu.upeu.cinemax.model.Sala;
import pe.edu.upeu.cinemax.repository.FuncionRepository;
import pe.edu.upeu.cinemax.repository.SalaRepository;
import pe.edu.upeu.cinemax.service.ISalaService;
import pe.edu.upeu.cinemax.util.Validador;

import java.util.List;

public class SalaServiceImp implements ISalaService {

    private final SalaRepository repository;
    private final FuncionRepository funcionRepository;

    public SalaServiceImp(SalaRepository repository, FuncionRepository funcionRepository) {
        this.repository = repository;
        this.funcionRepository = funcionRepository;
    }

    @Override
    public List<Sala> findAll() {
        return repository.findAll();
    }

    @Override
    public Sala save(Sala sala) {
        Validador.validar(sala);
        repository.findByNombre(sala.getNombre()).ifPresent(s -> {
            throw new BusinessException("Ya existe una sala con el nombre " + sala.getNombre() + ".");
        });
        return repository.save(sala);
    }

    @Override
    public Sala update(Long id, Sala sala) {
        if (repository.findById(id).isEmpty()) {
            throw new BusinessException("La sala que intentas actualizar no existe.");
        }
        Validador.validar(sala);
        repository.findByNombre(sala.getNombre()).ifPresent(s -> {
            if (!s.getIdSala().equals(id)) {
                throw new BusinessException("Ya existe otra sala con ese nombre.");
            }
        });
        sala.setIdSala(id);
        return repository.update(sala);
    }

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new BusinessException("La sala que intentas eliminar no existe.");
        }
        if (funcionRepository.existsBySala(id)) {
            throw new BusinessException("No se puede eliminar la sala porque tiene funciones registradas.");
        }
        repository.deleteById(id);
    }
}
