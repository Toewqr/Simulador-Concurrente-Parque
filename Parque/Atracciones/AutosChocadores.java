import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Teo Aranda
 */
public class AutosChocadores {

    private final int capAutos = 20;
    private final Semaphore empezarAutosChocadores = new Semaphore(capAutos);
    private final Semaphore terminarAutosChocadores = new Semaphore(0);
    private final Semaphore mutexAuto = new Semaphore(1);
    private int personasEnAutos;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> temporizador;
    private static final int TIEMPO_ESPERA = 5;

    public AutosChocadores() {
        this.personasEnAutos = 0;

    }

    private void iniciarTemporizador() {
        if (temporizador != null) {
            temporizador.cancel(false); // Cancela el temporizador activo
        }

        // Programa la salida del tren después de 5 minutos
        temporizador = scheduler.schedule(() -> {
            try {
                mutexAuto.acquire();
                if (personasEnAutos > 0 && personasEnAutos < capAutos) {
                    System.out
                            .println("Tiempo cumplido. No se pudo llenar los autos chocadores. Se van de la atracción "
                                    + personasEnAutos + " visitantes");
                    empezarAutosChocadores.drainPermits();
                    terminarAutosChocadores.release(personasEnAutos);
                }
                mutexAuto.release();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }, TIEMPO_ESPERA, TimeUnit.SECONDS);

        System.out.println("Temporizador de 5 minutos de los autos chocadores iniciado.");
    }

    private void cancelarTemporizador() {
        if (temporizador != null && !temporizador.isDone()) {
            temporizador.cancel(false);
            System.out.println("Temporizador cancelado. No hay mas autos disponibles.");
        }
    }

    public void subirseAutitosChocadores(String nombre) throws InterruptedException {
        // Espera que haya lugar para subirse
        empezarAutosChocadores.acquire();
        System.out.println(nombre + " está esperando para subirse a un autito chocador");

        mutexAuto.acquire();
        personasEnAutos++;

        if (personasEnAutos == 1) {
            iniciarTemporizador(); // Se inicia el temporizador
        }

        System.out.println(nombre + " se subió a un autito chocador");

        // Si se completa la capacidad, se libera la atracción
        if (personasEnAutos == capAutos) {
            System.out.println("Autos llenos. Inicia la atracción.");
            cancelarTemporizador(); // Se cancela cuando se llena la atracción
            terminarAutosChocadores.release(capAutos);
        }
        mutexAuto.release();

        // Esperan hasta que se llene la atracción
        terminarAutosChocadores.acquire();
        bajarseAutitosChocadores(nombre);
    }

    public void bajarseAutitosChocadores(String nombre) throws InterruptedException {
        mutexAuto.acquire();
        // Los visitantes bajan de a uno
        personasEnAutos--;
        System.out.println(nombre + " baja de los autitos chocadores. Personas restantes: " + personasEnAutos);

        // Si todos bajan, se permite que nuevos visitantes suban
        if (personasEnAutos == 0) {
            System.out.println("Se permite que entren nuevos visitantes a los autitos chocadores.");
            empezarAutosChocadores.release(capAutos);
        }

        mutexAuto.release();
    }

}
