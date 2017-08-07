package com.worldventures.dreamtrips.wallet.ui.settings.security.disabledefaultcard.holder;

import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.databinding.ListItemDisableDefaultCardBinding;
import com.worldventures.dreamtrips.databinding.WalletSettingsListHeaderBinding;
import com.worldventures.dreamtrips.wallet.ui.common.adapter.BaseHolder;
import com.worldventures.dreamtrips.wallet.ui.settings.common.model.SectionDividerModel;
import com.worldventures.dreamtrips.wallet.ui.settings.common.model.SettingsRadioModel;

public class DefaultCardHolderFactoryImpl implements DefaultCardHolderTypeFactory {

   private final ListItemDisableHolder.Callback itemDefaultCallback;

   public DefaultCardHolderFactoryImpl(ListItemDisableHolder.Callback itemDefaultCallback) {
      this.itemDefaultCallback = itemDefaultCallback;
   }

   @Override
   public BaseHolder holder(ViewGroup parent, int viewType) {
      switch (viewType) {
         case R.layout.wallet_settings_list_header:
            WalletSettingsListHeaderBinding headerBinding = DataBindingUtil
                  .bind(LayoutInflater
                        .from(parent.getContext()).inflate(viewType, parent, false));
            return new WalletSettingListHeaderHolder(headerBinding);
         case R.layout.list_item_disable_default_card:
            ListItemDisableDefaultCardBinding itemDefaultBinding = DataBindingUtil
                  .bind(LayoutInflater
                        .from(parent.getContext()).inflate(viewType, parent, false));
            return new ListItemDisableHolder(itemDefaultBinding, itemDefaultCallback);
         default:
            throw new IllegalArgumentException();
      }
   }

   @Override
   public int type(SectionDividerModel sectionDividerModel) {
      return R.layout.wallet_settings_list_header;
   }

   @Override
   public int type(SettingsRadioModel settingsRadioModel) {
      return R.layout.list_item_disable_default_card;
   }
}
