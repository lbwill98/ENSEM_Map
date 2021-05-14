package com.example.ensem_map;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Environment;

import androidx.annotation.RequiresApi;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * class permettant de gerer les fichiers utils pour l'application
 * ils sont stockers dans un dossier ENSEM_Map, présent dans le stockage interne de la tablette
 * les fichiers sont : les pdf des itinéraires, à uploader sur firebase
 *                     les images PNG des itinéraires, à afficher dans l'application
 */
public class File {
    public static final String DirectoryPath = Environment.getExternalStorageDirectory().toString()+"/ENSEM_Map";
    public static String[] etagesNames = {"rdj.png","rdc.png","premier.png","deuxieme.png","troisieme.png"};
    /*public static final String rdjName = "rdj.png";
    public static final String rdcName = "rdc.png";
    public static final String premierName = "premier.png";
    public static final String deuxiemeName = "deuxieme.png";
    public static final String troisiemeName = "troisieme.png";*/

    //A FAIRE : gerer la densité de l'ecran , controlleur avec getResources().getDisplayMetrics().density
    public static double screenDensity = 0.75;

    /**
     * création du dossier ENSEM_Map dans l'espace de stockage dee la tablette
     * Ne fait rien si le dossier existe déjà
     * A FAIRE : vider le dossier quand il est trop volumineux
     */
    public static void createFolder() {
        java.io.File file = new java.io.File(Environment.getExternalStorageDirectory(), "ENSEM_Map".trim());
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    /**
     * methode pour sauvegarder une bitmap au format PNG dans le stockage interne de la tablette (dossier ENSEM_Map)
     * elle prend en entrée le nom du fichier et la bitmap
     * elle retourne une string correspondant à l'uri du fichier
     * utile pour sauvegarder les plans avec le chemin tracer pour les afficcher dans l'image View ivPlan
     */
    public static String saveBitmapToPNGFile(String fileName, Bitmap btmPlanEtage){
        java.io.File myDir = new java.io.File(DirectoryPath);
        java.io.File file = new java.io.File(myDir, fileName);
        if (file.exists()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            btmPlanEtage.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file.toURI().toString();
    }

    /**
     * fonction qui ajoute une page contenant l'image bitmapPlan au document pdfDocument
     * elle retourne pdfDocument avec la page supplementaire
     * A FAIRE : gerer screenDensity (ne vaut pas 0.75 pour toutes les tablettes, il faut la récupérer aprs la methode onCreate de MainActivity)
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static PdfDocument addPageWithBitmapToPdf(Bitmap bitmapPlan, PdfDocument pdfDocument){
        int pageNumber = pdfDocument.getPages().size()+1;
        int pageWidth = (int)(BitmapHelper.width* screenDensity);
        int pageHeight = (int)(BitmapHelper.height* screenDensity) ;
        PdfDocument.PageInfo myPageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create();
        PdfDocument.Page myPage = pdfDocument.startPage(myPageInfo);
        Canvas canvasPage = myPage.getCanvas();
        canvasPage.drawBitmap(bitmapPlan, 0, 0, null);
        pdfDocument.finishPage(myPage);
        return pdfDocument;
    }

    /**
     * methode pour sauvegarder un document pdf dans le stockage interne de la tablette (dossier ENSEM_Map)
     * prend en entrée le nom du fichier et le pdfDocument à sauvegarder
     * elle retourne une string correspondant à l'uri du fichier
     * utile pour sauvegarder les pdf à uploader sur firebase
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String savePdfDocument(String fileName, PdfDocument pdfDocument){
        java.io.File myDir = new java.io.File(DirectoryPath);
        java.io.File file = new java.io.File(myDir, fileName+".pdf");
        if (file.exists()) file.delete ();
        try {
            pdfDocument.writeTo(new FileOutputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        pdfDocument.close();
        return file.toURI().toString();
    }
}
