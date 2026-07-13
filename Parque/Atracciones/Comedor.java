import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 *
 * @author Teo Aranda
 */

public class Comedor {
    private static final int CAPACIDAD = 25;

    private final Semaphore comedor = new Semaphore(CAPACIDAD);
    private final CyclicBarrier mesa = new CyclicBarrier(4);
    private final CyclicBarrier salida = new CyclicBarrier(4);
    private final Object mutex = new Object();

    Random random = new Random();

    public Comedor() {
    }

    public void entrarComedor(String nombre) throws Exception {
        if (comedor.tryAcquire()) {
            comedor.acquire();
            System.out.println(nombre + " espera para sentarse.");

            try {

                synchronized (mutex) {
                    if (this.mesa.isBroken()) {
                        System.out.println(nombre + "La mesa esta rota busca otra");
                        this.mesa.reset();
                    }
                }

                System.out.println("Intento sentarse en la mesa el " + nombre);
                mesa.await(5, TimeUnit.SECONDS);
                System.out.println(nombre + " se sento en la mesa");
                salirComedor(nombre);

            } catch (BrokenBarrierException | TimeoutException e) {

                System.out.println("No complete mesa en tiempo, me voy del comedor. Soy " + nombre);
                salirComedor(nombre);

            }
        } else {

            synchronized (mutex) {
                int decision = random.nextInt(0, 2);
                if (decision == 0) {
                    System.out.println(nombre + " decide esperar");
                    entrarComedor(nombre);
                } else {
                    System.out.println(nombre + " decide irse");
                }

            }

        }
    }

    public void salirComedor(String nombre) throws Exception {
        try {

            synchronized (mutex) {
                if (this.salida.isBroken()) {
                    System.out.println(nombre + "La salida esta rota busca otra");
                    this.salida.reset();
                }
            }

            salida.await(5, TimeUnit.SECONDS);

            System.out.println(nombre + " se va del comedor");

            this.comedor.release();

        } catch (BrokenBarrierException | TimeoutException e) {

            System.out.println("Se termino el tiempo para salir, se reinicia la salida.");

            this.comedor.release();

        }
    }

}