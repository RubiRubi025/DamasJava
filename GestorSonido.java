package damas;

import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl; // Importación nueva para el volumen

public class GestorSonido {

    // 1. La ruta exacta de tu archivo
    private String rutaSonidoClic = "damas/assets/sounds/the_mountain-game-179496.wav";
    
    // 2. Nuestro interruptor maestro (Nace encendido)
    private boolean estaMuteado = false;

    public void reproducirClic() {
        // Si el jugador silenció el juego, abortamos la misión y no suena nada
        if (estaMuteado) {
            return; 
        }

        try {
            File archivoSonido = new File(rutaSonidoClic);

            if (archivoSonido.exists()) {
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(archivoSonido);
                Clip clip = AudioSystem.getClip();
                clip.open(audioInput);
                
                // --- MAGIA DEL VOLUMEN ---
                // Capturamos el control de volumen del clip
                FloatControl controlVolumen = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                // Le restamos 15 decibelios para que sea un sonido sutil y elegante
                controlVolumen.setValue(-15.0f); 
                
                clip.start();
                
            } else {
                System.out.println("Ojo: No se encontró el archivo en -> " + rutaSonidoClic);
            }
        } catch (Exception e) {
            System.out.println("Error interno de audio: " + e.getMessage());
        }
    }

    // Método que llamaremos desde un futuro botón en la interfaz para apagar/prender el sonido
    public void alternarMute() {
        estaMuteado = !estaMuteado;
    }
}