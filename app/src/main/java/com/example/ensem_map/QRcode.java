package com.example.ensem_map;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

/**
 * class pour gerer les QRcode
 * le but est de fournir une bitmap : - de la bonne taille (celle de l'imageView, ivQRcode)
 *                                    - correspondant au QRcode de l'attribut string (le lien de telechargement du pdf via firebase)
 */
public class QRcode {

    private final String string;
    private final int width,height;
    private final Bitmap bitmap;

    public QRcode(String string, int width, int height) {
        this.string = string;
        this.width = width;
        this.height = height;
        this.bitmap = createBitmap();
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    /**
     * Methode retournant la bitmap de dimension width et heigth repressantant le code QR de l'attribut string
     * si le string est null, retourne la bitmap erreur
     */
    private Bitmap createBitmap(){
        Bitmap btm = erreurBitmap();
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try{
            BitMatrix bitMatrix = multiFormatWriter.encode(this.string, BarcodeFormat.QR_CODE,this.width,this.height);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            btm = barcodeEncoder.createBitmap(bitMatrix);
        }catch (Exception e){
            e.printStackTrace();
        }
        return btm;
    }

    /**
     * creation d'une bitmap indicant une erreur
     */
    private Bitmap erreurBitmap(){
        Bitmap btm = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(btm);
        canvas.drawText("erreur",this.width/2, this.height/2,new Paint());
        return  btm;
    }
}
