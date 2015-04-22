package com.techery.spares.adapter;

import java.util.ArrayList;

public interface IRoboSpiceAdapter<T> {

    int getCount();

    void clear();

    void addItems(ArrayList<T> baseItemClasses);

    void notifyDataSetChanged();
}
