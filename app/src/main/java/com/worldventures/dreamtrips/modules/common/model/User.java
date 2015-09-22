package com.worldventures.dreamtrips.modules.common.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.friends.model.Circle;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

public class User extends BaseEntity implements Parcelable {

    public static final String RBS_SUBSCTIPTION = "RBS"; //rep
    public static final String DTM_SUBSCTIPTION = "DTM"; //member
    public static final String DTS_SUBSCTIPTION = "DTS"; //standard
    public static final String DTG_SUBSCTIPTION = "DTG"; //gold
    public static final String DTP_SUBSCRIPTION = "DTP"; //platinum

    private String username;
    private String email;
    private String company;
    private Avatar avatar;
    private String firstName;
    private String lastName;
    private String location;
    private Date birthDate;
    private Date enrollDate;

    private double dreamTripsPoints;
    private double roviaBucks;
    private int tripImagesCount;
    private int bucketListItemsCount;

    private Relationship relationship;

    @SerializedName("background_photo_url")
    private String backgroundPhotoUrl;
    /**
     * RBS = Rep (i.e. this subscription is needed to show "Rep Tools")
     * DTM = Standard DreamTrips Member
     * DTL = DreamTrips Life membership (ignore for now)
     * DTG = DreamTrips Gold membership
     * DTP = DreamTrips Platinum
     * LDTM - ignore
     */
    private List<String> subscriptions;

    //TODO TEMP SOLUTION, NOT NEEDED IN FUTURE, JUST FOR APPERIAN RELEASE
    private boolean socialEnabled;


    @SerializedName("circle_ids")
    HashSet<String> circleIds;

    @SerializedName("mutual_friends")
    int mutualFriends;

    private transient String circles;
    private transient boolean avatarUploadInProgress;
    private transient boolean coverUploadInProgress;

    public User() {
    }

    public User(int id) {
        this.id = id;
    }


    public int getMutualFriends() {
        return mutualFriends;
    }

    public void setCircles(List<Circle> circles) {
        List<String> userCircles = new ArrayList<>();

        for (String s : circleIds) {
            for (Circle circle : circles) {
                if (circle.getId() != null && circle.getId().equals(s)) {
                    userCircles.add(circle.getName());
                    break;
                }
            }
        }

        this.circles = TextUtils.join(", ", userCircles);
    }

    public String getCircles() {
        return circles;
    }

    public HashSet<String> getCircleIds() {
        return circleIds;
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

    public boolean isSocialEnabled() {
        return socialEnabled;
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

    public String getLastName() {
        return lastName;
    }

    public String getCompany() {
        return company;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public Date getEnrollDate() {
        return enrollDate;
    }

    public double getDreamTripsPoints() {
        return dreamTripsPoints;
    }

    public double getRoviaBucks() {
        return roviaBucks;
    }

    public int getTripImagesCount() {
        return tripImagesCount;
    }

    public int getBucketListItemsCount() {
        return bucketListItemsCount;
    }

    public String getLocation() {
        return location;
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

    public String getFullName() {
        return TextUtils.join(" ", new String[]{getFirstName(), getLastName()});
    }

    public boolean isMember() {
        return contains(DTG_SUBSCTIPTION, DTP_SUBSCRIPTION, DTM_SUBSCTIPTION, DTS_SUBSCTIPTION);
    }

    public boolean isPlatinum() {
        return contains(DTP_SUBSCRIPTION);
    }

    public boolean isGold() {
        return contains(DTG_SUBSCTIPTION);
    }

    public boolean isGeneral() {
        return contains(DTM_SUBSCTIPTION, DTS_SUBSCTIPTION);
    }

    public boolean isRep() {
        return contains(RBS_SUBSCTIPTION);
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

    public Spanned getUsernameWithCompany(Context context) {
        String userWithCompany = !TextUtils.isEmpty(getCompany())
                ? context.getString(R.string.user_name_with_company, getFullName(), getCompany())
                : context.getString(R.string.user_name, getFullName());
        return Html.fromHtml(userWithCompany);
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
        @SerializedName("rejected")REJECT
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
        dest.writeByte(socialEnabled ? (byte) 1 : (byte) 0);
        dest.writeSerializable(this.circleIds);
        dest.writeInt(this.mutualFriends);
        dest.writeString(this.circles);
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
        this.socialEnabled = in.readByte() != 0;
        this.circleIds = (HashSet<String>) in.readSerializable();
        this.mutualFriends = in.readInt();
        this.circles = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
