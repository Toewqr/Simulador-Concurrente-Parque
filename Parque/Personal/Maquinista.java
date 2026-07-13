public class Maquinista implements Runnable {
    private Tren tren; 

    public Maquinista (Tren tren){
        this.tren=tren;
    }
    @Override
    public void run() {
        while (tren.terminoTurno()) {
            try {
                tren.llenarBoletos();
                tren.empezarTurno();
                tren.terminarViaje();
            } catch (Exception e) {
                System.out.println("🚨 El maquinista fue interrumpido.");
            }
        }
    }
}