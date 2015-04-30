package com.worldventures.dreamtrips.modules.membership.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;

import com.worldventures.dreamtrips.modules.common.model.BaseEntity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

public class InviteTemplate extends BaseEntity implements Parcelable {
    public static final int EMAIL = 0;
    public static final int SMS = 1;

    private String title;
    private CoverImage coverImage;
    private String video;
    private String locale;
    private String content;
    private ArrayList<Member> to = new ArrayList<>(0);
    private String from;
    @Type
    private int type;
    private String link;

    public ArrayList<Member> getTo() {
        return to;
    }

    public void setTo(ArrayList<Member> to) {
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTitle() {
        return title;
    }

    public CoverImage getCoverImage() {
        return coverImage;
    }

    public String getVideo() {
        return video;
    }

    public String getLocale() {
        return locale;
    }

    public String getContent() {
        return content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeSerializable(this.coverImage);
        dest.writeString(this.video);
        dest.writeString(this.locale);
        dest.writeString(this.content);
        dest.writeSerializable(this.to);
        dest.writeString(this.from);
        dest.writeInt(this.type);
        dest.writeInt(this.id);
        dest.writeString(this.link);
    }

    public InviteTemplate() {
    }

    private InviteTemplate(Parcel in) {
        this.title = in.readString();
        this.coverImage = (CoverImage) in.readSerializable();
        this.video = in.readString();
        this.locale = in.readString();
        this.content = in.readString();
        this.to = (ArrayList<Member>) in.readSerializable();
        this.from = in.readString();
        this.type = in.readInt();
        this.id = in.readInt();
        this.link = in.readString();
    }

    public static final Parcelable.Creator<InviteTemplate> CREATOR = new Parcelable.Creator<InviteTemplate>() {
        public InviteTemplate createFromParcel(Parcel source) {
            return new InviteTemplate(source);
        }

        public InviteTemplate[] newArray(int size) {
            return new InviteTemplate[size];
        }
    };

    @IntDef({EMAIL, SMS})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {
    }
}
