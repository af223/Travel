package com.codepath.travel.models;

import android.content.DialogInterface;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;
import java.util.Collections;

public class FilterDialog {

    private final ArrayList<Integer> typeList;
    private final String[] typeArray;
    private final TextView tvType;
    private final boolean[] selectedType;

    public FilterDialog(boolean[] selectedType, ArrayList<Integer> typeList, String[] typeArray, TextView tvType) {
        this.selectedType = selectedType;
        this.typeList = typeList;
        this.typeArray = typeArray;
        this.tvType = tvType;
    }

    public void buildSelectorDialog(AlertDialog.Builder builder) {
        builder.setTitle("Select a category");
        builder.setCancelable(false);
        builder.setMultiChoiceItems(typeArray, selectedType, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if (isChecked) {
                    typeList.add(which);
                    Collections.sort(typeList);
                } else {
                    typeList.remove(Integer.valueOf(which));
                }
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
                resetCategoryFilter();
                builder.show();
            }
        });
    }

    public void resetCategoryFilter() {
        for (int i = 0; i < selectedType.length; i++) {
            selectedType[i] = false;
            typeList.clear();
            tvType.setText("");
        }
    }
}
