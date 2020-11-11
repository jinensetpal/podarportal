package com.example.firstapp.podarportal;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.SimpleTimeZone;

public class MainPage extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    ArrayList<String> attendance = new ArrayList<>();
    ArrayList<String> attendance_days=new ArrayList<>();
    private Bitmap profilepic;
    String fullName;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final Intent intent = new Intent(this, Notes.class);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fullName = getIntent().getStringExtra("name");
        email = getIntent().getStringExtra("email");
        FloatingActionButton fab = findViewById(R.id.fab_diary);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra("email",email);
                startActivity(intent);
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        View view=navigationView.getHeaderView(0);
        try {
            File file = new File("/0/data/data/com.example.firstapp.podarportal/files/" + uid());
            profilepic = BitmapFactory.decodeStream(new FileInputStream(file));
            ImageView imageView = view.findViewById(R.id.imageView);
            imageView.setImageBitmap(profilepic);
        } catch (Exception ignored) {
        }
        TextView displayEmail = view.findViewById(R.id.displayEmail);
        TextView displayName = view.findViewById(R.id.displayName);
        try {
            attendance = getIntent().getStringArrayListExtra("attendance");
        } catch (Exception ignored) {
        }
        try {
            attendance_days = getIntent().getStringArrayListExtra("attendance_key");
        } catch (Exception ignored) {
        }
        displayName.setText(fullName);
        displayEmail.setText(email);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent intent = null;
        if (id == R.id.nav_home) {
            intent = new Intent(this, EventCalendar.class);
        } else if (id == R.id.nav_gallery) {
            intent = new Intent(this, Announcements.class);

        } else if (id == R.id.nav_slideshow) {
            intent = new Intent(this, Attendance.class);
            intent.putStringArrayListExtra("attendance", attendance);
            intent.putStringArrayListExtra("attendance_key", attendance_days);

        } else if (id == R.id.nav_tools) {
            intent = new Intent(this, UpdateActivity.class);
            intent.putExtra("email", email);
            intent.putExtra("pfp", profilepic);
        } else if (id == R.id.log_out)
            intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public String uid() {
        String s = email;
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