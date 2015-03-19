package com.techery.spares.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.techery.spares.ui.view.cell.BaseCell;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import butterknife.ButterKnife;


public class AdapterHelper {
    private final LayoutInflater layoutInflater;

    public AdapterHelper(LayoutInflater layoutInflater) {
        this.layoutInflater = layoutInflater;
    }

    public AbstractCell buildCell(Class<? extends AbstractCell> cellClass, ViewGroup parent) {
        Layout layoutAnnotation = cellClass.getAnnotation(Layout.class);

        View cellView = layoutInflater.inflate(layoutAnnotation.value(), parent, false);

        AbstractCell cellObject = null;

        try {

            Constructor constructor = cellClass.getConstructor(View.class);

            cellObject = (AbstractCell)constructor.newInstance(cellView);

        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            Log.e(AdapterHelper.class.getSimpleName(),"", e);
        }

        return cellObject;
    }
}
