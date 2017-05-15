package com.worldventures.dreamtrips.wallet.ui.common.base;

import android.animation.LayoutTransition;
import android.content.Context;
import android.support.annotation.StringRes;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.messenger.ui.presenter.ViewStateMvpPresenter;
import com.messenger.ui.view.layout.BaseViewStateLinearLayout;
import com.techery.spares.module.Injector;
import com.trello.rxlifecycle.RxLifecycle;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.layout.InjectorHolder;
import com.worldventures.dreamtrips.core.flow.path.PathView;
import com.worldventures.dreamtrips.core.flow.path.StyledPath;
import com.worldventures.dreamtrips.wallet.domain.entity.ConnectionStatus;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.RxLifecycleView;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;

import butterknife.ButterKnife;
import flow.path.Path;
import rx.Observable;

public abstract class WalletLinearLayout<V extends WalletScreen, P extends ViewStateMvpPresenter<V, ?>, T extends StyledPath> extends BaseViewStateLinearLayout<V, P> implements InjectorHolder, PathView<T>, RxLifecycleView {

   private Injector injector;
   private View connectionSmartCardHeader;
   private View connectionHttpHeader;
   private boolean visibleConnectionSmartCardLabel = true;
   private boolean visibleHttpConnectionLabel = false;

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

      connectionSmartCardHeader = LayoutInflater.from(getContext())
            .inflate(R.layout.wallet_smartcard_connection_plank, this, false);

      connectionHttpHeader = LayoutInflater.from(getContext())
            .inflate(R.layout.wallet_http_connection_plank, this, false);
   }

   public void showConnectionStatus(ConnectionStatus connectionStatus) {
      if (!visibleConnectionSmartCardLabel) return;

      viewLabelController(connectionSmartCardHeader, connectionStatus.isConnected());
   }

   private void viewLabelController(View view, boolean connected) {
      if (connected) {
         removeView(view);
      } else {
         if (indexOfChild(view) < 0) {
            addView(view, findCorrectIndex());
         }
      }
   }

   public void showHttpConnectionStatus(boolean connected) {
      if (!visibleHttpConnectionLabel || visibleConnectionSmartCardLabel) return;

      viewLabelController(connectionHttpHeader, connected);
   }

   private int findCorrectIndex() {
      if (getChildCount() > 0 && hasFirstViewIsToolbar(this)) {
         return 1;
      } else {
         return 0;
      }
   }

   private boolean hasFirstViewIsToolbar(ViewGroup viewGroup) {
      if (viewGroup.getChildAt(0) instanceof Toolbar) {
         return true;
      } else {
         for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            if (child instanceof ViewGroup) {
               return hasFirstViewIsToolbar((ViewGroup) child);
            } else {
               return false;
            }
         }
         return false;
      }
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
