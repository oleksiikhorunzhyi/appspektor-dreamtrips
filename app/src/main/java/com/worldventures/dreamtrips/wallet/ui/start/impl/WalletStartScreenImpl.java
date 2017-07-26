package com.worldventures.dreamtrips.wallet.ui.start.impl;

import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.service.command.wizard.FetchAssociatedSmartCardCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletBaseController;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.ErrorViewFactory;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.http.HttpErrorViewProvider;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.progress.WalletProgressView;
import com.worldventures.dreamtrips.wallet.ui.start.WalletStartPresenter;
import com.worldventures.dreamtrips.wallet.ui.start.WalletStartScreen;
import com.worldventures.dreamtrips.wallet.ui.widget.WalletProgressWidget;

import javax.inject.Inject;

import butterknife.InjectView;
import io.techery.janet.operationsubscriber.view.ComposableOperationView;
import io.techery.janet.operationsubscriber.view.OperationView;

public class WalletStartScreenImpl extends WalletBaseController<WalletStartScreen, WalletStartPresenter>
      implements WalletStartScreen {

   @InjectView(R.id.progress) WalletProgressWidget progressView;
   @InjectView(R.id.toolbar) Toolbar toolbar;

   @Inject WalletStartPresenter presenter;

   public WalletStartScreenImpl() {
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      return null;
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
}
