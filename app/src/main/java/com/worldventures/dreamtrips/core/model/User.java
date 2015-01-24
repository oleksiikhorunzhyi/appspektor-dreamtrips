package com.worldventures.dreamtrips.core.model;

import java.util.Date;

public class User extends BaseEntity {

    public static class Avatar {
        String original;
        String medium;
        String thumb;

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
    }

    String username;
    String email;
    String firstName;
    String lastName;
    Date birthDate;
    String location;
    Avatar avatar;

    String coverPath;

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
}
