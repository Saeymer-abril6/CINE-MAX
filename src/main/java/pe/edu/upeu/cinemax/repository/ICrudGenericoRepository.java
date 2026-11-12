package pe.edu.upeu.cinemax.repository;

import java.util.List;
import java.util.Optional;

/** Operaciones CRUD comunes a todos los repositorios. */
public interface ICrudGenericoRepository<T, ID> {

    List<T> findAll();

    Optional<T> findById(ID id);

    T save(T entity);

    T update(T entity);

    void deleteById(ID id);

    boolean existsById(ID id);
}
