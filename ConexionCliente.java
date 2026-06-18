package damas;

//Importaciones necesarias para la parte de red del cliente
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

//Clase que representa la conexión del cliente para el juego de damas, esto es un hilo 
//que se ejecuta en el fondo para manejar la comunicación con el servidor sin congelar la pantalla del cliente
public class ConexionCliente implements Runnable {

    //Atributos para la conexión del cliente
    private Socket cliente;
    private String direccionIP;
    private int puerto;

    //Streams/embudos para enviar y recibir datos entre el cliente y el servidor
    private DataInputStream entrada;
    private DataOutputStream salida;
    private Tablero tableroJuego;
    private Interfaz ventanaJuego;  


    //Constructor que inicializa la dirección IP y el puerto del servidor al que el cliente se conectará
    public ConexionCliente(String direccionIP, int puerto, Tablero tableroLogico) {
        this.direccionIP = direccionIP; // Guarda la dirección IP del servidor
        this.puerto = puerto; // Guarda el número de puerto del servidor
        this.tableroJuego = tableroLogico;

    }

    @Override
    public void run(){
        //Bloque try/catch para manejar posibles errores de red
        try {

            //Intenta conectarse al servidor usando la dirección IP y el puerto especificados
            cliente = new Socket(direccionIP, puerto);
            System.out.println("¡Conectado al servidor con éxito!");

            //Inicializa los streams para enviar y recibir datos
            entrada = new DataInputStream(cliente.getInputStream());
            salida = new DataOutputStream(cliente.getOutputStream());

                //Este bucle permanece activo mientras el cliente esté corriendo,
                //esperando y procesando mensajes del servidor

                while (true) {
                    
                    String mensaje = entrada.readUTF();
                    System.out.println("Mensaje recibido del servidor: " + mensaje);

                    // Aquí se pueden agregar condiciones para procesar diferentes tipos de mensajes
                String[] partesMensaje = mensaje.split(","); // Divide el mensaje en partes usando la coma como separador
                String accion = partesMensaje[0]; // La primera parte del mensaje indica la acción a realizar
                
                //procesamiento de un mensaje de movimiento
                if (accion.equals("MOVER")){

                    //Traducción de las partes del mensaje que son texto a números enteros.
                    int filaOrigen = Integer.parseInt(partesMensaje[1]); 
                    int columnaOrigen = Integer.parseInt(partesMensaje[2]);
                    int filaDestino = Integer.parseInt(partesMensaje[3]);
                    int columnaDestino = Integer.parseInt(partesMensaje[4]);
                    tableroJuego.moverFicha(filaOrigen, columnaOrigen, filaDestino, columnaDestino);
                    ventanaJuego.SincronizacionTablero();
                    ventanaJuego.setMiTurno(true);
                    //Se imprime en la consola del servidor el movimiento que el cliente ha realizado,
                    //mostrando las coordenadas de origen y destino de la ficha movida.
                    System.out.println("El rival movió la ficha de [" + filaOrigen + "," + columnaOrigen + "] a [" + filaDestino + "," + columnaDestino + "]");

                }
                }

            } catch (Exception e) {
            
            //Si ocurre un error, se imprime el mensaje de error en la consola
            System.out.println("Error: " + e.getMessage());
        }
    }

    //Método para enviar un mensaje al servidor
    public void enviarMensaje(String mensaje){
        try {
            salida.writeUTF(mensaje);
            
        } catch (Exception e) {
            System.out.println("Error al enviar: " + e.getMessage());

        }
    }
    public void setInterfaz(Interfaz ventana) {
    this.ventanaJuego = ventana;
}
}
    