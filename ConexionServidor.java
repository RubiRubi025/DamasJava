package damas;

//Importaciones son necesarias para la parte de red del servidor
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

//Clase que representa la conexión del servidor para el juego de damas
// La palabra "implements Runnable" convierte esta clase en un Hilo (Thread) 
// que puede correr en el fondo sin congelar la pantalla.
public class ConexionServidor implements Runnable {

    // Atributos para la conexión del servidor
    private int puerto;
    private ServerSocket servidor;
    private Socket cliente;

    // Streams/embudos para enviar y recibir datos entre el servidor y el cliente
    private DataInputStream entrada;
    private DataOutputStream salida;
    private Tablero tableroJuego;
    private Interfaz ventanaJuego;  

    // Constructor que inicializa el puerto del servidor
    public ConexionServidor(int puerto, Tablero tableroLogico) {

        this.puerto = puerto; // Guarda el número de puerto para el servidor
        this.tableroJuego = tableroLogico;
}

    // Método que se ejecuta cuando el hilo del servidor comienza a correr
    @Override
    public void run(){

        // Bloque try/catch para manejar posibles errores de red
        try {

            servidor= new ServerSocket(puerto); // Crea un servidor que escucha en el puerto especificado
            System.out.println("Servidor abierto. Esperando al jugador 2... ");
            cliente = servidor.accept(); // Espera a que un cliente se conecte y acepta la conexión
            System.out.println("¡Cliente conectado con éxito!");

            // Inicializa los streams para enviar y recibir datos
            entrada = new DataInputStream(cliente.getInputStream()); // Stream para recibir datos del cliente
            salida = new DataOutputStream(cliente.getOutputStream()); // Stream para enviar datos al cliente

            //Este bucle permanece activo mientras el servidor 
            //esté corriendo, esperando y procesando mensajes del cliente
            while(true){

                String mensaje = entrada.readUTF();
                System.out.println("Mensaje recibido del cliente: " + mensaje);

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

                    //Se llama al método moverFicha del tablero para actualizar el estado del juego 
                    //con el movimiento que el cliente ha realizado.
                    tableroJuego.moverFicha(filaOrigen, columnaOrigen, filaDestino, columnaDestino);

                    // Imprimimos en consola temporalmente para el diagnóstico (debugging)
                    tableroJuego.ImprimirTablero();

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
    
    // Método para enviar un mensaje al cliente
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
