package com.worldventures.dreamtrips.wallet.ui.settings.security.disabledefaultcard.holder;

import com.worldventures.dreamtrips.wallet.ui.common.adapter.HolderTypeFactory;
import com.worldventures.dreamtrips.wallet.ui.settings.common.model.SectionDividerModel;
import com.worldventures.dreamtrips.wallet.ui.settings.common.model.SettingsRadioModel;

public interface DefaultCardHolderTypeFactory extends HolderTypeFactory {

   int type(SectionDividerModel sectionDividerModel);

   int type(SettingsRadioModel settingsRadioModel);
}
