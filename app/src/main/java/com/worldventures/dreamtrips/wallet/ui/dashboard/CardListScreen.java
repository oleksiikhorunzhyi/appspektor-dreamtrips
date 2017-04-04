package com.worldventures.dreamtrips.wallet.ui.dashboard;

import android.app.Dialog;
import android.content.Context;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.eowise.recyclerview.stickyheaders.StickyHeadersBuilder;
import com.eowise.recyclerview.stickyheaders.StickyHeadersItemDecoration;
import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.ProjectTextUtils;
import com.worldventures.dreamtrips.modules.bucketlist.view.adapter.IgnoreFirstItemAdapter;
import com.worldventures.dreamtrips.wallet.domain.entity.record.Record;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.delegate.DialogOperationScreen;
import com.worldventures.dreamtrips.wallet.ui.dashboard.util.CardListHeaderAdapter;
import com.worldventures.dreamtrips.wallet.ui.dashboard.util.CardStackHeaderHolder;
import com.worldventures.dreamtrips.wallet.ui.dashboard.util.CardStackViewModel;
import com.worldventures.dreamtrips.wallet.ui.dashboard.util.ImmutableCardStackHeaderHolder;
import com.worldventures.dreamtrips.wallet.ui.dashboard.util.cell.CardStackCell;
import com.worldventures.dreamtrips.wallet.ui.dashboard.util.cell.CardStackHeaderCell;
import com.worldventures.dreamtrips.wallet.ui.dialog.InstallFirmwareErrorDialog;

import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;

public class CardListScreen extends WalletLinearLayout<CardListPresenter.Screen, CardListPresenter, CardListPath> implements CardListPresenter.Screen {

   private static final String KEY_SHOW_UPDATE_BUTTON_STATE = "CardListScreen#KEY_SHOW_UPDATE_BUTTON_STATE";

   @InjectView(R.id.bank_card_list) RecyclerView bankCardList;
   @InjectView(R.id.empty_card_view) View emptyCardListView;
   @InjectView(R.id.ll_sync_payments_block) LinearLayout llSyncPaymentsBlock;
   @InjectView(R.id.tv_fail_sync_msg) TextView tvFailsSyncMsg;
   @InjectView(R.id.add_card_button) FloatingActionButton addCardButton;
   @InjectView(R.id.btn_sync_cards) FloatingActionButton btnSyncPaymentCards;
   @InjectView(R.id.firmware_available) View firmwareAvailableView;
   @InjectView(R.id.tv_remove_payment_cards) View removePaymentCards;
   @InjectView(R.id.toolbar) Toolbar toolbar;

   private IgnoreFirstItemAdapter adapter;
   private CardStackHeaderHolder cardStackHeaderHolder;

   private InstallFirmwareErrorDialog installFirmwareErrorDialog;
   private Dialog synchronizationDialog;
   private MaterialDialog forceUpdateDialog;
   private Dialog addCardErrorDialog;
   private Dialog factoryResetConfirmationDialog;
   private Dialog scNonConnectionDialog;

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
      tvFailsSyncMsg.setText(ProjectTextUtils.fromHtml(getString(R.string.wallet_wizard_card_list_remove_payment_cards_message)));

      setupCardStackList();
   }

   @Override
   protected void onDetachedFromWindow() {
      dismissDialogs();

      super.onDetachedFromWindow();
   }

   private void dismissDialogs() {
      if (synchronizationDialog != null) synchronizationDialog.dismiss();
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
   public void showRecordsInfo(List<CardStackViewModel> result) {
      adapter.clear();
      adapter.addItems(result);
      emptyCardListView.setVisibility(adapter.getCount() <= 1 ? VISIBLE : GONE);
   }

   @Override
   public void setDefaultSmartCard() {
      notifySmartCardChanged(cardStackHeaderHolder);
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
      notifySmartCardChanged(cardStackHeaderHolder);
   }

   @Override
   public void setSmartCardUserAttrs(String fullname, String photoFileUrl) {
      cardStackHeaderHolder = ImmutableCardStackHeaderHolder.builder()
            .from(cardStackHeaderHolder)
            .fullname(fullname)
            .photoUrl(photoFileUrl)
            .build();
      notifySmartCardChanged(cardStackHeaderHolder);
   }

   @Override
   public void setFirmwareUpdateAvailable(boolean firmwareUpdateAvailable) {
      cardStackHeaderHolder = ImmutableCardStackHeaderHolder.builder()
            .from(cardStackHeaderHolder)
            .firmwareUpdateAvailable(firmwareUpdateAvailable)
            .build();
      notifySmartCardChanged(cardStackHeaderHolder);
   }

   @Override
   public void setCardsCount(int count) {
      cardStackHeaderHolder = ImmutableCardStackHeaderHolder.builder()
            .from(cardStackHeaderHolder)
            .cardCount(count)
            .build();
      notifySmartCardChanged(cardStackHeaderHolder);
   }

   private void notifySmartCardChanged(CardStackHeaderHolder cardStackHeaderHolder) {
      Object header = Queryable.from(adapter.getItems()).firstOrDefault(it -> it instanceof CardStackHeaderHolder);
      int headerPosition = 0;
      if (header != null) {
         headerPosition = adapter.getItems().indexOf(header);
         adapter.replaceItem(headerPosition, cardStackHeaderHolder);
         adapter.notifyItemChanged(headerPosition);
      } else {
         adapter.addItem(headerPosition, cardStackHeaderHolder);
         adapter.notifyItemInserted(headerPosition);
      }
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
   public void showCardSynchronizationDialog(boolean visible) {
      if (visible) {
         if (synchronizationDialog == null) createSynchronizationDialog();
         synchronizationDialog.show();
      } else {
         if (synchronizationDialog != null) synchronizationDialog.dismiss();
      }
   }

   private void createSynchronizationDialog() {
      synchronizationDialog = new MaterialDialog.Builder(getContext())
            .content(getString(R.string.wallet_wizard_card_list_card_synchronization_dialog_text))
            .progress(true, 0)
            .cancelable(false)
            .build();
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
               .onPositive((dialog, which) -> getPresenter().handleForceFirmwareUpdateConfirmation())
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
      factoryResetConfirmationDialog = new MaterialDialog.Builder(getContext())
            .content(R.string.wallet_dashboard_factory_reset_dialog_content)
            .negativeText(R.string.wallet_dashboard_factory_reset_dialog_btn_text_negative)
            .cancelListener(dialog -> getPresenter().navigateBack())
            .onNegative((dialog, which) -> getPresenter().navigateBack())
            .positiveText(R.string.wallet_dashboard_factory_reset_dialog_btn_text_positive)
            .onPositive((dialog, which) -> getPresenter().navigateToFirmwareUpdate())
            .build();
      factoryResetConfirmationDialog.show();
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
      adapter = new IgnoreFirstItemAdapter(getContext(), getInjector());
      adapter.registerCell(CardStackViewModel.class, CardStackCell.class);
      adapter.registerDelegate(CardStackViewModel.class, new CardStackCell.Delegate() {
         @Override
         public void onCardClicked(Record record) {
            getPresenter().cardClicked(record);
         }
      });
      adapter.registerIdDelegate(CardStackViewModel.class, model -> {
         CardStackViewModel vm = ((CardStackViewModel) model);
         return vm.getHeaderTitle() != null ? vm.getHeaderTitle().hashCode() : 0;
      });

      adapter.registerCell(ImmutableCardStackHeaderHolder.class, CardStackHeaderCell.class);
      adapter.registerDelegate(ImmutableCardStackHeaderHolder.class, new CardStackHeaderCell.Delegate() {
         @Override
         public void onCellClicked(CardStackHeaderHolder model) {

         }

         @Override
         public void onSettingsChosen() {
            presenter.onSettingsChosen();
         }
      });

      bankCardList.setAdapter(adapter);
      final DefaultItemAnimator listAnimator = new DefaultItemAnimator();
      listAnimator.setSupportsChangeAnimations(false);
      bankCardList.setItemAnimator(listAnimator);
      bankCardList.addItemDecoration(getStickyHeadersItemDecoration(adapter));
      LinearLayoutManager layout = new LinearLayoutManager(getContext());
      layout.setAutoMeasureEnabled(true);
      bankCardList.setLayoutManager(layout);
   }

   private StickyHeadersItemDecoration getStickyHeadersItemDecoration(BaseArrayListAdapter adapter) {
      return new StickyHeadersBuilder().setAdapter(adapter)
            .setRecyclerView(bankCardList)
            .setStickyHeadersAdapter(new CardListHeaderAdapter(adapter.getItems()), false)
            .build();
   }

   @OnClick(R.id.add_card_button)
   protected void addCardButtonClick() {
      getPresenter().addCardRequired(cardStackHeaderHolder.cardCount());
   }

   @OnClick(R.id.firmware_available)
   protected void firmwareAvailableBtnClick() {
      getPresenter().navigateToFirmwareUpdate();
   }

   @Override
   protected boolean hasToolbar() {
      return true;
   }

   @Override
   public void showSCNonConnectionDialog() {
      scNonConnectionDialog = new MaterialDialog.Builder(getContext())
            .title(R.string.wallet_card_settings_cant_connected)
            .content(R.string.wallet_card_settings_message_cant_connected)
            .positiveText(R.string.ok)
            .build();
      scNonConnectionDialog.show();
   }

   @OnClick(R.id.btn_sync_cards)
   protected void onSyncPaymentsCardsButtonClick() {
      // TODO: 4/4/17 start sync payment cards
   }

   @OnClick(R.id.tv_remove_payment_cards)
   protected void onRemovePaymentCardsClick() {
      // TODO: 4/4/17 Add logic for remove payment cards, and change ic on FAB
   }
}
