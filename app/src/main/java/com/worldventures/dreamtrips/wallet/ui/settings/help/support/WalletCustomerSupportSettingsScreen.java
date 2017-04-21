package com.worldventures.dreamtrips.wallet.ui.settings.help.support;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.domain.entity.settings.customer_support.Contact;
import com.worldventures.dreamtrips.wallet.service.command.settings.help.GetCustomerSupportContactCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.ErrorViewFactory;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.RetryErrorDialogView;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.http.HttpErrorViewProvider;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.progress.SimpleDialogProgressView;

import butterknife.InjectView;
import butterknife.OnClick;
import io.techery.janet.operationsubscriber.view.ComposableOperationView;
import io.techery.janet.operationsubscriber.view.OperationView;

public class WalletCustomerSupportSettingsScreen extends WalletLinearLayout<WalletCustomerSupportSettingsPresenter.Screen, WalletCustomerSupportSettingsPresenter, WalletCustomerSupportSettingsPath> implements WalletCustomerSupportSettingsPresenter.Screen {

   @InjectView(R.id.toolbar) Toolbar toolbar;

   @InjectView(R.id.container_cutomer_care_contacts) View containerContacts;
   @InjectView(R.id.tv_us_customer_care) TextView tvUsCustomerCare;
   @InjectView(R.id.tv_international_collect) TextView tvInternationalCollect;

   public WalletCustomerSupportSettingsScreen(Context context) {
      this(context, null);
   }

   public WalletCustomerSupportSettingsScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
      setLayoutTransition(null);
   }

   @Override
   protected void onFinishInflate() {
      super.onFinishInflate();
      supportConnectionStatusLabel(false);
      toolbar.setNavigationOnClickListener(v -> onNavigationClick());
      containerContacts.setVisibility(GONE);
   }

   @NonNull
   @Override
   public WalletCustomerSupportSettingsPresenter createPresenter() {
      return new WalletCustomerSupportSettingsPresenter(getContext(), getInjector());
   }

   protected void onNavigationClick() {
      getPresenter().goBack();
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      return null;
   }

   @OnClick(R.id.item_us_customer_care)
   void onClickUsCustomerCare() {
      getPresenter().dialPhoneNumber(String.valueOf(tvUsCustomerCare.getText()));
   }

   @OnClick(R.id.item_international_collect)
   void onClickInternationalCollect() {
      getPresenter().dialPhoneNumber(String.valueOf(tvInternationalCollect.getText()));
   }

   @OnClick(R.id.item_email_us)
   void onClickEmailUs() {
      getPresenter().openCustomerSupportFeedbackScreen();
   }

   @Override
   public void bindData(Contact contact) {
      tvUsCustomerCare.setText(contact.phone());
      tvInternationalCollect.setText(contact.fax());
      containerContacts.setVisibility(View.VISIBLE);
   }

   @Override
   public OperationView<GetCustomerSupportContactCommand> provideOperationView() {
      return new ComposableOperationView<>(new SimpleDialogProgressView<>(getContext(), R.string.loading, false),
            ErrorViewFactory.<GetCustomerSupportContactCommand>builder()
                  .defaultErrorView(new RetryErrorDialogView<>(getContext(), R.string.error_something_went_wrong,
                        command -> getPresenter().fetchCustomerSupportContact(),
                        command -> getPresenter().goBack()))
                  .addProvider(new HttpErrorViewProvider<>(getContext(),
                        command -> getPresenter().fetchCustomerSupportContact(),
                        command -> getPresenter().goBack()))
                  .build()
      );
   }
}
