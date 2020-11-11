package com.example.firstapp.podarportal;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class EventCalendar extends AppCompatActivity {

    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("/events/");
    ArrayList<String> mEventList = new ArrayList<>();
    ArrayList<String> mEventName = new ArrayList<>();
    ArrayList<String> mFrom = new ArrayList<>();
    ArrayList<String> mTo = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_calendar);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    String get = child.getKey();
                    mEventList.add(get);
                    mEventName.add(dataSnapshot.child(get).child("name").getValue(String.class));
                    mFrom.add(dataSnapshot.child(get).child("from").getValue(String.class));
                    mTo.add(dataSnapshot.child(get).child("to").getValue(String.class));
                    displayEvent();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void displayEvent() {
        LinearLayout linearLayout = findViewById(R.id.events_list);
        TextView textView = new TextView(this);
        textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
        textView.setGravity(Gravity.CENTER);
        textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
        textView.setTextColor(Color.parseColor("#834C9B"));
        textView.setTextSize(16);
        textView.setText(mEventName.get(mEventName.size()-1));
        linearLayout.addView(textView);
    }
}
