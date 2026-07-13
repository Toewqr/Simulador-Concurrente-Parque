
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Teo Aranda
 */
public class BarcoPirata {

    private final Semaphore espaciosBarco = new Semaphore(20);
    private final Semaphore mutexBarco = new Semaphore(1);
    private final Semaphore mutexPasajero = new Semaphore(1);
    private final Semaphore bajarBarco = new Semaphore(0);

    private int enBarco = 0;
    private final int capBarco = 20;

    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> temporizador;
    private static final int TIEMPO_ESPERA = 5;

    public BarcoPirata() {
    }

    private void iniciarTemporizador() {
        if (temporizador != null) {
            temporizador.cancel(false); // Cancela el temporizador activo
        }

        // Programa la salida del tren después de 5 minutos
        temporizador = scheduler.schedule(() -> {
            try {
                mutexBarco.acquire();
                mutexPasajero.acquire();
                if (enBarco > 0 && enBarco < capBarco) { // Si al menos hay un pasajero y el barco no se llenó
                    System.out.println("El barco sale con " + enBarco + " pasajeros.");
                    espaciosBarco.drainPermits();
                    bajarBarco.release(enBarco);
                }
                mutexPasajero.release();
                mutexBarco.release();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }, TIEMPO_ESPERA, TimeUnit.SECONDS);

        System.out.println("Temporizador de 5 minutos iniciado.");
    }

    private void cancelarTemporizador() {
        if (temporizador != null && !temporizador.isDone()) {
            temporizador.cancel(false);
            System.out.println("Temporizador cancelado. El barco esta lleno.");
        }
    }

    public void embarcar(String nombre) throws InterruptedException {
        mutexBarco.acquire();
        System.out.println(nombre + " intenta subir al barco.");

        // Si hay espacio, lo toma. Si no, espera
        espaciosBarco.acquire();
        mutexPasajero.acquire();
        enBarco++;
        System.out.println(nombre + "pudo subir. Pasajeros a bordo: " + enBarco);

        // Se inicia el temporizador cuando sube el primer pasajero
        if (enBarco == 1) {
            iniciarTemporizador();
        }

        // Si el barco se llena, se cancela el temporizador
        if (enBarco == capBarco) {
            System.out.println("El barco se lleno. Puede zarpar.");
            cancelarTemporizador();
            bajarBarco.release(capBarco);
        }

        mutexPasajero.release();
        mutexBarco.release();

        bajarBarco.acquire();
    }

    public void desembarcar(String nombre) throws InterruptedException {
        mutexPasajero.acquire();
        enBarco--;
        System.out.println(nombre + "bajo del barco. Quedan " + enBarco + " pasajeros.");

        // Cuando todos bajan, el ultimo libera los asientos
        if (enBarco == 0) {
            System.out.println("Se bajaron todos los pasajeros. Se permite el embarque nuevamente.");
            espaciosBarco.release(capBarco);
        }
        mutexPasajero.release();
    }

}
