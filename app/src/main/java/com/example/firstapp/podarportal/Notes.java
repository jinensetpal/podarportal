package com.example.firstapp.podarportal;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Notes extends AppCompatActivity {

    private EditText mEditNote;
    private DatabaseReference mDatabase;
    Map<String, String> diary = new HashMap<>();
    private TextView noNotes;
    private LinearLayout mNotesOnly;
    private LinearLayout mCheckbox;
    private ArrayList<String> notes = new ArrayList<>();
    int count;
    int mediumAnimationDuration;
    private TextView deleteNoteHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        Button mButton = findViewById(R.id.delete_note_button);
        deleteNoteHeader = findViewById(R.id.delete_note_header);
        mNotesOnly = findViewById(R.id.notes_only);
        noNotes = findViewById(R.id.no_note_message);
        mCheckbox = findViewById(R.id.checkbox);
        mEditNote = findViewById(R.id.note_edit);
        mDatabase = FirebaseDatabase.getInstance().getReference("/users/" + uid() + "/notes/");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    String ch = child.getValue(String.class);
                    notes.add(ch);
                    diary.put("note " + diary.size(), ch);
                    if (diary.size() != 1)
                        displayNote(ch);
                }
                if (diary.size() == 1)
                    noNotes.setVisibility(View.VISIBLE);
                else
                    noNotes.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                noNotes.setVisibility(View.VISIBLE);
            }
        });
        count = diary.size();
        mButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                if (mNotesOnly.getVisibility() == View.VISIBLE) {
                    for (int i = 1; i < notes.size(); i++) {
                        CheckBox checkBox = new CheckBox(Notes.this);
                        checkBox.setText(notes.get(i));
                        checkBox.setId(i);
                        checkBox.setTextColor(Color.parseColor("#834C9B"));
                        checkBox.setTextSize(16);
                        checkBox.setTypeface(checkBox.getTypeface(), Typeface.BOLD);
                        mCheckbox.addView(checkBox);
                    }
                    mediumAnimationDuration = getResources().getInteger(android.R.integer.config_mediumAnimTime);
                    mNotesOnly.setAlpha(1);
                    mNotesOnly.animate()
                            .alpha(0)
                            .setDuration(mediumAnimationDuration)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    mNotesOnly.setVisibility(View.GONE);
                                    deleteNoteHeader.setAlpha(0);
                                    deleteNoteHeader.animate()
                                            .alpha(1)
                                            .setDuration(mediumAnimationDuration)
                                            .setListener(new AnimatorListenerAdapter() {
                                                @Override
                                                public void onAnimationEnd(Animator animation) {
                                                    mCheckbox.setAlpha(0);
                                                    mCheckbox.setVisibility(View.VISIBLE);
                                                    mCheckbox.animate()
                                                            .alpha(1)
                                                            .setDuration(mediumAnimationDuration);
                                                }
                                            });
                                }
                            });

                } else {
                    ArrayList<String> temp=new ArrayList<>();
                    int i = 1;
                    do {
                        if (!checkCheckedBoxes(i))
                            temp.add(notes.get(i));
                        i++;
                    } while (i <= notes.size());
                    notes=temp;
                    updateHashmap();
                    mNotesOnly.setVisibility(View.VISIBLE);
                    deleteNoteHeader.setAlpha(1);
                    deleteNoteHeader.animate()
                            .alpha(0)
                            .setDuration(mediumAnimationDuration);
                    mCheckbox.animate()
                            .alpha(0)
                            .setDuration(mediumAnimationDuration)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    mNotesOnly.setVisibility(View.VISIBLE);
                                    mNotesOnly.setAlpha(0);
                                    mNotesOnly.animate()
                                            .alpha(1)
                                            .setDuration(mediumAnimationDuration);
                                }
                            });
                    mCheckbox.setVisibility(View.GONE);
                    deleteNoteHeader.setVisibility(View.GONE);
                }
            }
        });
    }

    private void updateHashmap() {
        diary.clear();
        diary.put("note 0", "default");
        mNotesOnly.removeAllViews();
        mCheckbox.removeAllViews();
        for (int i = 1; i < notes.size(); i++) {
            diary.put("note " + diary.size(), notes.get(i));
            displayNote(notes.get(i));
            mDatabase.setValue(diary);
        }
    }

    private boolean checkCheckedBoxes(int id) {
        CheckBox checkBox = findViewById(getResources().getIdentifier(String.valueOf(id),"id",getApplicationContext().getPackageName()));
        return checkBox.isChecked();
    }

    public void saveNote(View view) {
        String note = mEditNote.getText().toString();
        if (note != null) {
            mEditNote.setText("");
            diary.put("note " + diary.size(), note);
            displayNote(note);
            mDatabase.setValue(diary);
        } else {
            Toast.makeText(this, "Cannot store empty note!", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("SetTextI18n")
    private void displayNote(String note) {
        LinearLayout linearLayout = findViewById(R.id.notes_only);
        TextView textView = new TextView(this);
        textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
        textView.setGravity(Gravity.CENTER);
        textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
        textView.setText((diary.size() - 1) + ". " + note);
        textView.setTextSize(16);
        linearLayout.addView(textView);
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