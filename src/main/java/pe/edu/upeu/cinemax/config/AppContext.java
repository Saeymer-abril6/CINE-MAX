package pe.edu.upeu.cinemax.config;

import pe.edu.upeu.cinemax.controller.ClienteController;
import pe.edu.upeu.cinemax.controller.FuncionController;
import pe.edu.upeu.cinemax.controller.HistorialController;
import pe.edu.upeu.cinemax.controller.LoginController;
import pe.edu.upeu.cinemax.controller.MainController;
import pe.edu.upeu.cinemax.controller.PeliculaController;
import pe.edu.upeu.cinemax.controller.ProductoController;
import pe.edu.upeu.cinemax.controller.SalaController;
import pe.edu.upeu.cinemax.controller.VentaController;
import pe.edu.upeu.cinemax.repository.CategoriaRepository;
import pe.edu.upeu.cinemax.repository.ClienteRepository;
import pe.edu.upeu.cinemax.repository.FuncionRepository;
import pe.edu.upeu.cinemax.repository.PeliculaRepository;
import pe.edu.upeu.cinemax.repository.ProductoRepository;
import pe.edu.upeu.cinemax.repository.SalaRepository;
import pe.edu.upeu.cinemax.repository.UnidadMedidaRepository;
import pe.edu.upeu.cinemax.repository.UsuarioRepository;
import pe.edu.upeu.cinemax.repository.VentaRepository;
import pe.edu.upeu.cinemax.service.IClienteService;
import pe.edu.upeu.cinemax.service.IFuncionService;
import pe.edu.upeu.cinemax.service.IPeliculaService;
import pe.edu.upeu.cinemax.service.IProductoService;
import pe.edu.upeu.cinemax.service.ISalaService;
import pe.edu.upeu.cinemax.service.IUsuarioService;
import pe.edu.upeu.cinemax.service.IVentaService;
import pe.edu.upeu.cinemax.service.impl.ClienteServiceImp;
import pe.edu.upeu.cinemax.service.impl.FuncionServiceImp;
import pe.edu.upeu.cinemax.service.impl.PeliculaServiceImp;
import pe.edu.upeu.cinemax.service.impl.ProductoServiceImp;
import pe.edu.upeu.cinemax.service.impl.SalaServiceImp;
import pe.edu.upeu.cinemax.service.impl.UsuarioServiceImp;
import pe.edu.upeu.cinemax.service.impl.VentaServiceImp;

import java.util.HashMap;
import java.util.Map;

/**
 * Contenedor de dependencias de la aplicación (patrón Singleton).
 * Arma el grafo: repositorios → servicios → controladores.
 */
public final class AppContext {

    private static AppContext instance;

    private final Map<Class<?>, Object> contenedor = new HashMap<>();

    private AppContext() {
        DatabaseConfig.init();
        registrarRepositorios();
        registrarServicios();
        registrarControladores();
    }

    public static synchronized AppContext getInstance() {
        if (instance == null) {
            instance = new AppContext();
        }
        return instance;
    }

    private void registrarRepositorios() {
        registrar(UsuarioRepository.class, new UsuarioRepository());
        registrar(ClienteRepository.class, new ClienteRepository());
        registrar(PeliculaRepository.class, new PeliculaRepository());
        registrar(SalaRepository.class, new SalaRepository());
        registrar(FuncionRepository.class, new FuncionRepository());
        registrar(CategoriaRepository.class, new CategoriaRepository());
        registrar(UnidadMedidaRepository.class, new UnidadMedidaRepository());
        registrar(ProductoRepository.class, new ProductoRepository());
        registrar(VentaRepository.class, new VentaRepository());
    }

    private void registrarServicios() {
        registrar(IUsuarioService.class, new UsuarioServiceImp(
                getBean(UsuarioRepository.class)));

        registrar(IClienteService.class, new ClienteServiceImp(
                getBean(ClienteRepository.class),
                getBean(VentaRepository.class)));

        registrar(IPeliculaService.class, new PeliculaServiceImp(
                getBean(PeliculaRepository.class),
                getBean(FuncionRepository.class)));

        registrar(ISalaService.class, new SalaServiceImp(
                getBean(SalaRepository.class),
                getBean(FuncionRepository.class)));

        registrar(IFuncionService.class, new FuncionServiceImp(
                getBean(FuncionRepository.class),
                getBean(PeliculaRepository.class),
                getBean(SalaRepository.class)));

        registrar(IProductoService.class, new ProductoServiceImp(
                getBean(ProductoRepository.class),
                getBean(CategoriaRepository.class),
                getBean(UnidadMedidaRepository.class)));

        registrar(IVentaService.class, new VentaServiceImp(
                getBean(VentaRepository.class),
                getBean(ProductoRepository.class),
                getBean(ClienteRepository.class),
                getBean(FuncionRepository.class)));
    }

    private void registrarControladores() {
        registrar(LoginController.class, new LoginController(
                getBean(IUsuarioService.class)));
        registrar(MainController.class, new MainController());
        registrar(PeliculaController.class, new PeliculaController(
                getBean(IPeliculaService.class)));
        registrar(SalaController.class, new SalaController(
                getBean(ISalaService.class)));
        registrar(FuncionController.class, new FuncionController(
                getBean(IFuncionService.class),
                getBean(IPeliculaService.class),
                getBean(ISalaService.class)));
        registrar(ProductoController.class, new ProductoController(
                getBean(IProductoService.class)));
        registrar(ClienteController.class, new ClienteController(
                getBean(IClienteService.class)));
        registrar(VentaController.class, new VentaController(
                getBean(IVentaService.class),
                getBean(IClienteService.class),
                getBean(IProductoService.class),
                getBean(IFuncionService.class)));
        registrar(HistorialController.class, new HistorialController(
                getBean(IVentaService.class)));
    }

    private void registrar(Class<?> tipo, Object bean) {
        contenedor.put(tipo, bean);
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> tipo) {
        Object bean = contenedor.get(tipo);
        if (bean == null) {
            bean = contenedor.values().stream()
                    .filter(b -> tipo.isAssignableFrom(b.getClass()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Bean no encontrado: " + tipo.getName()));
        }
        return (T) bean;
    }
}
