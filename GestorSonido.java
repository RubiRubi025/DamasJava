package damas;

import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl; 

public class GestorSonido {
    
    private Clip clipMusica;
    private boolean estaMuteado = false;
    
    // Solo dejamos la ruta de la música de fondo épica
    private String rutaMusicaFondo = "damas/assets/sounds/the_mountain-game-179496.wav";

    public GestorSonido() {
        try {
            File archivoAudio = new File(rutaMusicaFondo);
            if (archivoAudio.exists()) {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(archivoAudio);
                clipMusica = AudioSystem.getClip();
                clipMusica.open(audioStream);
                
                // --- MAGIA DEL VOLUMEN ---
                FloatControl controlVolumen = (FloatControl) clipMusica.getControl(FloatControl.Type.MASTER_GAIN);
                controlVolumen.setValue(-15.0f); // Volumen elegante
            } else {
                System.out.println("Ojo: No se encontró la música en -> " + rutaMusicaFondo);
            }
        } catch (Exception e) {
            System.out.println("Error al cargar la música: " + e.getMessage());
        }
    }

    public void reproducirMusicaFondo() {
        if (clipMusica != null && !estaMuteado) {
            if (!clipMusica.isRunning()) { 
                clipMusica.setFramePosition(0); 
                clipMusica.loop(Clip.LOOP_CONTINUOUSLY); // Bucle infinito
            }
        }
    }

    public void alternarMute() {
        estaMuteado = !estaMuteado;
        if (clipMusica != null) {
            if (estaMuteado) {
                clipMusica.stop(); 
            } else {
                reproducirMusicaFondo(); 
            }
        }
    }

    // Mantenemos el método para que el resto del código no dé error (rojo), pero lo dejamos vacío
    public void reproducirClic() {
        // Misión abortada: No hay efectos de sonido por falta de tiempo.
    }
}