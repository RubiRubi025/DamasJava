package damas;

import java.awt.*;
import java.awt.event.*;
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
    private JButton botonVolver;

    public Interfaz(Tablero tableroLogico, ConexionServidor servidor, ConexionCliente cliente, String nombreBlancas, String nombreNegras) { 
        cargarIconosPersonalizados();

        this.tableroJuego = tableroLogico;
        this.servidor = servidor;
        this.cliente = cliente;
        
        this.modoCPU = nombreNegras.equals("CPU");
        
        if (modoCPU) {
            this.miColor = "B";
            this.miTurno = true;
            this.ia = new IA();
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

        if (this.servidor != null) {
            this.miTurno = true;
            this.miColor = "B";
        } else if (this.cliente != null) {
            this.miTurno = false;
            this.miColor = "N";
        } else {
            this.miTurno = true;
            this.miColor = "B";
        }

        setTitle("Juego de damas - Digital Shield");
        setSize(700, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        setLayout(new BorderLayout());

        panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBackground(new Color(15, 23, 42));
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JPanel panelInfo = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        panelInfo.setOpaque(false);
        
        labelNombreBlancas = new JLabel("⚪ Blancas: " + nombreBlancas);
        labelNombreBlancas.setForeground(Color.WHITE);
        labelNombreBlancas.setFont(new Font("Courier New", Font.BOLD, 14));
        
        labelNombreNegras = new JLabel("⚫ Negras: " + nombreNegras);
        labelNombreNegras.setForeground(Color.WHITE);
        labelNombreNegras.setFont(new Font("Courier New", Font.BOLD, 14));
        
        labelTurno = new JLabel("Turno: Blancas");
        labelTurno.setForeground(Color.YELLOW);
        labelTurno.setFont(new Font("Courier New", Font.BOLD, 16));
        
        panelInfo.add(labelTurno);
        panelInfo.add(labelNombreBlancas);
        panelInfo.add(labelNombreNegras);

        JPanel panelDerecho = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 5));
        panelDerecho.setOpaque(false);
        
        labelTiempo = new JLabel("00:00");
        labelTiempo.setForeground(Color.CYAN);
        labelTiempo.setFont(new Font("Courier New", Font.BOLD, 16));
        
        botonVolver = new JButton("🏠 Menú");
        botonVolver.setBackground(new Color(220, 53, 69));
        botonVolver.setForeground(Color.WHITE);
        botonVolver.setFont(new Font("Courier New", Font.BOLD, 14));
        botonVolver.setFocusPainted(false);
        botonVolver.setBorderPainted(false);
        botonVolver.setOpaque(true);
        botonVolver.setContentAreaFilled(true);
        botonVolver.setPreferredSize(new Dimension(100, 35));
        botonVolver.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        botonVolver.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                botonVolver.setBackground(new Color(200, 35, 50));
            }
            public void mouseExited(MouseEvent evt) {
                botonVolver.setBackground(new Color(220, 53, 69));
            }
        });
        
        botonVolver.addActionListener(e -> {
            if (temporizador != null) temporizador.stop();
            if (timerCPU != null) timerCPU.stop();
            dispose();
            SwingUtilities.invokeLater(() -> new MenuPrincipal());
        });
        
        panelDerecho.add(labelTiempo);
        panelDerecho.add(botonVolver);
        
        panelSuperior.add(panelInfo, BorderLayout.WEST);
        panelSuperior.add(panelDerecho, BorderLayout.EAST);
        add(panelSuperior, BorderLayout.NORTH);

        panelTablero = new JPanel(new GridLayout(8, 8));
        panelTablero.setPreferredSize(new Dimension(400, 400));

        panelFondo = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    ImageIcon imagenFondo = new ImageIcon(MenuPrincipal.rutaFondoElegido);
                    g.drawImage(imagenFondo.getImage(), 0, 0, getWidth(), getHeight(), this);
                } catch (Exception e) {
                    g.setColor(new Color(15, 23, 42));
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        panelFondo.add(panelTablero);
        add(panelFondo, BorderLayout.CENTER);

        botones = new JButton[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                JButton btn = new JButton();
                final int fila = i;
                final int col = j;

                if ((i + j) % 2 == 1) {
                    btn.setBackground(Color.BLACK);
                } else {
                    btn.setBackground(Color.WHITE);
                }

                btn.addActionListener(e -> {
                    if (!miTurno) return;

                    // ¡CORRECCIÓN!: NUEVO CANDADO PARA EL HOST
                    if (servidor != null && !servidor.isConectado()) {
                        JOptionPane.showMessageDialog(null, "Espera a que el Jugador 2 se conecte a la partida.", "Esperando Oponente", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    if (filaOrigen == -1) {
                        Ficha ficha = tableroJuego.getFicha(fila, col);
                        if (ficha == null) {
                            JOptionPane.showMessageDialog(null, "No hay ficha en esa casilla", "Selección inválida", JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                        if (!ficha.getColor().equals(miColor)) {
                            JOptionPane.showMessageDialog(null, "No puedes mover una ficha del oponente", "Turno incorrecto", JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                        filaOrigen = fila;
                        columnaOrigen = col;
                        botones[filaOrigen][columnaOrigen].setBackground(Color.CYAN);

                        for (int r = 0; r < 8; r++) {
                            for (int c = 0; c < 8; c++) {
                                if (tableroJuego.esMovimientoValido(filaOrigen, columnaOrigen, r, c)) {
                                    botones[r][c].setBackground(Color.GREEN);
                                    if (Math.abs(r - filaOrigen) == 2) {
                                        int filaPresa = (filaOrigen + r) / 2;
                                        int colPresa = (columnaOrigen + c) / 2;
                                        botones[filaPresa][colPresa].setBackground(Color.RED);
                                    }
                                }
                            }
                        }
                        return;
                    }

                    boolean movimientoValido = tableroJuego.moverFicha(filaOrigen, columnaOrigen, fila, col);
                    if (movimientoValido) {
                        String mensaje = "MOVER," + filaOrigen + "," + columnaOrigen + "," + fila + "," + col;
                        if (servidor != null) {
                            servidor.enviarMensaje(mensaje);
                            miTurno = false;
                            actualizarTurno();
                        } else if (cliente != null) {
                            cliente.enviarMensaje(mensaje);
                            miTurno = false;
                            actualizarTurno();
                        } else if (modoCPU) {
                            miTurno = false;
                            timerCPU.start();
                            actualizarTurno();
                        } else {
                            miColor = miColor.equals("B") ? "N" : "B";
                            actualizarTurno();
                        }
                    }
                    SincronizacionTablero();
                    filaOrigen = -1;
                    columnaOrigen = -1;
                });

                panelTablero.add(btn);
                botones[i][j] = btn;
            }
        }

        SincronizacionTablero();
        setVisible(true);
    }

    private void cargarIconosPersonalizados() {
        try {
            ImageIcon iconoBlancoTemp = new ImageIcon(MenuPrincipal.rutaFichaLocal);
            Image imagenBlanca = iconoBlancoTemp.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
            iconoBlanco = new ImageIcon(imagenBlanca);
            
            ImageIcon iconoNegroTemp = new ImageIcon(MenuPrincipal.rutaFichaRival);
            Image imagenNegra = iconoNegroTemp.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
            iconoNegro = new ImageIcon(imagenNegra);
        } catch (Exception e) {
            iconoBlanco = new ImageIcon("damas/assets/fichaBlanca.png");
            iconoNegro = new ImageIcon("damas/assets/fichaNegra.png");
            System.out.println("Error cargando iconos personalizados: " + e.getMessage());
        }
    }

    public void SincronizacionTablero() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if ((i + j) % 2 == 1) {
                    botones[i][j].setBackground(Color.BLACK);
                } else {
                    botones[i][j].setBackground(Color.WHITE);
                }
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
        labelNombreBlancas.setText("⚪ Blancas: " + nombre);
    }

    public void setNombreNegras(String nombre) {
        this.nombreNegras = nombre;
        labelNombreNegras.setText("⚫ Negras: " + nombre);
    }

    public void verificarFinDeJuego() {
        int sobrevivientesBlancas = tableroJuego.contarFichas("B");
        int sobrevivientesNegras = tableroJuego.contarFichas("N");

        if (sobrevivientesBlancas == 0) {
            miTurno = false;
            if(temporizador != null) temporizador.stop();
            
            int opcion = JOptionPane.showConfirmDialog(this, 
                "¡Felicidades " + nombreNegras + "! Las fichas Negras han ganado.\n¿Deseas volver al menú principal?", 
                "¡VICTORIA NEGRA!", 
                JOptionPane.YES_NO_OPTION);
            if (opcion == JOptionPane.YES_OPTION) {
                volverAlMenu();
            }
        } else if (sobrevivientesNegras == 0) {
            miTurno = false;
            if(temporizador != null) temporizador.stop();
            
            int opcion = JOptionPane.showConfirmDialog(this, 
                "¡Felicidades " + nombreBlancas + "! Las fichas Blancas han ganado.\n¿Deseas volver al menú principal?", 
                "¡VICTORIA BLANCA!", 
                JOptionPane.YES_NO_OPTION);
            if (opcion == JOptionPane.YES_OPTION) {
                volverAlMenu();
            }
        }
    }

    public void setMiTurno(boolean turnoActivo) {
        this.miTurno = turnoActivo;
    }

    public void actualizarTurno() {
        String turnoTexto;
        if (modoCPU) {
            String turno = miTurno ? "Tu turno (Blancas)" : "Turno de la CPU (Negras)";
            labelTurno.setText(turno);
            return;
        }
        
        if (miTurno) {
            turnoTexto = miColor.equals("B") ? "Blancas" : "Negras";
        } else {
            turnoTexto = miColor.equals("B") ? "Negras" : "Blancas";
        }
        labelTurno.setText("Turno: " + turnoTexto);
    }

    private void ejecutarCPU() {
        if (!modoCPU) return;
        int[] movimiento = ia.elegirMovimiento(tableroJuego, "N");
        if (movimiento == null) {
            JOptionPane.showMessageDialog(this, "La CPU no tiene movimientos. ¡Has ganado!");
            return;
        }
        int filaOri = movimiento[0], colOri = movimiento[1];
        int filaDes = movimiento[2], colDes = movimiento[3];
        boolean exito = tableroJuego.moverFicha(filaOri, colOri, filaDes, colDes);
        if (exito) {
            SincronizacionTablero();
            miTurno = true;
            actualizarTurno();
        } else {
            ejecutarCPU();
        }
    }

    private void volverAlMenu() {
        if (temporizador != null) temporizador.stop();
        if (timerCPU != null) timerCPU.stop();
        dispose();
        SwingUtilities.invokeLater(() -> new MenuPrincipal());
    }
}