package com.worldventures.dreamtrips.wallet.ui.dashboard;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.databinding.CardCellBindingBinding;
import com.worldventures.dreamtrips.databinding.ScreenWalletCardlistBinding;
import com.worldventures.dreamtrips.wallet.service.command.SyncSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.record.SyncRecordOnNewDeviceCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.delegate.DialogOperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.ErrorViewFactory;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.SimpleDialogErrorViewProvider;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.progress.AnimatorProgressView;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.progress.SimpleDialogProgressView;
import com.worldventures.dreamtrips.wallet.ui.dashboard.util.OverlapDecoration;
import com.worldventures.dreamtrips.wallet.ui.dashboard.util.adapter.BaseViewModel;
import com.worldventures.dreamtrips.wallet.ui.dashboard.util.adapter.MultiHolderAdapter;
import com.worldventures.dreamtrips.wallet.ui.dashboard.util.adapter.RecyclerItemClickListener;
import com.worldventures.dreamtrips.wallet.ui.dashboard.util.model.CommonCardViewModel;
import com.worldventures.dreamtrips.wallet.ui.dashboard.util.model.TransitionModel;
import com.worldventures.dreamtrips.wallet.ui.dashboard.util.viewholder.CardStackHeaderHolder;
import com.worldventures.dreamtrips.wallet.ui.dashboard.util.viewholder.CommonCardHolder;
import com.worldventures.dreamtrips.wallet.ui.dashboard.util.viewholder.ImmutableCardStackHeaderHolder;
import com.worldventures.dreamtrips.wallet.ui.dialog.InstallFirmwareErrorDialog;
import com.worldventures.dreamtrips.wallet.ui.widget.SmartCardWidget;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;
import io.techery.janet.operationsubscriber.view.ComposableOperationView;
import io.techery.janet.operationsubscriber.view.OperationView;
import io.techery.janet.smartcard.exception.WaitingResponseException;

public class CardListScreen extends WalletLinearLayout<CardListPresenter.Screen, CardListPresenter, CardListPath> implements CardListPresenter.Screen {

   private static final String KEY_SHOW_UPDATE_BUTTON_STATE = "CardListScreen#KEY_SHOW_UPDATE_BUTTON_STATE";
   private static final long FADE_ANIMATION_DURATION = 250;
   private static final double VISIBLE_SCALE = 0.64;

   @InjectView(R.id.bank_card_list) RecyclerView bankCardList;
   @InjectView(R.id.empty_card_view) TextView emptyCardListView;
   @InjectView(R.id.fab_button) FloatingActionButton fabButton;
   @InjectView(R.id.firmware_available) View firmwareAvailableView;
   @InjectView(R.id.toolbar) Toolbar toolbar;
   @InjectView(R.id.widget_dashboard_smart_card) SmartCardWidget smartCardWidget;
   @InjectView(R.id.content_layer) RelativeLayout contentLayout;

   private CardStackHeaderHolder cardStackHeaderHolder;

   private InstallFirmwareErrorDialog installFirmwareErrorDialog;
   private MaterialDialog forceUpdateDialog;
   private Dialog addCardErrorDialog;
   private Dialog factoryResetConfirmationDialog;
   private Dialog scNonConnectionDialog;

   private MultiHolderAdapter multiAdapter;
   private ScreenWalletCardlistBinding binding;

   public CardListScreen(Context context) {
      this(context, null);
   }

   public CardListScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
      cardStackHeaderHolder = ImmutableCardStackHeaderHolder.builder().build();
   }

   @NonNull
   @Override
   public CardListPresenter createPresenter() {
      return new CardListPresenter(getContext(), getInjector());
   }

   @Override
   protected void onPostAttachToWindowView() {
      toolbar.setNavigationOnClickListener(it -> presenter.navigationClick());
      if (isInEditMode()) return;
      binding = DataBindingUtil.bind(this);
      setupCardStackList();
   }


   @Override
   protected void onDetachedFromWindow() {
      dismissDialogs();

      super.onDetachedFromWindow();
   }

   private void dismissDialogs() {
      if (installFirmwareErrorDialog != null) installFirmwareErrorDialog.dismiss();
      if (forceUpdateDialog != null) forceUpdateDialog.dismiss();
      if (addCardErrorDialog != null) addCardErrorDialog.dismiss();
      if (factoryResetConfirmationDialog != null) factoryResetConfirmationDialog.dismiss();
      if (scNonConnectionDialog != null) scNonConnectionDialog.dismiss();
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      return new DialogOperationScreen(this);
   }

   @Override
   public void showRecordsInfo(List<BaseViewModel> result) {
      multiAdapter.updateItems(result);
      emptyCardListView.setVisibility(multiAdapter.getItemCount() <= 1 ? VISIBLE : GONE);
   }

   @Override
   public void setDefaultSmartCard() {
      smartCardWidget.bindCard(cardStackHeaderHolder);
   }

   @Override
   public void setSmartCardStatusAttrs(int batteryLevel, boolean connected, boolean lock, boolean stealthMode) {
      cardStackHeaderHolder = ImmutableCardStackHeaderHolder.builder()
            .from(cardStackHeaderHolder)
            .batteryLevel(batteryLevel)
            .connected(connected)
            .lock(lock)
            .stealthMode(stealthMode)
            .build();

      smartCardWidget.bindCard(cardStackHeaderHolder);
   }

   @Override
   public void setSmartCardUserAttrs(String fullname, String photoFileUrl) {
      cardStackHeaderHolder = ImmutableCardStackHeaderHolder.builder()
            .from(cardStackHeaderHolder)
            .fullname(fullname)
            .photoUrl(photoFileUrl)
            .build();

      smartCardWidget.bindCard(cardStackHeaderHolder);
   }

   @Override
   public void setFirmwareUpdateAvailable(boolean firmwareUpdateAvailable) {
      cardStackHeaderHolder = ImmutableCardStackHeaderHolder.builder()
            .from(cardStackHeaderHolder)
            .firmwareUpdateAvailable(firmwareUpdateAvailable)
            .build();

      smartCardWidget.bindCard(cardStackHeaderHolder);
   }

   @Override
   public void setCardsCount(int count) {
      cardStackHeaderHolder = ImmutableCardStackHeaderHolder.builder()
            .from(cardStackHeaderHolder)
            .cardCount(count)
            .build();

      smartCardWidget.bindCard(cardStackHeaderHolder);
   }

   @Override
   public void showAddCardErrorDialog(@ErrorDialogType int errorDialogType) {
      MaterialDialog.Builder builder = new MaterialDialog.Builder(getContext());

      switch (errorDialogType) {
         case ERROR_DIALOG_FULL_SMARTCARD:
            builder.content(R.string.wallet_wizard_full_card_list_error_message);
            break;
         case ERROR_DIALOG_NO_INTERNET_CONNECTION:
            builder.title(R.string.wallet_wizard_no_internet_connection_title);
            builder.content(R.string.wallet_wizard_limited_access);
            break;
         case ERROR_DIALOG_NO_SMARTCARD_CONNECTION:
            builder.title(R.string.wallet_wizard_no_connection_to_card_title);
            builder.content(R.string.wallet_wizard_limited_access);
            break;
      }

      addCardErrorDialog = builder.positiveText(R.string.ok)
            .negativeText(R.string.cancel)
            .build();
      addCardErrorDialog.show();
   }

   @Override
   public void hideFirmwareUpdateBtn() {
      firmwareAvailableView.setVisibility(GONE);
   }

   @Override
   public void showFirmwareUpdateBtn() {
      if (firmwareAvailableView.getVisibility() == VISIBLE) return;
      firmwareAvailableView.setVisibility(VISIBLE);
   }

   @Override
   public void showFirmwareUpdateError() {
      if (installFirmwareErrorDialog == null) {
         installFirmwareErrorDialog = new InstallFirmwareErrorDialog(getContext())
               .setOnRetryction(() -> presenter.navigateToFirmwareUpdate())
               .setOnCancelAction(() -> presenter.navigateBack());
      }
      if (!installFirmwareErrorDialog.isShowing()) {
         if (forceUpdateDialog != null && forceUpdateDialog.isShowing()) {
            forceUpdateDialog.setOnCancelListener(null);
            forceUpdateDialog.cancel();
         }
         installFirmwareErrorDialog.show();
      }
   }

   @Override
   public void showForceFirmwareUpdateDialog() {
      if (forceUpdateDialog == null) {
         forceUpdateDialog = new MaterialDialog.Builder(getContext())
               .title(R.string.wallet_dashboard_update_dialog_title)
               .content(R.string.wallet_dashboard_update_dialog_content)
               .negativeText(R.string.wallet_dashboard_update_dialog_btn_text_negative)
               .cancelListener(dialog -> getPresenter().navigateBack())
               .onNegative((dialog, which) -> getPresenter().navigateBack())
               .positiveText(R.string.wallet_dashboard_update_dialog_btn_text_positive)
               .onPositive((dialog, which) -> getPresenter().confirmForceFirmwareUpdate())
               .build();
      } else {
         forceUpdateDialog.dismiss();
      }
      if (installFirmwareErrorDialog == null || !installFirmwareErrorDialog.isShowing()) {
         forceUpdateDialog.show();
      }
   }

   @Override
   public void showFactoryResetConfirmationDialog() {
      if (factoryResetConfirmationDialog == null) {
         factoryResetConfirmationDialog = new MaterialDialog.Builder(getContext())
               .content(R.string.wallet_dashboard_factory_reset_dialog_content)
               .negativeText(R.string.wallet_dashboard_factory_reset_dialog_btn_text_negative)
               .cancelListener(dialog -> getPresenter().navigateBack())
               .onNegative((dialog, which) -> getPresenter().navigateBack())
               .positiveText(R.string.wallet_dashboard_factory_reset_dialog_btn_text_positive)
               .onPositive((dialog, which) -> getPresenter().navigateToFirmwareUpdate())
               .build();
      }
      if (!factoryResetConfirmationDialog.isShowing()) factoryResetConfirmationDialog.show();
   }

   @Override
   protected Parcelable onSaveInstanceState() {
      Bundle state = (Bundle) super.onSaveInstanceState();
      state.putInt(KEY_SHOW_UPDATE_BUTTON_STATE, firmwareAvailableView.getVisibility());
      return state;
   }

   @Override
   protected void onRestoreInstanceState(Parcelable state) {
      //noinspection all
      firmwareAvailableView.setVisibility(((Bundle) state).getInt(KEY_SHOW_UPDATE_BUTTON_STATE, GONE));
      super.onRestoreInstanceState(state);
   }


   private void setupCardStackList() {

      int dimension = getContext().getResources().getDimensionPixelSize(R.dimen.wallet_card_height);
      multiAdapter = new MultiHolderAdapter<>(new ArrayList<>());
      bankCardList.setAdapter(multiAdapter);
      final DefaultItemAnimator listAnimator = new DefaultItemAnimator();
      listAnimator.setSupportsChangeAnimations(false);
      bankCardList.setItemAnimator(listAnimator);
      bankCardList.addItemDecoration(new OverlapDecoration((int) (dimension * VISIBLE_SCALE * -1)));
      LinearLayoutManager layout = new LinearLayoutManager(getContext());
      layout.setAutoMeasureEnabled(true);
      bankCardList.setLayoutManager(layout);
      bankCardList.addOnItemTouchListener(new RecyclerItemClickListener(getContext(),
            new RecyclerItemClickListener.OnItemClickListener() {
               @Override
               public void onItemClick(View view, int position) {
                  if (multiAdapter.getItemViewType(position) == R.layout.card_cell_binding) {
                     showDetails(view, (int) (dimension * VISIBLE_SCALE * -1));
                  }
               }

               @Override
               public void onItemLongClick(View childView, int position, Point point) {

               }
            }));

      smartCardWidget.setOnSettingsClickListener(v -> presenter.onSettingsChosen());

      binding.transitionView.getRoot().setVisibility(GONE);
   }

   private void showDetails(View view, int overlap) {
      CommonCardViewModel model = ((CommonCardHolder) bankCardList.getChildViewHolder(view)).getData();
      TransitionModel transitionModel = presenter.getCardPosition(view, overlap, model.isCardBackGround());
      addTransitionView(model, transitionModel);
      smartCardWidget.animate().alpha(0).setDuration(FADE_ANIMATION_DURATION).start();
      bankCardList
            .animate()
            .alpha(0)
            .setDuration(FADE_ANIMATION_DURATION)
            .setListener(new AnimatorListenerAdapter() {
               @Override
               public void onAnimationEnd(Animator animation) {
                  super.onAnimationEnd(animation);
                  presenter.cardClicked(model.getRecordId(), transitionModel);
               }
            });
   }

   private void addTransitionView(CommonCardViewModel model, TransitionModel transitionModel) {
      CardCellBindingBinding transitionView = binding.transitionView;
      transitionView.setCardModel(model);
      setUpViewPosition(transitionModel, transitionView.getRoot());
      transitionView.getRoot().setVisibility(VISIBLE);
   }

   private void setUpViewPosition(TransitionModel params, View view) {
      int[] coords = new int[2];
      view.getLocationOnScreen(coords);
      view.setTranslationX(0);
      view.setTranslationY(params.getTop() - coords[1]);
   }

   @OnClick(R.id.firmware_available)
   protected void firmwareAvailableBtnClick() {
      getPresenter().navigateToFirmwareUpdate();
   }

   @Override
   public void showSCNonConnectionDialog() {
      if (scNonConnectionDialog == null) {
         scNonConnectionDialog = new MaterialDialog.Builder(getContext())
               .title(R.string.wallet_card_settings_cant_connected)
               .content(R.string.wallet_card_settings_message_cant_connected)
               .positiveText(R.string.ok)
               .build();
      }
      if (!scNonConnectionDialog.isShowing()) scNonConnectionDialog.show();
   }

   @Override
   public void modeAddCard() {
      emptyCardListView.setText(R.string.wallet_wizard_empty_card_list_label);
      fabButton.setImageResource(R.drawable.ic_white_plus);
      fabButton.setOnClickListener(v -> addCardButtonClick());
   }

   @Override
   public void modeSyncPaymentsFab() {
      emptyCardListView.setText(R.string.wallet_wizard_card_list_remove_payment_cards_message);
      fabButton.setImageResource(R.drawable.ic_sync);
      fabButton.setOnClickListener(v -> onSyncPaymentsCardsButtonClick());
   }

   private void addCardButtonClick() {
      getPresenter().addCardRequired(cardStackHeaderHolder.cardCount());
   }

   protected void onSyncPaymentsCardsButtonClick() {
      presenter.syncPayments();
   }

   @Override
   public void showSyncFailedOptionsDialog() {
      new MaterialDialog.Builder(getContext())
            .title(R.string.wallet_wizard_card_list_sync_fail_dialog_title)
            .content(R.string.wallet_wizard_card_list_sync_fail_dialog_message)
            .positiveText(R.string.wallet_wizard_card_list_sync_fail_dialog_cancel)
            .neutralText(R.string.wallet_wizard_card_list_sync_fail_dialog_retry)
            .negativeText(R.string.wallet_wizard_card_list_sync_fail_dialog_factory_reset)
            .onNeutral((dialog, which) -> presenter.syncPayments())
            .onNegative((dialog, which) -> presenter.goToFactoryReset())
            .build().show();
   }

   @Override
   public OperationView<SyncSmartCardCommand> provideOperationSyncSmartCard() {
      return new ComposableOperationView<>(
            new SimpleDialogProgressView<>(getContext(), R.string.wallet_wizard_card_list_card_synchronization_dialog_text, false),
            ErrorViewFactory.<SyncSmartCardCommand>builder()
                  .addProvider(new SimpleDialogErrorViewProvider<>(getContext(), WaitingResponseException.class, R.string.wallet_smart_card_is_disconnected))
                  .build()
      );
   }

   public OperationView<SyncRecordOnNewDeviceCommand> provideReSyncOperationView() {
      return new ComposableOperationView<>(
            new AnimatorProgressView<>(ObjectAnimator.ofFloat(fabButton, View.ROTATION.getName(), 0f, -360f)
                  .setDuration(650))
      );
   }
}
