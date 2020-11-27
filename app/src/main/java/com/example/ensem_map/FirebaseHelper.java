package com.example.ensem_map;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

/**
 * class permettant de gerer la communication avec firebase
 * utile pour l'envoie du pdf et la recupération du lien de telechargement
 */
public class FirebaseHelper {

    /**
     * fonction qui envoie le pdf sur firestor (uriString = uri du fichier pdf)
     * elle recupère l'url de telechargement une fois le fichier uploader
     * puis elle fait appel à showQRCode pour afficher le qrCode dans limageView ivQRcode
     */
    public static void uploadFile(String uriString , String fileName) {
        Uri uri = Uri.parse(uriString);
        FirebaseStorage storage;
        storage = FirebaseStorage.getInstance();

        final StorageReference storageReference = storage.getReference();//return root path

        final StorageReference ref = storageReference.child("Uploads").child(fileName);

        ref.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                uriTask.addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        MainActivity.showQRCode(Objects.requireNonNull(uriTask.getResult()).toString());
                        MainActivity.progressDialog.dismiss();
                    }
                });
            }
        })/*.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                MainActivity.progressDialog.setMessage("Téléchargement des fichiers");
                MainActivity.progressDialog.setProgress((int)(MainActivity.progressDialog.getProgress()+(100-MainActivity.progressDialog.getProgress())*snapshot.getBytesTransferred()/snapshot.getTotalByteCount()));
            }
        })*/;
    }
}

