package com.example.ensem_map;

import java.util.Vector;

/**
 * class pour construire le graph par listes representant les parcours possibles dans l'ensem
 * contient les methodes pour le recherche du plus court chemin avec Dijkstra
 */
public class GrapheParListe {

	public Liste[] adj; //un tableau de listes chainées

	/**
	 * constructeur
	 */
	public GrapheParListe(Vector<Point> lecturePoint) {
		Vector<String>inputG = remplirGraphe(lecturePoint);
		/*for(String s:inputG){
			System.out.println(s);
		}*/
		int nb_sommets = inputG.size();
		adj	= new	Liste[nb_sommets];
		for(int	i = 0; i < nb_sommets; i++) 
			adj[i] = null;
		//on saisie les arcs par sommet du départ
		String[] s;
		for(int	k=0; k<nb_sommets; k++) {
			s=inputG.elementAt(k).split(",");
			for (int l=1;l<s.length;l=l+2) {
				int	i = Integer.parseInt(s[0]);
				Point	j =lecturePoint.elementAt(Integer.parseInt(s[l]));
				double	val=Double.parseDouble(s[l+1]);
				adj[i] = new Liste(j, val, adj[i]);
			}
		}
	}

	/**
	 * tester s'il existe un arc entre deux sommets
	 */
	public boolean arc (Point source, Point dest){
		boolean	arcExiste= false;
		if(	adj[(source.getId())]!=null) {
			Liste a = adj[(source.getId())];
			while(a !=null) {
				if(a.num_noeud.getId()==(dest.getId())) arcExiste =true;
				if(arcExiste) a=null;
				else
					a = a.suivant;
			}
		}
		return	arcExiste;
	}

	/**
	 *retruner la valeur de l'arc entre deux sommets
	 */
	public  double valeurArc(Point source,Point	dest){
		double	val = 9999;
		boolean	arcExiste=false;
		Liste a = adj[(source.getId())];
		while(a !=null) {
			if(a.num_noeud== dest) {
				val = a.valeur; 
				arcExiste =true;
			}
			if(arcExiste) a=null;
			else	a = a.suivant;
		}
		return	val;
	}

	/**
	 *rechercher le plus court chemin d'un sommet source vers tous les autres noeuds
	 */
	public	Vector<Element> plusCourtChemin(int	num_sommet, Vector<Point> lecturePoint){
		final double INFINI = Integer.MAX_VALUE;
		Vector<Element> S = new Vector<>(); //vector de solution
		Vector<Element> D = new Vector<>(); //vector du départ
		//initialiser l'ensemble D
		for(int	i = 0; i <adj.length; i++){
			if(i!=num_sommet)D.addElement(new Element(i,INFINI, num_sommet));//rajout predecesseur
			else
				D.addElement(new Element(i,0,num_sommet));//rajout predecesseur
		}
		//construire l'ensemble S selon Dijkstra
		while(D.size()!=0){
			//on cherchel'élément qui a la plus petite distance
			int	indice_min=0;
			double	dm=INFINI;
			int	sm=(D.elementAt(0)).idPoint;
			int predecesseur = (D.elementAt(0)).idPrecedent;
			for	(int i = 0; i < D.size(); i++){
				if(dm > (D.elementAt(i)).distance){
					dm = (D.elementAt(i)).distance;
					sm = (D.elementAt(i)).idPoint;
					predecesseur = (D.elementAt(i)).idPrecedent; //rajout du predecesseur
					indice_min = i;
				}
			}
			Element m = new	Element(sm,dm,predecesseur);//rejout predecesseur
			//on l'ajoute dans S puis on le supprime de D
			S.addElement(m);
			D.removeElementAt(indice_min);
			//on recalcule dx pour tout sommet x de D qui possède un arc avec m ET LE PREDECESSEUR
			for(int	i = 0; i < D.size(); i++){
				Element x = D.elementAt(i);
				if(arc(lecturePoint.elementAt(m.idPoint),lecturePoint.elementAt(x.idPoint))) {
					double	t=m.distance+valeurArc(lecturePoint.elementAt(m.idPoint),lecturePoint.elementAt(x.idPoint));
					if	(t < x.distance) {
						x.distance=t;
						x.idPrecedent=m.idPoint;
						D.setElementAt(x,i);
					}
				}
			}
		}
		return	S;
	}

	/**
	 *retourne tableau de string corrrespondant à tous les chemins les plus courts partant du num_sommet vers tous les autres points du graphe
	 */
	public static String[] chemin(int num_sommet, Vector<Element> S, Vector<Point> lecturePoint) {
		int n = S.size();
		String[] chemin = new String[n];
		for(int i=0; i<n;i++) {
			chemin[i]=Integer.toString(lecturePoint.elementAt(S.elementAt(i).idPoint).getId());
			int j=i;
			while(S.elementAt(j).idPrecedent!=num_sommet) {
				chemin[i]=lecturePoint.elementAt(S.elementAt(j).idPrecedent).getId()+"-"+chemin[i];
				j=indicsommetDansS( S, S.elementAt(j).idPrecedent);
			}
			chemin[i]=lecturePoint.elementAt(num_sommet).getId()+"-"+chemin[i];
		}
		return(chemin);
	}

	/**
	 * les indices des sommets dans S et lectureStatiions sont differents donc il faut cette methode qui determine l'indice d'un sommet dans S
	 */
	public static int indicsommetDansS(Vector<Element> S,int sommet) {
		int i=0;
		while(sommet!=S.elementAt(i).idPoint) {
			i++;
			if(i>S.size()) {
				i=-1;
				break;
			}
		}
		return(i);
	}

	public static Vector<String> remplirGraphe(Vector<Point> lecturePoint){
		Vector<String> graphe = new Vector<>();
		for (int i=0; i<lecturePoint.size();i++) {
			graphe.add(predecesseurs( lecturePoint,lecturePoint.get(i)));
		}
		return(graphe);
	}

	public static String predecesseurs(Vector<Point> lecturePoint, Point point){
		StringBuilder string = new StringBuilder(Integer.toString(point.getId()));
		for (int i=0; i<point.getVoisins().length;i++){
			string.append(",").append(point.getVoisins()[i]).append(",").append(point.distanceTo(lecturePoint.get(point.getVoisins()[i])));
		}
		return string.toString();
	}
}

