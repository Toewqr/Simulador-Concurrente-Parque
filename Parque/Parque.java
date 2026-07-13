import java.time.LocalTime;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class Parque {
  private Comedor comedor;
  private Premios premios;
  private RealidadVirtual realidadVirtual;
  private Teatro teatro;
  private Semaphore molinetes;
  private Tren tren;
  private AutosChocadores autitos;
  private MontanaRusa ensaladarusa;
  private BarcoPirata barco;

  private boolean abierto18;
  private boolean abierto19;

  private Semaphore mutex;

  private final ScheduledExecutorService horario = Executors.newScheduledThreadPool(1);
  private LocalTime horaActual = LocalTime.of(9, 0);

  public Parque(Comedor comedor, Premios premios, RealidadVirtual realidadVirtual, Teatro teatro, Tren tren,
      AutosChocadores autitos, MontanaRusa ensaladarusa, BarcoPirata barco, int capMolinetes) {
    this.comedor = comedor;
    this.premios = premios;
    this.realidadVirtual = realidadVirtual;
    this.teatro = teatro;
    this.tren = tren;
    this.autitos = autitos;
    this.ensaladarusa = ensaladarusa;
    this.barco = barco;

    this.mutex = new Semaphore(1);
    this.molinetes = new Semaphore(capMolinetes);
    abierto18 = true;
    abierto19 = true;

    this.iniciarHorario();
  }

  public void entrarParque(String visitante) throws Exception {

    molinetes.acquire();
    mutex.acquire();

    if (abierto18) {
      if (new Random().nextInt(1) + 1 == 1) {
        molinetes.release();
        eleccion(visitante);
      } else {
        molinetes.release();
        System.out.println("se fue al shopping ");
        mutex.release();
      }
    } else {
      System.out.println(" cerro el parque");

      mutex.release();
    }
  }

  public void eleccion(String visitante) throws Exception {
    while (abierto19) {
      int eleccion = new Random().nextInt(1, 9);
      switch (eleccion) {
        case 1:
          mutex.release();
          this.subirseMontanaRusa(visitante);

          break;
        case 2:
          mutex.release();
          this.entrarRealidadVirtual(visitante);
          break;
        case 3:
          mutex.release();
          this.PremiosParque(visitante);
          break;
        case 4:
          mutex.release();
          this.TrenTuristico(visitante);
          break;
        case 5:
          mutex.release();
          EntrarTeatro(visitante);
          break;
        case 6:
          mutex.release();
          this.entrarAutitosChocadores(visitante);
          break;
        case 7:
          mutex.release();
          this.entrarBarcoPirata(visitante);
          break;
        case 8:
          mutex.release();
          this.entrarComedor(visitante);
          break;

        default:
          System.out.println(visitante + "recorre el parque");
          break;

      }
      mutex.acquire();

      // se elije al azar a que atraccion va a entrar el visitante

    }
    mutex.release();
    System.out.println("El parque esta cerrado ");
  }

  public void entrarAutitosChocadores(String v) throws Exception {
    this.autitos.subirseAutitosChocadores(v);

  }

  public void TrenTuristico(String visitante) throws Exception {
    this.tren.comprarTicket(visitante);
    this.tren.entrarTren(visitante);
    this.tren.salirTren(visitante);

  }

  public void entrarBarcoPirata(String v) throws Exception {
    barco.embarcar(v);
    barco.desembarcar(v);
  }

  public void entrarComedor(String v) throws Exception {
    comedor.entrarComedor(v);
    comedor.salirComedor(v);
  }

  public void PremiosParque(String v) throws Exception {
    premios.jugarJuego(v);
  }

  public void EntrarTeatro(String v) throws Exception {
    this.teatro.armarGrupo(v);
    this.teatro.entrarTeatro(v);
    this.teatro.salirTeatro(v);

  }

  public void entrarRealidadVirtual(String visitante) throws Exception {
    realidadVirtual.entrarRealidadVirtual(visitante);
    Thread.sleep(new Random().nextInt(2000, 3000));
    realidadVirtual.dejarEquipoCompleto(visitante);
  }

  public void subirseMontanaRusa(String visitante) throws Exception {
    this.ensaladarusa.subirseMontanaRusa(visitante);
  }

  public void cerrarParque() throws Exception {

    abierto18 = false;

  }

  public void cerrarAtracciones() throws Exception {

    abierto19 = false;

  }

  public void iniciarHorario() {
    horario.scheduleAtFixedRate(() -> {
      horaActual = horaActual.plusMinutes(30);
      System.out.println("🕒 Hora actual: " + horaActual);
      if (horaActual.equals(LocalTime.of(18, 0))) {

        try {
          cerrarParque();
        } catch (Exception e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
      if (horaActual.equals(LocalTime.of(19, 0))) {
        try {
          cerrarAtracciones();
        } catch (Exception e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
      if (horaActual.equals(LocalTime.of(23, 0))) {
        System.out.println("El parque ha cerrado completamente.");
        horario.shutdown();
        System.exit(0); // Terminar simulación
      }
    }, 0, 5, TimeUnit.SECONDS); // Avanza 30 minutos cada 5 segundos reales
  }

}
