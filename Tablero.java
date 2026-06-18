package damas;
import javax.swing.JOptionPane;

public class Tablero {
    private Ficha[][] tablero;

        public Tablero(){
            tablero = new Ficha[8][8];
            for(int i = 0; i < 8; i++){
                for(int j = 0; j < 8; j++){
                    if((i + j) % 2 == 1){
                        if(i < 3){
                            tablero[i][j] = new Ficha("B", i, j);
                        } else {
                            if(i > 4){
                                tablero[i][j] = new Ficha("N", i, j);
                            }
                        }
                    }
                }
            }
    }
        public void ImprimirTablero(){
            for(int i = 0; i < 8; i++){
                for(int j = 0; j < 8; j++){
                    if(tablero[i][j] == null){
                        System.out.print("[]");
                    }else{
                        System.out.print("[" + tablero[i][j].getColor() + "]");
                    }
                }
                System.out.println();
            }
        }

                public boolean moverFicha(int filaOrigen, int columnaOrigen, int filaDestino, int columnaDestino){
                    int distancia = Math.abs(filaDestino - filaOrigen);
                    if(tablero[filaOrigen][columnaOrigen] == null){
                    JOptionPane.showMessageDialog(null, "Aquí no hay fichas para mover", "Movimiento Inválido", JOptionPane.ERROR_MESSAGE);
                    return false;
                    }
                    if(tablero[filaDestino][columnaDestino] != null){
                        JOptionPane.showMessageDialog(null, "Aquí ya hay una ficha, selecciona una casilla vailda", "Movimiento Inválido", JOptionPane.ERROR_MESSAGE);
                        return false;
                    }
                    if(Math.abs(filaDestino - filaOrigen) != Math.abs(columnaDestino - columnaOrigen)){
                        JOptionPane.showMessageDialog(null, "Error en la distancia de movimiento", "Movimiento Inválido", JOptionPane.ERROR_MESSAGE);
                        return false;
                    }
                    if(distancia == 1){     
                        if(tablero[filaOrigen][columnaOrigen].isReina() == false && tablero[filaOrigen][columnaOrigen].getColor() == "B" && filaDestino < filaOrigen){
                                JOptionPane.showMessageDialog(null, "La ficha debe avanzar hacia otro lado", "Movimiento Inválido", JOptionPane.ERROR_MESSAGE);
                                return false;
                            }
                                if(tablero[filaOrigen][columnaOrigen].isReina() == false && tablero[filaOrigen][columnaOrigen].getColor() == "N" && filaDestino > filaOrigen){
                                JOptionPane.showMessageDialog(null, "La ficha debe avanzar hacia otro lado", "Movimiento Inválido", JOptionPane.ERROR_MESSAGE);
                                return false;
                            }
                    }
                    if(distancia == 2){
                        int filaIntermedia = (filaOrigen + filaDestino) / 2;
                        int columnaIntermedia = (columnaOrigen + columnaDestino) / 2;
                            if (tablero[filaIntermedia][columnaIntermedia] != null) {
                                String colorAtacante = tablero[filaOrigen][columnaOrigen].getColor();
                                String colorIntermedio = tablero[filaIntermedia][columnaIntermedia].getColor();
                            
                                if (!colorAtacante.equals(colorIntermedio)) {
                                tablero[filaIntermedia][columnaIntermedia] = null;
                                } else {
                                    JOptionPane.showMessageDialog(null, "No puedes capturar una ficha de tu propio bando", "Movimiento Inválido", JOptionPane.ERROR_MESSAGE);
                                    return false;
                                }
                    } else {
                        JOptionPane.showMessageDialog(null, "No hay ficha para capturar", "Movimiento Inválido", JOptionPane.ERROR_MESSAGE);
                        return false;
                    }        
                    }
                    if (distancia >= 3 || distancia < 1) {
                    JOptionPane.showMessageDialog(null, "Distancia de movimiento no permitida", "Movimiento Inválido", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                    tablero[filaDestino][columnaDestino] = tablero[filaOrigen][columnaOrigen];
                    tablero[filaOrigen][columnaOrigen] = null;
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

            if (distancia == 1) {
                if (tablero[filaOrigen][columnaOrigen].isReina() == false && tablero[filaOrigen][columnaOrigen].getColor().equals("B") && filaDestino < filaOrigen) {
                    return false;
                }
                if (tablero[filaOrigen][columnaOrigen].isReina() == false && tablero[filaOrigen][columnaOrigen].getColor().equals("N") && filaDestino > filaOrigen) {
                    return false;
                }
                return true; 
            }

            if (distancia == 2) {
                int filaIntermedia = (filaOrigen + filaDestino) / 2;
                int columnaIntermedia = (columnaOrigen + columnaDestino) / 2;
                
                if (tablero[filaIntermedia][columnaIntermedia] != null) {
                    String colorAtacante = tablero[filaOrigen][columnaOrigen].getColor();
                    String colorIntermedio = tablero[filaIntermedia][columnaIntermedia].getColor();
                    
                    if (colorAtacante.equals(colorIntermedio)) {
                        return false;
                    } else {
                        return true;
                    }
                } else {
                    return false; 
                }        
            }

            return false;
        }
    }

