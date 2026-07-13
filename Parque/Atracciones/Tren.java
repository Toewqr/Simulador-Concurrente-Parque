import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Teo Aranda
 */

public class Tren {
    private int capacidad;

    private BlockingQueue<Object> tren;
    private BlockingQueue<Object> salida;
    private BlockingQueue<Object> tickets;
    private BlockingQueue<Object> empezarTurno;

    private boolean parqueCerrado;
    private boolean iniciarTemp;

    private int personasTren;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> temporizador;
    private static final int TIEMPO_ESPERA = 5;

    public Tren(int capacidad) {

        this.capacidad = capacidad;
        this.tren = new ArrayBlockingQueue<>(capacidad);
        this.tickets = new ArrayBlockingQueue<>(capacidad);
        this.salida = new ArrayBlockingQueue<>(capacidad);
        this.empezarTurno = new ArrayBlockingQueue<>(capacidad);

        this.iniciarTemp = true;
        this.parqueCerrado = true;
        this.personasTren = 0;

    }

    private synchronized void iniciarTemporizador() {
        if (this.temporizador != null) {
            temporizador.cancel(false);
        }

        this.temporizador = scheduler.schedule(() -> {
            System.out.println("⌛ Tiempo cumplido. El tren partirá con o sin la capacidad maxima de visitantes");

            try {
                tickets.clear();
                empezarTurno.put(new Object());
            } catch (Exception e) {
                e.printStackTrace();
            }

        }, TIEMPO_ESPERA, TimeUnit.SECONDS);

        System.out.println("🕐 Temporizador de 5 minutos iniciado.");
    }

    public void comprarTicket(String visitante) throws Exception {

        System.out.println(visitante + " espera en la fila para pasar al tren y trata de comprar un ticket ");
        tickets.take();

        synchronized (this) { // si entra un hilo se inicia el temporizador
            if (this.iniciarTemp) {

                iniciarTemp = false;
                iniciarTemporizador();
            }
        }
        System.out.println(visitante + " compró un ticket e ingresa al tren ");
    }

    public void entrarTren(String visitante) throws Exception {

        System.out.println(visitante + "entrega el tiket al encargado para ingresar al tren");
        tren.put(visitante); // personas dentro del tren
        this.listoParaSalir(visitante); // sale si el tren esta completo
        System.out.println(visitante + " se sienta en el tren y espera a que comience el viaje");

        salida.take(); // los hilos deben esperar a que termine el viaje para tener permiso de salir
                       // del tren
        System.out.println(visitante + "Disfruto el viaje y se dispone a salir del tren");
    }

    public void salirTren(String nombre) throws Exception {
        tren.take();
        this.salieronTodos();
        System.out.println(nombre + "sale del tren ");

    }

    // uso un contador para saber quien es el ultimo, podria usar el metodo size de
    // las colas o isEmpty pero
    // si se hace eso se debe excluir a los hilos de que usen put y take para que no
    // interfieran al momento de comparar la capacidad de las colas
    public synchronized void salieronTodos() throws Exception {
        this.personasTren--;
        if (this.personasTren == 0) {
            this.iniciarTemp = true;
            empezarTurno.put(new Object());
        }
    }

    public synchronized void listoParaSalir(String visitante) throws Exception {
        this.personasTren++;
        if (this.capacidad == this.personasTren) {
            System.out.println("El tren lleno su capacidad y se dispone a salir ");
            this.empezarTurno.put(new Object());

        }
    }

    public void llenarBoletos() throws InterruptedException {
        int cap = tickets.remainingCapacity();
        for (int i = 0; i < cap; i++) {
            tickets.put(new Object());
        }
    }

    public void empezarTurno() throws Exception {

        this.empezarTurno.take(); // arranca por tiempo, el tren esta lleno o para terminar su turno

        if (this.parqueCerrado) { // cerrado es el estado de la atraccion solo puede cambiar cuando no hayan
            // pasajeros y sean 24.00
            if (this.tren.isEmpty()) {
                System.out.println("el tren esta vacio");
            } else {
                System.out.println("el maquinista se prepara para comenzar el viaje");
            }

        } else {
            System.out.println("el maqunista termino su turno");
        }

    }

    public void terminarViaje() throws Exception {

        System.out.println("Se termino el recorrido del tren se permite bajar a los pasajeros");

        int tren = this.tren.size();
        for (int i = 0; i < tren; i++) {

            Object permiso = new Object();
            this.salida.offer(permiso);
        }
        empezarTurno.take();
        llenarBoletos();
    }

    public boolean terminoTurno() {
        return this.parqueCerrado;
    }
}
