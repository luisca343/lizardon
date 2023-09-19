package es.allblue.lizardon.objects.pixelmon;

import es.allblue.lizardon.Lizardon;

public class PosicionEquipo {
    int equipo;
    int posicion;

    public PosicionEquipo(int equipo, int posicion){
        Lizardon.LOGGER.info("CREANDO POSICION EQUIPO: " + equipo + " " + posicion);
        this.equipo = equipo;
        this.posicion = posicion;
    }

    public int getEquipo() {
        return equipo;
    }

    public int getPosicion() {
        return posicion;
    }

    public void setEquipo(int equipo) {
        this.equipo = equipo;
    }

    public void setPosicion(int posicion) {
        this.posicion = posicion;
    }

    String letras = "abcdefghijklmnopqrstuvwxyz";
    public char getLetra(){
        return letras.charAt(posicion);
    }


    public String toString(){
        return "p" + this.getEquipo() + this.getLetra();
    }
}
