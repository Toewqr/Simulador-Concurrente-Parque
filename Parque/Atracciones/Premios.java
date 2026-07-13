
import java.util.Random;
import java.util.concurrent.Exchanger;

import java.util.concurrent.Semaphore;

/**
 *
 * @author Teo Aranda
 */

public class Premios {

    private final Exchanger<Integer> cambiarFichas;
    private final Exchanger<String> darPremio;
    private final Random random;

    private final Semaphore mutexFichas;
    private final Semaphore mutexPremios;

    private boolean cerrado = true;

    public Premios() {
        this.cambiarFichas = new Exchanger<>();
        this.darPremio = new Exchanger<>();
        this.random = new Random();
        this.mutexFichas = new Semaphore(1);
        this.mutexPremios = new Semaphore(1);

    }

    public synchronized boolean terminoTurno() {
        return cerrado;
    }

    public synchronized void cerrarJuego() {
        cerrado = false;
    }

    public void jugarJuego(String nombre) throws InterruptedException {
        /*
         * El visitante juega y consigue una cantidad al azar de fichas.
         * Cuando las consigue, va a intercambiarlas.
         */

        mutexFichas.acquire(); // Uso un mutex para evitar que entren muchos visitantes a la vez

        System.out.println(nombre + " decide jugar un juego");
        int fichas = random.nextInt(1, 6); // Cantidad de fichas conseguidas al azar
        System.out.println(nombre + " termino de jugar ganando " + fichas + " fichas");

        intercambiarFichas(fichas, nombre);
    }

    private void intercambiarFichas(int fichas, String nombre) throws InterruptedException {
        /*
         * El visitante espera hasta que un repartidor llegue y le entrega las fichas.
         * Si ya hay un hilo o si todavía no llegó el repartidor, se queda bloqueado
         */

        System.out.println(nombre + " pasa a cambiar las fichas obtenidas en los juegos");

        cambiarFichas.exchange(fichas); // Le da las fichas al repartidor y espera.

        System.out.println(nombre + " le entrego las fichas al encargado");
        String premioObtenido = darPremio.exchange(""); // Recibe el premio
        System.out.println(nombre + " recibio un premio. Obtuvo: " + premioObtenido);

        mutexFichas.release();
    }

    public void entregarPremio(String nombre) throws InterruptedException {
        /*
         * El repartidor espera hasta que algún visitante le de fichas y, cuando las
         * tenga,
         * revisa la cantidad y le devuelve el premio correspondiente.
         */
        mutexPremios.acquire(); // Uso un mutex para evitar que entre más de un repartidor a la vez

        int fichas = cambiarFichas.exchange(null); // Si no tiene fichas, se queda bloqueado hasta que alguien se las
                                                   // dé.

        System.out.println("El " + nombre + " obtuvo: " + fichas + " fichas.");
        // Revisa qué premio dar por el valor de las fichas
        String premio;
        switch (fichas) {
            case 1:
                premio = "caramelo";
                break;
            case 2:
                premio = "peluche";
                break;
            case 3:
                premio = "mp3";
                break;
            case 4:
                premio = "globo";
                break;
            case 5:
                premio = "bicicleta";
                break;
            default:
                premio = "nada";
                break;
        }

        darPremio.exchange(premio); // Da el premio correspondiente a la cantidad de fichas.
        System.out.println(nombre + " entrego un premio por el valor de " + fichas + " fichas");

        mutexPremios.release();
    }

}
