package com.worldventures.core.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.innahema.collections.query.queriables.Queryable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class User extends BaseEntity implements Parcelable, Cloneable {

   @SuppressWarnings("PMD.UnusedPrivateField")
   private static final String RBS_SUBSCRIPTION = "RBS";   //rep
   private static final String DTM_SUBSCRIPTION = "DTM";   //member
   private static final String DTS_SUBSCRIPTION = "DTS";   //standard
   private static final String DTG_SUBSCRIPTION = "DTG";   //gold
   private static final String DTP_SUBSCRIPTION = "DTP";   //platinum

   private String username;
   private String email;
   private String company;
   private Avatar avatar;
   private String firstName;
   private String lastName;
   private String location;
   private List<String> badges;
   private Date birthDate;
   private Date enrollDate;
   private String sponsorUsername;
   private String countryCode;

   private double dreamTripsPoints;
   private double roviaBucks;
   private int tripImagesCount;
   private int bucketListItemsCount;
   private int friendsCount;

   private boolean termsAccepted;

   private Relationship relationship;

   @SerializedName("background_photo_url") private String backgroundPhotoUrl;
   /**
    * RBS = Rep (i.e. this subscription is needed to show "Rep Tools")
    * DTM = Standard DreamTrips Member
    * DTL = DreamTrips Life membership (ignore for now)
    * DTG = DreamTrips Gold membership
    * DTP = DreamTrips Platinum
    * LDTM - ignore
    */
   private List<String> subscriptions;

   @SerializedName("circles") private List<Circle> circles;

   @SerializedName("mutuals") private MutualFriends mutualFriends;

   private transient boolean avatarUploadInProgress;
   private transient boolean coverUploadInProgress;

   public User() {
      super();
   }

   public User(int id) {
      this.id = id;
   }

   public void setMutualFriends(MutualFriends mutualFriends) {
      this.mutualFriends = mutualFriends;
   }

   public MutualFriends getMutualFriends() {
      return mutualFriends;
   }

   public String getCirclesString() {
      if (circles == null || circles.isEmpty()) {
         return "";
      }
      return TextUtils.join(", ", Queryable.from(circles).map(Circle::getName).toList());
   }

   public void setCircles(List<Circle> circles) {
      this.circles = circles;
   }

   @NonNull
   public List<Circle> getCircles() {
      if (circles == null) {
         circles = new ArrayList<>();
      }
      return circles;
   }

   public String getBackgroundPhotoUrl() {
      if (backgroundPhotoUrl == null) {
         backgroundPhotoUrl = "";
      }
      return backgroundPhotoUrl;
   }

   public void setBackgroundPhotoUrl(String backgroundPhotoUrl) {
      this.backgroundPhotoUrl = backgroundPhotoUrl;
   }

   public String getUsername() {
      return username;
   }

   public void setUsername(String username) {
      this.username = username;
   }

   public String getEmail() {
      return email;
   }

   public void setEmail(String email) {
      this.email = email;
   }

   public String getFirstName() {
      return firstName;
   }

   public void setFirstName(String firstName) {
      this.firstName = firstName;
   }

   public String getLastName() {
      return lastName;
   }

   public void setLastName(String lastName) {
      this.lastName = lastName;
   }

   public String getCompany() {
      return company;
   }

   public void setCompany(String company) {
      this.company = company;
   }

   public void setBirthDate(Date birthDate) {
      this.birthDate = birthDate;
   }

   public Date getBirthDate() {
      return birthDate;
   }

   public void setEnrollDate(Date enrollDate) {
      this.enrollDate = enrollDate;
   }

   public Date getEnrollDate() {
      return enrollDate;
   }

   public void setDreamTripsPoints(double dreamTripsPoints) {
      this.dreamTripsPoints = dreamTripsPoints;
   }

   public double getDreamTripsPoints() {
      return dreamTripsPoints;
   }

   public void setRoviaBucks(double roviaBucks) {
      this.roviaBucks = roviaBucks;
   }

   public double getRoviaBucks() {
      return roviaBucks;
   }

   public int getTripImagesCount() {
      return tripImagesCount;
   }

   public void setTripImagesCount(int tripImagesCount) {
      this.tripImagesCount = tripImagesCount;
   }

   public void setBucketListItemsCount(int bucketListItemsCount) {
      this.bucketListItemsCount = bucketListItemsCount;
   }

   public int getBucketListItemsCount() {
      return bucketListItemsCount;
   }

   public void setFriendsCount(int friendsCount) {
      this.friendsCount = friendsCount;
   }

   public int getFriendsCount() {
      return friendsCount;
   }

   public String getLocation() {
      return location;
   }

   public String getSponsorUsername() {
      return sponsorUsername;
   }

   public void setLocation(String location) {
      this.location = location;
   }

   public Avatar getAvatar() {
      return avatar;
   }

   public void setAvatar(Avatar avatar) {
      this.avatar = avatar;
   }

   public boolean isTermsAccepted() {
      return termsAccepted;
   }

   public void setTermsAccepted(boolean termsAccepted) {
      this.termsAccepted = termsAccepted;
   }

   public String getFullName() {
      return Queryable.from(new String[]{getFirstName(), getLastName()}).notNulls().joinStrings(" ");
   }

   public boolean isPlatinum() {
      return contains(DTP_SUBSCRIPTION);
   }

   public boolean isGold() {
      return contains(DTG_SUBSCRIPTION);
   }

   public boolean isGeneral() {
      return contains(DTM_SUBSCRIPTION, DTS_SUBSCRIPTION);
   }

   public boolean isMember() {
      return contains(DTM_SUBSCRIPTION, DTG_SUBSCRIPTION, DTP_SUBSCRIPTION);
   }

   private boolean contains(String... keys) {
      if (subscriptions != null) {
         for (String key : keys) {
            if (subscriptions.contains(key)) {
               return true;
            }
         }
      }
      return false;
   }

   public Relationship getRelationship() {
      return relationship;
   }

   public void setRelationship(Relationship relationship) {
      this.relationship = relationship;
   }

   public void unfriend() {
      relationship = Relationship.NONE;
   }

   public List<String> getBadges() {
      return badges;
   }

   public void setBadges(List<String> badges) {
      this.badges = badges;
   }

   public void setCountryCode(String countryCode) {
      this.countryCode = countryCode;
   }

   public String getCountryCode() {
      return countryCode;
   }

   public List<String> getSubscriptions() {
      return subscriptions;
   }

   public void setSubscriptions(List<String> subscriptions) {
      this.subscriptions = subscriptions;
   }

   public static class Avatar implements Parcelable, Serializable {
      public static final Creator<Avatar> CREATOR = new Creator<Avatar>() {
         public Avatar createFromParcel(Parcel source) {
            return new Avatar(source);
         }

         public Avatar[] newArray(int size) {
            return new Avatar[size];
         }
      };
      private String original;
      private String medium;
      private String thumb;

      public Avatar() {
         super();
      }

      private Avatar(Parcel in) {
         this.original = in.readString();
         this.medium = in.readString();
         this.thumb = in.readString();
      }

      public String getOriginal() {
         return original != null ? original : "";
      }

      public void setOriginal(String original) {
         this.original = original;
      }

      public String getMedium() {
         return medium != null ? medium : "";
      }

      public void setMedium(String medium) {
         this.medium = medium;
      }

      public String getThumb() {
         return thumb != null ? thumb : "";
      }

      public void setThumb(String thumb) {
         this.thumb = thumb;
      }

      @Override
      public int describeContents() {
         return 0;
      }

      @Override
      public void writeToParcel(Parcel dest, int flags) {
         dest.writeString(this.original);
         dest.writeString(this.medium);
         dest.writeString(this.thumb);
      }

   }

   public static class MutualFriends implements Parcelable, Serializable {

      private int count;

      public MutualFriends() {
         super();
      }

      public MutualFriends(int count) {
         this.count = count;
      }

      protected MutualFriends(Parcel in) {
         this.count = in.readInt();
      }

      public static final Creator<MutualFriends> CREATOR = new Creator<MutualFriends>() {
         @Override
         public MutualFriends createFromParcel(Parcel in) {
            return new MutualFriends(in);
         }

         @Override
         public MutualFriends[] newArray(int size) {
            return new MutualFriends[size];
         }
      };

      public int getCount() {
         return count;
      }

      @Override
      public int describeContents() {
         return 0;
      }

      @Override
      public void writeToParcel(Parcel dest, int flags) {
         dest.writeInt(this.count);
      }
   }

   public void setAvatarUploadInProgress(boolean avatarUploadInProgress) {
      this.avatarUploadInProgress = avatarUploadInProgress;
   }

   public void setCoverUploadInProgress(boolean coverUploadInProgress) {
      this.coverUploadInProgress = coverUploadInProgress;
   }

   public boolean isAvatarUploadInProgress() {
      return avatarUploadInProgress;
   }

   public boolean isCoverUploadInProgress() {
      return coverUploadInProgress;
   }

   public enum Relationship {
      @SerializedName("none")NONE,
      @SerializedName("friend")FRIEND,
      @SerializedName("incoming_request")INCOMING_REQUEST,
      @SerializedName("outgoing_request")OUTGOING_REQUEST,
      @SerializedName("rejected")REJECTED,
      UNKNOWN
   }

   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      super.writeToParcel(dest, flags);
      dest.writeString(this.username);
      dest.writeString(this.email);
      dest.writeParcelable(this.avatar, 0);
      dest.writeString(this.firstName);
      dest.writeString(this.lastName);
      dest.writeString(this.company);
      dest.writeString(this.location);
      dest.writeLong(birthDate != null ? birthDate.getTime() : -1);
      dest.writeLong(enrollDate != null ? enrollDate.getTime() : -1);
      dest.writeDouble(this.dreamTripsPoints);
      dest.writeDouble(this.roviaBucks);
      dest.writeInt(this.tripImagesCount);
      dest.writeInt(this.bucketListItemsCount);
      dest.writeInt(this.relationship == null ? -1 : this.relationship.ordinal());
      dest.writeString(this.backgroundPhotoUrl);
      dest.writeStringList(this.subscriptions);
      dest.writeByte(termsAccepted ? (byte) 1 : (byte) 0);
      dest.writeParcelable(this.mutualFriends, 0);
      dest.writeString(sponsorUsername);
      dest.writeString(countryCode);
      dest.writeList(this.circles);
      dest.writeStringList(this.badges);
   }

   protected User(Parcel in) {
      super(in);
      this.username = in.readString();
      this.email = in.readString();
      this.avatar = in.readParcelable(Avatar.class.getClassLoader());
      this.firstName = in.readString();
      this.lastName = in.readString();
      this.company = in.readString();
      this.location = in.readString();
      long tmpBirthDate = in.readLong();
      this.birthDate = tmpBirthDate == -1 ? null : new Date(tmpBirthDate);
      long tmpEnrollDate = in.readLong();
      this.enrollDate = tmpEnrollDate == -1 ? null : new Date(tmpEnrollDate);
      this.dreamTripsPoints = in.readDouble();
      this.roviaBucks = in.readDouble();
      this.tripImagesCount = in.readInt();
      this.bucketListItemsCount = in.readInt();
      int tmpRelationship = in.readInt();
      this.relationship = tmpRelationship == -1 ? null : Relationship.values()[tmpRelationship];
      this.backgroundPhotoUrl = in.readString();
      this.subscriptions = in.createStringArrayList();
      this.termsAccepted = in.readByte() != 0;
      this.mutualFriends = in.readParcelable(MutualFriends.class.getClassLoader());
      this.sponsorUsername = in.readString();
      this.countryCode = in.readString();
      circles = new ArrayList<>();
      in.readList(circles, Circle.class.getClassLoader());
      badges = new ArrayList<>();
      in.readStringList(badges);
   }

   public static final Creator<User> CREATOR = new Creator<User>() {
      public User createFromParcel(Parcel source) {
         return new User(source);
      }

      public User[] newArray(int size) {
         return new User[size];
      }
   };

   @Override
   public User clone() throws CloneNotSupportedException {
      return (User) super.clone();
   }

   public User copy() {
      try {
         return clone();
      } catch (CloneNotSupportedException e) {
         return null;
      }
   }

}
