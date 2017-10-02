package com.worldventures.core.ui.view.cell;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public abstract class AbstractCell<T> extends RecyclerView.ViewHolder implements BaseCell<T> {

   private T modelObject;

   public AbstractCell(View view) {
      super(view);
      view.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
         @Override
         public void onViewAttachedToWindow(View v) {
            onAttachedToWindow(v);
         }

         @Override
         public void onViewDetachedFromWindow(View v) {
            clearResources();
         }
      });
   }

   protected Resources getResources() {
      return itemView.getResources();
   }

   protected void onAttachedToWindow(View v) { }

   public T getModelObject() {
      return modelObject;
   }

   public void setModelObject(T modelObject) {
      this.modelObject = modelObject;
   }

   @Override
   public void fillWithItem(T item) {
      setModelObject(item);
      syncUIStateWithModel();
   }

   @Override
   public void prepareForReuse() {

   }

   protected abstract void syncUIStateWithModel();

   public void afterInject() {

   }

   public boolean shouldInject() {
      return true;
   }

   @Override
   public void clearResources() {
   }
}
