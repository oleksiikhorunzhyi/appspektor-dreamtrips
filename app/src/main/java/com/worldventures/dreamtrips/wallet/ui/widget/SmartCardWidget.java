package com.worldventures.dreamtrips.wallet.ui.widget;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.QuantityHelper;
import com.worldventures.dreamtrips.modules.common.view.custom.BadgeView;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardStatus;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUser;

import java.io.File;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SmartCardWidget extends FrameLayout {

   @InjectView(R.id.cardListSCAvatar) SimpleDraweeView scAvatar;
   @InjectView(R.id.bankLabel) TextView bankLabel;
   @InjectView(R.id.connectedCardsCount) TextView connectedCardsCount;
   @InjectView(R.id.batteryView) BatteryView batteryView;
   @InjectView(R.id.batteryLevel) TextView batteryLevel;
   @InjectView(R.id.settings_button) View settingsButton;

   @InjectView(R.id.stealth_indicator) View stealthIndicator;
   @InjectView(R.id.lock_indicator) ImageView lockIndicator;
   @InjectView(R.id.link_indicator) ImageView lickIndicator;
   @InjectView(R.id.smartcard_badge) BadgeView badgeView;

   public SmartCardWidget(Context context) {
      this(context, null);
   }

   public SmartCardWidget(Context context, AttributeSet attrs) {
      super(context, attrs);
      setup();
   }

   private void setup() {
      LayoutInflater.from(getContext()).inflate(R.layout.custom_view_wallet_smartcard, this);
      ButterKnife.inject(this);
      setVisibility(INVISIBLE);
   }

   public void bindCard(@NonNull SmartCard smartCard, @NonNull SmartCardStatus smartCardStatus, @Nullable SmartCardUser user, boolean isFirmwareAvailable) {
      if (user != null) {
         File photoFile = user.userPhoto().monochrome();
         if (photoFile != null) scAvatar.setImageURI(Uri.fromFile(photoFile));
         bankLabel.setText(user.fullName());
      }

      batteryView.setLevel(smartCardStatus.batteryLevel());
      batteryLevel.setText(String.format(Locale.US, "%d%%", smartCardStatus.batteryLevel()));
      stealthIndicator.setVisibility(smartCardStatus.stealthMode() ? VISIBLE : GONE);
      bindLockStatus(smartCardStatus.lock());
      bindConnectionStatus(smartCardStatus.connectionStatus().isConnected());
      if (isFirmwareAvailable) {
         badgeView.setText("1"); // maybe we should show count of available firmware versions. Need contract with the server
         badgeView.show();
      } else {
         badgeView.hide();
      }
      setVisibility(VISIBLE);
   }

   private void bindLockStatus(boolean lock) {
      lockIndicator.setImageResource(lock ? R.drawable.ic_wallet_lock_indicator : R.drawable.ic_wallet_unlock_indicator);
   }

   private void bindConnectionStatus(boolean connected) {
      lickIndicator.setImageResource(connected ? R.drawable.ic_wallet_link_indicator : R.drawable.ic_wallet_unlink_indicator);
   }

   public void bindCount(int cardCount) {
      if (cardCount > 0) {
         int resId = QuantityHelper.chooseResource(cardCount, R.string.wallet_card_list_record_connected, R.string.wallet_card_list_records_connected);
         connectedCardsCount.setText(getResources().getString(resId, cardCount));
         connectedCardsCount.setVisibility(VISIBLE);
      } else {
         connectedCardsCount.setVisibility(INVISIBLE);
      }
   }

   public void setOnSettingsClickListener(View.OnClickListener listener) {
      settingsButton.setOnClickListener(listener);
   }
}
