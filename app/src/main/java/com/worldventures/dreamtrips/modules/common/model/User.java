package com.worldventures.dreamtrips.modules.common.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.Date;

public class User extends BaseEntity implements Parcelable, Serializable {


    public static final Creator<User> CREATOR = new Creator<User>() {
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };

    String username;
    String email;
    String firstName;
    String lastName;
    Date birthDate;
    String location;
    Avatar avatar;

    String coverPath;

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

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
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
        return getFirstName() + " " + getLastName();
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
        String original;
        String medium;
        String thumb;

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
