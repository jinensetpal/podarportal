package com.example.firstapp.podarportal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private static EditText mPassword, mEmail;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    String fullName;
    ArrayList<String> attendance= new ArrayList<>();
    ArrayList<String> attendance_key=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("/users/");
    }

    private boolean isValid() {
        String password = mPassword.getText().toString();
        String username = mEmail.getText().toString();
        if (password.length() >= 8 && username != null)
            return true;
        else
            return false;
    }

    public void toMainPage(View view) throws Exception {
        final String email = mEmail.getText().toString();
        final String password = mPassword.getText().toString();
        mDatabase.child(uid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                fullName = dataSnapshot.child("fullName").getValue(String.class);
                for (DataSnapshot child : dataSnapshot.child("attendance").getChildren()) {
                    attendance.add(child.getValue(String.class));
                    attendance_key.add(child.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Error", "Failed to read value.", databaseError.toException());
            }
        });
        Toast toast;
        if (isValid()) {
            final ProgressDialog progressDialog = ProgressDialog.show(this, "Please wait", "Processing", true);
            (mAuth.signInWithEmailAndPassword(email, password)).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
                        Intent intent = new Intent(LoginActivity.this,MainPage.class);
                        intent.putExtra("name",fullName);
                        intent.putExtra("email",email);
                        intent.putStringArrayListExtra("attendance",attendance);
                        intent.putStringArrayListExtra("attendance_key", attendance_key);
                        startActivity(intent);
                    } else {
                        (mAuth.createUserWithEmailAndPassword(email, password)).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressDialog.dismiss();
                                if (task.isSuccessful()) {
                                    Map<String, String> notes=new HashMap<>();
                                    mDatabase.child(uid()).setValue(notes);
                                    Map<String, String> userData = new HashMap<>();
                                    userData.put("email", email);
                                    mDatabase.child(uid()).setValue(userData);
                                    Intent intent = new Intent(LoginActivity.this, UpdateActivity.class);
                                    intent.putExtra("uid",uid());
                                    startActivity(intent);
                                } else {
                                    String e = task.getException().toString();
                                    Toast.makeText(LoginActivity.this, e, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }

                }
            });
        } else if (email == null) {
            toast = Toast.makeText(this, "Add your email!", Toast.LENGTH_LONG);
            toast.show();
        } else {
            toast = Toast.makeText(this, "Minimum password length is 8 characters", Toast.LENGTH_LONG);
            toast.show();
        }
    }

    public String uid() {
        String s = mEmail.getText().toString();
        String res = "";
        for (int i = 0; i < s.length(); i++) {
            if (!s.substring(i, (i + 1)).equals("@")) {
                if (s.substring(i, (i + 1)).equals("."))
                    continue;
                res += s.substring(i, i + 1);
            }
            else
                break;
        }
        return res;
    }
}