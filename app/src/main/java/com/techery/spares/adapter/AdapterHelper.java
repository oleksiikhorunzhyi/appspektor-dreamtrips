package com.techery.spares.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import timber.log.Timber;

public class AdapterHelper {
   private final LayoutInflater layoutInflater;

   public AdapterHelper(LayoutInflater layoutInflater) {
      this.layoutInflater = layoutInflater;
   }

   public AbstractCell buildCell(Class<? extends AbstractCell> cellClass, ViewGroup parent) {
      Layout layoutAnnotation = cellClass.getAnnotation(Layout.class);

      View cellView = layoutInflater.inflate(layoutAnnotation.value(), parent, false);

      return (AbstractCell) buildHolder(cellClass, cellView);
   }

   public static RecyclerView.ViewHolder buildHolder(Class<? extends RecyclerView.ViewHolder> cellClass, View cellView) {
      RecyclerView.ViewHolder cellObject = null;
      try {

         Constructor<? extends RecyclerView.ViewHolder> constructor = cellClass.getConstructor(View.class);
         cellObject = constructor.newInstance(cellView);

      } catch (InstantiationException | IllegalAccessException |
            NoSuchMethodException | InvocationTargetException e) {
         Timber.e(e, "Can't create cell");
      }
      return cellObject;
   }
}
