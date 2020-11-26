package com.example.ensem_map;

/**
 * class Element pour les elements des tableaux D et S lors de l'algo de Dijkstra, plusCourtChemin(...) dans la class GraphParListe
 */
public class Element {
	
	int idPoint;
	double distance; 	//distance avec le sommet précedent dans le graphe
	int idPrecedent;

	public Element(int idPoint, double distance, int idPrecedent) {
		this.idPoint = idPoint;
		this.distance = distance;
		this.idPrecedent = idPrecedent;
	}
}