package damas;
import java.awt.*;
import javax.swing.*;

public class Interfaz extends JFrame {

    private JPanel panelTablero;
    private JButton[][] botones;
    private JPanel panelFondo;
    private JPanel panelSuperior;
    private JLabel Etiqueta;
    private 

    public Interfaz(Tablero TableroLogico){

        // 1. Primero instanciamos el tablero y le damos sus reglas
        panelTablero = new JPanel(new GridLayout(8, 8));
        panelTablero.setPreferredSize(new Dimension(400, 400));

        // 2. Instanciamos el fondo inyectando la imagen
        panelFondo = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon imagenFondo = new ImageIcon("damas/fondo.jpg"); 
                g.drawImage(imagenFondo.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };

        // 3. Configuramos la ventana madre
        setTitle("Juego de damas");
        setSize(600, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);

        // 4. Armamos la Matrioshka
        panelFondo.add(panelTablero);
        add(panelFondo);

        // 5. Fabricamos los botones y pintamos el piso
        botones = new JButton[8][8];
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                botones[i][j] = new JButton();
                if((i + j) % 2 == 1){
                    botones[i][j].setBackground(Color.BLACK);
                } else {
                    botones[i][j].setBackground(Color.WHITE);
                }
                panelTablero.add(botones[i][j]);
            }
        }
        setVisible(true);
    }

    public void SincronizacionTablero(){

    }
}