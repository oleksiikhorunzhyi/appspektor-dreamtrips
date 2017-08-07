package com.worldventures.dreamtrips.wallet.ui.settings.security.disabledefaultcard.holder;

import com.worldventures.dreamtrips.databinding.WalletSettingsListHeaderBinding;
import com.worldventures.dreamtrips.wallet.ui.common.adapter.BaseHolder;
import com.worldventures.dreamtrips.wallet.ui.settings.common.model.SectionDividerModel;

public class WalletSettingListHeaderHolder extends BaseHolder<SectionDividerModel> {

   private final WalletSettingsListHeaderBinding binding;

   public WalletSettingListHeaderHolder(WalletSettingsListHeaderBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
   }

   @Override
   public void setData(SectionDividerModel data) {
      binding.title.setText(data.getTitleId());
   }
}
