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
    private String miColor; // "B" para blancas, "N" para negras

    public Interfaz(Tablero tableroLogico, ConexionServidor servidor, ConexionCliente cliente) {
        // Cargar iconos
        iconoBlanco = new ImageIcon("damas/assets/fichaBlanca.png");
        iconoNegro = new ImageIcon("damas/assets/fichaNegra.png");

        this.tableroJuego = tableroLogico;
        this.servidor = servidor;
        this.cliente = cliente;

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

        // Panel del tablero (8x8)
        panelTablero = new JPanel(new GridLayout(8, 8));
        panelTablero.setPreferredSize(new Dimension(400, 400));

        // Panel de fondo con imagen
        panelFondo = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon imagenFondo = new ImageIcon("damas/assets/fondo.jpg");
                g.drawImage(imagenFondo.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        panelFondo.add(panelTablero);
        add(panelFondo);

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
                        // Actualizar la interfaz
                        SincronizacionTablero();
                    } else {
                        // Si el movimiento no es válido, mostramos mensaje (ya lo muestra moverFicha)
                        // y reiniciamos la selección
                    }
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
    }

    public void setMiTurno(boolean turnoActivo) {
        this.miTurno = turnoActivo;
    }
}