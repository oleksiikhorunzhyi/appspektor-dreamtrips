package com.worldventures.dreamtrips.wallet.ui.settings.security.clear.common.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletBaseController;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.ErrorViewFactory;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.SmartCardErrorViewProvider;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.success.SimpleToastSuccessView;
import com.worldventures.dreamtrips.wallet.ui.settings.security.clear.common.WalletDelayRadioGroup;
import com.worldventures.dreamtrips.wallet.ui.settings.security.clear.common.items.SettingsRadioModel;

import java.util.ArrayList;
import java.util.List;

import io.techery.janet.operationsubscriber.view.ComposableOperationView;
import io.techery.janet.operationsubscriber.view.OperationView;

public abstract class WalletBaseClearDelayScreenImpl<S extends WalletBaseClearDelayScreen, P extends WalletBaseClearDelayPresenter>
      extends WalletBaseController<S, P> implements WalletBaseClearDelayScreen {

   private static final String KEY_DELAY_CHANGED = "key_delay_changed";

   private WalletDelayRadioGroup selectionView;
   private boolean delayWasChanged;

   @StringRes
   protected abstract int getTitle();

   @StringRes
   protected abstract int getHeader();

   @StringRes
   protected abstract int getSuccessMessage();

   @Override
   protected void onFinishInflate(View view) {
      super.onFinishInflate(view);
      final Toolbar toolbar = view.findViewById(R.id.toolbar);
      toolbar.setTitle(getTitle());
      final TextView header = view.findViewById(R.id.tv_header);
      header.setText(getHeader());
      toolbar.setNavigationOnClickListener(v -> onNavigationClick());
      selectionView = view.findViewById(R.id.delay_selection_view);
      selectionView.setOnChosenListener(radioModel -> getPresenter().onTimeSelected(radioModel.getValue()));
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

   private void onNavigationClick() {
      getPresenter().goBack();
   }

   @Override
   public void setItems(List<SettingsRadioModel> items) {
      selectionView.setItems(new ArrayList<>(items));
   }

   @Override
   public void setSelectedPosition(int position) {
      selectionView.check(position);
   }

   @Override
   public int getSelectedPosition() {
      return selectionView.getCheckedRadioButtonId();
   }

   @Override
   public void setDelayWasChanged(boolean autoClearWasChanged) {
      this.delayWasChanged = autoClearWasChanged;
   }

   @Override
   public <T> OperationView<T> provideOperationView() {
      return new ComposableOperationView<>(new SimpleToastSuccessView<>(getContext(), getSuccessMessage()),
            ErrorViewFactory.<T>builder()
                  .addProvider(new SmartCardErrorViewProvider<>(getContext()))
                  .build());
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
