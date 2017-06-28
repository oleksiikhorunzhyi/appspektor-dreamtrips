package com.worldventures.dreamtrips.wallet.ui.settings.general.newcard.success;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;

import butterknife.InjectView;
import butterknife.OnClick;

public class UnassignSuccessScreen extends WalletLinearLayout<UnassignSuccessPresenter.Screen, UnassignSuccessPresenter, UnassignSuccessPath> implements UnassignSuccessPresenter.Screen {

   @InjectView(R.id.toolbar) Toolbar toolbar;

   public UnassignSuccessScreen(Context context) {
      super(context);
   }

   public UnassignSuccessScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @Override
   protected void onFinishInflate() {
      supportConnectionStatusLabel(false);
      super.onFinishInflate();
      if (isInEditMode()) return;
      toolbar.setNavigationOnClickListener(v -> getPresenter().goBack());
   }

   @NonNull
   @Override
   public UnassignSuccessPresenter createPresenter() {
      return new UnassignSuccessPresenter(getContext(), getInjector());
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      return null;
   }

   @OnClick(R.id.get_started_button)
   public void onGetStarted() {
      presenter.navigateToWizard();
   }
}
