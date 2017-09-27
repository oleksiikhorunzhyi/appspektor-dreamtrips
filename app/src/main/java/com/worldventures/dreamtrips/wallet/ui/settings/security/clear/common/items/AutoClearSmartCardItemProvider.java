package com.worldventures.dreamtrips.wallet.ui.settings.security.clear.common.items;

import android.content.Context;

import com.worldventures.dreamtrips.R;

import java.util.concurrent.TimeUnit;

import static java.util.Arrays.asList;

public class AutoClearSmartCardItemProvider extends BaseItemProvider {

   public AutoClearSmartCardItemProvider(Context context) {
      super(asList(
            createRadioModel(context, R.string.wallet_settings_clear_flye_card_1_day, TimeUnit.DAYS.toMinutes(1)),
            createRadioModel(context, R.string.wallet_settings_clear_flye_card_2_days, TimeUnit.DAYS.toMinutes(2)),
            createRadioModel(context, R.string.wallet_settings_clear_flye_card_3_days, TimeUnit.DAYS.toMinutes(3)),
            createRadioModel(context, R.string.wallet_settings_clear_flye_card_4_days, TimeUnit.DAYS.toMinutes(4)),
            createRadioModel(context, R.string.wallet_settings_clear_flye_card_5_days, TimeUnit.DAYS.toMinutes(5)),
            createRadioModel(context, R.string.wallet_settings_clear_flye_card_never, 0)
      ));
   }
}
