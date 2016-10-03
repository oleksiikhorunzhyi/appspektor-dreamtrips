package com.worldventures.dreamtrips.wallet.ui.common.base;

import android.animation.LayoutTransition;
import android.content.Context;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.messenger.ui.presenter.ViewStateMvpPresenter;
import com.messenger.ui.view.layout.BaseViewStateLinearLayout;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.layout.InjectorHolder;
import com.worldventures.dreamtrips.core.flow.path.PathView;
import com.worldventures.dreamtrips.core.flow.path.StyledPath;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;

import butterknife.ButterKnife;
import flow.path.Path;

public abstract class WalletLinearLayout<V extends WalletScreen, P extends ViewStateMvpPresenter<V, ?>, T extends StyledPath> extends BaseViewStateLinearLayout<V, P> implements InjectorHolder, PathView<T> {

   private static final long HIDE_ANIMATION_DELAY = 2000L;

   private Injector injector;
   private TextView connectionLabel;
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

      connectionLabel = (TextView) LayoutInflater.from(getContext())
            .inflate(R.layout.wallet_smartcard_connection_plank, this, false);
   }

   public void showConnectionStatus(SmartCard.ConnectionStatus connectionStatus) {
      if (!visibleConnectionLabel) return;

      switch (connectionStatus) {
         case CONNECTED:
            connectionLabel.setText(R.string.wallet_smartcard_connected_label);
            postDelayed(() -> removeView(connectionLabel), HIDE_ANIMATION_DELAY);
            break;
         case DISCONNECTED:
            connectionLabel.setText(R.string.wallet_smartcard_disconnected_label);
            addView(connectionLabel, hasToolbar() ? 1 : 0);
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

   protected void supportConnectionStatusLabel(boolean showLabel){
      visibleConnectionLabel = showLabel;
   }
}