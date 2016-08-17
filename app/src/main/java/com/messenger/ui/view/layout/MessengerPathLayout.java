package com.messenger.ui.view.layout;

import android.content.Context;
import android.util.AttributeSet;

import com.messenger.ui.presenter.MessengerPresenter;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.flow.layout.InjectorHolder;
import com.worldventures.dreamtrips.core.flow.path.PathView;
import com.worldventures.dreamtrips.core.flow.path.StyledPath;
import com.worldventures.dreamtrips.core.utils.tracksystem.MonitoringHelper;

public abstract class MessengerPathLayout<V extends MessengerScreen, P extends MessengerPresenter<V, ?>, T extends StyledPath> extends MessengerLinearLayout<V, P> implements PathView<T>, InjectorHolder {

   protected Injector injector;
   private T path;

   public MessengerPathLayout(Context context) {
      super(context);
   }

   public MessengerPathLayout(Context context, AttributeSet attrs) {
      super(context, attrs);
      MonitoringHelper.startInteractionName(this);
   }

   @Override
   public void setInjector(Injector injector) {
      this.injector = injector;
   }

   @Override
   public T getPath() {
      return (T) path;
   }

   @Override
   public void setPath(T path) {
      this.path = path;
      onPrepared();
   }

   /** Safe method to init UI with path provided */
   protected void onPrepared() {
   }
}
