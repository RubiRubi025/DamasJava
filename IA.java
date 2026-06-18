package damas;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class IA {

    private Random random = new Random();

    // Devuelve un movimiento elegido por la IA, o null si no hay movimiento posible
    public int[] elegirMovimiento(Tablero tablero, String color) {
        List<int[]> movimientos = obtenerTodosLosMovimientos(tablero, color);
        if (movimientos.isEmpty()) return null;
        // Priorizar capturas (distancia 2)
        List<int[]> capturas = new ArrayList<>();
        List<int[]> simples = new ArrayList<>();
        for (int[] mov : movimientos) {
            int filaOri = mov[0], colOri = mov[1], filaDes = mov[2], colDes = mov[3];
            if (Math.abs(filaDes - filaOri) == 2) {
                capturas.add(mov);
            } else {
                simples.add(mov);
            }
        }
        List<int[]> seleccion = capturas.isEmpty() ? simples : capturas;
        return seleccion.get(random.nextInt(seleccion.size()));
    }

    private List<int[]> obtenerTodosLosMovimientos(Tablero tablero, String color) {
        List<int[]> movimientos = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Ficha f = tablero.getFicha(i, j);
                if (f != null && f.getColor().equals(color)) {
                    // Probar todos los destinos posibles
                    for (int di = -2; di <= 2; di++) {
                        for (int dj = -2; dj <= 2; dj++) {
                            if (Math.abs(di) != Math.abs(dj) || (di == 0 && dj == 0)) continue;
                            int filaDes = i + di;
                            int colDes = j + dj;
                            if (filaDes >= 0 && filaDes < 8 && colDes >= 0 && colDes < 8) {
                                if (tablero.esMovimientoValido(i, j, filaDes, colDes)) {
                                    movimientos.add(new int[]{i, j, filaDes, colDes});
                                }
                            }
                        }
                    }
                }
            }
        }
        return movimientos;
    }
}