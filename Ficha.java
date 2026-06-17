package damas;

// Clase que representa una ficha en el juego de damas
public class Ficha{
    
    // Atributos de la ficha
    private String color;
    private boolean esReina;
    private int fila;
    private int columna;

    //Constructor
    public Ficha(String color, int fila, int columna) {
        this.color = color;
        this.fila = fila;         // Guarda la posición X inicial
        this.columna = columna;   // Guarda la posición Y inicial
        this.esReina = false;     // Nace como peón
    }

    //Métodos getters para acceder a los atributos de la ficha
    public String getColor() {
        return color;
    }

    public int getFila() {
        return fila;
    }

    public int getColumna() {
        return columna;
    }

    // El is para los booleanos es equivalente a un get
    public boolean isReina() {
        return esReina;
    }

    //Métodos setters para modificar los atributos de la ficha
    // Se usa cuando la ficha camina o salta a una nueva fila
    public void setFila(int nuevaFila) {
        this.fila = nuevaFila;
    }

    // Se usa cuando la ficha camina o salta a una nueva columna
    public void setColumna(int nuevaColumna) {
        this.columna = nuevaColumna;
    }

    // Se usa cuando la ficha llega al final del tablero y es coronada
    public void setesReina(boolean nuevaReina) {
        this.esReina = nuevaReina;
    }

}
