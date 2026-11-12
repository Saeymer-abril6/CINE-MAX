package pe.edu.upeu.cinemax.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class Formato {

    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");

    private Formato() {
    }

    public static String dinero(BigDecimal valor) {
        return "S/ " + String.format(Locale.US, "%,.2f", valor == null ? BigDecimal.ZERO : valor);
    }

    public static String fecha(LocalDate fecha) {
        return fecha == null ? "" : FECHA.format(fecha);
    }

    public static String hora(LocalTime hora) {
        return hora == null ? "" : HORA.format(hora);
    }

    public static String porcentaje(BigDecimal valor) {
        if (valor == null) return "0 %";
        return valor.multiply(BigDecimal.valueOf(100))
                .setScale(0, RoundingMode.HALF_UP)
                .toPlainString() + " %";
    }

    public static BigDecimal decimal(String texto) {
        if (texto == null || texto.isBlank()) return BigDecimal.ZERO;
        String limpio = texto.replace("S/", "").replace("%", "").replace(",", "").trim();
        try {
            return new BigDecimal(limpio);
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    public static int entero(String texto) {
        try {
            return Integer.parseInt(texto == null ? "" : texto.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static LocalTime hora(String texto) {
        if (texto == null || texto.isBlank()) return null;
        try {
            return LocalTime.parse(texto.trim(), HORA);
        } catch (Exception e) {
            return null;
        }
    }

    public static String normalizar(String texto) {
        if (texto == null) return "";
        String sinAcentos = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return sinAcentos.toLowerCase(Locale.ROOT).trim();
    }
}
