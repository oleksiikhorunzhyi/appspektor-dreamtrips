package com.worldventures.dreamtrips.wallet.ui.wizard.pairkey;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.service.command.CreateAndConnectToCardCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.ErrorViewFactory;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.SimpleDialogErrorViewProvider;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.progress.SimpleDialogProgressView;
import com.worldventures.dreamtrips.wallet.util.SmartCardConnectException;

import butterknife.InjectView;
import butterknife.OnClick;
import io.techery.janet.operationsubscriber.view.ComposableOperationView;
import io.techery.janet.operationsubscriber.view.OperationView;

public class PairKeyScreen extends WalletLinearLayout<PairKeyPresenter.Screen, PairKeyPresenter, PairKeyPath> implements PairKeyPresenter.Screen {

   @InjectView(R.id.toolbar) Toolbar toolbar;

   public PairKeyScreen(Context context) {
      super(context);
   }

   public PairKeyScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @Override
   protected void onFinishInflate() {
      super.onFinishInflate();
      supportConnectionStatusLabel(false);
   }

   @NonNull
   @Override
   public PairKeyPresenter createPresenter() {
      final PairKeyPath path = getPath();
      return new PairKeyPresenter(getContext(), getInjector(), path.mode, path.barcode);
   }

   @Override
   public OperationScreen provideOperationDelegate() {return null;}

   @Override
   protected boolean hasToolbar() {
      return true;
   }

   @OnClick(R.id.button_next)
   public void onConnectToSmartCard() {
      presenter.tryToPairAndConnectSmartCard();
   }

   @Override
   public OperationView<CreateAndConnectToCardCommand> provideOperationCreateAndConnect() {
      return new ComposableOperationView<>(
            new SimpleDialogProgressView<>(getContext(), R.string.loading, false),
            ErrorViewFactory.<CreateAndConnectToCardCommand>builder()
                  .addProvider(new SimpleDialogErrorViewProvider<>(
                        getContext(),
                        SmartCardConnectException.class,
                        R.string.wallet_smartcard_connection_error,
                        (dialog, which) -> presenter.goBack())
                  ).build());
   }

   @Override
   public void showBackButton() {
      toolbar.setNavigationIcon(R.drawable.back_icon);
      toolbar.setNavigationOnClickListener(v -> presenter.goBack());
   }

   @Override
   public void hideBackButton() {
      toolbar.setNavigationIcon(new ColorDrawable(Color.TRANSPARENT));
      toolbar.setNavigationOnClickListener(null);
   }

   @Override
   public View getView() {
      return this;
   }
}