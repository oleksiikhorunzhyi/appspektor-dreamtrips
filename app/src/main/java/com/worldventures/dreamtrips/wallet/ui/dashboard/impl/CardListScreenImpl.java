package com.worldventures.dreamtrips.wallet.ui.dashboard.impl;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.databinding.CardCellBindingBinding;
import com.worldventures.dreamtrips.databinding.ScreenWalletCardlistBinding;
import com.worldventures.dreamtrips.wallet.domain.WalletConstants;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUser;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUserPhone;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUserPhoto;
import com.worldventures.dreamtrips.wallet.service.command.SyncSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.record.SyncRecordOnNewDeviceCommand;
import com.worldventures.dreamtrips.wallet.service.command.reset.ResetSmartCardCommand;
import com.worldventures.dreamtrips.wallet.ui.common.adapter.BaseViewModel;
import com.worldventures.dreamtrips.wallet.ui.common.adapter.RecyclerItemClickListener;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletBaseController;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.delegate.DialogOperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.ErrorViewFactory;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.SimpleDialogErrorViewProvider;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.SmartCardErrorViewProvider;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.progress.AnimatorProgressView;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.progress.SimpleDialogProgressView;
import com.worldventures.dreamtrips.wallet.ui.common.recycler.WrapContentLinearLayoutManager;
import com.worldventures.dreamtrips.wallet.ui.dashboard.CardListPresenter;
import com.worldventures.dreamtrips.wallet.ui.dashboard.CardListScreen;
import com.worldventures.dreamtrips.wallet.ui.dashboard.util.OverlapDecoration;
import com.worldventures.dreamtrips.wallet.ui.dashboard.util.adapter.DashboardHolderAdapter;
import com.worldventures.dreamtrips.wallet.ui.dashboard.util.adapter.DashboardHolderFactoryImpl;
import com.worldventures.dreamtrips.wallet.ui.dashboard.util.model.CommonCardViewModel;
import com.worldventures.dreamtrips.wallet.ui.dashboard.util.model.TransitionModel;
import com.worldventures.dreamtrips.wallet.ui.dashboard.util.viewholder.CardStackHeaderHolder;
import com.worldventures.dreamtrips.wallet.ui.dashboard.util.viewholder.CommonCardHolder;
import com.worldventures.dreamtrips.wallet.ui.dashboard.util.viewholder.ImmutableCardStackHeaderHolder;
import com.worldventures.dreamtrips.wallet.ui.dialog.InstallFirmwareErrorDialog;
import com.worldventures.dreamtrips.wallet.ui.settings.general.reset.FactoryResetDelegate;
import com.worldventures.dreamtrips.wallet.ui.settings.general.reset.FactoryResetOperationView;
import com.worldventures.dreamtrips.wallet.ui.widget.SmartCardWidget;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import io.techery.janet.operationsubscriber.view.ComposableOperationView;
import io.techery.janet.operationsubscriber.view.OperationView;
import io.techery.janet.smartcard.exception.WaitingResponseException;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class CardListScreenImpl extends WalletBaseController<CardListScreen, CardListPresenter>
      implements CardListScreen {
   private static final String KEY_SHOW_UPDATE_BUTTON_STATE = "CardListScreen#KEY_SHOW_UPDATE_BUTTON_STATE";
   private static final double VISIBLE_SCALE = 0.64;

   @InjectView(R.id.bank_card_list) RecyclerView bankCardList;
   @InjectView(R.id.empty_card_view) TextView emptyCardListView;
   @InjectView(R.id.fab_button) FloatingActionButton fabButton;
   @InjectView(R.id.firmware_available) View firmwareAvailableView;
   @InjectView(R.id.toolbar) Toolbar toolbar;
   @InjectView(R.id.widget_dashboard_smart_card) SmartCardWidget smartCardWidget;

   @Inject CardListPresenter presenter;

   private CardStackHeaderHolder cardStackHeaderHolder;

   private InstallFirmwareErrorDialog installFirmwareErrorDialog;
   private MaterialDialog forceUpdateDialog;
   private Dialog addCardErrorDialog;
   private Dialog factoryResetConfirmationDialog;
   private Dialog scNonConnectionDialog;

   private DashboardHolderAdapter multiAdapter;
   private ScreenWalletCardlistBinding binding;

   private ArrayList<BaseViewModel> cardViewModels;
   private static final String KEY_LOADED_CARDS_LIST = "CardListScreen#KEY_CARD_LIST";

   public CardListScreenImpl() {
      cardStackHeaderHolder = ImmutableCardStackHeaderHolder.builder().build();
   }

   @Override
   protected void onFinishInflate(View view) {
      super.onFinishInflate(view);
      toolbar.setNavigationOnClickListener(it -> getPresenter().navigationClick());
      binding = DataBindingUtil.bind(view);
      setupCardStackList();
   }

   @Override
   protected void onDetach(@NonNull View view) {
      dismissDialogs();
      super.onDetach(view);
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
      return new DialogOperationScreen(getView());
   }

   @Override
   public void showRecordsInfo(ArrayList<BaseViewModel> result) {
      if (this.cardViewModels == null) {
         bankCardList.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(getContext(),
               R.anim.wallet_bottom_to_top_layout_anim));
         bankCardList.scheduleLayoutAnimation();
      } else {
         bankCardList.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(getContext(),
               R.anim.wallet_instant_layout_anim));
      }
      multiAdapter.swapList(result);
      this.cardViewModels = result;
      emptyCardListView.setVisibility(result != null && !result.isEmpty() ? GONE : VISIBLE);
   }

   @Override
   public void setDefaultSmartCard() {
      smartCardWidget.bindCard(cardStackHeaderHolder);
   }

   @Override
   public void setSmartCardStatusAttrs(int batteryLevel, boolean connected, boolean lock, boolean stealthMode) {
      smartCardWidget.bindCard(cardStackHeaderHolder = ImmutableCardStackHeaderHolder.builder()
            .from(cardStackHeaderHolder)
            .batteryLevel(batteryLevel)
            .connected(connected)
            .lock(lock)
            .stealthMode(stealthMode)
            .build());
   }

   @Override
   public void setSmartCardUser(SmartCardUser smartCardUser) {
      final SmartCardUserPhoto photo = smartCardUser.userPhoto();
      final SmartCardUserPhone phone = smartCardUser.phoneNumber();
      smartCardWidget.bindCard(cardStackHeaderHolder = ImmutableCardStackHeaderHolder.builder()
            .from(cardStackHeaderHolder)
            .firstName(smartCardUser.firstName())
            .middleName(smartCardUser.middleName())
            .lastName(smartCardUser.lastName())
            .photoUrl(photo != null ? photo.uri() : "")
            .phoneNumber(phone != null ? phone.fullPhoneNumber() : "")
            .build());
   }

   @Override
   public void setFirmwareUpdateAvailable(boolean firmwareUpdateAvailable) {
      smartCardWidget.bindCard(cardStackHeaderHolder = ImmutableCardStackHeaderHolder.builder()
            .from(cardStackHeaderHolder)
            .firmwareUpdateAvailable(firmwareUpdateAvailable)
            .build());
   }

   @Override
   public void setCardsCount(int count) {
      smartCardWidget.bindCard(cardStackHeaderHolder = ImmutableCardStackHeaderHolder.builder()
            .from(cardStackHeaderHolder)
            .cardCount(count)
            .build());
   }

   @Override
   public void setDisplayType(int displayType) {
      smartCardWidget.bindCard(cardStackHeaderHolder = ImmutableCardStackHeaderHolder.builder()
            .from(cardStackHeaderHolder)
            .displayType(displayType)
            .build());
   }

   @Override
   public void showAddCardErrorDialog(@CardListScreen.ErrorDialogType int errorDialogType) {
      MaterialDialog.Builder builder = new MaterialDialog.Builder(getContext());

      switch (errorDialogType) {
         case ERROR_DIALOG_FULL_SMARTCARD:
            builder.content(R.string.wallet_wizard_full_card_list_error_message, WalletConstants.MAX_CARD_LIMIT);
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
               .setOnRetryction(() -> getPresenter().retryFWU())
               .setOnCancelAction(() -> getPresenter().retryFWUCanceled());
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
               .cancelable(false)
               .onNegative((dialog, which) -> getPresenter().navigateBack())
               .positiveText(R.string.wallet_dashboard_update_dialog_btn_text_positive)
               .onPositive((dialog, which) -> getPresenter().confirmForceFirmwareUpdate())
               .build();
      } else {
         forceUpdateDialog.dismiss();
      }
      if (!forceUpdateDialog.isShowing() && (installFirmwareErrorDialog == null || !installFirmwareErrorDialog.isShowing())) {
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
   protected void onSaveViewState(@NonNull View view, @NonNull Bundle outState) {
      outState.putInt(KEY_SHOW_UPDATE_BUTTON_STATE, firmwareAvailableView.getVisibility());
      super.onSaveViewState(view, outState);
   }

   @SuppressWarnings("WrongConstant")
   @Override
   protected void onRestoreViewState(@NonNull View view, @NonNull Bundle savedViewState) {
      super.onRestoreViewState(view, savedViewState);
      firmwareAvailableView.setVisibility(savedViewState.getInt(KEY_SHOW_UPDATE_BUTTON_STATE, GONE));
   }

   @Override
   protected void onSaveInstanceState(@NonNull Bundle outState) {
      outState.putParcelableArrayList(KEY_LOADED_CARDS_LIST, this.cardViewModels);
      super.onSaveInstanceState(outState);
   }

   @SuppressWarnings("WrongConstant")
   @Override
   protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
      super.onRestoreInstanceState(savedInstanceState);
      this.cardViewModels = savedInstanceState.getParcelableArrayList(KEY_LOADED_CARDS_LIST);
   }

   private void setupCardStackList() {
      int dimension = getResources().getDimensionPixelSize(R.dimen.wallet_card_height);
      multiAdapter = new DashboardHolderAdapter<>(new ArrayList<>(), new DashboardHolderFactoryImpl());
      bankCardList.setAdapter(multiAdapter);
      final DefaultItemAnimator listAnimator = new DefaultItemAnimator();
      listAnimator.setSupportsChangeAnimations(false);
      bankCardList.setItemAnimator(listAnimator);
      bankCardList.addItemDecoration(new OverlapDecoration((int) (dimension * VISIBLE_SCALE * -1)));
      WrapContentLinearLayoutManager layout = new WrapContentLinearLayoutManager(getContext());
      layout.setAutoMeasureEnabled(true);
      bankCardList.setLayoutManager(layout);
      bankCardList.addOnItemTouchListener(new RecyclerItemClickListener(getContext(),
            new RecyclerItemClickListener.OnItemClickListener() {
               @Override
               public void onItemClick(View view, int position) {
                  if (!getPresenter().isCardDetailSupported()) return;
                  if (multiAdapter.getItemViewType(position) == R.layout.card_cell_binding) {
                     showDetails(view, (int) (dimension * VISIBLE_SCALE * -1));
                  }
               }

               @Override
               public void onItemLongClick(View childView, int position, Point point) {

               }
            }));

      smartCardWidget.setOnSettingsClickListener(v -> getPresenter().onSettingsChosen());
      smartCardWidget.setOnPhotoClickListener(v -> getPresenter().onProfileChosen());
      binding.transitionView.getRoot().setVisibility(GONE);


      if (null != this.cardViewModels) {
         showRecordsInfo(this.cardViewModels);
      }
   }

   private void showDetails(View view, int overlap) {
      CommonCardViewModel model = ((CommonCardHolder) bankCardList.getChildViewHolder(view)).getData();
      TransitionModel transitionModel = getPresenter().getCardPosition(view, overlap, model.getCardBackGround(),
            model.isDefaultCard());
      addTransitionView(model, transitionModel);
      getPresenter().cardClicked(model, transitionModel);
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
      fabButton.setRotation(0);
      fabButton.setImageResource(R.drawable.ic_wallet_vector_white_plus);
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
      getPresenter().syncPayments();
   }

   @Override
   public void showSyncFailedOptionsDialog() {
      new MaterialDialog.Builder(getContext())
            .title(R.string.wallet_wizard_card_list_sync_fail_dialog_title)
            .content(R.string.wallet_wizard_card_list_sync_fail_dialog_message)
            .positiveText(R.string.wallet_wizard_card_list_sync_fail_dialog_cancel)
            .neutralText(R.string.wallet_wizard_card_list_sync_fail_dialog_retry)
            .negativeText(R.string.wallet_wizard_card_list_sync_fail_dialog_factory_reset)
            .onNeutral((dialog, which) -> getPresenter().syncPayments())
            .onNegative((dialog, which) -> getPresenter().goToFactoryReset())
            .build().show();
   }

   @Override
   public OperationView<SyncSmartCardCommand> provideOperationSyncSmartCard() {
      return new ComposableOperationView<>(
            new SimpleDialogProgressView<>(getContext(), R.string.wallet_wizard_card_list_card_synchronization_dialog_text, false),
            ErrorViewFactory.<SyncSmartCardCommand>builder()
                  .addProvider(new SimpleDialogErrorViewProvider<>(getContext(), WaitingResponseException.class, R.string.wallet_smart_card_is_disconnected))
                  .addProvider(new SmartCardErrorViewProvider<>(getContext()))
                  .build()
      );
   }

   public OperationView<SyncRecordOnNewDeviceCommand> provideReSyncOperationView() {
      return new ComposableOperationView<>(
            new AnimatorProgressView<>(ObjectAnimator.ofFloat(fabButton, View.ROTATION.getName(), 0f, -360f)
                  .setDuration(650))
      );
   }

   @Override
   public Context getViewContext() {
      return getContext();
   }

   @Override
   public FloatingActionButton getCardListFab() {
      return fabButton;
   }

   @Override
   public TextView getEmptyCardListView() {
      return emptyCardListView;
   }

   @Override
   public OperationView<ResetSmartCardCommand> provideResetOperationView(FactoryResetDelegate factoryResetDelegate) {
      return FactoryResetOperationView.create(getContext(),
            factoryResetDelegate::factoryReset,
            () -> {
            },
            R.string.wallet_error_enter_pin_title,
            R.string.wallet_error_enter_pin_msg,
            R.string.retry,
            R.string.cancel,
            R.string.loading,
            false);
   }

   @Override
   public CardListPresenter getPresenter() {
      return presenter;
   }

   @Override
   public View inflateView(LayoutInflater layoutInflater, ViewGroup viewGroup) {
      return layoutInflater.inflate(R.layout.screen_wallet_cardlist, viewGroup, false);
   }

   @Override
   public boolean supportConnectionStatusLabel() {
      return true;
   }

   @Override
   public boolean supportHttpConnectionStatusLabel() {
      return true;
   }
}
