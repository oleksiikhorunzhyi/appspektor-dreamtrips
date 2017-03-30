package com.worldventures.dreamtrips.wallet.ui.common.base;

import android.animation.LayoutTransition;
import android.content.Context;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.messenger.ui.presenter.ViewStateMvpPresenter;
import com.messenger.ui.view.layout.BaseViewStateLinearLayout;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.layout.InjectorHolder;
import com.worldventures.dreamtrips.core.flow.path.PathView;
import com.worldventures.dreamtrips.core.flow.path.StyledPath;
import com.worldventures.dreamtrips.wallet.domain.entity.ConnectionStatus;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;

import butterknife.ButterKnife;
import flow.path.Path;

public abstract class WalletLinearLayout<V extends WalletScreen, P extends ViewStateMvpPresenter<V, ?>, T extends StyledPath> extends BaseViewStateLinearLayout<V, P> implements InjectorHolder, PathView<T> {

   private Injector injector;
   private View connectionHeader;
   private boolean visibleConnectionLabel = true;

   public WalletLinearLayout(Context context) {
      this(context, null);
   }

   public WalletLinearLayout(Context context, AttributeSet attrs) {
      super(context, attrs);
      initContainer();
   }

   private void initContainer() {
      setOrientation(VERTICAL);
      setLayoutTransition(new LayoutTransition());
   }

   @Override
   protected void onFinishInflate() {
      super.onFinishInflate();
      ButterKnife.inject(this);


      connectionHeader = LayoutInflater.from(getContext())
            .inflate(R.layout.wallet_smartcard_connection_plank, this, false);
   }

   public void showConnectionStatus(ConnectionStatus connectionStatus) {
      if (!visibleConnectionLabel) return;

      switch (connectionStatus) {
         case CONNECTED:
            removeView(connectionHeader);
            break;
         case DISCONNECTED:
            if (indexOfChild(connectionHeader) < 0) {
               addView(connectionHeader, hasToolbar() ? 1 : 0);
            }
            break;
      }
   }

   protected abstract boolean hasToolbar();

   @Deprecated
   @Override
   public void setPath(T path) {
   }

   @Override
   public T getPath() {
      return Path.get(getContext());
   }

   @Override
   public void setInjector(Injector injector) {
      this.injector = injector;
   }

   public Injector getInjector() {
      return injector;
   }

   protected String getString(@StringRes int stringId) {
      return getResources().getString(stringId);
   }

   protected String getString(@StringRes int stringId, Object... formatArgs) {
      return getResources().getString(stringId, formatArgs);
   }

   protected void supportConnectionStatusLabel(boolean showLabel) {
      visibleConnectionLabel = showLabel;
   }
}
