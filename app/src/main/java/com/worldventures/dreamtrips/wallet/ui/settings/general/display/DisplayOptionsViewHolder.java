package com.worldventures.dreamtrips.wallet.ui.settings.general.display;

import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.tripsimages.vision.ImageUtils;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUser;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUserPhone;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUserPhoto;

import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;

import static io.techery.janet.smartcard.action.settings.SetHomeDisplayTypeAction.DISPLAY_NAME_ONLY;
import static io.techery.janet.smartcard.action.settings.SetHomeDisplayTypeAction.DISPLAY_PHONE_ONLY;
import static io.techery.janet.smartcard.action.settings.SetHomeDisplayTypeAction.DISPLAY_PICTURE_AND_NAME;
import static io.techery.janet.smartcard.action.settings.SetHomeDisplayTypeAction.DISPLAY_PICTURE_ONLY;

class DisplayOptionsViewHolder {

   @InjectView(R.id.tv_title) TextView title;

   @InjectView(R.id.tv_first_name) TextView firstName;
   @InjectView(R.id.tv_full_name) TextView fullName;
   @InjectView(R.id.iv_photo) SimpleDraweeView photo;

   @InjectView(R.id.iv_silhouette) View silhouette;
   @InjectView(R.id.tv_add_phone) TextView addPhone;
   @InjectView(R.id.tv_add_photo) TextView addPhoto;
   @InjectView(R.id.tv_photo_required) TextView photoRequired;

   private final View rootView;

   private DisplayOptionsClickListener clickListener;

   DisplayOptionsViewHolder(View view) {
      ButterKnife.inject(this, view);
      rootView = view;
      ImageUtils.applyGrayScaleColorFilter(photo);
   }

   void bindData(SmartCardUser smartCardUser, DisplayOptionsEnum displayOption) {
      int type = displayOption.getDisplayType();

      SmartCardUserPhoto userPhoto = smartCardUser.userPhoto();
      SmartCardUserPhone userPhone = smartCardUser.phoneNumber();
      String phone = (userPhone == null) ? String.format(Locale.US, "(%s)", rootView.getResources()
            .getString(R.string.wallet_settings_general_display_phone_required)) : "+" + userPhone.fullPhoneNumber();

      title.setText(displayOption.getTitleRes());

      firstName.setVisibility(type == DISPLAY_PICTURE_AND_NAME ? View.VISIBLE : View.GONE);
      fullName.setVisibility((type == DISPLAY_NAME_ONLY || type == DISPLAY_PHONE_ONLY) ? View.VISIBLE : View.GONE);

      boolean hasPhoto = (type == DISPLAY_PICTURE_ONLY || type == DISPLAY_PICTURE_AND_NAME) && userPhoto == null;
      addPhoto.setVisibility(hasPhoto ? View.VISIBLE : View.GONE);
      photoRequired.setVisibility(hasPhoto ? View.VISIBLE : View.GONE);
      silhouette.setVisibility(hasPhoto ? View.VISIBLE : View.GONE);

      addPhone.setVisibility(type == DISPLAY_PHONE_ONLY && userPhone == null ? View.VISIBLE : View.GONE);

      switch (type) {
         case DISPLAY_PICTURE_ONLY:
            if (userPhoto != null) photo.setImageURI(userPhoto.photoUrl());
            break;
         case DISPLAY_PICTURE_AND_NAME:
            if (userPhoto != null) photo.setImageURI(userPhoto.photoUrl());
            firstName.setText(smartCardUser.firstName());
            break;
         case DISPLAY_NAME_ONLY:
            fullName.setText(smartCardUser.fullName());
            break;
         case DISPLAY_PHONE_ONLY:
            fullName.setText(smartCardUser.fullName() + "\n\n" + phone);
            break;
      }
   }

   void setClickListener(DisplayOptionsClickListener clickListener) {
      this.clickListener = clickListener;
   }

   void onPagePositionUpdated(float position, float alphaOffset) {
      final float scale = Math.abs(Math.abs(position) - 1) * 0.2f + 0.8f;
      rootView.setScaleX(scale);
      rootView.setScaleY(scale);

      final float alpha = (Math.abs(position) > alphaOffset) ? 0 : (alphaOffset - Math.abs(position)) / alphaOffset;
      updateViewsAlpha(alpha);
   }

   private void updateViewsAlpha(float alpha) {
      title.setAlpha(alpha);
      if (addPhoto != null) addPhoto.setAlpha(alpha);
      if (addPhone != null) addPhone.setAlpha(alpha);
   }

   @Optional
   @OnClick({R.id.tv_add_phone, R.id.tv_add_photo})
   void onClickAddInfo() {
      if (clickListener != null) clickListener.onAddInfoClicked();
   }
}