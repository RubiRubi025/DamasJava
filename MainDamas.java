package damas;


public class MainDamas {
    public static void main(String[] args) {
        Tablero tablero = new Tablero();
        tablero.ImprimirTablero();
        tablero.moverFicha(2, 1, 3, 2);
        tablero.ImprimirTablero();
        tablero.moverFicha(5,0,4,1);
        tablero.ImprimirTablero();
        tablero.moverFicha(3,2,5,0);
        tablero.ImprimirTablero();
    }

}
