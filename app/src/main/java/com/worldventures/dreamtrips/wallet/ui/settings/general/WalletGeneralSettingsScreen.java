package com.worldventures.dreamtrips.wallet.ui.settings.general;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;

import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUserPhoto;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.settings.general.reset.FactoryResetView;

import java.util.List;

public interface WalletGeneralSettingsScreen extends WalletScreen, FactoryResetView {

   void setPreviewPhoto(@Nullable SmartCardUserPhoto photo);

   void setUserName(String firstName, String middleName, String lastName);

   void firmwareUpdateCount(int count);

   void showFirmwareVersion();

   void showFirmwareBadge();

   void showSCNonConnectionDialog();

   void showConfirmFactoryResetDialog();

   void showConfirmRestartSCDialog();

   Context getViewContext();

   List<View> getToggleableItems();

}
