package com.worldventures.core.ui.view.adapter;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.ui.view.cell.AbstractCell;

import java.lang.reflect.Constructor;

import timber.log.Timber;

public class AdapterHelper {
   private final LayoutInflater layoutInflater;

   public AdapterHelper(LayoutInflater layoutInflater) {
      this.layoutInflater = layoutInflater;
   }

   @Nullable
   AbstractCell buildCell(Class<? extends AbstractCell> cellClass, ViewGroup parent) {
      final Layout layoutAnnotation = cellClass.getAnnotation(Layout.class);
      return (AbstractCell) (layoutAnnotation != null ?
            createHolderWithAnnotation(cellClass, layoutAnnotation, parent) :
            createHolderWithoutAnnotation(cellClass, parent)
      );
   }

   @Nullable
   private RecyclerView.ViewHolder createHolderWithAnnotation(Class<? extends RecyclerView.ViewHolder> cellClass, Layout layoutAnnotation, ViewGroup parent) {
      final View cellView = layoutInflater.inflate(layoutAnnotation.value(), parent, false);
      RecyclerView.ViewHolder cellObject = null;
      try {

         Constructor<? extends RecyclerView.ViewHolder> constructor = cellClass.getConstructor(View.class);
         cellObject = constructor.newInstance(cellView);

      } catch (Exception e) {
         Timber.d(e, "Can't create cell %s", cellClass);
      }
      return cellObject;
   }

   @Nullable
   private RecyclerView.ViewHolder createHolderWithoutAnnotation(Class<? extends RecyclerView.ViewHolder> cellClass, ViewGroup parent) {
      RecyclerView.ViewHolder cellObject = null;

      try {
         Constructor<? extends RecyclerView.ViewHolder> constructor = cellClass.getConstructor(ViewGroup.class);
         cellObject = constructor.newInstance(parent);
      } catch (Exception e) {
         Timber.d(e, "Can't create cell %s", cellClass);
      }
      return cellObject;
   }

   @Deprecated
   public static RecyclerView.ViewHolder createMessengerViewHolder(Class<? extends RecyclerView.ViewHolder> cellClass, View cellView) {
      RecyclerView.ViewHolder cellObject = null;
      try {

         Constructor<? extends RecyclerView.ViewHolder> constructor = cellClass.getConstructor(View.class);
         cellObject = constructor.newInstance(cellView);

      } catch (Exception e) {
         Timber.d(e, "Can't create cell %s", cellClass);
      }
      return cellObject;
   }
}
