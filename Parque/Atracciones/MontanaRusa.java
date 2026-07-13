
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Teo Aranda
 */
public class MontanaRusa {

    private Semaphore asientosDisponibles;
    private Semaphore mutexMontana;
    private Semaphore viajeCompletado;
    private int asientosMontana;
    private final int capMontana = 5;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> temporizador;
    private static final int TIEMPO_ESPERA = 5;

    public MontanaRusa() {
        this.asientosDisponibles = new Semaphore(capMontana);
        this.mutexMontana = new Semaphore(1);
        this.viajeCompletado = new Semaphore(0);
        this.asientosMontana = 0;

    }

    private void iniciarTemporizador() {
        if (temporizador != null) {
            temporizador.cancel(false); // Cancela el temporizador activo
        }

        // Programa la salida del tren después de 5 minutos
        temporizador = scheduler.schedule(() -> {
            try {
                mutexMontana.acquire();

                if (asientosMontana > 0 && asientosMontana < capMontana) {
                    System.out.println("Tiempo cumplido. No se pudo llenar la montana. Se van de la atracción "
                            + asientosMontana + " visitantes");
                    asientosDisponibles.drainPermits();
                    viajeCompletado.release(asientosMontana);
                }

                mutexMontana.release();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }, TIEMPO_ESPERA, TimeUnit.SECONDS);

        System.out.println("Temporizador de 5 minutos de la montana rusa iniciado.");
    }

    public void subirseMontanaRusa(String nombre) throws InterruptedException {
        // Se espera hasta que haya espacio
        asientosDisponibles.acquire(); // Si lo cambio por un tryAcquire se rompe.

        mutexMontana.acquire();
        asientosMontana++;
        System.out.println(nombre + " sube y ocupa un asiento de la montana. Asientos ocupados: " + asientosMontana);

        if (asientosMontana == 1) {
            iniciarTemporizador(); // Se inicia el temporizador
        }

        // Si la montaña está llena, inicia el viaje
        if (asientosMontana == capMontana) {
            System.out.println("La montaña rusa está llena, comienza el viaje.");
            viajeCompletado.release(capMontana);
        }
        mutexMontana.release();
        viajeCompletado.acquire();
        bajarseMontana(nombre);

    }

    public void bajarseMontana(String nombre) throws InterruptedException {
        // Bajan de a uno
        mutexMontana.acquire();
        asientosMontana--;
        System.out.println(nombre + " baja de la montaña rusa. Asientos restantes: " + asientosMontana);

        // Si todos bajan, se permite que nuevos visitantes suban
        if (asientosMontana == 0) {
            System.out.println("Se permite que entren nuevos visitantes a la montana rusa.");
            asientosDisponibles.release(capMontana);
        }
        mutexMontana.release();
    }

}
