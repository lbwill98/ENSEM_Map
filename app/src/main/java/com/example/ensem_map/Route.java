package com.example.ensem_map;

import java.util.ArrayList;
import java.util.Vector;

/**
 * class qui controle la recherche du plus court chemin
 * A FAIRE : pour l'instant, elle sert juste à connaitre les étages que le chemin emprunte pour MyAdapter
 *           il faudra gerer ici la construction du graphe et ce qui ne concerne pas la view dans le onClik()
 */
public class Route {

    public static ArrayList<Integer> etagesPresents = new ArrayList<>();

    /**
     *fonction qui retourne la liste des etages parcourus par le chemin
     * utile pour  afficher les bonnes cartes sur Myadapter
     */
    public static void setEtagesPresents(Vector<Point> cheminPoint){
        ArrayList<Integer> etagesPresents = new ArrayList<>();
        for(Point p:cheminPoint){
            if(!etagesPresents.contains(p.getEtage())){
                etagesPresents.add(p.getEtage());
            }
        }
        Route.etagesPresents = etagesPresents;
    }
}
