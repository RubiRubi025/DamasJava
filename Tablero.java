package damas;

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
            System.out.println("Aqui no hay fichas");
            return false;
            }
            if(tablero[filaDestino][columnaDestino] != null){
                System.out.println("Aqui ya hay una ficha");
                return false;
            }
            if(Math.abs(filaDestino - filaOrigen) != Math.abs(columnaDestino - columnaOrigen)){
                System.out.println("Movimiento no válido");
                return false;
            }
            if(distancia == 1){     
                if(tablero[filaOrigen][columnaOrigen].isReina() == false && tablero[filaOrigen][columnaOrigen].getColor() == "B" && filaDestino < filaOrigen){
                        System.out.println("La ficha debe avanzar hacia otro lado");
                        return false;
                    }
                        if(tablero[filaOrigen][columnaOrigen].isReina() == false && tablero[filaOrigen][columnaOrigen].getColor() == "N" && filaDestino > filaOrigen){
                        System.out.println("La ficha debe avanzar hacia otro lado");
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
                            System.out.println("No puedes capturar una ficha de tu propio bando");
                            return false;
                        }
            } else {
                System.out.println("No hay ficha para capturar");
                return false;
            }        
            }
            if (distancia >= 3 || distancia < 1) {
            System.out.println("Distancia de movimiento no permitida");
            return false;
        }
            tablero[filaDestino][columnaDestino] = tablero[filaOrigen][columnaOrigen];
            tablero[filaOrigen][columnaOrigen] = null;
            System.out.println("Movimiento exitoso");
            return true;
        }
    }

