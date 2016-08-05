package com.worldventures.dreamtrips.modules.common.model;

import android.os.Parcel;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.core.session.acl.Feature;

import java.util.List;

public class Session extends BaseEntity {

    private String token;
    @SerializedName("sso_token")
    private String ssoToken;
    private User user;
    private String locale;
    private List<Feature> permissions;

    public Session() {
        super();
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSsoToken() {
        return ssoToken;
    }

    public String getLocale() {
        return locale;
    }

    public void setSsoToken(String ssoToken) {
        this.ssoToken = ssoToken;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Feature> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<Feature> permissions) {
        this.permissions = permissions;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.token);
        dest.writeString(this.ssoToken);
        dest.writeParcelable(this.user, flags);
        dest.writeList(this.permissions);
    }

    public Session(Parcel in) {
        super(in);
        this.token = in.readString();
        this.ssoToken = in.readString();
        this.user = in.readParcelable(User.class.getClassLoader());
        in.readList(this.permissions, Feature.class.getClassLoader());
    }

    public static final Creator<Session> CREATOR = new Creator<Session>() {
        @Override
        public Session createFromParcel(Parcel in) {
            return new Session(in);
        }

        @Override
        public Session[] newArray(int size) {
            return new Session[size];
        }
    };
}