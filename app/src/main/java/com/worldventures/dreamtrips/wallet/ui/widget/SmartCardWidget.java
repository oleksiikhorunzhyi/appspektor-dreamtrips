package com.worldventures.dreamtrips.wallet.ui.widget;

import android.content.Context;
import android.text.TextUtils;
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
import com.worldventures.dreamtrips.modules.tripsimages.vision.ImageUtils;
import com.worldventures.dreamtrips.wallet.ui.dashboard.util.viewholder.CardStackHeaderHolder;

import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SmartCardWidget extends FrameLayout {

   @InjectView(R.id.photo_container) View photoContainer;
   @InjectView(R.id.cardListSCAvatar) SimpleDraweeView scAvatar;
   @InjectView(R.id.place_holder) TextView tvPlaceHolder;
   @InjectView(R.id.bankLabel) TextView bankLabel;
   @InjectView(R.id.connectedCardsCount) TextView connectedCardsCount;
   @InjectView(R.id.batteryView) BatteryView batteryView;
   @InjectView(R.id.batteryLevel) TextView batteryLevel;
   @InjectView(R.id.settings_button) View settingsButton;

   @InjectView(R.id.stealth_indicator) View stealthIndicator;
   @InjectView(R.id.lock_indicator) ImageView lockIndicator;
   @InjectView(R.id.link_indicator) ImageView linkIndicator;
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
      if (isInEditMode()) return;
      ButterKnife.inject(this);
      ImageUtils.applyGrayScaleColorFilter(scAvatar);
      setVisibility(INVISIBLE);
   }

   public void bindCard(CardStackHeaderHolder holder) {
      final StringBuilder fullNameBuilder = new StringBuilder(holder.firstName());
      if (!holder.middleName().isEmpty()) {
         fullNameBuilder.append("\n").append(holder.middleName());
      }
      if (!holder.lastName().isEmpty()) {
         fullNameBuilder.append("\n").append(holder.lastName());
      }
      tvPlaceHolder.setText(fullNameBuilder);

      if (!TextUtils.isEmpty(holder.photoUrl())) {
         scAvatar.setImageURI(holder.photoUrl());
      }
      bankLabel.setText(fullNameBuilder.toString().replace("\n", " "));
      batteryView.setLevel(holder.batteryLevel());
      batteryLevel.setText(String.format(Locale.US, "%d%%", holder.batteryLevel()));
      stealthIndicator.setVisibility(holder.stealthMode() ? VISIBLE : GONE);
      bindLockStatus(holder.lock());
      bindConnectionStatus(holder.connected());
      if (holder.firmwareUpdateAvailable()) {
         badgeView.setText("1"); // maybe we should show count of available firmware versions. Need contract with the server
         badgeView.show();
      } else {
         badgeView.hide();
      }
      bindCount(holder.cardCount());
      setVisibility(VISIBLE);
   }

   private void bindLockStatus(boolean lock) {
      lockIndicator.setImageResource(lock ? R.drawable.ic_wallet_lock_indicator : R.drawable.ic_wallet_unlock_indicator);
   }

   private void bindConnectionStatus(boolean connected) {
      linkIndicator.setImageResource(connected ? R.drawable.ic_wallet_link_indicator : R.drawable.ic_wallet_unlink_indicator);
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

   public void setOnPhotoClickListener(View.OnClickListener listener) {
      photoContainer.setOnClickListener(listener);
   }
}
