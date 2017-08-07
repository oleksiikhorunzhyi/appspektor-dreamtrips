package com.worldventures.dreamtrips.wallet.ui.settings.security.removecards.impl;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.selectable.SingleSelectionManager;
import com.worldventures.dreamtrips.wallet.ui.common.adapter.MultiHolderAdapter;
import com.worldventures.dreamtrips.wallet.ui.common.adapter.SimpleMultiHolderAdapter;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletBaseController;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.delegate.DialogOperationScreen;
import com.worldventures.dreamtrips.wallet.ui.settings.common.model.SectionDividerModel;
import com.worldventures.dreamtrips.wallet.ui.settings.common.model.SettingsRadioModel;
import com.worldventures.dreamtrips.wallet.ui.settings.security.disabledefaultcard.holder.DefaultCardHolderFactoryImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.security.disabledefaultcard.holder.ListItemDisableHolder;
import com.worldventures.dreamtrips.wallet.ui.settings.security.removecards.WalletAutoClearCardsPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.security.removecards.WalletAutoClearCardsScreen;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;

public class WalletAutoClearCardsScreenImpl extends WalletBaseController<WalletAutoClearCardsScreen, WalletAutoClearCardsPresenter> implements WalletAutoClearCardsScreen {

   private static final String KEY_DELAY_CHANGED = "key_delay_changed";

   @InjectView(R.id.toolbar) Toolbar toolbar;
   @InjectView(R.id.recycler_view) RecyclerView recyclerView;

   @Inject WalletAutoClearCardsPresenter presenter;

   private SingleSelectionManager selectionManager;
   private MultiHolderAdapter adapter;
   private boolean autoClearWasChanged;

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
      prepareRecyclerView();
      super.onAttach(view);
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
      adapter = new SimpleMultiHolderAdapter<>(new ArrayList<>(), new DefaultCardHolderFactoryImpl(itemDisableCallback));

      selectionManager = new SingleSelectionManager(recyclerView);
      adapter.addItem(new SectionDividerModel(R.string.wallet_settings_clear_flye_card_description));
      recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
      recyclerView.setAdapter(selectionManager.provideWrappedAdapter(adapter));
   }

   private ListItemDisableHolder.Callback itemDisableCallback = new ListItemDisableHolder.Callback() {

      @Override
      public boolean isLast(int position) {
         return adapter.getItemCount() - 1 == position;
      }

      @Override
      public void onClick(SettingsRadioModel model) {
         getPresenter().onTimeSelected(model.getValue());
      }

      @Override
      public void toggleSelection(int position) {
         selectionManager.toggleSelection(position);
      }

      @Override
      public void setSelection(int position, boolean isSelected) {
         selectionManager.setSelection(position, isSelected);
      }

      @Override
      public boolean isSelected(int position) {
         return selectionManager.isSelected(position);
      }
   };

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

   @Override
   public void setAutoClearWasChanged(boolean autoClearWasChanged) {
      this.autoClearWasChanged = autoClearWasChanged;
   }

   @Override
   protected void onSaveInstanceState(@NonNull Bundle outState) {
      outState.putBoolean(KEY_DELAY_CHANGED, autoClearWasChanged);
      super.onSaveInstanceState(outState);
   }

   @Override
   protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
      this.autoClearWasChanged = savedInstanceState.getBoolean(KEY_DELAY_CHANGED, false);
      super.onRestoreInstanceState(savedInstanceState);
   }

   @Override
   protected void onDetach(@NonNull View view) {
      if (autoClearWasChanged) {
         getPresenter().trackChangedDelay();
      }
      super.onDetach(view);
   }
}
