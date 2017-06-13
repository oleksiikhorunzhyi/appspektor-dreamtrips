package com.worldventures.dreamtrips.wallet.ui.settings.common.provider;

import android.content.Context;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.settings.common.model.SettingsRadioModel;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.util.Arrays.asList;

public class AutoClearSmartCardItemProvider extends BaseItemProvider {

   public AutoClearSmartCardItemProvider(Context context) {
      super(asList(
            new SettingsRadioModel(context.getString(R.string.wallet_settings_clear_flye_card_1_day), TimeUnit.DAYS.toMinutes(1)),
            new SettingsRadioModel(context.getString(R.string.wallet_settings_clear_flye_card_2_days), TimeUnit.DAYS.toMinutes(2)),
            new SettingsRadioModel(context.getString(R.string.wallet_settings_clear_flye_card_3_days), TimeUnit.DAYS.toMinutes(3)),
            new SettingsRadioModel(context.getString(R.string.wallet_settings_clear_flye_card_4_days), TimeUnit.DAYS.toMinutes(4)),
            new SettingsRadioModel(context.getString(R.string.wallet_settings_clear_flye_card_5_days), TimeUnit.DAYS.toMinutes(5)),
            new SettingsRadioModel(context.getString(R.string.wallet_settings_clear_flye_card_never), 0)
      ));
   }
}
