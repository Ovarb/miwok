package com.example.android.miwok;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class NumbersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_numbers);

        ArrayList<String> words = new ArrayList<>();
        words.add("one");
        words.add("two");
        words.add("three");
        words.add("four");
        words.add("five");
        words.add("six");
        words.add("seven");
        words.add("eight");
        words.add("nine");
        words.add("ten");

        //find the parent View
        LinearLayout rootView = (LinearLayout) findViewById(R.id.rootView);

        int index = 0;
        int arrayListLength = words.size();
        while (index < arrayListLength) {

            //create new View element
            TextView wordView = new TextView(this);
            wordView.setText(words.get(index));
            //add new View in the parent View
            rootView.addView(wordView);

            index++;
        }







    }
}
