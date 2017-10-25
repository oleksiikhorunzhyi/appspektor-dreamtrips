package com.worldventures.dreamtrips.wallet.ui.records.detail.impl;


import android.databinding.DataBindingUtil;
import android.databinding.Observable.OnPropertyChangedCallback;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.databinding.library.baseAdapters.BR;
import com.worldventures.core.utils.HttpErrorHandlingUtil;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.databinding.ScreenWalletWizardViewCardDetailsBinding;
import com.worldventures.dreamtrips.wallet.domain.entity.record.Record;
import com.worldventures.dreamtrips.wallet.service.command.SetDefaultCardOnDeviceCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetPaymentCardAction;
import com.worldventures.dreamtrips.wallet.service.command.record.DeleteRecordCommand;
import com.worldventures.dreamtrips.wallet.service.command.record.UpdateRecordCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletBaseController;
import com.worldventures.dreamtrips.wallet.ui.common.binding.LastPositionSelector;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.ErrorViewFactory;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.SmartCardErrorViewProvider;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.http.HttpErrorViewProvider;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.progress.SimpleDialogProgressView;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.success.SimpleToastSuccessView;
import com.worldventures.dreamtrips.wallet.ui.dashboard.util.model.CommonCardViewModel;
import com.worldventures.dreamtrips.wallet.ui.dialog.ChangeDefaultPaymentCardDialog;
import com.worldventures.dreamtrips.wallet.ui.records.detail.CardDetailsPresenter;
import com.worldventures.dreamtrips.wallet.ui.records.detail.CardDetailsScreen;
import com.worldventures.dreamtrips.wallet.ui.records.detail.RecordDetailViewModel;
import com.worldventures.dreamtrips.wallet.util.WalletRecordUtil;

import javax.inject.Inject;

import io.techery.janet.operationsubscriber.view.ComposableOperationView;
import io.techery.janet.operationsubscriber.view.OperationView;

import static com.worldventures.dreamtrips.wallet.util.WalletCardNameUtil.bindSpannableStringToTarget;

public class CardDetailsScreenImpl extends WalletBaseController<CardDetailsScreen, CardDetailsPresenter> implements CardDetailsScreen {

   private static final String KEY_MODIFY_RECORD = "key_modify_record";

   private ScreenWalletWizardViewCardDetailsBinding binding;

   @Inject CardDetailsPresenter presenter;
   @Inject HttpErrorHandlingUtil httpErrorHandlingUtil;

   private RecordDetailViewModel detailViewModel;

   private MaterialDialog networkConnectionErrorDialog;

   public static CardDetailsScreenImpl create(CommonCardViewModel recordViewModel) {
      final Bundle args = new Bundle();
      args.putParcelable(KEY_MODIFY_RECORD, recordViewModel);
      return new CardDetailsScreenImpl(args);
   }

   public CardDetailsScreenImpl(Bundle args) {
      super(args);
   }

   @Override
   protected void onFinishInflate(View view) {
      super.onFinishInflate(view);
      setupToolbar();
      binding.deleteButton.setOnClickListener(deleteBtn -> getPresenter().onDeleteCardClick());
      binding.payThisCardButton.setOnClickListener(payThisCardBtn -> getPresenter().payThisCard());
      bindSpannableStringToTarget(binding.cardNicknameLabel, R.string.wallet_card_details_label_card_nickname,
            R.string.wallet_add_card_details_hint_card_name_length, false, false);
   }

   @Override
   public View inflateView(LayoutInflater layoutInflater, ViewGroup viewGroup) {
      detailViewModel = new RecordDetailViewModel(getArgs().getParcelable(KEY_MODIFY_RECORD));
      binding = DataBindingUtil.inflate(layoutInflater, R.layout.screen_wallet_wizard_view_card_details, viewGroup, false);
      binding.setRecordDetails(detailViewModel);
      binding.setLastPositionSelector(new LastPositionSelector());
      detailViewModel.addOnPropertyChangedCallback(new OnPropertyChangedCallback() {
         @Override
         public void onPropertyChanged(android.databinding.Observable sender, int propertyId) {
            if (propertyId == BR.recordName) {
               presenter.validateRecordName(detailViewModel.getRecordName().trim());
            } else if (propertyId == BR.defaultRecord) {
               if (detailViewModel.getRecordModel().isDefaultCard() == detailViewModel.isDefaultRecord()) return;
               presenter.changeDefaultCard(detailViewModel.isDefaultRecord());
            }
         }
      });
      return binding.getRoot();
   }

   @Override
   public boolean supportConnectionStatusLabel() {
      return false;
   }

   @Override
   public boolean supportHttpConnectionStatusLabel() {
      return false;
   }

   private void setupToolbar() {
      final Toolbar toolbar = binding.toolbar;
      toolbar.setNavigationOnClickListener(v -> getPresenter().goBack());
      toolbar.inflateMenu(R.menu.wallet_payment_card_detail);
      toolbar.setOnMenuItemClickListener(item -> {
         switch (item.getItemId()) {
            case R.id.action_save:
               getPresenter().updateNickName();
            default:
               return false;
         }
      });
   }

   @Override
   public void showDefaultCardDialog(Record defaultRecord) {
      new ChangeDefaultPaymentCardDialog(getContext(), WalletRecordUtil.bankNameWithCardNumber(defaultRecord))
            .setOnConfirmAction(() -> getPresenter().onChangeDefaultCardConfirmed())
            .setOnCancelAction(() -> getPresenter().onChangeDefaultCardCanceled())
            .show();
   }

   @Override
   public void showDeleteCardDialog() {
      new MaterialDialog.Builder(getContext())
            .title(R.string.wallet_card_details_delete_card_dialog_title)
            .content(R.string.wallet_card_details_delete_card_dialog_content)
            .positiveText(R.string.ok)
            .negativeText(R.string.cancel)
            .onPositive((dialog, which) -> getPresenter().onDeleteCardConfirmed())
            .build()
            .show();
   }

   @Override
   public void showNetworkConnectionErrorDialog() {
      if (networkConnectionErrorDialog == null) {
         networkConnectionErrorDialog = new MaterialDialog.Builder(getContext())
               .title(R.string.wallet_error_label)
               .content(R.string.wallet_no_internet_connection)
               .positiveText(R.string.ok)
               .onPositive((dialog, which) -> dialog.dismiss())
               .build();
      }
      if (!networkConnectionErrorDialog.isShowing()) networkConnectionErrorDialog.show();
   }

   @Override
   public void showCardIsReadyDialog(String cardName) {
      MaterialDialog.Builder builder = new MaterialDialog.Builder(getContext());
      builder.content(getString(R.string.wallet_wizard_card_list_card_is_ready_text, cardName))
            .positiveText(R.string.ok)
            .onPositive((dialog, which) -> getPresenter().onCardIsReadyDialogShown())
            .build()
            .show();
   }

   @Override
   public void showSCNonConnectionDialog() {
      new MaterialDialog.Builder(getContext())
            .title(R.string.wallet_card_settings_cant_connected)
            .content(R.string.wallet_card_settings_message_cant_connected)
            .positiveText(R.string.ok)
            .build()
            .show();
   }

   @Override
   public void showCardNameError() {
      detailViewModel.setNameInputError(getString(R.string.wallet_card_details_nickname_error));
   }

   @Override
   public void hideCardNameError() {
      detailViewModel.setNameInputError("");
   }

   @Override
   public OperationView<UpdateRecordCommand> provideOperationSaveCardData() {
      return new ComposableOperationView<>(
            new SimpleDialogProgressView<>(getContext(), R.string.wallet_card_details_progress_save, false),
            new SimpleToastSuccessView<>(getContext(), R.string.wallet_card_details_success_save),
            ErrorViewFactory.<UpdateRecordCommand>builder()
                  .addProvider(new SmartCardErrorViewProvider<>(getContext(), command -> getPresenter().updateNickName()))
                  .addProvider(new HttpErrorViewProvider<>(getContext(), httpErrorHandlingUtil, command -> getPresenter()
                        .updateNickName(), command -> {
                  }))
                  .build()
      );
   }

   @Override
   public void notifyCardDataIsSaved() {
      detailViewModel.setChanged(false);
      Toast.makeText(getContext(), R.string.wallet_card_details_success_save, Toast.LENGTH_SHORT).show();
   }

   @Override
   public void defaultCardChanged(boolean isDefault) {
      detailViewModel.getRecordModel().setDefaultCard(isDefault);
   }

   @Override
   public void undoDefaultCardChanges() {
      detailViewModel.setDefaultRecord(detailViewModel.getRecordModel().isDefaultCard());
   }

   @Override
   public OperationView<DeleteRecordCommand> provideOperationDeleteRecord() {
      return new ComposableOperationView<>(
            new SimpleDialogProgressView<>(getContext(), R.string.loading, false),
            ErrorViewFactory.<DeleteRecordCommand>builder()
                  .addProvider(new SmartCardErrorViewProvider<>(getContext(), command -> getPresenter().onDeleteCardClick()))
                  .build()
      );
   }

   @Override
   public OperationView<SetDefaultCardOnDeviceCommand> provideOperationSetDefaultOnDevice() {
      return new ComposableOperationView<>(
            new SimpleDialogProgressView<>(getContext(), R.string.loading, false),
            ErrorViewFactory.<SetDefaultCardOnDeviceCommand>builder()
                  .addProvider(new SmartCardErrorViewProvider<>(getContext()))
                  .build()
      );
   }

   @Override
   public OperationView<SetPaymentCardAction> provideOperationSetPaymentCardAction() {
      return new ComposableOperationView<>(
            new SimpleDialogProgressView<>(getContext(), R.string.loading, false),
            ErrorViewFactory.<SetPaymentCardAction>builder()
                  .addProvider(new SmartCardErrorViewProvider<>(getContext(), command -> getPresenter().payThisCard()))
                  .build()
      );
   }

   @Override
   public RecordDetailViewModel getDetailViewModel() {
      return detailViewModel;
   }

   @Override
   protected void onDetach(@NonNull View view) {
      if (networkConnectionErrorDialog != null) networkConnectionErrorDialog.dismiss();
      super.onDetach(view);
   }

   @Override
   public CardDetailsPresenter getPresenter() {
      return presenter;
   }
}
