
public class Mainn {

    public static void main(String[] args) {

        Premios prem = new Premios();
        Tren tren = new Tren(10);

        for (int i = 0; i < 4; i++) {
            Encargado encargado = new Encargado(prem, "encargado" + i++);
            Thread encar = new Thread(encargado);
            encar.start();
        }

        Maquinista maniquinista = new Maquinista(tren);
        Thread maqui = new Thread(maniquinista);
        maqui.start();

        Parque parque = new Parque(new Comedor(), prem, new RealidadVirtual(), new Teatro(), tren,
                new AutosChocadores(), new MontanaRusa(), new BarcoPirata(), 5);

        for (int j = 0; j < 50; j++) {
            Visitante r = new Visitante(parque, "visitante " + (j + 1));
            Thread visitante = new Thread(r);
            visitante.start();
        }

    }

}
