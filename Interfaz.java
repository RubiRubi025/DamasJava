package damas;

import java.awt.*;
import javax.swing.*;

public class Interfaz extends JFrame {

    private JPanel panelTablero;
    private JButton[][] botones;
    private JPanel panelFondo;
    private Tablero tableroJuego;
    private int filaOrigen = -1;
    private int columnaOrigen = -1;
    private ImageIcon iconoBlanco;
    private ImageIcon iconoNegro;
    private ConexionServidor servidor;
    private ConexionCliente cliente;
    private boolean miTurno;
    private String miColor;
    private String nombreBlancas;
    private String nombreNegras;
    private JLabel labelNombreBlancas;
    private JLabel labelNombreNegras;
    private JLabel labelTiempo;
    private Timer temporizador;
    private int segundos;
    private JPanel panelTiempo;
    private JLabel labelTurno;
    private JPanel panelSuperior;
    private boolean modoCPU;
    private IA ia;
    private Timer timerCPU;

    public Interfaz(Tablero tableroLogico, ConexionServidor servidor, ConexionCliente cliente, String nombreBlancas, String nombreNegras) { 
        // Cargar iconos
        iconoBlanco = new ImageIcon("damas/assets/fichaBlanca.png");
        iconoNegro = new ImageIcon("damas/assets/fichaNegra.png");

        this.tableroJuego = tableroLogico;
        this.servidor = servidor;
        this.cliente = cliente;
        this.modoCPU = modoCPU;

            // modo cpu

            if (modoCPU) {
    this.miColor = "B";
    this.miTurno = true;
    this.ia = new IA();
    // Inicializar timer para la CPU
    timerCPU = new Timer(500, e -> {
        timerCPU.stop();
        ejecutarCPU();
    });
    timerCPU.setRepeats(false);
}



        
        segundos = 0;
        labelTiempo = new JLabel("00:00");
        temporizador = new Timer(1000, e -> {
            segundos++;
            int mins = segundos / 60;
            int secs = segundos % 60;
            labelTiempo.setText(String.format("%02d:%02d", mins, secs));
        });
        
        temporizador.start();

        // Determinar color y turno inicial
        if (this.servidor != null) {
            this.miTurno = true;
            this.miColor = "B"; // servidor juega con blancas
        } else if (this.cliente != null) {
            this.miTurno = false;
            this.miColor = "N"; // cliente juega con negras
        } else {
            this.miTurno = true;
            this.miColor = "B"; // modo local: empiezan blancas
        }

        // Configuración de la ventana
            setTitle("Juego de damas");
            setSize(600, 600);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setResizable(true);
            setLayout(new BorderLayout());
    
            // Panel superior
            panelSuperior = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
                labelNombreBlancas = new JLabel("Blancas: " + nombreBlancas);
                labelNombreNegras = new JLabel("Negras: " + nombreNegras);
                labelTurno = new JLabel("Turno: Blancas");  
                    add(panelSuperior, BorderLayout.NORTH);
                    panelSuperior.add(labelTiempo);
                    panelSuperior.add(labelTurno);
                    panelSuperior.add(labelNombreNegras);
                    panelSuperior.add(labelNombreBlancas);

        // Panel del tablero (8x8)
        panelTablero = new JPanel(new GridLayout(8, 8));
    panelTablero.setPreferredSize(new Dimension(400, 400));

    panelFondo = new JPanel(new GridBagLayout()) {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            ImageIcon imagenFondo = new ImageIcon("damas/assets/fondo.jpg");
            g.drawImage(imagenFondo.getImage(), 0, 0, getWidth(), getHeight(), this);
        }
    };
    panelFondo.add(panelTablero);
    add(panelFondo, BorderLayout.CENTER);
    segundos = 0;
        // Crear botones (casillas)
        botones = new JButton[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                JButton btn = new JButton();
                final int fila = i;
                final int col = j;

                // Color de fondo inicial
                if ((i + j) % 2 == 1) {
                    btn.setBackground(Color.BLACK);
                } else {
                    btn.setBackground(Color.WHITE);
                }

                // ActionListener para cada casilla
                btn.addActionListener(e -> {
                    if (!miTurno) {
                        return; // no es mi turno
                    }

                    // Si no hay origen seleccionado, intentamos seleccionar una ficha
                    if (filaOrigen == -1) {
                        Ficha ficha = tableroJuego.getFicha(fila, col);
                        if (ficha == null) {
                            JOptionPane.showMessageDialog(null, "No hay ficha en esa casilla", "Selección inválida", JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                        // Verificar que la ficha sea del color del jugador
                        if (!ficha.getColor().equals(miColor)) {
                            JOptionPane.showMessageDialog(null, "No puedes mover una ficha del oponente", "Turno incorrecto", JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                        // Seleccionamos origen
                        filaOrigen = fila;
                        columnaOrigen = col;
                        // Resaltar la casilla de origen
                        botones[filaOrigen][columnaOrigen].setBackground(Color.CYAN);

                        // Mostrar movimientos válidos (radar)
                        for (int r = 0; r < 8; r++) {
                            for (int c = 0; c < 8; c++) {
                                if (tableroJuego.esMovimientoValido(filaOrigen, columnaOrigen, r, c)) {
                                    botones[r][c].setBackground(Color.GREEN);
                                    // Si es captura (distancia 2), marcar la ficha intermedia en rojo
                                    if (Math.abs(r - filaOrigen) == 2) {
                                        int filaPresa = (filaOrigen + r) / 2;
                                        int colPresa = (columnaOrigen + c) / 2;
                                        botones[filaPresa][colPresa].setBackground(Color.RED);
                                    }
                                }
                            }
                        }
                        return; // esperamos el segundo clic (destino)
                    }

                    // Si ya hay origen seleccionado, intentamos mover al destino (fila, col)
                    boolean movimientoValido = tableroJuego.moverFicha(filaOrigen, columnaOrigen, fila, col);
                    if (movimientoValido) {
                        // Notificar al oponente en modo red
                        String mensaje = "MOVER," + filaOrigen + "," + columnaOrigen + "," + fila + "," + col;
                        if (servidor != null) {
                            servidor.enviarMensaje(mensaje);
                            miTurno = false;
                        } else if (cliente != null) {
                            cliente.enviarMensaje(mensaje);
                            miTurno = false;
                        } else {
                            // Modo local: alternar color
                            miColor = miColor.equals("B") ? "N" : "B";
                        }    
                        if(modoCPU) {
                        // Cambiar turno: ahora es turno de la CPU (negras)
                        miTurno = false;
                        // Iniciar el timer para que la CPU "piense"
                        timerCPU.start();
                        }
                        // Actualizar la interfaz

                        actualizarTurno();
                    } 
                    SincronizacionTablero();
                    // Reiniciamos selección (tanto si fue válido como si no)
                    filaOrigen = -1;
                    columnaOrigen = -1;
                });

                panelTablero.add(btn);
                botones[i][j] = btn;
            }
        }

        // Sincronizar el tablero con el estado lógico
        SincronizacionTablero();
        setVisible(true);
    }

public void SincronizacionTablero() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                // Restaurar colores de fondo
                if ((i + j) % 2 == 1) {
                    botones[i][j].setBackground(Color.BLACK);
                } else {
                    botones[i][j].setBackground(Color.WHITE);
                }
                // Colocar iconos según fichas
                Ficha ficha = tableroJuego.getFicha(i, j);
                if (ficha != null) {
                    if (ficha.getColor().equals("B")) {
                        botones[i][j].setIcon(iconoBlanco);
                    } else {
                        botones[i][j].setIcon(iconoNegro);
                    }
                } else {
                    botones[i][j].setIcon(null);
                }
            } 
        } 
        verificarFinDeJuego(); 
    }
public void setNombreBlancas(String nombre) {
    this.nombreBlancas = nombre;
    labelNombreBlancas.setText("Blancas: " + nombre);
}
public void setNombreNegras(String nombre) {
    this.nombreNegras = nombre;
    labelNombreNegras.setText("Negras: " + nombre);
}
public void verificarFinDeJuego() {
        int sobrevivientesBlancas = tableroJuego.contarFichas("B");
        int sobrevivientesNegras = tableroJuego.contarFichas("N");

        // Si las blancas se quedan sin fichas, ganan las negras
        if (sobrevivientesBlancas == 0) {
            miTurno = false; // Bloqueamos el tablero para siempre
            if(temporizador != null) temporizador.stop(); // Detenemos el cronómetro
            
            JOptionPane.showMessageDialog(this, 
                "¡Felicidades " + nombreNegras + "! Las fichas Negras han dominado el tablero y han ganado la partida.", 
                "¡VICTORIA NEGRA!", 
                JOptionPane.INFORMATION_MESSAGE);
        } 
        // Si las negras se quedan sin fichas, ganan las blancas
        else if (sobrevivientesNegras == 0) {
            miTurno = false; // Bloqueamos el tablero para siempre
            if(temporizador != null) temporizador.stop(); // Detenemos el cronómetro
            
            JOptionPane.showMessageDialog(this, 
                "¡Felicidades " + nombreBlancas + "! Las fichas Blancas han dominado el tablero y han ganado la partida.", 
                "¡VICTORIA BLANCA!", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void setMiTurno(boolean turnoActivo) {
        this.miTurno = turnoActivo;
    }
    public void actualizarTurno() {
    String turnoTexto;
    if (miTurno) {
        turnoTexto = miColor.equals("B") ? "Blancas" : "Negras";
    } else {
        turnoTexto = miColor.equals("B") ? "Negras" : "Blancas";
    }
    if (modoCPU) {
        String turno = miTurno ? "Tu turno (Blancas)" : "Turno de la CPU (Negras)";
        labelTurno.setText(turno);
    } else {
        String turno = (miTurno ? miColor : (miColor.equals("B") ? "N" : "B"));
        labelTurno.setText("Turno: " + (turno.equals("B") ? "Blancas" : "Negras"));
    }
    labelTurno.setText("Turno: " + turnoTexto);
}
private void ejecutarCPU() {
    if (!modoCPU) return;
    int[] movimiento = ia.elegirMovimiento(tableroJuego, "N");
    if (movimiento == null) {
        JOptionPane.showMessageDialog(this, "La CPU no tiene movimientos. ¡Has ganado!");
        // Aquí se podría finalizar la partida
        return;
    }
    int filaOri = movimiento[0], colOri = movimiento[1];
    int filaDes = movimiento[2], colDes = movimiento[3];
    boolean exito = tableroJuego.moverFicha(filaOri, colOri, filaDes, colDes);
    if (exito) {
        // Actualizar tablero
        SincronizacionTablero();
        // Cambiar turno a jugador
        miTurno = true;
        actualizarTurno();
        // Verificar si el jugador tiene movimientos, si no, fin de partida
        // (opcional)
    } else {
        // Si falla (no debería), intentar de nuevo
        ejecutarCPU();
    }
}
}