package com.worldventures.dreamtrips.wallet.ui.settings.common.provider;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.settings.common.model.SettingsRadioModel;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.util.Arrays.asList;

public class AutoClearSmartCardItemProvider extends BaseItemProvider {

   private final List<SettingsRadioModel> items = asList(
         new SettingsRadioModel(R.string.wallet_settings_clear_flye_card_1_day, TimeUnit.DAYS.toMinutes(1)),
         new SettingsRadioModel(R.string.wallet_settings_clear_flye_card_2_days, TimeUnit.DAYS.toMinutes(2)),
         new SettingsRadioModel(R.string.wallet_settings_clear_flye_card_3_days, TimeUnit.DAYS.toMinutes(3)),
         new SettingsRadioModel(R.string.wallet_settings_clear_flye_card_4_days, TimeUnit.DAYS.toMinutes(4)),
         new SettingsRadioModel(R.string.wallet_settings_clear_flye_card_5_days, TimeUnit.DAYS.toMinutes(5)),
         new SettingsRadioModel(R.string.wallet_settings_clear_flye_card_never, 0)
   );

   @Override
   public List<SettingsRadioModel> items() {
      return items;
   }

   @Override
   public int getDefaultPosition() {
      return 0;
   }

   @Override
   public int getDefaultTextId() {
      return items.get(getDefaultPosition()).getStringId();
   }
}
