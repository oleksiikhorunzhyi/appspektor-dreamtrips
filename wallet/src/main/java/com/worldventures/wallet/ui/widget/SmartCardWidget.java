package com.worldventures.wallet.ui.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.worldventures.core.utils.ProjectTextUtils;
import com.worldventures.core.utils.QuantityHelper;
import com.worldventures.wallet.R;
import com.worldventures.wallet.domain.WalletConstants;
import com.worldventures.wallet.ui.dashboard.util.viewholder.CardStackHeaderHolder;
import com.worldventures.wallet.util.SmartCardAvatarHelper;

import java.util.Locale;

import static io.techery.janet.smartcard.action.settings.SetHomeDisplayTypeAction.DISPLAY_NAME_ONLY;
import static io.techery.janet.smartcard.action.settings.SetHomeDisplayTypeAction.DISPLAY_PHONE_AND_NAME;
import static io.techery.janet.smartcard.action.settings.SetHomeDisplayTypeAction.DISPLAY_PICTURE_AND_NAME;
import static io.techery.janet.smartcard.action.settings.SetHomeDisplayTypeAction.DISPLAY_PICTURE_ONLY;

public class SmartCardWidget extends ConstraintLayout {

   private static final float WIDGET_SIZE_RATIO = 195 / 320f;

   private View photoContainer;
   private SimpleDraweeView scAvatar;
   private TextView tvPhotoFullName;
   private TextView tvPhotoFirstName;
   private TextView tvFullName;
   private TextView tvCardsLoaded;
   private BatteryView batteryView;
   private TextView tvBatteryLevel;
   private View stealthIndicator;
   private ImageView lockIndicator;
   private ImageView linkIndicator;

   public SmartCardWidget(Context context) {
      this(context, null);
   }

   public SmartCardWidget(Context context, AttributeSet attrs) {
      super(context, attrs);
      setup();
   }

   private void setup() {
      final View view = LayoutInflater.from(getContext()).inflate(R.layout.wallet_custom_view_smartcard, this);
      if (isInEditMode()) {
         return;
      }
      photoContainer = view.findViewById(R.id.photo_container);
      scAvatar = view.findViewById(R.id.cardListSCAvatar);
      SmartCardAvatarHelper.applyGrayScaleColorFilter(scAvatar);
      tvPhotoFullName = view.findViewById(R.id.tv_photo_full_name);
      tvPhotoFirstName = view.findViewById(R.id.tv_photo_first_name);
      tvFullName = view.findViewById(R.id.tv_full_name);
      tvCardsLoaded = view.findViewById(R.id.tv_cards_loaded);
      batteryView = view.findViewById(R.id.battery_indicator);
      tvBatteryLevel = view.findViewById(R.id.battery_indicator_text);
      stealthIndicator = view.findViewById(R.id.stealth_indicator);
      lockIndicator = view.findViewById(R.id.lock_indicator);
      linkIndicator = view.findViewById(R.id.link_indicator);
      setVisibility(INVISIBLE);
   }

   @Override
   protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
      int originalWidth = MeasureSpec.getSize(widthMeasureSpec);
      int calculatedHeight = (int) (originalWidth * WIDGET_SIZE_RATIO);

      super.onMeasure(
            MeasureSpec.makeMeasureSpec(originalWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(calculatedHeight, MeasureSpec.EXACTLY));
   }

   @SuppressLint("SetTextI18n")
   public void bindCard(CardStackHeaderHolder holder) {
      final int type = getNormalizedType(holder);

      final StringBuilder fullNameBuilder = new StringBuilder(holder.getFirstName());
      if (!holder.getMiddleName().isEmpty()) {
         fullNameBuilder.append('\n').append(holder.getMiddleName());
      }
      if (!holder.getLastName().isEmpty()) {
         fullNameBuilder.append('\n').append(holder.getLastName());
      }

      final String photoFullName = fullNameBuilder.toString();
      final String fullName = photoFullName.replace("\n", " ");

      tvFullName.setText(fullName);

      switch (type) {
         case DISPLAY_PICTURE_ONLY:
            scAvatar.setImageURI(holder.getPhotoUrl());
            break;
         case DISPLAY_PICTURE_AND_NAME:
            scAvatar.setImageURI(holder.getPhotoUrl());
            tvPhotoFirstName.setText(holder.getFirstName());
            break;
         case DISPLAY_NAME_ONLY:
            tvPhotoFullName.setText(photoFullName);
            break;
         case DISPLAY_PHONE_AND_NAME:
            final String phoneNumber = ProjectTextUtils.isEmpty(holder.getPhoneNumber())
                  ? String.format(Locale.US, "(%s)", getResources().getString(R.string.wallet_settings_general_display_phone_required))
                  : holder.getPhoneNumber();
            tvPhotoFullName.setText(photoFullName + "\n\n" + phoneNumber);
            break;
         default:
            break;
      }

      tvPhotoFirstName.setVisibility(type == DISPLAY_PICTURE_AND_NAME ? View.VISIBLE : View.GONE);
      tvPhotoFullName.setVisibility((type == DISPLAY_NAME_ONLY || type == DISPLAY_PHONE_AND_NAME) ? View.VISIBLE : View.GONE);
      scAvatar.setVisibility((type == DISPLAY_PICTURE_AND_NAME || type == DISPLAY_PICTURE_ONLY) ? View.VISIBLE : View.GONE);

      bindCardLoadedCount(holder.getCardCount());

      bindConnectionIndicator(holder.getConnected());
      bindStealthModeIndicator(holder.getStealthMode());
      bindLockIndicator(holder.getLock());
      bindBatteryIndicator(holder.getBatteryLevel());

      setVisibility(VISIBLE);
   }

   /**
    * Fallback to default type if there is not enough user information or display type is invalid.
    */
   private int getNormalizedType(CardStackHeaderHolder holder) {
      final int displayType = holder.getDisplayType();
      switch (displayType) {
         case DISPLAY_PICTURE_ONLY:
         case DISPLAY_PICTURE_AND_NAME:
            if (ProjectTextUtils.isEmpty(holder.getPhotoUrl())) {
               break;
            } else {
               return displayType;
            }
         case DISPLAY_PHONE_AND_NAME:
            if (ProjectTextUtils.isEmpty(holder.getPhoneNumber())) {
               break;
            } else {
               return displayType;
            }
         case DISPLAY_NAME_ONLY:
            return displayType;
         default:
            break;
      }
      return WalletConstants.SMART_CARD_DEFAULT_DISPLAY_TYPE;
   }

   private void bindConnectionIndicator(boolean connected) {
      linkIndicator.setImageResource(connected ? R.drawable.ic_wallet_vector_link_indicator : R.drawable.ic_wallet_vector_unlink_indicator);
   }

   private void bindStealthModeIndicator(boolean stealthModeEnabled) {
      stealthIndicator.setVisibility(stealthModeEnabled ? VISIBLE : GONE);

   }

   private void bindLockIndicator(boolean lock) {
      lockIndicator.setImageResource(lock ? R.drawable.ic_wallet_vector_lock_indicator : R.drawable.ic_wallet_vector_unlock_indicator);
   }

   private void bindBatteryIndicator(int batteryLevel) {
      batteryView.setLevel(batteryLevel);
      tvBatteryLevel.setText(String.format(Locale.US, "%d%%", batteryLevel));
   }

   private void bindCardLoadedCount(int cardCount) {
      if (cardCount > 0) {
         int resId = QuantityHelper.chooseResource(cardCount, R.string.wallet_card_list_record_connected, R.string.wallet_card_list_records_connected);
         tvCardsLoaded.setText(getResources().getString(resId, cardCount));
         tvCardsLoaded.setVisibility(VISIBLE);
      } else {
         tvCardsLoaded.setVisibility(INVISIBLE);
      }
   }

   public void setOnPhotoClickListener(View.OnClickListener listener) {
      photoContainer.setOnClickListener(listener);
   }
}
