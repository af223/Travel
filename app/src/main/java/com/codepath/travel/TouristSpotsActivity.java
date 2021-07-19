package com.codepath.travel;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

public class TouristSpotsActivity extends AppCompatActivity {

    private TextView tvActivityType;
    private boolean[] selectedType;
    private ArrayList<Integer> typeList = new ArrayList<>();
    private String[] typeArray = {"Amusement Parks", "Art Galleries", "Beaches", "Gardens", "Hiking",
                                "Landmarks/Historical Buildings", "Museums", "Nightlife", "Shopping",
                                "Spas", "Sports", "Tours"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tourist_spots);

        tvActivityType = findViewById(R.id.tvActivityType);

        selectedType = new boolean[typeArray.length];

        tvActivityType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(TouristSpotsActivity.this);
                builder.setTitle("Select a category");
                builder.setCancelable(false);
                builder.setMultiChoiceItems(typeArray, selectedType, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if (isChecked) {
                            typeList.add(which);
                            Collections.sort(typeList);
                        } else {
                            typeList.remove(which);
                        }
                    }
                });

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        StringBuilder stringBuilder = new StringBuilder();
                        for (int i = 0; i < typeList.size(); i++) {
                            stringBuilder.append(typeArray[typeList.get(i)]);

                            if (i != typeList.size()-1) {
                                stringBuilder.append(", ");
                            }
                        }
                        tvActivityType.setText(stringBuilder.toString());
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.setNeutralButton("Clear All", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (int i = 0; i < selectedType.length; i++) {
                            selectedType[i] = false;
                            typeList.clear();
                            tvActivityType.setText("");
                        }
                    }
                });

                builder.show();
            }
        });
    }
}