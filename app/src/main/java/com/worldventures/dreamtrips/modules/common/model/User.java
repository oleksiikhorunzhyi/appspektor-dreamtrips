package com.worldventures.dreamtrips.modules.common.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.worldventures.dreamtrips.modules.friends.model.Circle;

import java.util.Date;
import java.util.List;

public class User extends BaseEntity implements Parcelable {

    public static final String RBS_SUBSCTIPTION = "RBS";
    public static final String DTM_SUBSCTIPTION = "DTM";
    public static final String DTS_SUBSCTIPTION = "DTS";
    public static final String DTG_SUBSCTIPTION = "DTG";
    public static final String DTP_SUBSCRIPTION = "DTP";

    public static final String RELATION_NONE = "none";
    public static final String RELATION_FRIEND = "friend";
    public static final String RELATION_INCOMING_REQUEST = "incoming_request";
    public static final String RELATION_OUTGOING_REQUEST = "outgoing_request";
    public static final String RELATION_REJECT = "rejected";

    public static final Creator<User> CREATOR = new Creator<User>() {
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };

    private String username;
    private String email;
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

    private String relationship;

    private String coverPath;

    /**
     * RBS = Rep (i.e. this subscription is needed to show "Rep Tools")
     * DTM = Standard DreamTrips Member
     * DTL = DreamTrips Life membership (ignore for now)
     * DTG = DreamTrips Gold membership
     * DTP = DreamTrips Platinum
     * LDTM - ignore
     */
    private List<String> subscriptions;

    public User() {
    }

    private User(Parcel in) {
        this.username = in.readString();
        this.email = in.readString();
        this.firstName = in.readString();
        this.lastName = in.readString();
        long tmpBirthDate = in.readLong();
        this.birthDate = tmpBirthDate == -1 ? null : new Date(tmpBirthDate);
        this.location = in.readString();
        this.avatar = in.readParcelable(Avatar.class.getClassLoader());
        this.coverPath = in.readString();
        this.id = in.readInt();
        this.enrollDate = (Date)in.readSerializable();
        this.relationship = in.readString();
    }

    public String getCoverPath() {
        if (coverPath == null) {
            coverPath = "";
        }
        return coverPath;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
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

   public String getRelationship() {
        return relationship;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.username);
        dest.writeString(this.email);
        dest.writeString(this.firstName);
        dest.writeString(this.lastName);
        dest.writeLong(birthDate != null ? birthDate.getTime() : -1);
        dest.writeString(this.location);
        dest.writeParcelable(this.avatar, flags);
        dest.writeString(this.coverPath);
        dest.writeInt(this.id);
        dest.writeSerializable(enrollDate);
        dest.writeString(this.relationship);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        User user = (User) o;

        return !(username != null ? !username.equals(user.username) : user.username != null);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (username != null ? username.hashCode() : 0);
        return result;
    }

    public static class Avatar implements Parcelable {
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
}
