package damas;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import javax.swing.SwingUtilities;

public class ConexionCliente implements Runnable {

    private Socket cliente;
    private String direccionIP;
    private int puerto;
    
    // PARCHE DE INGENIERÍA: 'volatile'
    private volatile DataInputStream entrada;
    private volatile DataOutputStream salida;
    
    private Tablero tableroJuego;
    private volatile Interfaz ventanaJuego;  
    private String miNombre;

    public ConexionCliente(String direccionIP, int puerto, Tablero tableroLogico, String miNombre) throws Exception {
        this.direccionIP = direccionIP;
        this.puerto = puerto;
        this.tableroJuego = tableroLogico;
        this.miNombre = miNombre;
        
        cliente = new Socket(direccionIP, puerto);
        System.out.println("¡Conectado al servidor con éxito!");
    }

    @Override
    public void run() {
        try {
            entrada = new DataInputStream(cliente.getInputStream());
            salida = new DataOutputStream(cliente.getOutputStream());
            
            enviarMensaje("NOMBRE," + miNombre);

            while (true) {
                String mensaje = entrada.readUTF();
                System.out.println("Mensaje recibido del servidor: " + mensaje);

                String[] partesMensaje = mensaje.split(",");
                String accion = partesMensaje[0];
                
                if (accion.equals("NOMBRE")) {
                    String nombreRival = partesMensaje[1];
                    while(ventanaJuego == null) {
                        try { Thread.sleep(100); } catch(Exception e) {}
                    }
                    SwingUtilities.invokeLater(() -> {
                        ventanaJuego.setNombreBlancas(nombreRival);
                    });
                }
                
                if (accion.equals("MOVER")) {
                    int filaOrigen = Integer.parseInt(partesMensaje[1]);
                    int columnaOrigen = Integer.parseInt(partesMensaje[2]);
                    int filaDestino = Integer.parseInt(partesMensaje[3]);
                    int columnaDestino = Integer.parseInt(partesMensaje[4]);
                    
                    tableroJuego.moverFicha(filaOrigen, columnaOrigen, filaDestino, columnaDestino);
                    
                    SwingUtilities.invokeLater(() -> {
                        ventanaJuego.SincronizacionTablero();
                        ventanaJuego.setMiTurno(true);
                        ventanaJuego.actualizarTurno();
                    });
                }
            }
        } catch (Exception e) {
            System.out.println("Error en ConexionCliente: " + e.getMessage());
        }
    }

    public void enviarMensaje(String mensaje) {
        try {
            if (salida != null) {
                salida.writeUTF(mensaje);
                salida.flush(); // Empuja la data inmediatamente por el cable
            }
        } catch (Exception e) {
            System.out.println("Error al enviar: " + e.getMessage());
        }
    }
    
    public void setInterfaz(Interfaz ventana) {
        this.ventanaJuego = ventana;
    }
}