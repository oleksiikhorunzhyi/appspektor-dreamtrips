package com.worldventures.dreamtrips.wallet.ui.start;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.service.command.wizard.FetchAssociatedSmartCardCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.progress.WalletProgressView;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.ErrorViewFactory;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.http.HttpErrorViewProvider;
import com.worldventures.dreamtrips.wallet.ui.widget.WalletProgressWidget;

import butterknife.InjectView;
import io.techery.janet.operationsubscriber.view.ComposableOperationView;
import io.techery.janet.operationsubscriber.view.OperationView;

import static com.worldventures.dreamtrips.wallet.ui.start.WalletStartPresenter.Screen;

public class WalletStartScreen extends WalletLinearLayout<Screen, WalletStartPresenter, WalletStartPath>
      implements Screen {

   @InjectView(R.id.progress) WalletProgressWidget progressView;
   @InjectView(R.id.toolbar) Toolbar toolbar;

   public WalletStartScreen(Context context) {
      super(context);
   }

   public WalletStartScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @Override
   protected void onFinishInflate() {
      super.onFinishInflate();
      supportConnectionStatusLabel(false);
   }

   @NonNull
   @Override
   public WalletStartPresenter createPresenter() {
      return new WalletStartPresenter(getContext(), getInjector());
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      return null;
   }

   @Override
   public OperationView<FetchAssociatedSmartCardCommand> provideOperationView() {
      return new ComposableOperationView<>(new WalletProgressView<>(progressView),
            ErrorViewFactory.<FetchAssociatedSmartCardCommand>builder()
                  .addProvider(new HttpErrorViewProvider<>(getContext(),
                        command -> presenter.retryFetchingCard(),
                        command -> presenter.cancelFetchingCard()))
                  .build());
   }
}
