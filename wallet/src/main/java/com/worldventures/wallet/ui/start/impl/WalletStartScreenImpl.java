package com.worldventures.wallet.ui.start.impl;

import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.worldventures.wallet.R;
import com.worldventures.wallet.service.command.wizard.FetchAssociatedSmartCardCommand;
import com.worldventures.wallet.ui.common.base.WalletBaseController;
import com.worldventures.wallet.ui.common.helper2.error.ErrorViewFactory;
import com.worldventures.wallet.ui.common.helper2.error.http.HttpErrorViewProvider;
import com.worldventures.wallet.ui.common.helper2.progress.WalletProgressView;
import com.worldventures.wallet.ui.start.WalletStartPresenter;
import com.worldventures.wallet.ui.start.WalletStartScreen;
import com.worldventures.wallet.ui.widget.WalletProgressWidget;

import javax.inject.Inject;

import io.techery.janet.operationsubscriber.view.ComposableOperationView;
import io.techery.janet.operationsubscriber.view.OperationView;

public class WalletStartScreenImpl extends WalletBaseController<WalletStartScreen, WalletStartPresenter>
      implements WalletStartScreen {

   private WalletProgressWidget progressView;

   @Inject WalletStartPresenter presenter;

   public WalletStartScreenImpl() {
   }

   @Override
   protected void onFinishInflate(View view) {
      super.onFinishInflate(view);
      progressView = view.findViewById(R.id.progress);
   }

   @Override
   public OperationView<FetchAssociatedSmartCardCommand> provideOperationView() {
      return new ComposableOperationView<>(new WalletProgressView<>(progressView),
            ErrorViewFactory.<FetchAssociatedSmartCardCommand>builder()
                  .addProvider(new HttpErrorViewProvider<>(getContext(), getPresenter().httpErrorHandlingUtil(),
                        command -> getPresenter().retryFetchingCard(),
                        command -> getPresenter().cancelFetchingCard()))
                  .build());
   }

   @Override
   public WalletStartPresenter getPresenter() {
      return presenter;
   }

   @Override
   public View inflateView(LayoutInflater layoutInflater, ViewGroup viewGroup) {
      return layoutInflater.inflate(R.layout.screen_wallet_start, viewGroup, false);
   }

   @Override
   public boolean supportConnectionStatusLabel() {
      return false;
   }

   @Override
   public boolean supportHttpConnectionStatusLabel() {
      return false;
   }

   @Nullable
   @Override
   protected Object screenModule() {
      return new WalletStartScreenModule();
   }
}
