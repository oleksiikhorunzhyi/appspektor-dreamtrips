package com.worldventures.dreamtrips.core.model;

import java.util.Date;

public class User extends BaseEntity {

    String username;
    String email;
    String firstName;
    String lastName;
    Date birthDate;
    String location;
    Avatar avatar;

    // {id=1.0, username=888888,
    // email=wvoperations@gmail.com,
    // first_name=wvoperations@gmail.com,
    // last_name=Account5,
    // enroll_date=2007-06-14,
    // birth_date=null,
    // location=null,
    // avatar={original=/avatars/original/missing.png, medium=/avatars/medium/missing.png, thumb=/avatars/thumb/missing.png}}}


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
}
