package com.worldventures.dreamtrips.modules.common.model;

import android.os.Parcel;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.core.session.acl.Feature;
import com.worldventures.dreamtrips.modules.settings.model.Setting;

import java.util.ArrayList;
import java.util.List;

public class Session extends BaseEntity {

    private String token;
    @SerializedName("sso_token")
    private String ssoToken;
    private User user;
    private String locale;
    private List<Feature> permissions;
    private List<Setting> settings;

    public Session() {
        super();
    }

    protected Session(Parcel in) {
        super(in);
        this.token = in.readString();
        this.ssoToken = in.readString();
        this.user = in.readParcelable(User.class.getClassLoader());
        in.readList(this.permissions, Feature.class.getClassLoader());
        in.readList(this.settings, Setting.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.token);
        dest.writeString(this.ssoToken);
        dest.writeParcelable(this.user, flags);
        dest.writeList(this.permissions);
        dest.writeList(this.settings);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getToken() {
        return token;
    }

    public String getSsoToken() {
        return ssoToken;
    }

    public String getLocale() {
        return locale;
    }

    public User getUser() {
        return user;
    }

    public List<Feature> getPermissions() {
        return permissions;
    }

    public List<Setting> getSettings() {
        if (settings == null)
            return new ArrayList<>();
        return settings;
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