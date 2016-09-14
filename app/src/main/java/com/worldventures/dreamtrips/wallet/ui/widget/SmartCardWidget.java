package com.worldventures.dreamtrips.wallet.ui.widget;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.QuantityHelper;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SmartCardWidget extends FrameLayout {

   @InjectView(R.id.cardListSCAvatar) SimpleDraweeView scAvatar;
   @InjectView(R.id.bankLabel) TextView bankLabel;
   @InjectView(R.id.connectedCardsCount) TextView connectedCardsCount;
   @InjectView(R.id.batteryView) BatteryView batteryView;
   @InjectView(R.id.settings_button) View settingsButton;

   @InjectView(R.id.stealth_indicator) View stealthIndicator;
   @InjectView(R.id.lock_indicator) ImageView lockIndicator;
   @InjectView(R.id.link_indicator) ImageView lickIndicator;

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

   public void bindCard(SmartCard smartCard) {
      String url = smartCard.userPhoto();
      bankLabel.setText(smartCard.cardName());
      if (url != null) scAvatar.setImageURI(Uri.parse(url));
      batteryView.setLevel(smartCard.batteryLevel());
      stealthIndicator.setVisibility(smartCard.stealthMode() ? VISIBLE : GONE);
      bindLockStatus(smartCard.lock());
      bindConnectionStatus(smartCard.connectionStatus() == SmartCard.ConnectionStatus.CONNECTED);
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
