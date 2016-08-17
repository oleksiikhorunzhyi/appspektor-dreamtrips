package com.worldventures.dreamtrips.modules.tripsimages.view.custom;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.kbeanie.imagechooser.api.ChooserType;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.kbeanie.imagechooser.api.ImageChooserListener;
import com.kbeanie.imagechooser.api.ImageChooserManager;

import icepick.Icepick;
import icepick.State;
import timber.log.Timber;

public class PickImageDelegate implements ImageChooserListener {

   public static final int FACEBOOK = 346;
   public static final int CAPTURE_PICTURE = ChooserType.REQUEST_CAPTURE_PICTURE;
   public static final int PICK_PICTURE = ChooserType.REQUEST_PICK_PICTURE;

   public static final String FOLDERNAME = "dreamtrip_folder_temp_sd";

   private Activity activity;

   @State int requestType;
   @State String filePath;
   @State int requesterId;

   private ImageChooserManager imageChooserManager;

   private ImagePickCallback imageCallback;
   private ImagePickErrorCallback errorCallback;

   public PickImageDelegate(Activity activity) {
      this.activity = activity;
   }

   public void saveInstanceState(Bundle outState) {
      Icepick.saveInstanceState(this, outState);
   }

   public void restoreInstanceState(Bundle savedState) {
      Icepick.restoreInstanceState(this, savedState);
   }

   public void setRequesterId(int requesterId) {
      this.requesterId = requesterId;
   }

   public void setRequestType(int requestType) {
      this.requestType = requestType;
   }

   public void show() {
      switch (requestType) {
         case CAPTURE_PICTURE:
            takePicture();
            break;
      }
   }

   private void takePicture() {
      imageChooserManager = new ImageChooserManager(activity, requestType, FOLDERNAME, true);
      imageChooserManager.setImageChooserListener(this);
      try {
         filePath = imageChooserManager.choose();
      } catch (Exception e) {
         Timber.e(e, "Problem on image choosing");
      }
   }

   public void onActivityResult(int requestCode, int resultCode, Intent data) {
      if (resultCode == Activity.RESULT_OK) {
         if (requestCode == ChooserType.REQUEST_PICK_PICTURE || requestCode == ChooserType.REQUEST_CAPTURE_PICTURE) {
            if (imageChooserManager == null) {
               imageChooserManager = new ImageChooserManager(activity, requestType, FOLDERNAME, true);
               imageChooserManager.setImageChooserListener(this);
            }

            imageChooserManager.reinitialize(filePath);
            imageChooserManager.submit(requestCode, data);
         }
      }
   }

   public int getRequesterId() {
      return requesterId;
   }

   public int getRequestType() {
      return requestType;
   }

   @Override
   public void onImageChosen(ChosenImage chosenImage) {
      chosenImage.setFileThumbnail("file://" + chosenImage.getFileThumbnail());
      if (imageCallback != null) imageCallback.onImagePicked(chosenImage);
   }

   @Override
   public void onError(String reason) {
      if (errorCallback != null) errorCallback.onError(reason);
   }

   public void setImageCallback(ImagePickCallback imageCallback) {
      this.imageCallback = imageCallback;
   }

   public void setErrorCallback(ImagePickErrorCallback errorCallback) {
      this.errorCallback = errorCallback;
   }

   public interface ImagePickCallback {
      void onImagePicked(ChosenImage... chosenImage);
   }

   public interface ImagePickErrorCallback {
      void onError(String reason);
   }
}