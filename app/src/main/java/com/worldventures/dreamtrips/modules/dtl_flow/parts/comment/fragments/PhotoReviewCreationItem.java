package com.worldventures.dreamtrips.modules.dtl_flow.parts.comment.fragments;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.core.modules.picker.model.MediaPickerAttachment;
import com.worldventures.core.model.Location;
import com.worldventures.dreamtrips.social.ui.feed.view.custom.tagview.viewgroup.newio.model.PhotoTag;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PhotoReviewCreationItem implements Parcelable {

   long id;
   @NotNull String fileUri;
   @NotNull String filePath;
   @NotNull String originUrl = "";
   @NotNull String location;
   @NotNull ArrayList<PhotoTag> basePhotoTags = new ArrayList<>();
   @NotNull ArrayList<PhotoTag> cachedAddedPhotoTags = new ArrayList<>();
   @NotNull ArrayList<PhotoTag> cachedRemovedPhotoTags = new ArrayList<>();
   @NotNull String mediaAttachmentType;
   String title;

   boolean canEdit;
   boolean canDelete;

   List<PhotoTag> suggestions = new ArrayList<>();
   private int width;
   private int height;

   //analytics related
   private MediaPickerAttachment.Source source;
   private Location locationFromExif;

   public PhotoReviewCreationItem() {
      //do nothing
   }

   public String getFileUri() {
      return fileUri;
   }

   public void setFileUri(String fileUri) {
      this.fileUri = fileUri;
   }

   public void setFilePath(String filePath) {
      this.filePath = filePath;
   }

   @NotNull
   public String getFilePath() {
      return filePath;
   }

   @NotNull
   public String getOriginUrl() {
      return originUrl;
   }

   public void setOriginUrl(@NotNull String originUrl) {
      this.originUrl = originUrl;
   }

   public String getLocation() {
      return location;
   }

   public void setLocation(String location) {
      this.location = location;
   }

   public void setBasePhotoTags(ArrayList<PhotoTag> basePhotoTags) {
      this.basePhotoTags = basePhotoTags;
   }

   @NotNull
   public ArrayList<PhotoTag> getCachedAddedPhotoTags() {
      return cachedAddedPhotoTags;
   }

   @NotNull
   public ArrayList<PhotoTag> getCachedRemovedPhotoTags() {
      return cachedRemovedPhotoTags;
   }

   public List<PhotoTag> getSuggestions() {
      return suggestions;
   }

   public long getId() {
      return id;
   }

   public void setId(long id) {
      this.id = id;
   }

   public List<PhotoTag> getCombinedTags() {
      List<PhotoTag> combinedTags = new ArrayList<>(cachedAddedPhotoTags);
      combinedTags.removeAll(cachedRemovedPhotoTags);
      combinedTags.addAll(basePhotoTags);
      combinedTags.removeAll(cachedRemovedPhotoTags);

      return combinedTags;
   }

   @NotNull
   public String getTitle() {
      return title != null ? title : "";
   }

   public void setSuggestions(List<PhotoTag> suggestions) {
      this.suggestions = suggestions;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public boolean isCanEdit() {
      return canEdit;
   }

   public void setCanEdit(boolean canEdit) {
      this.canEdit = canEdit;
   }

   public boolean isCanDelete() {
      return canDelete;
   }

   public void setCanDelete(boolean canDelete) {
      this.canDelete = canDelete;
   }

   public Location getLocationFromExif() {
      return locationFromExif;
   }

   public void setLocationFromExif(Location locationFromExif) {
      this.locationFromExif = locationFromExif;
   }

   public MediaPickerAttachment.Source getSource() {
      return source;
   }

   public void setSource(MediaPickerAttachment.Source source) {
      this.source = source;
   }

   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeLong(this.id);
      dest.writeString(this.filePath);
      dest.writeString(this.fileUri);
      dest.writeString(this.originUrl);
      dest.writeString(this.location);
      dest.writeTypedList(basePhotoTags);
      dest.writeTypedList(cachedAddedPhotoTags);
      dest.writeTypedList(cachedRemovedPhotoTags);
      dest.writeString(this.mediaAttachmentType);
      dest.writeTypedList(suggestions);
      dest.writeString(title);
      dest.writeParcelable(locationFromExif, flags);
      dest.writeInt(source != null ? source.ordinal() : MediaPickerAttachment.Source.UNKNOWN.ordinal());
   }

   protected PhotoReviewCreationItem(Parcel in) {
      this.id = in.readLong();
      this.filePath = in.readString();
      this.fileUri = in.readString();
      this.originUrl = in.readString();
      this.location = in.readString();
      int tmpStatus = in.readInt(); //NOPMD
      this.basePhotoTags = in.createTypedArrayList(PhotoTag.CREATOR);
      this.cachedAddedPhotoTags = in.createTypedArrayList(PhotoTag.CREATOR);
      this.cachedRemovedPhotoTags = in.createTypedArrayList(PhotoTag.CREATOR);
      this.mediaAttachmentType = in.readString();
      this.suggestions = in.createTypedArrayList(PhotoTag.CREATOR);
      this.title = in.readString();
      this.location = in.readParcelable(Location.class.getClassLoader());
      this.source = MediaPickerAttachment.Source.values()[in.readInt()];
   }

   public static final Creator<PhotoReviewCreationItem> CREATOR = new Creator<PhotoReviewCreationItem>() {
      @Override
      public PhotoReviewCreationItem createFromParcel(Parcel source) {
         return new PhotoReviewCreationItem(source);
      }

      @Override
      public PhotoReviewCreationItem[] newArray(int size) {
         return new PhotoReviewCreationItem[size];
      }
   };

   public void setWidth(int width) {
      this.width = width;
   }

   public void setHeight(int height) {
      this.height = height;
   }

   public int getWidth() {
      return width;
   }

   public int getHeight() {
      return height;
   }
}
