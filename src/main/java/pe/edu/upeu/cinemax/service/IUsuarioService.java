package pe.edu.upeu.cinemax.service;

import pe.edu.upeu.cinemax.model.Usuario;

import java.util.Optional;

public interface IUsuarioService {

    Optional<Usuario> login(String usuario, String clave);
}
