package pe.edu.upeu.cinemax;

/**
 * Lanzador de CineMax.
 *
 * <p>Permite usar el botón verde "Run" del IDE sin el error
 * <em>"JavaFX runtime components are missing"</em>, porque esta clase
 * no extiende de Application: solo llama al main de {@link App}.</p>
 */
public class SysCineMax {

    public static void main(String[] args) {
        App.main(args);
    }
}
