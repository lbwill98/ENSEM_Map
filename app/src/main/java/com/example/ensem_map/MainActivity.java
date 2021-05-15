package com.example.ensem_map;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.pdf.PdfDocument;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Vector;

public class MainActivity extends AppCompatActivity {

    //Déclaration des attriuts graphiques
    AutoCompleteTextView etRecherche;
    SwitchCompat swtcMobiliteReduite;
    Button btnRecherche;
    static ImageView ivQRCode;
    com.google.android.material.tabs.TabLayout tableLayout;
    ViewPager2 vpPlan;

    static ProgressDialog progressDialog;

    MyAdapter myAdapter;
    BitmapHelper bitmapHelper;
    DatabaseHelper databaseHelper;
    GrapheParListe grapheParListe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //on vérifie les permissions
        checkPermissions();

        //Assignations des attributs avec éléments graphques xml
        etRecherche = findViewById(R.id.etRecherche);
        swtcMobiliteReduite = findViewById(R.id.swtcMobiliteReduite);
        btnRecherche = findViewById(R.id.btnRecherche);
        ivQRCode = findViewById(R.id.ivQRCode);
        tableLayout = findViewById(R.id.tableLayout);
        vpPlan = findViewById(R.id.vpPlan);

        // Save switch state in shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences( "save", MODE_PRIVATE);
        swtcMobiliteReduite.setChecked(sharedPreferences.getBoolean("value",true));
        swtcMobiliteReduite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(swtcMobiliteReduite.isChecked()){
                    //when switch checked
                    SharedPreferences.Editor editor = getSharedPreferences("save",MODE_PRIVATE).edit();
                    editor.putBoolean("value", true);
                    editor.apply();
                    swtcMobiliteReduite.setChecked(true);
                }
                else{
                    // when switch unchecked
                    SharedPreferences.Editor editor = getSharedPreferences("save",MODE_PRIVATE).edit();
                    editor.putBoolean("value", false);
                    editor.apply();
                    swtcMobiliteReduite.setChecked(false);
                }
            }
        });

        //On met les images des plans des étages sur le viewPager avec la class myAdapter
        myAdapter = new MyAdapter();
        vpPlan.setAdapter(myAdapter);

        //On se met par default sur l'étage 0
        vpPlan.setCurrentItem(1);

        //On affiche les numéros des étages avec le tabLayout
        new TabLayoutMediator(tableLayout, vpPlan,  (tab, position) -> tab.setText("  "+(position - 1)+"  ")).attach();

        //initialisation de l'instance de la classe BitmapHelper
        bitmapHelper = new BitmapHelper(getResources());

        //initialisation de l'instance de la classe DatabaseHelper
        databaseHelper = new DatabaseHelper(MainActivity.this, "ENSEMMapDataBase.db",1);
        databaseHelper.initDatabase();

        //on place l'adapteur sur l'autoCompletTextView
        etRecherche.setAdapter(databaseHelper.createAdapter(MainActivity.this));

        //on affecte la bonne valeur de densité d'écran à la variable density de File, utile pour la génération des fichiers
        File.screenDensity = getResources().getDisplayMetrics().density;

        //on cree le graphparliste et on lance la recherche de chemin depuis le point 0 vers tous les autres points
        Vector<Point> points = databaseHelper.lecturePoint();
        grapheParListe = new GrapheParListe(points);
        Route.S = grapheParListe.plusCourtChemin(0,points);
        Route.chemins = GrapheParListe.chemin(0,Route.S,points);
        for(String s:Route.chemins){
            System.out.println(s);
        }

        btnRecherche.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                MyAsyncTask myAsyncTask = new MyAsyncTask();
                myAsyncTask.execute();
            }
        });

    }

    /**
     * tache asynchrone qui permet d'exécuter les différentes fonctions de la recherche en parralèle de l'affichage des éléments graphiques
     * on affiche un progressDialog pendant l'exécution de ces fonctions
     */
    private class MyAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            //progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected Void doInBackground(Void... arg) {

            progressDialog.setMessage("Recherche de l'itinéraire");
            int idDestination = Integer.parseInt(databaseHelper.getIdDestination(etRecherche.getText().toString().split(" ")));
            System.out.println(idDestination);
            String[] cheminId = Route.chemins[GrapheParListe.indicesommetDansS(Route.S,GrapheParListe.idHahMap.get(idDestination))].split("-");
            Vector<Point> cheminPoint = databaseHelper.listeIdToListePoint(cheminId);
            Route.setEtagesPresents(cheminPoint);

            String fileName = "plan";
            PdfDocument pdfDocument = new PdfDocument();
            File.createFolder();

            for(int etage : Route.etagesPresents){
                Bitmap Btm = bitmapHelper.loadBitmapFromDrawable(MyAdapter.images[etage+1]); //etage var goes from -1 to 2 and we want a value from 0 to 3 for images tab index
                Canvas canvasBtm = new Canvas(Btm);
                bitmapHelper.drawRoute(canvasBtm, Route.getPointsPresents(cheminPoint,etage));
                File.saveBitmapToPNGFile(File.etagesNames[etage+1],Btm);
                File.addPageWithBitmapToPdf(Btm, pdfDocument);
            }
            String uriString = File.savePdfDocument(fileName, pdfDocument);


            progressDialog.setMessage("Téléchargement des fichiers");
            //FirebaseHelper.uploadFile(uriString,fileName);
            progressDialog.dismiss();

            return  null;
        }

        @Override
        protected void onPostExecute(Void a) {
            vpPlan.setAdapter(myAdapter);
            vpPlan.setCurrentItem(1);
        }
    }

    /**
     * enlève le focus sur etRecherche lorsque l'on touche ailleurs
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof AutoCompleteTextView) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }

    /**
     * enlève le focus sur etRecherche quand on met l'application en pause
     */
    @Override
    protected void onPause() {
        super.onPause();
        etRecherche.clearFocus();
    }

    /**
     * Vérification des permissions, l'application ferme si les permissions sont refusées
     */
    private void checkPermissions(){
        boolean permissionAcceptee = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED;
        if(!permissionAcceptee) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},10);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean demandeRefusee = requestCode!=10 || (grantResults.length == 0) || (grantResults[0]==PackageManager.PERMISSION_DENIED);
        if(demandeRefusee){
            System.exit(0);
        }
    }

    /**
     * génération du QRcode et affichage sur ivQRcode
     */
    public static void showQRCode(String string){
        QRcode qRcode = new QRcode(string,ivQRCode.getWidth(),ivQRCode.getHeight());
        ivQRCode.setImageBitmap(qRcode.getBitmap());
    }

    /**
     * initialiser le switch et ensuite vérifier son état
     */

}