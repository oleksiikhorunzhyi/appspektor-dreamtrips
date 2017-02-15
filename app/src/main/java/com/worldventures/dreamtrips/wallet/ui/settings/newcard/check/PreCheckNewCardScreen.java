package com.worldventures.dreamtrips.wallet.ui.settings.newcard.check;

import android.content.Context;
import android.util.AttributeSet;

import com.afollestad.materialdialogs.MaterialDialog;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;

public class PreCheckNewCardScreen extends WalletLinearLayout<PreCheckNewCardPresenter.Screen, PreCheckNewCardPresenter, PreCheckNewCardPath> implements PreCheckNewCardPresenter.Screen {

   private MaterialDialog addCardContinueDialog = null;

   public PreCheckNewCardScreen(Context context) {
      super(context);
   }

   public PreCheckNewCardScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @Override
   protected boolean hasToolbar() {
      return true;
   }

   @Override
   public PreCheckNewCardPresenter createPresenter() {
      return new PreCheckNewCardPresenter(getContext(), getInjector());
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      return null;
   }

   @Override
   public void showAddCardContinueDialog(String scId) {
      if (addCardContinueDialog == null) {
         addCardContinueDialog = new MaterialDialog.Builder(getContext())
               .content(R.string.wallet_new_card_pre_install_confirm_message)
               .positiveText(R.string.wallet_continue_label)
               .onPositive((dialog, which) -> presenter.navigateNext())
               .onNegative((dialog, which) -> presenter.goBack())
               .negativeText(R.string.cancel)
               .build();
      }
      if(!addCardContinueDialog.isShowing()) addCardContinueDialog.show();
   }

   @Override
   protected void onDetachedFromWindow() {
      if(addCardContinueDialog != null) addCardContinueDialog.dismiss();
      super.onDetachedFromWindow();
   }
}
