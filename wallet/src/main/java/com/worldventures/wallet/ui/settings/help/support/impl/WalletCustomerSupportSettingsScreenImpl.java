package com.worldventures.wallet.ui.settings.help.support.impl;


import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.worldventures.wallet.R;
import com.worldventures.wallet.domain.entity.settings.customer_support.Contact;
import com.worldventures.wallet.service.command.settings.help.GetCustomerSupportContactCommand;
import com.worldventures.wallet.ui.common.base.WalletBaseController;
import com.worldventures.wallet.ui.common.helper2.error.ErrorViewFactory;
import com.worldventures.wallet.ui.common.helper2.error.RetryDialogErrorView;
import com.worldventures.wallet.ui.common.helper2.error.http.HttpErrorViewProvider;
import com.worldventures.wallet.ui.common.helper2.progress.SimpleDialogProgressView;
import com.worldventures.wallet.ui.settings.help.support.WalletCustomerSupportSettingsPresenter;
import com.worldventures.wallet.ui.settings.help.support.WalletCustomerSupportSettingsScreen;

import javax.inject.Inject;

import io.techery.janet.operationsubscriber.view.ComposableOperationView;
import io.techery.janet.operationsubscriber.view.OperationView;

import static android.view.View.GONE;

public class WalletCustomerSupportSettingsScreenImpl
      extends WalletBaseController<WalletCustomerSupportSettingsScreen, WalletCustomerSupportSettingsPresenter>
      implements WalletCustomerSupportSettingsScreen {

   private View containerContacts;
   private TextView tvUsCustomerCare;
   private TextView tvInternationalCollect;

   @Inject WalletCustomerSupportSettingsPresenter presenter;

   @Override
   protected void onFinishInflate(View view) {
      super.onFinishInflate(view);
      ((ViewGroup) view).setLayoutTransition(null);
      final Toolbar toolbar = view.findViewById(R.id.toolbar);
      toolbar.setNavigationOnClickListener(v -> onNavigationClick());
      tvUsCustomerCare = view.findViewById(R.id.tv_us_customer_care);
      tvInternationalCollect = view.findViewById(R.id.tv_international_collect);
      containerContacts = view.findViewById(R.id.container_cutomer_care_contacts);
      containerContacts.setVisibility(GONE);
      final View itemUsCustomerCare = view.findViewById(R.id.item_us_customer_care);
      itemUsCustomerCare.setOnClickListener(customerCare
            -> getPresenter().dialPhoneNumber(String.valueOf(tvUsCustomerCare.getText())));
      final View itemInternationalCollect = view.findViewById(R.id.item_international_collect);
      itemInternationalCollect.setOnClickListener(internationalCollect
            -> getPresenter().dialPhoneNumber(String.valueOf(tvInternationalCollect.getText())));
      final View itemEmailUs = view.findViewById(R.id.item_email_us);
      itemEmailUs.setOnClickListener(emailUs -> getPresenter().openCustomerSupportFeedbackScreen());
   }

   protected void onNavigationClick() {
      getPresenter().goBack();
   }

   @Override
   public void bindData(Contact contact) {
      tvUsCustomerCare.setText(contact.getPhone());
      tvInternationalCollect.setText(contact.getFax());
      containerContacts.setVisibility(View.VISIBLE);
   }

   @Override
   public OperationView<GetCustomerSupportContactCommand> provideOperationView() {
      return new ComposableOperationView<>(new SimpleDialogProgressView<>(getContext(), R.string.wallet_loading, false),
            ErrorViewFactory.<GetCustomerSupportContactCommand>builder()
                  .defaultErrorView(new RetryDialogErrorView<>(getContext(), R.string.wallet_error_something_went_wrong,
                        command -> getPresenter().fetchCustomerSupportContact(),
                        command -> getPresenter().goBack()))
                  .addProvider(new HttpErrorViewProvider<>(getContext(), getPresenter().httpErrorHandlingUtil(),
                        command -> getPresenter().fetchCustomerSupportContact(),
                        command -> getPresenter().goBack()))
                  .build()
      );
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
