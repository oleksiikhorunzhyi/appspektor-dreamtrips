package com.worldventures.dreamtrips.wallet.ui.settings.common.provider;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.settings.common.model.SettingsRadioModel;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.util.Arrays.asList;

public class AutoClearSmartCardItemProvider extends BaseItemProvider {

   private final List<SettingsRadioModel> items = asList(
         new SettingsRadioModel(R.string.wallet_settings_clear_flye_card_never, 0),
         new SettingsRadioModel(R.string.wallet_settings_clear_flye_card_2_hours, TimeUnit.HOURS.toMinutes(2)),
         new SettingsRadioModel(R.string.wallet_settings_clear_flye_card_4_hours, TimeUnit.HOURS.toMinutes(4)),
         new SettingsRadioModel(R.string.wallet_settings_clear_flye_card_6_hours, TimeUnit.HOURS.toMinutes(6)),
         new SettingsRadioModel(R.string.wallet_settings_clear_flye_card_8_hours, TimeUnit.HOURS.toMinutes(8)),
         new SettingsRadioModel(R.string.wallet_settings_clear_flye_card_12_hours, TimeUnit.HOURS.toMinutes(12)),
         new SettingsRadioModel(R.string.wallet_settings_clear_flye_card_24_hours, TimeUnit.HOURS.toMinutes(24)),
         new SettingsRadioModel(R.string.wallet_settings_clear_flye_card_48_hours, TimeUnit.HOURS.toMinutes(48)),
         new SettingsRadioModel(R.string.wallet_settings_clear_flye_card_1_week, TimeUnit.DAYS.toMinutes(7)),
         new SettingsRadioModel(R.string.wallet_settings_clear_flye_card_1_month, TimeUnit.DAYS.toMinutes(30))
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
