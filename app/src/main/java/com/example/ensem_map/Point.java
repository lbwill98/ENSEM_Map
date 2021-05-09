package com.example.ensem_map;

/**
 * un objet Point est un point sur les plans et un noeud du graphparliste
 * voir fichier point-rdc pour visualiser l'emplacement des points au rdc
 * les informations des points sont stockées dans la base de données
 */
public class Point {

    private final int id,x,y,etage;
    private final int[] voisins;
    //private Point precedent, suivant;//precedent et suivant, peut-etre utile pour gerer les étages...
    //double ditanceToPrecedent;

    public Point( int id, int x, int y, int etage, int[] voisins) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.etage = etage;
        this.voisins = voisins;
    }

    public int getId() {
        return id;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getEtage() {
        return etage;
    }

    public int[] getVoisins() {
        return voisins;
    }

    /**
     *fonction pour obtenir la distance entre deux points
     * distance calculée à partir des coordonnées en picels sur les images
     * donc il faut fair attention à l'échelle, elle doit etre la meme d'une image à l'autre
     */
    public double distanceTo(Point point){
        return Math.sqrt((this.x-point.x)*(this.x-point.x)+(this.y-point.y)*(this.y-point.y));
    }

    public String toString(){
        return("id=" + id +" x="+x +" y="+y);
    }
}

