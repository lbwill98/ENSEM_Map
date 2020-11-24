package com.example.ensem_map;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BitmapHelper {

    //taille des cartes en pixels
    public static final int width = 4000, height = 7000;
    private BitmapFactory.Options options;
    private Resources resources;

    /**
     * constructeur permetant de déclarer les options et de récupérer les resources de MainActivity
     * déclarer depuis MainActivity
     * inScaled = false : on conserve les tailles originales des images
     *                    sinon elles sont automatiquement adaptes à la résolution et densité de l'écran
     * inMutable = true : on peut modifier les bitmap en dessinant avec des canvas
     */
    public BitmapHelper(Resources resources) {
        this.options = new BitmapFactory.Options();
        this.options.inScaled = false;
        this.options.inMutable = true;
        this.resources = resources;
    }

    /**
     * fonction permettant de charger une image du drawable sous forme de bitmap
     * @param drawable
     * @return
     */
    public Bitmap loadBitmapFromDrawable(int drawable){
        return (BitmapFactory.decodeResource(this.resources,drawable,this.options));
    }

}
