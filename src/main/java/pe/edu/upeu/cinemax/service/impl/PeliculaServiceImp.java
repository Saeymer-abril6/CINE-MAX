package pe.edu.upeu.cinemax.service.impl;

import pe.edu.upeu.cinemax.exception.BusinessException;
import pe.edu.upeu.cinemax.model.Pelicula;
import pe.edu.upeu.cinemax.repository.FuncionRepository;
import pe.edu.upeu.cinemax.repository.PeliculaRepository;
import pe.edu.upeu.cinemax.service.IPeliculaService;
import pe.edu.upeu.cinemax.util.Validador;

import java.util.List;

public class PeliculaServiceImp implements IPeliculaService {

    private final PeliculaRepository repository;
    private final FuncionRepository funcionRepository;

    public PeliculaServiceImp(PeliculaRepository repository, FuncionRepository funcionRepository) {
        this.repository = repository;
        this.funcionRepository = funcionRepository;
    }

    @Override
    public List<Pelicula> findAll() {
        return repository.findAll();
    }

    @Override
    public Pelicula save(Pelicula pelicula) {
        Validador.validar(pelicula);
        repository.findByTitulo(pelicula.getTitulo()).ifPresent(p -> {
            throw new BusinessException("Ya existe una película con el título " + pelicula.getTitulo() + ".");
        });
        return repository.save(pelicula);
    }

    @Override
    public Pelicula update(Long id, Pelicula pelicula) {
        if (repository.findById(id).isEmpty()) {
            throw new BusinessException("La película que intentas actualizar no existe.");
        }
        Validador.validar(pelicula);
        repository.findByTitulo(pelicula.getTitulo()).ifPresent(p -> {
            if (!p.getIdPelicula().equals(id)) {
                throw new BusinessException("Ya existe otra película con ese título.");
            }
        });
        pelicula.setIdPelicula(id);
        return repository.update(pelicula);
    }

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new BusinessException("La película que intentas eliminar no existe.");
        }
        if (funcionRepository.existsByPelicula(id)) {
            throw new BusinessException("No se puede eliminar la película porque tiene funciones registradas.");
        }
        repository.deleteById(id);
    }
}
