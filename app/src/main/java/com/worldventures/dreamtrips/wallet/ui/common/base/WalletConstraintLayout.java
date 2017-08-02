package com.worldventures.dreamtrips.wallet.ui.common.base;

import android.content.Context;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.messenger.ui.presenter.ViewStateMvpPresenter;
import com.techery.spares.module.Injector;
import com.trello.rxlifecycle.RxLifecycle;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.layout.BaseViewStateConstraintLayout;
import com.worldventures.dreamtrips.core.flow.layout.InjectorHolder;
import com.worldventures.dreamtrips.core.flow.path.PathView;
import com.worldventures.dreamtrips.core.flow.path.StyledPath;
import com.worldventures.dreamtrips.wallet.domain.entity.ConnectionStatus;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.RxLifecycleView;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;

import butterknife.ButterKnife;
import flow.path.Path;
import rx.Observable;

public abstract class WalletConstraintLayout<V extends WalletScreen, P extends ViewStateMvpPresenter<V, ?>, T extends StyledPath> extends BaseViewStateConstraintLayout<V, P> implements InjectorHolder, PathView<T>, RxLifecycleView {

   private Injector injector;

   private View connectionSmartCardHeader;
   private View connectionHttpHeader;

   private boolean visibleConnectionSmartCardLabel = true;
   private boolean visibleHttpConnectionLabel = false;

   public WalletConstraintLayout(Context context) {
      this(context, null);
   }

   public WalletConstraintLayout(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @Override
   protected void onFinishInflate() {
      super.onFinishInflate();
      ButterKnife.inject(this);

      connectionSmartCardHeader = LayoutInflater.from(getContext())
            .inflate(R.layout.wallet_smartcard_connection_plank, this, false);

      connectionHttpHeader = LayoutInflater.from(getContext())
            .inflate(R.layout.wallet_http_connection_plank, this, false);
   }

   public void showConnectionStatus(ConnectionStatus connectionStatus) {
      if (!visibleConnectionSmartCardLabel) return;

      viewLabelController(connectionSmartCardHeader, connectionStatus.isConnected());
   }

   public void showHttpConnectionStatus(boolean connected) {
      if (!visibleHttpConnectionLabel || visibleConnectionSmartCardLabel) return;

      viewLabelController(connectionHttpHeader, connected);
   }

   private void viewLabelController(View view, boolean show) {
      // TODO: 5/22/17 Come up with an idea on how to show these labels more nicely
   }

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
      visibleConnectionSmartCardLabel = showLabel;
   }

   protected void supportHttpConnectionStatusLabel(boolean showLabel) {
      visibleHttpConnectionLabel = showLabel;
   }

   @Override
   public <Type> Observable.Transformer<Type, Type> lifecycle() {
      return RxLifecycle.bindView(this);
   }
}
