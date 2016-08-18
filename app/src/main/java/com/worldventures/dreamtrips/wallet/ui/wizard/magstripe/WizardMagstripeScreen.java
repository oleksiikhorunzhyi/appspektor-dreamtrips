package com.worldventures.dreamtrips.wallet.ui.wizard.magstripe;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard.CardType;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletFrameLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.delegate.DialogOperationScreen;

import butterknife.InjectView;

public class WizardMagstripeScreen extends WalletFrameLayout<WizardMagstripePresenter.Screen, WizardMagstripePresenter, WizardMagstripePath> implements WizardMagstripePresenter.Screen {

   @InjectView(R.id.toolbar) Toolbar toolbar;
   @InjectView(R.id.header_text_view) TextView headerTextView;

   public WizardMagstripeScreen(Context context) {
      super(context);
   }

   public WizardMagstripeScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @Override
   protected void onPostAttachToWindowView() {
      super.onPostAttachToWindowView();
      setupScreen(getPath().cardType);
   }

   @Override
   protected void onFinishInflate() {
      super.onFinishInflate();
      toolbar.setNavigationOnClickListener(v -> navigateClick());
   }

   @NonNull
   @Override
   public WizardMagstripePresenter createPresenter() {
      return new WizardMagstripePresenter(getContext(), getInjector(), getPath().cardType);
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      return new DialogOperationScreen(this);
   }

   private void navigateClick() {
      presenter.goBack();
   }

   private void setupScreen(CardType cardType) {
      if (cardType == CardType.DEBIT) {
         headerTextView.setText(R.string.wallet_wizard_magstripe_swipe_debit);
      } else {
         headerTextView.setText(R.string.wallet_wizard_magstripe_swipe_credit);
      }
   }

}
