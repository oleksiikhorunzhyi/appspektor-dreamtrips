package com.worldventures.wallet.ui.settings.general.display;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.worldventures.wallet.R;
import com.worldventures.wallet.domain.entity.SmartCardUser;
import com.worldventures.wallet.domain.entity.SmartCardUserPhone;
import com.worldventures.wallet.domain.entity.SmartCardUserPhoto;
import com.worldventures.wallet.util.SmartCardAvatarHelper;

import java.util.Locale;

import io.techery.janet.smartcard.action.settings.SetHomeDisplayTypeAction;

import static com.worldventures.wallet.util.SCUserUtils.userFullName;
import static io.techery.janet.smartcard.action.settings.SetHomeDisplayTypeAction.DISPLAY_NAME_ONLY;
import static io.techery.janet.smartcard.action.settings.SetHomeDisplayTypeAction.DISPLAY_PHONE_AND_NAME;
import static io.techery.janet.smartcard.action.settings.SetHomeDisplayTypeAction.DISPLAY_PICTURE_AND_NAME;
import static io.techery.janet.smartcard.action.settings.SetHomeDisplayTypeAction.DISPLAY_PICTURE_ONLY;

public class DisplayOptionsViewHolder {

   private final View rootView;

   private TextView title;
   private TextView firstName;
   private TextView fullName;
   private SimpleDraweeView photo;
   private View silhouette;
   private TextView addPhone;
   private TextView addPhoto;
   private TextView photoRequired;
   private DisplayOptionsClickListener clickListener;

   DisplayOptionsViewHolder(View view) {
      this.rootView = view;
      title = view.findViewById(R.id.tv_title);
      firstName = view.findViewById(R.id.tv_first_name);
      fullName = view.findViewById(R.id.tv_full_name);
      photo = view.findViewById(R.id.iv_photo);
      SmartCardAvatarHelper.applyGrayScaleColorFilter(photo);
      silhouette = view.findViewById(R.id.iv_silhouette);
      addPhone = view.findViewById(R.id.tv_add_phone);
      addPhone.setOnClickListener(addPhone -> {
         if (clickListener != null) {
            clickListener.onAddPhone();
         }
      });
      addPhoto = view.findViewById(R.id.tv_add_photo);
      addPhoto.setOnClickListener(addPhoto -> {
         if (clickListener != null) {
            clickListener.onAddPhoto();
         }
      });
      photoRequired = view.findViewById(R.id.tv_photo_required);
   }

   void bindData(@SetHomeDisplayTypeAction.HomeDisplayType int type, @StringRes int titleRes, @NonNull SmartCardUser user) {
      SmartCardUserPhoto userPhoto = user.userPhoto();
      SmartCardUserPhone userPhone = user.phoneNumber();
      String phone = (userPhone == null) ? String.format(Locale.US, "(%s)", rootView.getResources()
            .getString(R.string.wallet_settings_general_display_phone_required)) : userPhone.fullPhoneNumber();

      title.setText(titleRes);

      firstName.setVisibility(type == DISPLAY_PICTURE_AND_NAME ? View.VISIBLE : View.GONE);
      fullName.setVisibility((type == DISPLAY_NAME_ONLY || type == DISPLAY_PHONE_AND_NAME) ? View.VISIBLE : View.GONE);

      boolean hasPhoto = (type == DISPLAY_PICTURE_ONLY || type == DISPLAY_PICTURE_AND_NAME) && userPhoto == null;
      addPhoto.setVisibility(hasPhoto ? View.VISIBLE : View.GONE);
      photoRequired.setVisibility(hasPhoto ? View.VISIBLE : View.GONE);
      silhouette.setVisibility(hasPhoto ? View.VISIBLE : View.GONE);

      addPhone.setVisibility(type == DISPLAY_PHONE_AND_NAME && userPhone == null ? View.VISIBLE : View.GONE);

      switch (type) {
         case DISPLAY_PICTURE_ONLY:
            if (userPhoto != null) {
               photo.setImageURI(userPhoto.getUri());
            }
            break;
         case DISPLAY_PICTURE_AND_NAME:
            if (userPhoto != null) {
               photo.setImageURI(userPhoto.getUri());
            }
            firstName.setText(user.firstName());
            break;
         case DISPLAY_NAME_ONLY:
            fullName.setText(userFullName(user));
            break;
         case DISPLAY_PHONE_AND_NAME:
            fullName.setText(userFullName(user) + "\n\n" + phone);
            break;
         default:
            break;
      }
   }

   void setClickListener(DisplayOptionsClickListener clickListener) {
      this.clickListener = clickListener;
   }

   public void onPagePositionUpdated(float position, float alphaOffset) {
      final float scale = Math.abs(Math.abs(position) - 1) * 0.2f + 0.8f;
      rootView.setScaleX(scale);
      rootView.setScaleY(scale);

      final float alpha = (Math.abs(position) > alphaOffset) ? 0 : (alphaOffset - Math.abs(position)) / alphaOffset;
      updateViewsAlpha(alpha);
   }

   private void updateViewsAlpha(float alpha) {
      title.setAlpha(alpha);
      if (addPhoto != null) {
         addPhoto.setAlpha(alpha);
      }
      if (addPhone != null) {
         addPhone.setAlpha(alpha);
      }
   }
}
