package com.worldventures.dreamtrips.wallet.ui.settings.common.provider;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.settings.common.model.SettingsRadioModel;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.util.Arrays.asList;

public class DisableDefaultCardItemProvider extends BaseItemProvider {

   private final List<SettingsRadioModel> items = asList(
         new SettingsRadioModel(R.string.wallet_settings_disable_default_card_never, 0),
         new SettingsRadioModel(R.string.wallet_settings_disable_default_card_2_hours, TimeUnit.HOURS.toMillis(2)),
         new SettingsRadioModel(R.string.wallet_settings_disable_default_card_4_hours, TimeUnit.HOURS.toMillis(4)),
         new SettingsRadioModel(R.string.wallet_settings_disable_default_card_6_hours, TimeUnit.HOURS.toMillis(6)),
         new SettingsRadioModel(R.string.wallet_settings_disable_default_card_8_hours, TimeUnit.HOURS.toMillis(8)),
         new SettingsRadioModel(R.string.wallet_settings_disable_default_card_12_hours, TimeUnit.HOURS.toMillis(12)),
         new SettingsRadioModel(R.string.wallet_settings_disable_default_card_24_hours, TimeUnit.HOURS.toMillis(24)),
         new SettingsRadioModel(R.string.wallet_settings_disable_default_card_48_hours, TimeUnit.HOURS.toMillis(48)),
         new SettingsRadioModel(R.string.wallet_settings_disable_default_card_1_week, TimeUnit.DAYS.toMillis(7)),
         new SettingsRadioModel(R.string.wallet_settings_disable_default_card_1_month, TimeUnit.DAYS.toMillis(30))
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
