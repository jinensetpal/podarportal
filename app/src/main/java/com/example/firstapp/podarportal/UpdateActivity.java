package com.example.firstapp.podarportal;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UpdateActivity extends AppCompatActivity {

    ImageView imageView;
    EditText editText;
    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        imageView = findViewById(R.id.imageView);
        editText = findViewById(R.id.fullName);
        databaseReference = FirebaseDatabase.getInstance().getReference("/users/" + uid() + "/");
        try {
            imageView.setImageBitmap(getImageBitmap());
        }catch (Exception ignored){
        }
    }

    private Bitmap getImageBitmap(){
        Bitmap bm = null;
        FileInputStream fileInputStream;
        try {
            fileInputStream = getApplicationContext().openFileInput(uid());
            bm = BitmapFactory.decodeStream(fileInputStream);
            fileInputStream.close();
        } catch (Exception e) {
            Log.d("loadImage", "No image loaded");
        }
        return bm;
    }

    public void uploadProfilePic(View view) throws IOException {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select an Image"), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            Bitmap bitmap = Bitmap.createScaledBitmap(MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData()), 480, 480, false);
            imageView.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void toMainPage(View view) {
        saveImage();
        progressDialog.dismiss();
        startActivity(new Intent(this, MainPage.class));
    }

    public void saveImage() {
        progressDialog = ProgressDialog.show(this, "Please Wait...", "Processing", true);
        databaseReference.child("fullName").setValue(editText.getText().toString());
        try {
            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);
            File file = new File(Environment.getExternalStorageDirectory() + File.separator + uid() + ".png");
            FileOutputStream fileOutputStream;
            try {
                fileOutputStream = getApplicationContext().openFileOutput(uid(), Context.MODE_PRIVATE);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                fileOutputStream.close();
            } catch (Exception e) {
                Log.d("saveImage", "This failed too.");
            }
        } catch (Exception ignored) {
        }
    }

    public String uid() {
        String s = getIntent().getStringExtra("email");
        String res = "";
        for (int i = 0; i < s.length(); i++) {
            if (!s.substring(i, (i + 1)).equals("@")) {
                if (s.substring(i, (i + 1)).equals("."))
                    continue;
                res += s.substring(i, i + 1);
            } else
                break;
        }
        return res;
    }
}