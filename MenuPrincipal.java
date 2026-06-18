package damas;
import java.awt.*;
import javax.swing.*;

public class MenuPrincipal {
    
    JFrame menuPrincipal;
    JPanel panelMenuPrincipal;
    
    // Título simple y elegante
    JLabel titulo;
    
    // Botones
    JButton botonHost;
    JButton botonClient;
    JButton botonLocal;
    
    // Herramientas
    GestorSonido reproductor;
    
    public MenuPrincipal(){
        // 1. Instanciamos el sonido (Paso 1 completado)
        reproductor = new GestorSonido();
        
        // 2. Configuración básica de la ventana
        menuPrincipal = new JFrame("Juego de Damas - Inicio");
        menuPrincipal.setSize(500, 600); 
        menuPrincipal.setLocationRelativeTo(null);
        menuPrincipal.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        menuPrincipal.setResizable(false); 

        // 3. Panel con fondo nocturno y GridBagLayout
        panelMenuPrincipal = new JPanel(new GridBagLayout()); 
        panelMenuPrincipal.setBackground(new Color(15, 23, 42)); // Azul noche oscuro

        // --- DISEÑO DEL TÍTULO MINIMALISTA ---
        // Fuente grande y en negrita
        Font fuenteTitulo = new Font("Courier New", Font.BOLD, 70); 
        
        titulo = new JLabel("DAMAS");
        titulo.setFont(fuenteTitulo);
        titulo.setForeground(Color.WHITE); // Blanco puro
        // Centramos el texto dentro del JLabel
        titulo.setHorizontalAlignment(SwingConstants.CENTER); 
        
        // --- FIN DISEÑO TÍTULO ---

        // 4. Instanciamos y estilizamos los botones
        botonLocal = new JButton("JUEGO LOCAL");
        botonHost = new JButton("CREAR PARTIDA (HOST)");
        botonClient = new JButton("UNIRSE A PARTIDA");

        estilizarBoton(botonLocal);
        estilizarBoton(botonHost);
        estilizarBoton(botonClient);

        // 5. Configuramos el Layout para apilar todo verticalmente
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; // Columna central
        gbc.fill = GridBagConstraints.HORIZONTAL; 
        
        // -- AGREGAMOS EL TÍTULO (En la fila 0) --
        gbc.gridy = 0;
        // Damos buen espacio alrededor del título
        gbc.insets = new Insets(30, 20, 60, 20); 
        panelMenuPrincipal.add(titulo, gbc);

        // -- AGREGAMOS LOS BOTONES (Filas 1, 2, 3) --
        gbc.insets = new Insets(10, 50, 10, 50); // Márgenes más pequeños entre botones
        
        gbc.gridy = 1;
        panelMenuPrincipal.add(botonLocal, gbc);
        
        gbc.gridy = 2;
        panelMenuPrincipal.add(botonHost, gbc);
        
        gbc.gridy = 3;
        panelMenuPrincipal.add(botonClient, gbc);

        // 6. Los ActionListeners con sonido
botonLocal.addActionListener(e -> {
    reproductor.reproducirClic(); 
    
    // Pedimos ambos nombres
    String nombreB = JOptionPane.showInputDialog("Nombre Jugador 1 (Blancas):");
    if (nombreB == null || nombreB.trim().isEmpty()) nombreB = "Jugador 1";
    
    String nombreN = JOptionPane.showInputDialog("Nombre Jugador 2 (Negras):");
    if (nombreN == null || nombreN.trim().isEmpty()) nombreN = "Jugador 2";
    
    Tablero tableroLogico = new Tablero();
    Interfaz juego = new Interfaz(tableroLogico, null, null, nombreB, nombreN);
    menuPrincipal.dispose();
});

                botonHost.addActionListener(e -> {
                    reproductor.reproducirClic();
                    
                    // 1. Pedimos el nombre
                    String miNombre = JOptionPane.showInputDialog("Ingresa tu nombre (Host/Blancas):");
                    if(miNombre == null || miNombre.trim().isEmpty()) miNombre = "Host";
                    
                    Tablero tableroLogico = new Tablero();
                    // 2. Le pasamos el nombre al hilo de red
                    ConexionServidor servidor = new ConexionServidor(5000, tableroLogico, miNombre);
                    Thread hiloServidor = new Thread(servidor);
                    hiloServidor.start();
                    
                    try {
                        String miIp = java.net.InetAddress.getLocalHost().getHostAddress();
                        JOptionPane.showMessageDialog(null, "Servidor abierto.\nOponente IP: " + miIp + "\nPuerto: 5000");
                    } catch (Exception ex) { System.out.println("Error IP"); }
                    
                    // 3. Rompemos el corto circuito: 5 parámetros. El rival temporalmente es "Esperando..."
                    Interfaz juego = new Interfaz(tableroLogico, servidor, null, miNombre, "Esperando...");
                    servidor.setInterfaz(juego);
                    menuPrincipal.dispose();
        });

        botonClient.addActionListener(e -> {
            reproductor.reproducirClic();
            
            // 1. Pedimos el nombre
            String miNombre = JOptionPane.showInputDialog("Ingresa tu nombre (Cliente/Negras):");
            if(miNombre == null || miNombre.trim().isEmpty()) miNombre = "Cliente";
            
            String ipHost = JOptionPane.showInputDialog("Ingresa la IP del Host:");
            if (ipHost != null && !ipHost.trim().isEmpty()) {
                Tablero tableroLogico = new Tablero();
                // 2. Le pasamos el nombre a la red
                ConexionCliente cliente = new ConexionCliente(ipHost, 5000, tableroLogico, miNombre);
                Thread hiloCliente = new Thread(cliente);
                hiloCliente.start();
                
                // 3. 5 parámetros. El rival temporalmente es "Esperando..."
                Interfaz juego = new Interfaz(tableroLogico, null, cliente, "Esperando...", miNombre);
                cliente.setInterfaz(juego);
                menuPrincipal.dispose();
            }
        });

        menuPrincipal.add(panelMenuPrincipal);
        menuPrincipal.setVisible(true);
    }

    // Método ayudante para diseño de botones (¡Ahora con efectos Hover!)
    private void estilizarBoton(JButton boton) {
        // Definimos nuestra paleta de colores para el botón
        Color colorOriginal = new Color(109, 40, 217); // Morado retro original
        Color colorIluminado = new Color(139, 92, 246); // Morado más claro y brillante
        
        boton.setBackground(colorOriginal); 
        boton.setForeground(Color.WHITE); 
        boton.setFont(new Font("Courier New", Font.BOLD, 18)); 
        boton.setFocusPainted(false); 
        
        // Reglas de Mac
        boton.setOpaque(true);
        boton.setBorderPainted(false);
        
        boton.setPreferredSize(new Dimension(300, 50)); 

        // --- LA MAGIA INTERACTIVA ---
        
        // 1. Cambiamos la flecha por la "manito" al pasar por encima
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // 2. Le enseñamos al botón a reaccionar al ratón (Hover)
        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                // Cuando el ratón ENTRA, cambiamos al color brillante
                boton.setBackground(colorIluminado);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                // Cuando el ratón SALE, lo regresamos a su color original
                boton.setBackground(colorOriginal);
            }
        });
    }

}