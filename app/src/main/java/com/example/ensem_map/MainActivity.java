package com.example.ensem_map;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;

import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {

    //Déclaration des attriuts graphiques
    EditText etRecherche;
    Switch swtcMobiliteReduite;
    Button btnRecherche;
    static ImageView ivQRCode;
    com.google.android.material.tabs.TabLayout tableLayout;
    ViewPager2 vpPlan;

    static int[] imagesPlan = {R.drawable.rdj,R.drawable.rdc,R.drawable.premier, R.drawable.deuxieme,R.drawable.troisieme};

    MyAdapter myAdapter;
    BitmapHelper bitmapHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //on vérifie les permissions
        checkPermissions();

        //Assiciations des attributs avec éléments graphques xml
        etRecherche = findViewById(R.id.etRecherche);
        swtcMobiliteReduite = findViewById(R.id.swtcMobiliteReduite);
        btnRecherche = findViewById(R.id.btnRecherche);
        ivQRCode = findViewById(R.id.ivQRCode);
        tableLayout = findViewById(R.id.tableLayout);
        vpPlan = findViewById(R.id.vpPlan);

        //On met les images des plans des étages sur le viewPager avec la class myAdapter
        myAdapter = new MyAdapter(imagesPlan);
        vpPlan.setAdapter(myAdapter);

        //On se met par default sur l'étage 0
        vpPlan.setCurrentItem(1);

        //On affiche les numéros des étages avec le tabLayout
        new TabLayoutMediator(tableLayout, vpPlan,  (tab, position) -> tab.setText("  "+(position - 1)+"  ")).attach();

        //initialisation de l'instance de la classe BitmapHelper
        bitmapHelper = new BitmapHelper(getResources());

        btnRecherche.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
                //A FAIRE : gerer le nom des fichiers (itinéraire rechercher et date+heure de recherche)
                String fileName = "plan";
                File.createFolder();
                String uriString = File.savePdfDocument(fileName,File.addPageWithBitmapToPdf(
                        bitmapHelper.loadBitmapFromDrawable(R.drawable.premier),File.addPageWithBitmapToPdf(bitmapHelper.loadBitmapFromDrawable(R.drawable.rdc), new PdfDocument())));
                FirebaseHelper.uploadFile(uriString,fileName);
            }
        });
    }

    /**
     * enlève le focus sur etRecherche lorsque l'on touche ailleurs
     * @param event
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
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
}