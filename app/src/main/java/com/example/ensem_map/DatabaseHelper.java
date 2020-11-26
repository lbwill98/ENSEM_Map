package com.example.ensem_map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.ArrayAdapter;

import androidx.annotation.Nullable;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Vector;

/**
 * classe permettant de gerer la base de données
 * la base de données ENSEMMapDataBase est stocker dans les assets
 * elle est copiée dans data/data/"+"com.example.ensem_map"+"/databases/ lors du onCreate() de MainActivity, fonction initDatabase()
 * puis on fait des requettes avec la fonction makeQuery()
 * A FAIRE : completer la base de données, dans un premier temps pour le rdc
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private final Context context;
    private final String dbName,dbPath;

    private SQLiteDatabase db;

    /**
     * constructeur, contexte MainActivity.this, nom et version de la database sont rentrés dans le onCreate() de MainActivity
     */
    public DatabaseHelper(@Nullable Context context, @Nullable String name, int version) {
        super(context, name, null, version);
        this.context = context;
        this.dbName = name;
        this.dbPath = "data/data/"+"com.example.ensem_map"+"/databases/";
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    /**
     * fonction pour initialiser la base de données
     */
    public void initDatabase(){
        try {
            copyDatabase();
        }catch (Exception e){
            e.printStackTrace();
        }
        try {
            openDataBase();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * methode qui copie la base de données depuis les assets vers dbPath = data/data/"+"com.example.ensem_map"+"/databases/
     * En réalité on ne devrait la copier que si la base de données n'existe pas encore dans l'appareil mais cela permet de faire les mises à jours
     * voir avec onUpgrade()...
     */
    private void copyDatabase(){
        this.getReadableDatabase();
        try{
            assert context != null;
            InputStream ios = context.getAssets().open(dbName);
            OutputStream os = new FileOutputStream(dbPath + dbName);
            byte[] buffer = new byte[1024];
            int len;
            while((len = ios.read(buffer))>0){
                os.write(buffer,0,len);
            }
            os.flush();
            ios.close();
            os.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void openDataBase(){
        String filePath = dbPath + dbName;
        this.db=SQLiteDatabase.openDatabase(filePath,null,0);
    }

    public void close(){
        if(db!=null){
            this.db.close();
        }
    }

    /**
     * fonction pour faire des requettes,
     * par exemple : makeQuery("select * from pointtable")
     *     a pour resultat : [0 2000 5700 0 1,2,4,7,8 ENSEMMap null null, 1 1900 5570 0 0 Accueil null null, ...]
     */
    public ArrayList<String> makeQuery(String SQLReq){
        ArrayList<String> array_list = new ArrayList<>();
        try {
            @SuppressLint("Recycle") Cursor c = db.rawQuery(SQLReq, new String[]{});
            int n = c.getColumnCount();
            while (c.moveToNext()){
                StringBuilder res = new StringBuilder(c.getString(0));
                for(int i=1; i<n; i++) {
                    res.append(" ").append(c.getString(i));
                }
                array_list.add(res.toString());
            }
        }catch (Exception e){
            array_list.add("req invalide");
        }
        return array_list;
    }

    /**
     * fonction qui recupère les les nom prenom personnel et nom salle depuis la db
     * puis elle les mets dans un ArrayAdapteur qu'elle retourne
     * utile pour l'adapteur de l'autoCompletTextView dans MainActivity
     */
    public ArrayAdapter<String> createAdapter(Context context){
        ArrayList<String> arrayList;
        arrayList = makeQuery("Select PrenomPersonnel, NomPersonnel from PointTable WHERE PrenomPersonnel not null");
        arrayList.addAll(makeQuery("Select NomSalle from PointTable WHERE NomSalle not NULL"));
        return new ArrayAdapter<>(context, android.R.layout.simple_list_item_1,arrayList.toArray(new String[0]));
    }

    /**
     *création un vector contenant les Points dans l'ordre d'apparition dans la base de données
     * utile pour la création du graphParliste
     */
    public Vector<Point> lecturePoint() {
        Vector<Point> lecturePoint = new Vector<>();
        ArrayList<String> arrayList = makeQuery("select idpoint,x,y,etage,pointsvoisins from pointtable");
        int n = arrayList.size();
        for (int i=0; i<n; i++){
            String[] string = arrayList.get(i).split(" ");
            String[] sVoisins = string[4].split(",");
            int[] iVoisins = new int[sVoisins.length];
            for(int j=0;j<sVoisins.length; j++){
                iVoisins[j]=Integer.parseInt(sVoisins[j]);
            }
            lecturePoint.add(new Point(Integer.parseInt(string[0]),Integer.parseInt(string[1]),Integer.parseInt(string[2]),Integer.parseInt(string[3]),iVoisins));
        }
        return(lecturePoint);
    }

    // recherche = etRecherche.getText().toString().split(" ");

    /**
     * fonction pour retourner l'id du point de destination à partir de la recherche dans l'etRecherche
     * retourne l'id à partir du nom de la salle ou de l'identité du personnel
     */
    public String getIdDestination(String[] recherche){
        String id = "0";
        try {
            if(recherche.length==2) {
                id = makeQuery("Select IdPoint from PointTable WHERE PrenomPersonnel = \"" + recherche[0] + "\" and NomPersonnel = \"" + recherche[1] + "\" or NomSalle = \""+ recherche[0]+" "+ recherche[1]+"\"").get(0);
            }
            if(recherche.length==1){
                id = makeQuery("Select IdPoint from PointTable WHERE NomSalle = \""+ recherche[0]+"\"").get(0);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return(id);
    }

    /**
     * fonction qui retourne la liste des points correspondants aux id contenus dans le tableau de string en entrée
     * utile pour tracer les points et le chemin sur le plan
     */
   public Vector<Point> listeIdToListePoint(String[] cheminId){
       Vector<Point> cheminPoint = new Vector<>();
       for (String s : cheminId) {
           ArrayList<String> arrayList = makeQuery("select idpoint,x,y,etage,pointsvoisins from pointtable where idpoint = " + s);
           String[] string = arrayList.get(0).split(" ");
           String[] sVoisins = string[4].split(",");
           int[] iVoisins = new int[sVoisins.length];
           for (int j = 0; j < sVoisins.length; j++) {
               iVoisins[j] = Integer.parseInt(sVoisins[j]);
           }
           cheminPoint.add(new Point(Integer.parseInt(string[0]), Integer.parseInt(string[1]), Integer.parseInt(string[2]), Integer.parseInt(string[3]), iVoisins));
       }
       return cheminPoint;
   }
}
