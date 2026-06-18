package damas;
import java.awt.*;
import javax.swing.*;

public class Interfaz extends JFrame{

    private JPanel panelTablero;
    private JButton[][] botones;
    private JPanel panelFondo;
    private JPanel panelSuperior;
    private JLabel Etiqueta;
    private Tablero tableroJuego;
    private int filaOrigen = -1;
    private int columnaOrigen = -1;
    private ImageIcon iconoBlanco;
    private ImageIcon iconoNegro;
    private ConexionServidor servidor;
    private ConexionCliente cliente;
    private boolean miTurno;
    private String turnoColorLocal = "B";

    public Interfaz(Tablero TableroLogico, ConexionServidor servidor, ConexionCliente cliente) {{
        
        iconoBlanco = new ImageIcon("damas/assets/fichaBlanca.png");
        iconoNegro = new ImageIcon("damas/assets/fichaNegra.png");
        this.tableroJuego = TableroLogico;
        this.servidor = servidor;
        this.cliente = cliente;

        if (this.servidor != null) {
            this.miTurno = true;
        } else if (this.cliente != null) {
            this.miTurno = false;
        } else {
            this.miTurno = true;
        }
        // 1. Primero instanciamos el tablero y le damos sus reglas
        panelTablero = new JPanel(new GridLayout(8, 8));
        panelTablero.setPreferredSize(new Dimension(400, 400));

        // 2. Instanciamos el fondo inyectando la imagen
        panelFondo = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon imagenFondo = new ImageIcon("damas/assets/fondo.jpg"); 
                g.drawImage(imagenFondo.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };

        // 3. Configuramos la ventana madre
        setTitle("Juego de damas");
        setSize(600, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);

        // 4. Armamos la Matrioshka
        panelFondo.add(panelTablero);
        add(panelFondo);

        // 5. Fabricamos los botones y pintamos el piso
        botones = new JButton[8][8];
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                botones[i][j] = new JButton();
                final int fila = i;
                final int col = j;
                botones[i][j].addActionListener(e -> {
                    if(miTurno == false){
                        return;
                    }
                            if (filaOrigen == -1) {
                            filaOrigen = fila;
                            columnaOrigen = col;
                            
                            // 1. Iluminamos tu ficha (Azul Cyan)
                            botones[filaOrigen][columnaOrigen].setBackground(Color.CYAN); 

                            // 2. Encendemos el Radar Mejorado
                            for(int r = 0; r < 8; r++){
                                for(int c = 0; c < 8; c++){
                                    
                                    if(tableroJuego.esMovimientoValido(filaOrigen, columnaOrigen, r, c) == true){
                                        
                                        // Pintamos la casilla vacía de aterrizaje en verde
                                        botones[r][c].setBackground(Color.GREEN);

                                        // Si la distancia es 2, localizamos al enemigo y lo pintamos de rojo
                                        if (Math.abs(r - filaOrigen) == 2) {
                                            int filaPresa = (filaOrigen + r) / 2;
                                            int colPresa = (columnaOrigen + c) / 2;
                                            botones[filaPresa][colPresa].setBackground(Color.RED);
                                        }
                                    }
                                }
                            }
                        } else {
                        boolean movimientoValido = tableroJuego.moverFicha(filaOrigen, columnaOrigen, fila, col);
                        if(movimientoValido == true){
                                String mensaje = "MOVER," + filaOrigen + "," + columnaOrigen + "," + fila + "," + col;
                                
                                // Si hay servidor, enviamos el mensaje y BLOQUEAMOS el turno
                                if(servidor != null){
                                    servidor.enviarMensaje(mensaje);
                                    miTurno = false; 
                                } 
                                // Si hay cliente, enviamos el mensaje y BLOQUEAMOS el turno
                                else if(cliente != null){
                                    cliente.enviarMensaje(mensaje);
                                    miTurno = false;
                                }
                                // Si ambos son null (Local), el turno NUNCA se hace false.

                                // La barredora se ejecuta siempre
                                SincronizacionTablero();
                            }
                                    
                            filaOrigen = -1;
                            columnaOrigen = -1;       
                    }
                    

                });
                if((i + j) % 2 == 1){
                    botones[i][j].setBackground(Color.BLACK);
                } else {
                    botones[i][j].setBackground(Color.WHITE);
                }
                panelTablero.add(botones[i][j]);
            }

    }
            SincronizacionTablero();
            setVisible(true);
    }
}

    public void SincronizacionTablero(){


        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                if((i + j) % 2 == 1){
                botones[i][j].setBackground(Color.BLACK);
                } else {
                    botones[i][j].setBackground(Color.WHITE);
                }
                Ficha fichaActual = tableroJuego.getFicha(i, j);
                if(fichaActual != null){
                    if(fichaActual.getColor().equals("B")){
                        botones[i][j].setIcon(iconoBlanco);
                    }else{
                        botones[i][j].setIcon(iconoNegro);
                    }
            }
            else{
                botones[i][j].setIcon(null);
            }
        }
    }
    }
    public void setMiTurno(boolean turnoActivo) {
    this.miTurno = turnoActivo;
}
}
