package com.messenger.entities;

import android.os.Parcel;

import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.Image;

import java.util.Date;

public class PhotoAttachment implements IFullScreenObject {

    private final String messageId;
    private final String conversationId;
    private final User user;
    private final Image image;
    private final Date date;
    private final boolean flaggingEnabled;

    public PhotoAttachment(Parcel source) {
        this.messageId = source.readString();
        this.conversationId = source.readString();
        this.image = source.readParcelable(Image.class.getClassLoader());
        this.user = source.readParcelable(User.class.getClassLoader());
        this.date = (Date) source.readSerializable();
        this.flaggingEnabled = source.readInt() == 1;
    }

    public static final Creator<PhotoAttachment> CREATOR = new Creator<PhotoAttachment>() {
        public PhotoAttachment createFromParcel(Parcel source) {
            return new PhotoAttachment(source);
        }

        public PhotoAttachment[] newArray(int size) {
            return new PhotoAttachment[size];
        }
    };

    private PhotoAttachment(Builder builder) {
        messageId = builder.messageId;
        conversationId = builder.conversationId;
        user = builder.user;
        image = builder.image;
        date = builder.date;
        flaggingEnabled = builder.flaggingEnabled;
    }

    @Override
    public Image getFSImage() {
        return image;
    }

    @Override
    public String getFSTitle() {
        return null;
    }

    @Override
    public String getFSDescription() {
        return null;
    }

    @Override
    public String getFSShareText() {
        return null;
    }

    @Override
    public String getFSId() {
        return image.getUrl();
    }

    @Override
    public int getFSCommentCount() {
        return 0;
    }

    @Override
    public int getFSLikeCount() {
        return 0;
    }

    @Override
    public String getFSLocation() {
        return null;
    }

    @Override
    public String getFSDate() {
        return null;
    }

    @Override
    public String getFSUserPhoto() {
        return user.getAvatar().getOriginal();
    }

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public String getImagePath() {
        return image.getUrl();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public Date getDate() {
        return date;
    }

    public boolean isFlaggingEnabled() {
        return flaggingEnabled;
    }

    public String getConversationId() {
        return conversationId;
    }

    public String getMessageId() {
        return messageId;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(messageId);
        dest.writeString(conversationId);
        dest.writeParcelable(image, flags);
        dest.writeParcelable(user, flags);
        dest.writeSerializable(date);
        dest.writeInt(flaggingEnabled ? 1 : 0);
    }


    public static final class Builder {
        private String messageId;
        private String conversationId;
        private User user;
        private Image image;
        private Date date;
        private boolean flaggingEnabled;

        public Builder() {
        }

        public Builder messageId(String messageId) {
            this.messageId = messageId;
            return this;
        }

        public Builder conversationId(String conversationId) {
            this.conversationId = conversationId;
            return this;
        }

        public Builder user(User val) {
            user = val;
            return this;
        }

        public Builder image(Image val) {
            image = val;
            return this;
        }

        public Builder date(Date val) {
            date = val;
            return this;
        }

        public Builder flaggingEnabled(boolean flaggingEnabled) {
            this.flaggingEnabled = flaggingEnabled;
            return this;
        }

        public PhotoAttachment build() {
            return new PhotoAttachment(this);
        }
    }
}
