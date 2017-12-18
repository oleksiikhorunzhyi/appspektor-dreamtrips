package com.worldventures.wallet.ui.settings.security.clear.common.items;

import android.content.Context;

import com.worldventures.wallet.R;

import java.util.concurrent.TimeUnit;

import static java.util.Arrays.asList;

public class DisableDefaultCardItemProvider extends BaseItemProvider {

   public DisableDefaultCardItemProvider(Context context) {
      super(asList(
            createRadioModel(context, R.string.wallet_settings_disable_default_card_never, 0),
            createRadioModel(context, R.string.wallet_settings_disable_default_card_2_hours, TimeUnit.HOURS.toMinutes(2)),
            createRadioModel(context, R.string.wallet_settings_disable_default_card_4_hours, TimeUnit.HOURS.toMinutes(4)),
            createRadioModel(context, R.string.wallet_settings_disable_default_card_6_hours, TimeUnit.HOURS.toMinutes(6)),
            createRadioModel(context, R.string.wallet_settings_disable_default_card_8_hours, TimeUnit.HOURS.toMinutes(8)),
            createRadioModel(context, R.string.wallet_settings_disable_default_card_12_hours, TimeUnit.HOURS.toMinutes(12)),
            createRadioModel(context, R.string.wallet_settings_disable_default_card_24_hours, TimeUnit.HOURS.toMinutes(24)),
            createRadioModel(context, R.string.wallet_settings_disable_default_card_48_hours, TimeUnit.HOURS.toMinutes(48)),
            createRadioModel(context, R.string.wallet_settings_disable_default_card_1_week, TimeUnit.DAYS.toMinutes(7)),
            createRadioModel(context, R.string.wallet_settings_disable_default_card_1_month, TimeUnit.DAYS.toMinutes(30))
      ));
   }
}
