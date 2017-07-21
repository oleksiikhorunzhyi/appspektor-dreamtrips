package com.worldventures.dreamtrips.wallet.ui.settings.security.removecards.impl;


import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.techery.spares.adapter.BaseDelegateAdapter;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.selectable.SingleSelectionManager;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletBaseController;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.delegate.DialogOperationScreen;
import com.worldventures.dreamtrips.wallet.ui.settings.common.cell.SectionDividerCell;
import com.worldventures.dreamtrips.wallet.ui.settings.common.cell.SettingsRadioCell;
import com.worldventures.dreamtrips.wallet.ui.settings.common.model.SectionDividerModel;
import com.worldventures.dreamtrips.wallet.ui.settings.common.model.SettingsRadioModel;
import com.worldventures.dreamtrips.wallet.ui.settings.security.removecards.WalletAutoClearCardsPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.security.removecards.WalletAutoClearCardsScreen;

import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;

public class WalletAutoClearCardsScreenImpl extends WalletBaseController<WalletAutoClearCardsScreen, WalletAutoClearCardsPresenter> implements WalletAutoClearCardsScreen {

   @InjectView(R.id.toolbar) Toolbar toolbar;
   @InjectView(R.id.recycler_view) RecyclerView recyclerView;

   @Inject WalletAutoClearCardsPresenter presenter;

   private SingleSelectionManager selectionManager;
   private BaseDelegateAdapter adapter;

   @Override
   protected void onFinishInflate(View view) {
      super.onFinishInflate(view);
      toolbar.setNavigationOnClickListener(v -> onNavigationClick());
   }

   @Override
   public View inflateView(LayoutInflater layoutInflater, ViewGroup viewGroup) {
      return layoutInflater.inflate(R.layout.screen_wallet_settings_clear_cards, viewGroup, false);
   }

   @Override
   public boolean supportConnectionStatusLabel() {
      return false;
   }

   @Override
   public boolean supportHttpConnectionStatusLabel() {
      return false;
   }

   @Override
   protected void onAttach(@NonNull View view) {
      super.onAttach(view);
      prepareRecyclerView();
   }

   @Override
   public WalletAutoClearCardsPresenter getPresenter() {
      return presenter;
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      return new DialogOperationScreen(getView());
   }

   private void onNavigationClick() {
      getPresenter().goBack();
   }

   private void prepareRecyclerView() {
      //TODO : get rid from this adapter and Injector
      adapter = new BaseDelegateAdapter(getContext(), (Injector) getContext());
      adapter.registerCell(SectionDividerModel.class, SectionDividerCell.class);
      adapter.registerCell(SettingsRadioModel.class, SettingsRadioCell.class, new SettingsRadioCell.Delegate() {
         @Override
         public boolean isLast(int position) {
            return adapter.getCount() - 1 == position;
         }

         @Override
         public void onCellClicked(SettingsRadioModel model) {
            getPresenter().onTimeSelected(model.getValue());
         }
      });
      adapter.addItem(new SectionDividerModel(R.string.wallet_settings_clear_flye_card_description));
      selectionManager = new SingleSelectionManager(recyclerView);
      recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
      recyclerView.setAdapter(selectionManager.provideWrappedAdapter(adapter));
   }


   @Override
   public void setItems(List<SettingsRadioModel> items) {
      adapter.addItems(items);
   }

   @Override
   public void setSelectedPosition(int position) {
      selectionManager.setSelection(position + 1, true);
   }

   @Override
   public int getSelectedPosition() {
      int position = selectionManager.getSelectedPosition() - 1; // first item is header
      return position < 0 ? 0 : position;
   }

   @Override
   public String getTextBySelectedModel(SettingsRadioModel settingsRadioModel) {
      return getString(settingsRadioModel.getTextResId());
   }
}
