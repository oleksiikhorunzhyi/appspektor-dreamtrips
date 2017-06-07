package com.worldventures.dreamtrips.wallet.ui.records.detail;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jakewharton.rxbinding.widget.RxCompoundButton;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.domain.entity.record.Record;
import com.worldventures.dreamtrips.wallet.service.command.SetDefaultCardOnDeviceCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetPaymentCardAction;
import com.worldventures.dreamtrips.wallet.service.command.record.DeleteRecordCommand;
import com.worldventures.dreamtrips.wallet.service.command.record.UpdateRecordCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.delegate.DialogOperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.ErrorViewFactory;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.SmartCardErrorViewProvider;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.http.HttpErrorViewProvider;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.progress.SimpleDialogProgressView;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.success.SimpleToastSuccessView;
import com.worldventures.dreamtrips.wallet.ui.dashboard.util.model.TransitionModel;
import com.worldventures.dreamtrips.wallet.ui.dialog.ChangeDefaultPaymentCardDialog;
import com.worldventures.dreamtrips.wallet.ui.widget.BankCardWidget;
import com.worldventures.dreamtrips.wallet.ui.widget.WalletSwitcher;
import com.worldventures.dreamtrips.wallet.util.WalletRecordUtil;

import butterknife.InjectView;
import butterknife.OnClick;
import io.techery.janet.operationsubscriber.view.ComposableOperationView;
import io.techery.janet.operationsubscriber.view.OperationView;
import rx.Observable;

import static com.worldventures.dreamtrips.wallet.util.WalletCardNameUtil.bindSpannableStringToTarget;

public class CardDetailsScreen extends WalletLinearLayout<CardDetailsPresenter.Screen, CardDetailsPresenter, CardDetailsPath>
      implements CardDetailsPresenter.Screen {

   private static final long CARD_TRANSITION_DURATION = 350;

   @InjectView(R.id.toolbar) Toolbar toolbar;
   @InjectView(R.id.card) BankCardWidget bankCardWidget;
   @InjectView(R.id.controls_layout) LinearLayout controlsLayout;

   @InjectView(R.id.card_name) EditText etCardNickname;
   @InjectView(R.id.card_nickname_label) TextView cardNicknameLabel;
   @InjectView(R.id.default_payment_card_checkbox) WalletSwitcher defaultPaymentCardSwitcher;
   @InjectView(R.id.cardNameInputLayout) TextInputLayout cardNameInputLayout;

   private Observable<Boolean> setAsDefaultCardObservable;
   private Observable<String> cardNicknameObservable;
   private MaterialDialog networkConnectionErrorDialog;
   private final WalletRecordUtil walletRecordUtil;

   public CardDetailsScreen(Context context) {
      this(context, null);
   }

   public CardDetailsScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
      walletRecordUtil = new WalletRecordUtil(context);
   }

   @NonNull
   @Override
   public CardDetailsPresenter createPresenter() {
      return new CardDetailsPresenter(getContext(), getInjector(), getPath().getRecord());
   }

   @Override
   protected void onFinishInflate() {
      super.onFinishInflate();

      if (isInEditMode()) return;
      setupToolbar();

      setAsDefaultCardObservable = RxCompoundButton.checkedChanges(defaultPaymentCardSwitcher).skip(1);
      cardNicknameObservable = RxTextView.afterTextChangeEvents(etCardNickname).map(event -> event.editable()
            .toString()).skip(1);

      bindSpannableStringToTarget(cardNicknameLabel, R.string.wallet_card_details_label_card_nickname,
            R.string.wallet_add_card_details_hint_card_name_length, false, false);
   }

   private void setupToolbar() {
      toolbar.setNavigationOnClickListener(v -> presenter.goBack());
      toolbar.inflateMenu(R.menu.menu_wallet_payment_card_detail);
      toolbar.setOnMenuItemClickListener(item -> {
         switch (item.getItemId()) {
            case R.id.action_save:
               presenter.updateNickName();
            default:
               return false;
         }
      });
   }

   @OnClick(R.id.delete_button)
   public void onDeleteCardClicked() {
      getPresenter().onDeleteCardClick();
   }

   @OnClick(R.id.pay_this_card_button)
   public void onPayThisCardClicked() {
      getPresenter().payThisCard();
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      return new DialogOperationScreen(this);
   }

   @Override
   public void showWalletRecord(Record record) {
      bankCardWidget.setBankCard(record);

      final String nickName = record.nickName();
      etCardNickname.setText(nickName);
      etCardNickname.setSelection(nickName.length());
   }

   @Override
   public void showDefaultCardDialog(Record defaultRecord) {
      new ChangeDefaultPaymentCardDialog(getContext(), walletRecordUtil.bankNameWithCardNumber(defaultRecord))
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
   public void setCardNickname(String cardNickname) {
      bankCardWidget.setCardName(cardNickname);
   }

   @Override
   public Observable<Boolean> setAsDefaultPaymentCardCondition() {
      return setAsDefaultCardObservable;
   }

   @Override
   public Observable<String> getCardNicknameObservable() {
      return cardNicknameObservable;
   }

   @Override
   public void setDefaultCardCondition(boolean defaultCard) {
      defaultPaymentCardSwitcher.setCheckedWithoutNotify(defaultCard);
   }

   @Override
   public String getUpdateNickname() {
      return etCardNickname.getText().toString().trim();
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
      cardNameInputLayout.setError(getString(R.string.wallet_card_details_nickname_error));
   }

   @Override
   public void hideCardNameError() {
      cardNameInputLayout.setError("");
   }

   @Override
   public OperationView<UpdateRecordCommand> provideOperationSaveCardData() {
      return new ComposableOperationView<>(
            new SimpleDialogProgressView<>(getContext(), R.string.wallet_card_details_progress_save, false),
            new SimpleToastSuccessView<>(getContext(), R.string.wallet_card_details_success_save),
            ErrorViewFactory.<UpdateRecordCommand>builder()
                  .addProvider(new SmartCardErrorViewProvider<>(getContext(), command -> getPresenter().updateNickName()))
                  .addProvider(new HttpErrorViewProvider<>(getContext(), command -> getPresenter().updateNickName(), command -> {
                  }))
                  .build()
      );
   }

   @Override
   public void notifyCardDataIsSaved() {
      Toast.makeText(getContext(), R.string.wallet_card_details_success_save, Toast.LENGTH_SHORT).show();
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
   public void animateCard() {
      TransitionModel transitionModel = getPath().getTransitionModel();
      if (transitionModel != null) {

         setUpViewPosition(transitionModel, bankCardWidget);
         bankCardWidget.setBackground(ContextCompat.getDrawable(getContext(), transitionModel.getBackground()));

         bankCardWidget.setVisibility(View.VISIBLE);
         controlsLayout.setAlpha(0);

         if (transitionModel.isDefaultCard()) {
            bankCardWidget.animateCardFromDefault(ContextCompat.getDrawable(getContext(),
                  transitionModel.getBackground()), (int) CARD_TRANSITION_DURATION);
         }

         bankCardWidget
               .animate()
               .translationY(0)
               .setListener(new AnimatorListenerAdapter() {
                  @Override
                  public void onAnimationEnd(Animator animation) {
                     super.onAnimationEnd(animation);
                     controlsLayout.animate().alpha(1).setDuration(500);
                  }
               })
               .setDuration(CARD_TRANSITION_DURATION)
               .start();
      }
   }

   private void setUpViewPosition(TransitionModel params, View view) {
      int[] coords = new int[2];
      view.getLocationOnScreen(coords);
      view.setTranslationY(params.getTop() - coords[1] + params.getOverlap());
   }

   @Override
   protected void onDetachedFromWindow() {
      if (networkConnectionErrorDialog != null) networkConnectionErrorDialog.dismiss();
      super.onDetachedFromWindow();
   }
}
