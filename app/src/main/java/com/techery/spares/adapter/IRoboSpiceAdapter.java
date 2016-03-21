package com.techery.spares.adapter;

import java.util.List;

public interface IRoboSpiceAdapter<T> {

    int getCount();

    void clear();

    void addItems(List<T> baseItemClasses);

    void notifyDataSetChanged();
}
