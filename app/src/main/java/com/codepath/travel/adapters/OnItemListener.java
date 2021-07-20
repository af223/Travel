package com.codepath.travel.adapters;

import java.time.LocalDate;

public interface OnItemListener {
    void onItemClick(int position, LocalDate date);
}
