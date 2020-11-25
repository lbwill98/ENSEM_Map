package com.example.ensem_map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * classe permettant de gerer la base de données
 * la base de données ENSEMMapDataBase est stocker dans les assets
 * elle est copiée dans data/data/"+"com.example.ensem_map"+"/databases/ lors du onCreate() de MainActivity, fonction initDatabase()
 * puis on fait des requettes avec la fonction makeQuery()
 * A FAIRE : completer la base de données, dans un premier temps pour le rdc
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private final Context context;
    private final String dbName;
    private final String dbPath;

    private SQLiteDatabase db;

    /**
     * constructeur, contexte MainActivity.this, nom et version de la database sont rentrés dans le onCreate() de MainActivity
     * @param context
     * @param name
     * @param version
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
        copyDatabase();
        openDataBase();
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
     * @param SQLReq
     * @return
     */
    public ArrayList<String> makeQuery(String SQLReq){
        ArrayList<String> array_list = new ArrayList<String>();
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

    //cree un vector contenant les Points dans l'ordre d'apparition dans le fichier sations
    /*public Vector<Point> lecturePoint() {
        Vector<Point> lecturePoint = new Vector<Point>();
        ArrayList<String> arrayList = makeQuery("select idpoint,x,y,etage,pointsvoisins from pointtable");
        int n = arrayList.size();
        for (int i=0; i<n; i++){
            String string[] = arrayList.get(i).split(" ");
            String[] sVoisins = string[4].split(",");
            int iVoisins[] = new int[sVoisins.length];
            for(int j=0;j<sVoisins.length; j++){
                iVoisins[j]=Integer.parseInt(sVoisins[j]);
            }
            lecturePoint.add(new Point(Integer.parseInt(string[0]),Integer.parseInt(string[1]),Integer.parseInt(string[2]),Integer.parseInt(string[3]),iVoisins));
        }
        return(lecturePoint);
    }*/

}
