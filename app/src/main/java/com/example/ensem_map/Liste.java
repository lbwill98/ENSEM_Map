package com.example.ensem_map;

/**
 * class Liste pour la construction du graphParListe
 * les noeuds sont des points
 */
public class Liste {

	Point num_noeud; //pour 	représenter le numéro d'un sommet
	double	valeur; //pour 	représenter la valeur de l'arc
	Liste suivant;

	Liste(Point p, double v, Liste t) {
		num_noeud=p;
		valeur=v;
		suivant= t;
	}
}