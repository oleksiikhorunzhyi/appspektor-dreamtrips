package com.worldventures.dreamtrips.wallet.ui.settings.help.support.impl;


import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.domain.entity.settings.customer_support.Contact;
import com.worldventures.dreamtrips.wallet.service.command.settings.help.GetCustomerSupportContactCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletBaseController;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.ErrorViewFactory;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.RetryDialogErrorView;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.http.HttpErrorViewProvider;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.progress.SimpleDialogProgressView;
import com.worldventures.dreamtrips.wallet.ui.settings.help.support.WalletCustomerSupportSettingsPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.help.support.WalletCustomerSupportSettingsScreen;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import io.techery.janet.operationsubscriber.view.ComposableOperationView;
import io.techery.janet.operationsubscriber.view.OperationView;

import static android.view.View.GONE;

public class WalletCustomerSupportSettingsScreenImpl extends WalletBaseController<WalletCustomerSupportSettingsScreen, WalletCustomerSupportSettingsPresenter> implements WalletCustomerSupportSettingsScreen {

   @InjectView(R.id.toolbar) Toolbar toolbar;
   @InjectView(R.id.container_cutomer_care_contacts) View containerContacts;
   @InjectView(R.id.tv_us_customer_care) TextView tvUsCustomerCare;
   @InjectView(R.id.tv_international_collect) TextView tvInternationalCollect;

   @Inject WalletCustomerSupportSettingsPresenter presenter;

   @Override
   protected void onFinishInflate(View view) {
      super.onFinishInflate(view);
      ((ViewGroup) view).setLayoutTransition(null);
      toolbar.setNavigationOnClickListener(v -> onNavigationClick());
      containerContacts.setVisibility(GONE);
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
                  .defaultErrorView(new RetryDialogErrorView<>(getContext(), R.string.error_something_went_wrong,
                        command -> getPresenter().fetchCustomerSupportContact(),
                        command -> getPresenter().goBack()))
                  .addProvider(new HttpErrorViewProvider<>(getContext(), getPresenter().httpErrorHandlingUtil(),
                        command -> getPresenter().fetchCustomerSupportContact(),
                        command -> getPresenter().goBack()))
                  .build()
      );
   }

   @Override
   public Context getViewContext() {
      return getContext();
   }

   @Override
   public WalletCustomerSupportSettingsPresenter getPresenter() {
      return presenter;
   }

   @Override
   public View inflateView(LayoutInflater layoutInflater, ViewGroup viewGroup) {
      return layoutInflater.inflate(R.layout.screen_wallet_settings_customer_support, viewGroup, false);
   }

   @Override
   public boolean supportConnectionStatusLabel() {
      return false;
   }

   @Override
   public boolean supportHttpConnectionStatusLabel() {
      return true;
   }
}
