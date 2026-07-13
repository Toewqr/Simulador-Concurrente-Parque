public class Encargado implements Runnable {
    private Premios prem;
    private String nombre;

    public Encargado(Premios tren, String nom) {
        this.prem = tren;
        this.nombre = nom;
    }

    @Override
    public void run() {
        while (prem.terminoTurno()) {
            try {
                prem.entregarPremio(nombre);
              

            } catch (Exception e) {
                System.out.println("🚨 El maquinista fue interrumpido.");
            }
        }
    }
}