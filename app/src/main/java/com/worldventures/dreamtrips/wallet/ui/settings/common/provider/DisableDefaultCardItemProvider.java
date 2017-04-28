package com.worldventures.dreamtrips.wallet.ui.settings.common.provider;

import android.content.Context;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.settings.common.model.SettingsRadioModel;

import java.util.concurrent.TimeUnit;

import static java.util.Arrays.asList;

public class DisableDefaultCardItemProvider extends BaseItemProvider {

   public DisableDefaultCardItemProvider(Context context) {
      super(asList(
            new SettingsRadioModel(context.getString(R.string.wallet_settings_disable_default_card_never), 0),
            new SettingsRadioModel(context.getString(R.string.wallet_settings_disable_default_card_2_hours), TimeUnit.HOURS.toMinutes(2)),
            new SettingsRadioModel(context.getString(R.string.wallet_settings_disable_default_card_4_hours), TimeUnit.HOURS.toMinutes(4)),
            new SettingsRadioModel(context.getString(R.string.wallet_settings_disable_default_card_6_hours), TimeUnit.HOURS.toMinutes(6)),
            new SettingsRadioModel(context.getString(R.string.wallet_settings_disable_default_card_8_hours), TimeUnit.HOURS.toMinutes(8)),
            new SettingsRadioModel(context.getString(R.string.wallet_settings_disable_default_card_12_hours), TimeUnit.HOURS.toMinutes(12)),
            new SettingsRadioModel(context.getString(R.string.wallet_settings_disable_default_card_24_hours), TimeUnit.HOURS.toMinutes(24)),
            new SettingsRadioModel(context.getString(R.string.wallet_settings_disable_default_card_48_hours), TimeUnit.HOURS.toMinutes(48)),
            new SettingsRadioModel(context.getString(R.string.wallet_settings_disable_default_card_1_week), TimeUnit.DAYS.toMinutes(7)),
            new SettingsRadioModel(context.getString(R.string.wallet_settings_disable_default_card_1_month), TimeUnit.DAYS.toMinutes(30))
      ));
   }
}
