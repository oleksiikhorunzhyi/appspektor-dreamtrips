package com.worldventures.wallet.ui.settings.general.display.impl;

import android.database.DataSetObserver;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.worldventures.wallet.R;
import com.worldventures.wallet.ui.settings.general.profile.common.ProfileViewModel;
import com.worldventures.wallet.util.SCUserUtils;
import com.worldventures.wallet.util.SmartCardAvatarHelper;

import java.util.Locale;

import io.techery.janet.smartcard.action.settings.SetHomeDisplayTypeAction;

import static io.techery.janet.smartcard.action.settings.SetHomeDisplayTypeAction.DISPLAY_NAME_ONLY;
import static io.techery.janet.smartcard.action.settings.SetHomeDisplayTypeAction.DISPLAY_PHONE_AND_NAME;
import static io.techery.janet.smartcard.action.settings.SetHomeDisplayTypeAction.DISPLAY_PICTURE_AND_NAME;
import static io.techery.janet.smartcard.action.settings.SetHomeDisplayTypeAction.DISPLAY_PICTURE_ONLY;

public class DisplayOptionsViewHolder {

   private final View rootView;

   private final TextView title;
   private final TextView firstName;
   private final TextView fullName;
   private final SimpleDraweeView photo;
   private final View silhouette;
   private final TextView addPhone;
   private final TextView addPhoto;
   private final TextView photoRequired;
   private DisplayOptionsClickListener clickListener;

   private int type;
   private int titleRes;
   private ProfileViewModel profileViewModel;

   final DataSetObserver dataSetObserver = new DataSetObserver() {
      @Override
      public void onChanged() {
         if (profileViewModel == null) return;
         internalBindData(type, titleRes, profileViewModel);
      }
   };

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

   void bindData(@SetHomeDisplayTypeAction.HomeDisplayType int type, @StringRes int titleRes, @NonNull ProfileViewModel profileViewModel) {
      this.type = type;
      this.titleRes = titleRes;
      this.profileViewModel = profileViewModel;
      internalBindData(type, titleRes, profileViewModel);
   }

   private void internalBindData(@SetHomeDisplayTypeAction.HomeDisplayType int type, @StringRes int titleRes, ProfileViewModel profileViewModel) {
      final String userPhoto = profileViewModel.getChosenPhotoUri();
      final String userPhone = profileViewModel.getPhoneNumber();
      final String phone = userPhone.isEmpty() ? String.format(Locale.US, "(%s)", rootView.getResources()
            .getString(R.string.wallet_settings_general_display_phone_required)) :
            profileViewModel.getPhoneCode() + userPhone;

      title.setText(titleRes);

      firstName.setVisibility(type == DISPLAY_PICTURE_AND_NAME ? View.VISIBLE : View.GONE);
      fullName.setVisibility((type == DISPLAY_NAME_ONLY || type == DISPLAY_PHONE_AND_NAME) ? View.VISIBLE : View.GONE);

      boolean hasPhoto = (type == DISPLAY_PICTURE_ONLY || type == DISPLAY_PICTURE_AND_NAME) && userPhoto == null;
      addPhoto.setVisibility(hasPhoto ? View.VISIBLE : View.GONE);
      photoRequired.setVisibility(hasPhoto ? View.VISIBLE : View.GONE);
      silhouette.setVisibility(hasPhoto ? View.VISIBLE : View.GONE);

      addPhone.setVisibility(type == DISPLAY_PHONE_AND_NAME && userPhone.isEmpty() ? View.VISIBLE : View.GONE);


      switch (type) {
         case DISPLAY_PICTURE_ONLY:
            if (userPhoto != null) {
               photo.setImageURI(userPhoto);
            }
            break;
         case DISPLAY_PICTURE_AND_NAME:
            if (userPhoto != null) {
               photo.setImageURI(userPhoto);
            }
            firstName.setText(profileViewModel.getFirstName());
            break;
         case DISPLAY_NAME_ONLY:
            fullName.setText(getFullName(profileViewModel));
            break;
         case DISPLAY_PHONE_AND_NAME:
            fullName.setText(getFullName(profileViewModel) + "\n\n" + phone);
            break;
         default:
            break;
      }
   }

   private String getFullName(ProfileViewModel profile) {
      return SCUserUtils.userFullName(profile.getFirstName(), profile.getMiddleName(), profile.getLastName());
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
