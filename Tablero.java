package damas;

import javax.swing.JOptionPane;

public class Tablero {
    private Ficha[][] tablero;

    public Tablero() {
        tablero = new Ficha[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if ((i + j) % 2 == 1) {
                    if (i < 3) {
                        tablero[i][j] = new Ficha("B", i, j);
                    } else if (i > 4) {
                        tablero[i][j] = new Ficha("N", i, j);
                    }
                }
            }
        }
    }

    public void ImprimirTablero() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (tablero[i][j] == null) {
                    System.out.print("[]");
                } else {
                    System.out.print("[" + tablero[i][j].getColor() + "]");
                }
            }
            System.out.println();
        }
    }

    public boolean moverFicha(int filaOrigen, int columnaOrigen, int filaDestino, int columnaDestino) {
        int distancia = Math.abs(filaDestino - filaOrigen);

        // Validaciones básicas
        if (tablero[filaOrigen][columnaOrigen] == null) {
            JOptionPane.showMessageDialog(null, "Aquí no hay fichas para mover", "Movimiento Inválido", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (tablero[filaDestino][columnaDestino] != null) {
            JOptionPane.showMessageDialog(null, "La casilla destino ya está ocupada", "Movimiento Inválido", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (Math.abs(filaDestino - filaOrigen) != Math.abs(columnaDestino - columnaOrigen)) {
            JOptionPane.showMessageDialog(null, "El movimiento debe ser en diagonal", "Movimiento Inválido", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (distancia != 1 && distancia != 2) {
            JOptionPane.showMessageDialog(null, "Solo se permite movimiento de 1 o 2 casillas (captura)", "Movimiento Inválido", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validar dirección para peones (no reinas)
        Ficha ficha = tablero[filaOrigen][columnaOrigen];
        if (!ficha.isReina()) {
            if (ficha.getColor().equals("B") && filaDestino < filaOrigen) {
                JOptionPane.showMessageDialog(null, "Las blancas solo pueden avanzar hacia abajo (fila mayor)", "Movimiento Inválido", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            if (ficha.getColor().equals("N") && filaDestino > filaOrigen) {
                JOptionPane.showMessageDialog(null, "Las negras solo pueden avanzar hacia arriba (fila menor)", "Movimiento Inválido", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }

        // Captura (distancia 2)
        if (distancia == 2) {
            int filaIntermedia = (filaOrigen + filaDestino) / 2;
            int columnaIntermedia = (columnaOrigen + columnaDestino) / 2;
            if (tablero[filaIntermedia][columnaIntermedia] == null) {
                JOptionPane.showMessageDialog(null, "No hay ficha para capturar", "Movimiento Inválido", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            String colorAtacante = ficha.getColor();
            String colorIntermedio = tablero[filaIntermedia][columnaIntermedia].getColor();
            if (colorAtacante.equals(colorIntermedio)) {
                JOptionPane.showMessageDialog(null, "No puedes capturar una ficha de tu propio bando", "Movimiento Inválido", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            // Eliminar la ficha capturada
            tablero[filaIntermedia][columnaIntermedia] = null;
        }

        // Realizar el movimiento
        tablero[filaDestino][columnaDestino] = ficha;
        tablero[filaOrigen][columnaOrigen] = null;

        // Actualizar coordenadas de la ficha
        ficha.setFila(filaDestino);
        ficha.setColumna(columnaDestino);

        // Promoción a reina
        if (!ficha.isReina()) {
            if (ficha.getColor().equals("B") && filaDestino == 7) {
                ficha.setEsReina(true);
                JOptionPane.showMessageDialog(null, "¡Ficha blanca coronada como reina!");
            } else if (ficha.getColor().equals("N") && filaDestino == 0) {
                ficha.setEsReina(true);
                JOptionPane.showMessageDialog(null, "¡Ficha negra coronada como reina!");
            }
        }

        return true;
    }

    public Ficha getFicha(int fila, int columna) {
        return tablero[fila][columna];
    }

    public boolean esMovimientoValido(int filaOrigen, int columnaOrigen, int filaDestino, int columnaDestino) {
        int distancia = Math.abs(filaDestino - filaOrigen);

        if (tablero[filaOrigen][columnaOrigen] == null) return false;
        if (tablero[filaDestino][columnaDestino] != null) return false;
        if (Math.abs(filaDestino - filaOrigen) != Math.abs(columnaDestino - columnaOrigen)) return false;
        if (distancia != 1 && distancia != 2) return false;

        Ficha ficha = tablero[filaOrigen][columnaOrigen];
        if (!ficha.isReina()) {
            if (ficha.getColor().equals("B") && filaDestino < filaOrigen) return false;
            if (ficha.getColor().equals("N") && filaDestino > filaOrigen) return false;
        }

        if (distancia == 2) {
            int filaIntermedia = (filaOrigen + filaDestino) / 2;
            int columnaIntermedia = (columnaOrigen + columnaDestino) / 2;
            if (tablero[filaIntermedia][columnaIntermedia] == null) return false;
            String colorAtacante = ficha.getColor();
            String colorIntermedio = tablero[filaIntermedia][columnaIntermedia].getColor();
            if (colorAtacante.equals(colorIntermedio)) return false;
        }

        return true;
    }
    public int contarFichas(String colorBuscado) {
        int contador = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Ficha fichaActual = tablero[i][j];
                // Si hay una ficha y es del color que estamos buscando, sumamos 1
                if (fichaActual != null && fichaActual.getColor().equals(colorBuscado)) {
                    contador++;
                }
            }
        }
        return contador;
    }
}