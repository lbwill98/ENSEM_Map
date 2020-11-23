package com.example.ensem_map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;

import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    //Déclaration des attriuts graphiques
    EditText etRecherche;
    Switch swtcMobiliteReduite;
    Button btnRecherche;
    ImageView ivQRCode;
    com.google.android.material.tabs.TabLayout tableLayout;
    ViewPager2 vpPlan;

    int[] imagesPlan = {R.drawable.rdj,R.drawable.rdc,R.drawable.premier, R.drawable.deuxieme,R.drawable.troisieme};

    MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //on enlève la barre de l'application
        Objects.requireNonNull(getSupportActionBar()).hide();

        //on vérifie les permissions
        checkPermissions();

        //Assiciations des attributs avec éléments graphques xml
        etRecherche = (EditText)findViewById(R.id.etRecherche);
        swtcMobiliteReduite = (Switch) findViewById(R.id.swtcMobiliteReduite);
        btnRecherche = (Button) findViewById(R.id.btnRecherche);
        ivQRCode =(ImageView) findViewById(R.id.ivQRCode);
        tableLayout = (com.google.android.material.tabs.TabLayout)findViewById(R.id.tableLayout);
        vpPlan = (ViewPager2)findViewById(R.id.vpPlan);

        //On met les images des plans des étages sur le viewPager avec la class myAdapter
        myAdapter = new MyAdapter(imagesPlan);
        vpPlan.setAdapter(myAdapter);

        //On se met par default sur l'étage 0
        vpPlan.setCurrentItem(1);

        //On affiche les numéros des étages avec le tabLayout
        new TabLayoutMediator(tableLayout, vpPlan,  (tab, position) -> tab.setText("  "+(position - 1)+"  ")).attach();

        btnRecherche.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showQRCode();
            }
        });
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
    private void showQRCode(){
        QRcode qRcode = new QRcode(etRecherche.getText().toString(),ivQRCode.getWidth(),ivQRCode.getHeight());
        ivQRCode.setImageBitmap(qRcode.getBitmap());
    }
}