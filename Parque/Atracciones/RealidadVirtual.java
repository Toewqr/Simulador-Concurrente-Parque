/**
 *
 * @author Teo Aranda
 */
public class RealidadVirtual {

    private static final int CANTIDAD_GAFAS = 20;
    private static final int CANTIDAD_MANOPLAS = 20;
    private static final int CANTIDAD_BASES = 20;

    private int cantGafas;
    private int cantManoplas;
    private int cantBases;

    public RealidadVirtual() {
        cantGafas = CANTIDAD_GAFAS;
        cantManoplas = CANTIDAD_MANOPLAS;
        cantBases = CANTIDAD_BASES;
    }

    private boolean equipoDisponible() {
        /*
         * Devuelve true si hay al menos un elemento de cada
         * parte del equipo
         */

        return cantGafas > 0 && cantManoplas > 0 && cantBases > 0;
    }

    public synchronized void entrarRealidadVirtual(String nombre) throws InterruptedException {
        /*
         * El visitante intenta entrar al juego de realidad virtual y solo va a poder
         * hacerlo
         * si puede tomar un equipo completo
         */

        System.out.println(nombre + " intenta agarrar el equipo para jugar a realidad virtual.");

        // Se revisa si hay un equipo completo dispobible, sino se duerme
        while (!equipoDisponible()) {
            System.out.println(nombre + " debe esperar para agarrar un equipo completo.");
            wait();
        }

        // Toma cada elemento del equipo
        cantGafas--;
        cantManoplas -= 2;
        cantBases--;

        System.out.println(nombre + " recibio un equipo completo (1 gafas, 2 manoplas, 1 base).");
        System.out.println(
                "Equipo restante: Gafas: " + cantGafas + ". Manoplas: " + cantManoplas + ". Bases: " + cantBases + ".");
    }

    public synchronized void dejarEquipoCompleto(String nombre) throws InterruptedException {
        /*
         * El visitante deja todo el equipo y avisa a los demÃ¡s visitantes que ya
         * pueden
         * agarrarlo
         */

        cantGafas++;
        cantManoplas += 2;
        cantBases++;

        System.out.println(nombre + " devuelve el equipo completo.");
        System.out.println("Equipos disponibles: Gafas: " + cantGafas + ". Manoplas: " + cantManoplas + ". Bases: "
                + cantBases + ".");

        notifyAll();
    }

    public static void main(String[] arg) {

        RealidadVirtual virtual = new RealidadVirtual();

        for (int i = 0; i < 31; i++) {
            Visitante v = new Visitante(virtual, "Visitante " + i);
            Thread visitante = new Thread(v);
            visitante.start();
        }

    }

    public static class Visitante implements Runnable {

        private String nombre;
        private RealidadVirtual virtual;

        public Visitante(RealidadVirtual virtual, String nombre) {
            this.virtual = virtual;
            this.nombre = nombre;
        }

        @Override
        public void run() {

            try {
                virtual.entrarRealidadVirtual(nombre);
                Thread.sleep(2400);
                virtual.dejarEquipoCompleto(nombre);
            } catch (Exception e) {
                // TODO: handle exception
            }

        }
    }
}