package com.techery.spares.ui.view.cell;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.worldventures.dreamtrips.core.navigation.router.Router;

import javax.inject.Inject;

import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

public abstract class AbstractCell<T> extends RecyclerView.ViewHolder implements BaseCell<T> {

   private T modelObject;

   @Inject protected Router router;

   private EventBus eventBus;

   public AbstractCell(View view) {
      super(view);
      ButterKnife.inject(this, view);
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

   protected void onAttachedToWindow(View v) { }

   @Override
   public void setEventBus(EventBus bus) {
      this.eventBus = bus;
   }

   public EventBus getEventBus() {
      return eventBus;
   }

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
      if (eventBus != null && eventBus.isRegistered(this)) {
         eventBus.unregister(this);
      }
   }
}
