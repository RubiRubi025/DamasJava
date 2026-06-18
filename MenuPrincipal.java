package damas;
import javax.swing.*;
import java.awt.*;

public class MenuPrincipal {
    
    JFrame menuPrincipal;
    JPanel panelMenuPrincipal;
    JButton botonHost;
    JButton botonClient;
    
    public MenuPrincipal(){
    menuPrincipal = new JFrame("Juego de damas");
    menuPrincipal.setSize(400, 400);
    menuPrincipal.setLocationRelativeTo(null);
    menuPrincipal.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    menuPrincipal.setResizable(true);
    panelMenuPrincipal = new JPanel(new GridLayout(1, 3));
    botonHost = new JButton("Host");
    botonClient = new JButton("Cliente");
    JButton botonLocal = new JButton("Local");
    panelMenuPrincipal.add(botonLocal);
    panelMenuPrincipal.add(botonHost);
    panelMenuPrincipal.add(botonClient);
    botonLocal.addActionListener(e -> {
    Tablero tableroLogico = new Tablero();
    Interfaz juego = new Interfaz(tableroLogico, null, null);
    menuPrincipal.dispose();
});
    botonHost.addActionListener(e -> {
        Tablero tableroLogico = new Tablero();
        ConexionServidor servidor = new ConexionServidor(5000, tableroLogico);
        Thread hiloServidor = new Thread(servidor);
        hiloServidor.start();
        try {
            String miIp = java.net.InetAddress.getLocalHost().getHostAddress();
            JOptionPane.showMessageDialog(null, "Tu servidor está abierto.\nDile a tu oponente que use la IP: " + miIp + "\nPuerto: 5000");
        } catch (Exception ex) {
            System.out.println("Error sacando la IP");
        }
        Interfaz juego = new Interfaz(tableroLogico, servidor, null);
        servidor.setInterfaz(juego);
        menuPrincipal.dispose();

    });
        botonClient.addActionListener(e -> {
        String ipHost = JOptionPane.showInputDialog("Ingresa la IP del Host:");
        if (ipHost != null && !ipHost.trim().isEmpty()) {
            Tablero tableroLogico = new Tablero();
            ConexionCliente cliente = new ConexionCliente(ipHost, 5000, tableroLogico);
            Thread hiloCliente = new Thread(cliente);
            hiloCliente.start();
            Interfaz juego = new Interfaz(tableroLogico, null, cliente);
            cliente.setInterfaz(juego);
            menuPrincipal.dispose();
        }
    });
    menuPrincipal.add(panelMenuPrincipal);
    menuPrincipal.setVisible(true);
    }
}