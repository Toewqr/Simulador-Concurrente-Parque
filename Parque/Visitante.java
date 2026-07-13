public class Visitante implements Runnable {
    private String nombre;
    private Parque parque;

    public Visitante(Parque parque, String nombre) {
        this.nombre = nombre;
        this.parque = parque;
    }

    @Override
    public void run() {
        try {
            this.parque.entrarParque(nombre);
            System.out.println(nombre + "se fue del parque");

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
