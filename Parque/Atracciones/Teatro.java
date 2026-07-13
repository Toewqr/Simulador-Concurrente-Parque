import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author Teo Aranda
 */

public class Teatro {

    private final int capacidadTeatro = 20;
    private int enTeatro = 0;
    private int personaEnGrupo = 0;

    private boolean temp = true;
    private boolean grupoFormado = false;
    private boolean estaLleno = false;

    private ReentrantLock lock = new ReentrantLock();
    private Condition teatro = lock.newCondition();
    private Condition grupo = lock.newCondition();

    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> temporizador;
    private static final int TIEMPO_ESPERA = 5;

    public Teatro() {
    }

    private void iniciarTemporizador() {
        lock.lock();
        try {
            if (temporizador != null) {
                temporizador.cancel(false); // Cancela el temporizador activo
            }

            // Programa el temporizador
            temporizador = scheduler.schedule(() -> {
                lock.lock();
                try {
                    if (enTeatro > 0 && enTeatro < capacidadTeatro
                            || personaEnGrupo > 0 && enTeatro < capacidadTeatro) {
                        System.out.println("El teatro tardó mucho tiempo en llenarse. Se van "
                                + (enTeatro + personaEnGrupo) + " visitantes.");
                        grupoFormado = true;

                    }
                    grupo.signalAll();
                    teatro.signalAll();
                } finally {
                    lock.unlock();
                }
            }, TIEMPO_ESPERA, TimeUnit.SECONDS);

        } finally {
            lock.unlock();
        }
    }

    public void armarGrupo(String nombre) throws InterruptedException {
        lock.lock();
        try {
            System.out.println(nombre + " intenta formar un grupo");

            personaEnGrupo++;

            if (personaEnGrupo == 5) {
                System.out.println("se formo un grupo y se le permite ingresar al teatro");
                grupoFormado = true;
                grupo.signalAll();
            }
            // deberia cancelar el temporizador con cada grupo que entre o mientras sigan
            // entrando personas a armar grupo
            iniciarTemporizador();

            while (!grupoFormado) {
                grupo.await();
            }
            System.out.println(nombre + " forma un grupo y procede a entrar al teatro");

        } finally {
            lock.unlock();
        }
    }

    public void entrarTeatro(String nombre) throws InterruptedException {
        lock.lock();
        try {
            System.out.println(nombre + " ingresa al teatro con su grupo");
            personaEnGrupo--;
            enTeatro++;

            if (enTeatro % 5 == 0) {
                System.out.println("ingreso un grupo entero al teatro");
            }

            if (personaEnGrupo == 0 && enTeatro < capacidadTeatro && temp) { // es para que no se permita ingresara ams
                                                                             // de 4 grupos
                grupoFormado = false;
                grupo.signalAll();
            }

            if (personaEnGrupo == 0) { // esta aca por si salen los visitantes por tiempo
                temp = false;
                System.out.println("estan todos los visitantes listos para la obra");
                teatro.signalAll();
            }

            while (capacidadTeatro > enTeatro && temp) { // Espera a que el teatro se llene o el temporizador expire
                teatro.await();
            }

            if (!estaLleno) { // Se inicia la obra y evita que más hilos entren
                System.out.println("Se da inicio a la obra");
                estaLleno = true;
                teatro.signalAll();
            }

        } finally {
            lock.unlock();
        }
    }

    public void salirTeatro(String nombre) throws InterruptedException {
        lock.lock();
        try {
            enTeatro--;
            System.out.println(nombre + " se fue del teatro");

            if (enTeatro == 0) {
                temp = true;
                estaLleno = false;
                System.out.println(" se permite el ingreso a nuevos visitantes para formar grupos");
                grupoFormado = false;
                grupo.signalAll();
            }

        } finally {
            lock.unlock();
        }
    }
}
