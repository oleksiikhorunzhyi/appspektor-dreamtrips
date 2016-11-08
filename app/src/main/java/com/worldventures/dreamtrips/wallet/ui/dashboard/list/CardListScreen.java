package com.worldventures.dreamtrips.wallet.ui.dashboard.list;

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

import com.afollestad.materialdialogs.MaterialDialog;
import com.eowise.recyclerview.stickyheaders.StickyHeadersBuilder;
import com.eowise.recyclerview.stickyheaders.StickyHeadersItemDecoration;
import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.bucketlist.view.adapter.IgnoreFirstItemAdapter;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.delegate.DialogOperationScreen;
import com.worldventures.dreamtrips.wallet.ui.dashboard.list.util.CardListHeaderAdapter;
import com.worldventures.dreamtrips.wallet.ui.dashboard.list.util.CardStackHeaderHolder;
import com.worldventures.dreamtrips.wallet.ui.dashboard.list.util.CardStackViewModel;
import com.worldventures.dreamtrips.wallet.ui.dashboard.list.util.ImmutableCardStackHeaderHolder;
import com.worldventures.dreamtrips.wallet.ui.dashboard.list.util.cell.CardStackCell;
import com.worldventures.dreamtrips.wallet.ui.dashboard.list.util.cell.CardStackHeaderCell;
import com.worldventures.dreamtrips.wallet.ui.dialog.InstallFirmwareErrorDialog;

import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;

public class CardListScreen extends WalletLinearLayout<CardListPresenter.Screen, CardListPresenter, CardListPath> implements CardListPresenter.Screen {

   private static final String KEY_SHOW_UPDATE_BUTTON_STATE = "CardListScreen#KEY_SHOW_UPDATE_BUTTON_STATE";

   @InjectView(R.id.bank_card_list) RecyclerView bankCardList;
   @InjectView(R.id.add_card_button) FloatingActionButton addCardButton;
   @InjectView(R.id.firmware_available) View firmwareAvailableView;
   @InjectView(R.id.toolbar) Toolbar toolbar;

   private IgnoreFirstItemAdapter adapter;
   private InstallFirmwareErrorDialog installFirmwareErrorDialog;

   public CardListScreen(Context context) {
      super(context);
   }

   public CardListScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @NonNull
   @Override
   public CardListPresenter createPresenter() {
      return new CardListPresenter(getContext(), getInjector());
   }

   @Override
   protected void onPostAttachToWindowView() {
      toolbar.setNavigationOnClickListener(it -> presenter.navigationClick());

      setupCardStackList();
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      return new DialogOperationScreen(this);
   }

   @Override
   public void showRecordsInfo(List<CardStackViewModel> result) {
      adapter.clear();
      adapter.addItems(result);
   }

   @Override
   public void notifySmartCardChanged(CardStackHeaderHolder cardStackHeaderHolder) {
      Object header = Queryable.from(adapter.getItems()).firstOrDefault(it -> it instanceof CardStackHeaderHolder);
      if (header != null) {
         adapter.remove(header);
      }
      adapter.addItem(0, cardStackHeaderHolder);
      adapter.notifyDataSetChanged();
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

      builder.positiveText(R.string.ok)
            .negativeText(R.string.cancel)
            .build()
            .show();
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
               .setOnRetryction(() -> presenter.navigateToInstallFirmware())
               .setOnCancelAction(() -> presenter.navigateBack());
      }
      if (!installFirmwareErrorDialog.isShowing()) {
         installFirmwareErrorDialog.show();
      }
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
         public void onCardClicked(BankCard bankCard) {
            getPresenter().cardClicked(bankCard);
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
      bankCardList.setItemAnimator(new DefaultItemAnimator());
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
      new MaterialDialog.Builder(getContext())
            .title(R.string.wallet_wizard_card_list_add_card_coming_soon_title)
            .content(R.string.wallet_wizard_card_list_add_card_coming_soon_text)
            .positiveText(R.string.ok)
            .show();
   }

   @OnClick(R.id.firmware_available)
   protected void firmwareAvailableBtnClick() {
      getPresenter().firmwareAvailable();
   }

   @Override
   protected boolean hasToolbar() {
      return true;
   }
}
