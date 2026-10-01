package pe.edu.upeu.cinemax.enums;
public enum TipoEntrada { GENERAL(15), PREFERENCIAL(25), VIP(40);
    private final double precio; TipoEntrada(double precio){this.precio=precio;} public double getPrecio(){return precio;}
    @Override public String toString(){return name().charAt(0)+name().substring(1).toLowerCase()+" - S/ "+String.format("%.2f",precio);}
}
