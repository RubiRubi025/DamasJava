package damas;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class MenuPrincipal {
    
    JFrame menuPrincipal;
    JPanel panelMenuPrincipal;
    
    public static String rutaFondoElegido = "damas/assets/fondo1.jpg";
    public static String rutaFichaLocal = "damas/assets/fichaBlanca.png";
    public static String rutaFichaRival = "damas/assets/fichaNegra.png";
    
    JLabel titulo;
    JButton botonHost;
    JButton botonClient;
    JButton botonLocal;
    JButton botonConfig;
    
    GestorSonido reproductor;
    private boolean musicaActivada = true;
    private String nombreFondoSeleccionado = "Bosque";
    private String nombreFichasSeleccionado = "Clásicas";
    
    public MenuPrincipal() {
        reproductor = new GestorSonido();
        reproductor.reproducirMusicaFondo();
        
        menuPrincipal = new JFrame("Digital Shield - Juego de Damas");
        menuPrincipal.setSize(500, 680);
        menuPrincipal.setLocationRelativeTo(null);
        menuPrincipal.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        menuPrincipal.setResizable(false);
        
        panelMenuPrincipal = new JPanel(new GridBagLayout());
        panelMenuPrincipal.setBackground(new Color(15, 23, 42));
        
        titulo = new JLabel();
        try {
            ImageIcon imagenLogo = new ImageIcon("damas/assets/damasTitular.png");
            Image logoEscalado = imagenLogo.getImage().getScaledInstance(280, -1, Image.SCALE_SMOOTH);
            titulo.setIcon(new ImageIcon(logoEscalado));
        } catch (Exception e) {
            titulo.setText("DAMAS");
            titulo.setFont(new Font("Courier New", Font.BOLD, 70));
            titulo.setForeground(Color.WHITE);
        }
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        
        botonLocal = new JButton("JUEGO LOCAL");
        botonHost = new JButton("CREAR PARTIDA (HOST)");
        botonClient = new JButton("UNIRSE A PARTIDA");
        botonConfig = new JButton("⚙ AJUSTES");
        
        estilizarBotonPrincipal(botonLocal);
        estilizarBotonPrincipal(botonHost);
        estilizarBotonPrincipal(botonClient);
        estilizarBotonPrincipal(botonConfig);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 20, 40, 20);
        panelMenuPrincipal.add(titulo, gbc);
        
        gbc.insets = new Insets(10, 50, 10, 50);
        gbc.gridy = 1;
        panelMenuPrincipal.add(botonLocal, gbc);
        gbc.gridy = 2;
        panelMenuPrincipal.add(botonHost, gbc);
        gbc.gridy = 3;
        panelMenuPrincipal.add(botonClient, gbc);
        
        gbc.gridy = 4;
        gbc.insets = new Insets(30, 50, 10, 50);
        panelMenuPrincipal.add(botonConfig, gbc);
        
        botonLocal.addActionListener(e -> {
            reproductor.reproducirClic();
            String nombreB = JOptionPane.showInputDialog("Nombre Jugador 1 (Blancas):");
            if (nombreB == null) return;
            if (nombreB.trim().isEmpty()) nombreB = "Jugador 1";
            
            String nombreN = JOptionPane.showInputDialog("Nombre Jugador 2 (Negras):");
            if (nombreN == null) return;
            if (nombreN.trim().isEmpty()) nombreN = "Jugador 2";
            
            Tablero tableroLogico = new Tablero();
            Interfaz juego = new Interfaz(tableroLogico, null, null, nombreB, nombreN);
            menuPrincipal.dispose();
        });
        
        botonHost.addActionListener(e -> {
            reproductor.reproducirClic();
            String miNombre = JOptionPane.showInputDialog("Ingresa tu nombre (Host/Blancas):");
            if (miNombre == null) return;
            if (miNombre.trim().isEmpty()) miNombre = "Host";
            
            Tablero tableroLogico = new Tablero();
            ConexionServidor servidor = new ConexionServidor(5000, tableroLogico, miNombre);
            Thread hiloServidor = new Thread(servidor);
            hiloServidor.start();
            
            try {
                String miIp = java.net.InetAddress.getLocalHost().getHostAddress();
                JOptionPane.showMessageDialog(null, "Servidor abierto.\nOponente IP: " + miIp + "\nPuerto: 5000");
            } catch (Exception ex) {
                System.out.println("Error IP");
            }
            
            Interfaz juego = new Interfaz(tableroLogico, servidor, null, miNombre, "Esperando...");
            servidor.setInterfaz(juego);
            menuPrincipal.dispose();
        });
        
        botonClient.addActionListener(e -> {
            reproductor.reproducirClic();
            String miNombre = JOptionPane.showInputDialog("Ingresa tu nombre (Cliente/Negras):");
            if (miNombre == null) return;
            if (miNombre.trim().isEmpty()) miNombre = "Cliente";
            
            String ipHost = JOptionPane.showInputDialog("Ingresa la IP del Host:");
            if (ipHost != null && !ipHost.trim().isEmpty()) {
                Tablero tableroLogico = new Tablero();
                ConexionCliente cliente = new ConexionCliente(ipHost, 5000, tableroLogico, miNombre);
                Thread hiloCliente = new Thread(cliente);
                hiloCliente.start();
                
                Interfaz juego = new Interfaz(tableroLogico, null, cliente, "Esperando...", miNombre);
                cliente.setInterfaz(juego);
                menuPrincipal.dispose();
            }
        });
        
        botonConfig.addActionListener(e -> {
            reproductor.reproducirClic();
            abrirMenuAjustesProfesional();
        });
        
        menuPrincipal.add(panelMenuPrincipal);
        menuPrincipal.setVisible(true);
    }
    
    private void abrirMenuAjustesProfesional() {
        JDialog ventanaAjustes = new JDialog(menuPrincipal, "Configuración", true);
        ventanaAjustes.setSize(450, 450);
        ventanaAjustes.setLocationRelativeTo(menuPrincipal);
        ventanaAjustes.setLayout(new BorderLayout());
        ventanaAjustes.getContentPane().setBackground(new Color(15, 23, 42));
        
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setOpaque(false);
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));
        
        JLabel tituloAjustes = new JLabel("CONFIGURACIÓN", SwingConstants.CENTER);
        tituloAjustes.setForeground(Color.WHITE);
        tituloAjustes.setFont(new Font("Courier New", Font.BOLD, 24));
        
        JButton botonCerrar = new JButton("Cerrar");
        botonCerrar.setBackground(new Color(220, 53, 69));
        botonCerrar.setForeground(Color.WHITE);
        botonCerrar.setFont(new Font("Courier New", Font.BOLD, 14));
        botonCerrar.setFocusPainted(false);
        botonCerrar.setBorderPainted(false);
        botonCerrar.setOpaque(true);
        botonCerrar.setContentAreaFilled(true);
        botonCerrar.setPreferredSize(new Dimension(100, 35));
        botonCerrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botonCerrar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 40, 50), 1),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        
        botonCerrar.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                botonCerrar.setBackground(new Color(200, 35, 50));
            }
            public void mouseExited(MouseEvent evt) {
                botonCerrar.setBackground(new Color(220, 53, 69));
            }
        });
        
        botonCerrar.addActionListener(evt -> {
            reproductor.reproducirClic();
            ventanaAjustes.dispose();
        });
        
        panelSuperior.add(tituloAjustes, BorderLayout.CENTER);
        panelSuperior.add(botonCerrar, BorderLayout.EAST);
        
        JPanel panelOpciones = new JPanel(new GridBagLayout());
        panelOpciones.setOpaque(false);
        panelOpciones.setBorder(BorderFactory.createEmptyBorder(10, 30, 20, 30));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);
        
        JButton btnMusica = new JButton("Música: ON");
        cargarIconoBoton(btnMusica, "musicaIcono.png", "🎵 ");
        estilizarBotonSecundario(btnMusica);
        
        btnMusica.addActionListener(evt -> {
            reproductor.alternarMute();
            musicaActivada = !musicaActivada;
            btnMusica.setText("Música: " + (musicaActivada ? "ON" : "OFF"));
        });
        
        gbc.gridy = 0;
        panelOpciones.add(btnMusica, gbc);
        
        JButton btnFondo = new JButton("Fondo actual: " + nombreFondoSeleccionado);
        cargarIconoBoton(btnFondo, "fondoIcono.png", "🖼 ");
        estilizarBotonSecundario(btnFondo);
        
        btnFondo.addActionListener(evt -> {
            reproductor.reproducirClic();
            
            Object[] opcionesFondos = {
                "🏞️ Fondo 1 - Bosque",
                "🌃 Fondo 2 - Ciudad",
                "🎨 Fondo 3 - Totoro (Ghibli)",
                "🌅 Fondo 4 - Atardecer",
                "🌸 Fondo 5 - Sakura (Cerezos)"
            };
            
            int s = JOptionPane.showOptionDialog(ventanaAjustes,
                "Selecciona el fondo para el tablero:",
                "Cambiar Fondo",
                0, 1, null, opcionesFondos, opcionesFondos[0]);
            
            switch(s) {
                case 0:
                    rutaFondoElegido = "damas/assets/fondo1.jpg";
                    nombreFondoSeleccionado = "Bosque";
                    break;
                case 1:
                    rutaFondoElegido = "damas/assets/fondo2.jpg";
                    nombreFondoSeleccionado = "Ciudad";
                    break;
                case 2:
                    rutaFondoElegido = "damas/assets/fondo3.png";
                    nombreFondoSeleccionado = "Totoro (Ghibli)";
                    break;
                case 3:
                    rutaFondoElegido = "damas/assets/fondo4.png";
                    nombreFondoSeleccionado = "Atardecer";
                    break;
                case 4:
                    rutaFondoElegido = "damas/assets/fondo5.jpeg";
                    nombreFondoSeleccionado = "Sakura (Cerezos)";
                    break;
            }
            
            if(s >= 0) {
                btnFondo.setText("Fondo actual: " + nombreFondoSeleccionado);
                JOptionPane.showMessageDialog(ventanaAjustes,
                    "✅ Fondo cambiado a: " + nombreFondoSeleccionado +
                    "\nSe aplicará en la siguiente partida.");
            }
        });
        
        gbc.gridy = 1;
        panelOpciones.add(btnFondo, gbc);
        
        JButton btnFichas = new JButton("Fichas: " + nombreFichasSeleccionado);
        cargarIconoBoton(btnFichas, "fichaIcono.png", "♟ ");
        estilizarBotonSecundario(btnFichas);
        
        btnFichas.addActionListener(evt -> {
            reproductor.reproducirClic();
            
            Object[] opcionesFichas = {
                "⚪⚫ Clásicas (Blanco vs Negro)",
                "🟣🟢 Cyberpunk (Púrpura vs Verde)",
                "🟠🔵 Fuego/Agua (Naranja vs Azul)",
                "🟡⚫ Eléctrico (Amarillo vs Negro)"
            };
            
            int s = JOptionPane.showOptionDialog(ventanaAjustes,
                "Selecciona el estilo de fichas:",
                "Cambiar Fichas",
                0, 1, null, opcionesFichas, opcionesFichas[0]);
            
            switch(s) {
                case 0:
                    rutaFichaLocal = "damas/assets/fichaBlanca.png";
                    rutaFichaRival = "damas/assets/fichaNegra.png";
                    nombreFichasSeleccionado = "Clásicas";
                    break;
                case 1:
                    rutaFichaLocal = "damas/assets/fichaPurpura.png";
                    rutaFichaRival = "damas/assets/fichaVerde.png";
                    nombreFichasSeleccionado = "Cyberpunk";
                    break;
                case 2:
                    rutaFichaLocal = "damas/assets/fichaNaranja.png";
                    rutaFichaRival = "damas/assets/fichaAzul.png";
                    nombreFichasSeleccionado = "Fuego/Agua";
                    break;
                case 3:
                    rutaFichaLocal = "damas/assets/fichaAmarilla.png";
                    rutaFichaRival = "damas/assets/fichaNegra.png";
                    nombreFichasSeleccionado = "Eléctrico";
                    break;
            }
            
            if(s >= 0) {
                btnFichas.setText("Fichas: " + nombreFichasSeleccionado);
                JOptionPane.showMessageDialog(ventanaAjustes,
                    "✅ Estilo de fichas cambiado a: " + nombreFichasSeleccionado +
                    "\nSe aplicará en la siguiente partida.");
            }
        });
        
        gbc.gridy = 2;
        panelOpciones.add(btnFichas, gbc);
        
        ventanaAjustes.add(panelSuperior, BorderLayout.NORTH);
        ventanaAjustes.add(panelOpciones, BorderLayout.CENTER);
        ventanaAjustes.setVisible(true);
    }
    
    private void cargarIconoBoton(JButton boton, String nombreIcono, String textoAlternativo) {
        try {
            ImageIcon icono = new ImageIcon("damas/assets/" + nombreIcono);
            
            if (icono.getIconWidth() > 0 && icono.getIconHeight() > 0) {
                Image imagenEscalada = icono.getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH);
                ImageIcon iconoEscalado = new ImageIcon(imagenEscalada);
                boton.setIcon(iconoEscalado);
                boton.setHorizontalAlignment(SwingConstants.LEFT);
                boton.setIconTextGap(10);
            } else {
                boton.setText(textoAlternativo + boton.getText());
            }
        } catch (Exception e) {
            boton.setText(textoAlternativo + boton.getText());
        }
    }
    
    private void estilizarBotonPrincipal(JButton boton) {
        Color colorOriginal = new Color(109, 40, 217);
        Color colorIluminado = new Color(139, 92, 246);
        boton.setBackground(colorOriginal);
        boton.setForeground(Color.WHITE);
        boton.setFont(new Font("Courier New", Font.BOLD, 18));
        boton.setFocusPainted(false);
        boton.setOpaque(true);
        boton.setBorderPainted(false);
        boton.setPreferredSize(new Dimension(300, 50));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                boton.setBackground(colorIluminado);
            }
            public void mouseExited(MouseEvent evt) {
                boton.setBackground(colorOriginal);
            }
        });
    }
    
    private void estilizarBotonSecundario(JButton boton) {
        Color colorOriginal = new Color(75, 85, 99);
        Color colorIluminado = new Color(107, 114, 128);
        
        boton.setBackground(colorOriginal);
        boton.setForeground(Color.WHITE);
        boton.setFont(new Font("Courier New", Font.BOLD, 14));
        boton.setFocusPainted(false);
        boton.setOpaque(true);
        boton.setBorderPainted(false);
        boton.setPreferredSize(new Dimension(350, 45));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        boton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                boton.setBackground(colorIluminado);
            }
            public void mouseExited(MouseEvent evt) {
                boton.setBackground(colorOriginal);
            }
        });
    }
}