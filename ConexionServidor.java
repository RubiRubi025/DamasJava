package damas;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.SwingUtilities;

public class ConexionServidor implements Runnable {

    private int puerto;
    private ServerSocket servidor;
    private Socket cliente;
    
    // PARCHE DE INGENIERÍA: 'volatile' obliga a la Interfaz a ver estos datos en tiempo real
    private volatile DataInputStream entrada;
    private volatile DataOutputStream salida;
    private volatile boolean conectado = false; // Nuestro sensor blindado
    
    private Tablero tableroJuego;
    private volatile Interfaz ventanaJuego; 
    private String miNombre; 

    public ConexionServidor(int puerto, Tablero tableroLogico, String miNombre) throws Exception {
        this.miNombre = miNombre;
        this.puerto = puerto;
        this.tableroJuego = tableroLogico;
        this.servidor = new ServerSocket(puerto); 
        System.out.println("Servidor inicializado en el puerto " + puerto);
    }

    // Ahora la interfaz lee el sensor, no la variable 'salida'
    public boolean isConectado() {
        return conectado; 
    }

    @Override
    public void run() {
        try {
            System.out.println("Servidor abierto. Esperando al jugador 2... ");
            cliente = servidor.accept(); 
            System.out.println("¡Cliente conectado con éxito!");

            entrada = new DataInputStream(cliente.getInputStream());
            salida = new DataOutputStream(cliente.getOutputStream());
            
            // ¡ACTIVAMOS EL SENSOR! La interfaz se destraba automáticamente
            conectado = true; 

            enviarMensaje("NOMBRE," + miNombre);

            while(true) {
                String mensaje = entrada.readUTF();
                System.out.println("Mensaje recibido del cliente: " + mensaje);

                String[] partesMensaje = mensaje.split(",");
                String accion = partesMensaje[0];
                
                if (accion.equals("NOMBRE")) {
                    String nombreRival = partesMensaje[1];
                    while(ventanaJuego == null) {
                        try { Thread.sleep(100); } catch(Exception e) {}
                    }
                    SwingUtilities.invokeLater(() -> {
                        ventanaJuego.setNombreNegras(nombreRival);
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
            System.out.println("Error en ConexionServidor: " + e.getMessage());
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