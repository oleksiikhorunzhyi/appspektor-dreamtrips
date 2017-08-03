package com.worldventures.dreamtrips.wallet.ui.settings.security.disabledefaultcard.impl;


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
import com.worldventures.dreamtrips.wallet.ui.settings.security.disabledefaultcard.WalletDisableDefaultCardPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.security.disabledefaultcard.WalletDisableDefaultCardScreen;
import com.worldventures.dreamtrips.wallet.ui.settings.security.disabledefaultcard.holder.DefaultCardHolderFactoryImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.security.disabledefaultcard.holder.ListItemDisableHolder;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;

public class WalletDisableDefaultCardScreenImpl extends WalletBaseController<WalletDisableDefaultCardScreen, WalletDisableDefaultCardPresenter> implements WalletDisableDefaultCardScreen {

   private static final String KEY_DELAY_CHANGED = "key_delay_changed";

   @InjectView(R.id.toolbar) Toolbar toolbar;
   @InjectView(R.id.disable_variant_list) RecyclerView recyclerView;

   @Inject WalletDisableDefaultCardPresenter presenter;

   private SingleSelectionManager selectionManager;
   private MultiHolderAdapter adapter;
   private boolean delayWasChanged;

   @Override
   protected void onFinishInflate(View view) {
      super.onFinishInflate(view);
      toolbar.setNavigationOnClickListener(v -> onNavigationClick());
   }

   @Override
   public View inflateView(LayoutInflater layoutInflater, ViewGroup viewGroup) {
      return layoutInflater.inflate(R.layout.screen_wallet_settings_disable_default_card, viewGroup, false);
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
   public WalletDisableDefaultCardPresenter getPresenter() {
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

      adapter.addItem(new SectionDividerModel(R.string.wallet_settings_disable_default_card_description));
      selectionManager = new SingleSelectionManager(recyclerView);
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
   public String getTextBySelectedModel(SettingsRadioModel selectedDelay) {
      return getString(selectedDelay.getTextResId());
   }

   @Override
   public void setDelayChanged(boolean delayWasChanged) {
      this.delayWasChanged = delayWasChanged;
   }

   @Override
   protected void onSaveInstanceState(@NonNull Bundle outState) {
      outState.putBoolean(KEY_DELAY_CHANGED, delayWasChanged);
      super.onSaveInstanceState(outState);
   }

   @Override
   protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
      this.delayWasChanged = savedInstanceState.getBoolean(KEY_DELAY_CHANGED, false);
      super.onRestoreInstanceState(savedInstanceState);
   }

   @Override
   protected void onDetach(@NonNull View view) {
      if (delayWasChanged) {
         getPresenter().trackChangedDelay();
      }
      super.onDetach(view);
   }
}
