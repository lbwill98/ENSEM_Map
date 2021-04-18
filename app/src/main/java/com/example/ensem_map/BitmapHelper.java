package com.example.ensem_map;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.Vector;

public class BitmapHelper {

    //taille des cartes en pixels
    public static final int width = 4000, height = 7000;
    private final BitmapFactory.Options options;
    private final Resources resources;

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
        //this.options.inPreferredConfig = Bitmap.Config.RGB_565;
        this.resources = resources;
    }

    /**
     * fonction permettant de charger une image du drawable sous forme de bitmap
     */
    public Bitmap loadBitmapFromDrawable(int drawable){
        return (BitmapFactory.decodeResource(this.resources,drawable,this.options));
    }

    /**
     *fonction pour tracer le chemin representer par le vector cheminPoint
     * on trace avec canvas associé à la bitmap d'un étage du plan
     */
    public void drawRoute(Canvas canvas, Vector<Point> cheminPoint){
        Paint paint = new Paint();
        paint.setARGB(255,255,0,0);
        paint.setStrokeWidth(10);
        canvas.drawCircle(cheminPoint.get(0).getX(),cheminPoint.get(0).getY(),50,paint);
        for(int i=0; i<cheminPoint.size()-1;i++) {
            canvas.drawLine(cheminPoint.get(i).getX(),cheminPoint.get(i).getY(),cheminPoint.get(i+1).getX(),cheminPoint.get(i+1).getY(),paint);
        }
        canvas.drawCircle(cheminPoint.lastElement().getX(),cheminPoint.lastElement().getY(),50,paint);
    }

}
