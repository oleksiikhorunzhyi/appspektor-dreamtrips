package com.worldventures.dreamtrips.core.uploader.model;

import android.os.Parcel;

import com.worldventures.dreamtrips.core.model.IFullScreenAvailableObject;
import com.worldventures.dreamtrips.core.model.Image;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.RealmObject;
import io.realm.annotations.Index;

public class ImageUploadTask extends RealmObject implements Serializable {

    @Index
    private String taskId;

    private String fileUri;
    private float progress;

    private String title;
    private String locationName;
    private float latitude;
    private float longitude;
    private Date shotAt;
    private String originUrl;

    /**
     * Temporary for fix RealmDB null object problem
     */
    public static ImageUploadTask copy(ImageUploadTask obj) {
        ImageUploadTask t = new ImageUploadTask();
        t.setTaskId(obj.getTaskId());
        t.setFileUri(obj.getFileUri());
        t.setProgress(obj.getProgress());
        t.setTitle(obj.getTitle());
        t.setLocationName(obj.getLocationName());
        t.setLatitude(obj.getLatitude());
        t.setLongitude(obj.getLongitude());
        t.setShotAt(obj.getShotAt());
        t.setOriginUrl(obj.getOriginUrl());
        return t;
    }

    public static List<ImageUploadTask> copy(List<ImageUploadTask> lst) {
        List<ImageUploadTask> result = new ArrayList<>();
        for (ImageUploadTask uploadTask : lst) {
            result.add(copy(uploadTask));
        }
        return result;
    }

    public static List<IFullScreenAvailableObject> from(List<ImageUploadTask> lst) {
        List<IFullScreenAvailableObject> result = new ArrayList<>();
        for (ImageUploadTask uploadTask : lst) {
            result.add(from(uploadTask));
        }
        return result;
    }


    public static IFullScreenAvailableObject from(ImageUploadTask task) {
        ImageUploadTaskFullscreen result = new ImageUploadTaskFullscreen();
        result.setImageUploadTask(copy(task));
        return result;
    }


    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getFileUri() {
        return fileUri;
    }

    public void setFileUri(String fileUri) {
        this.fileUri = fileUri;
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public Date getShotAt() {
        return shotAt;
    }

    public void setShotAt(Date shotAt) {
        this.shotAt = shotAt;
    }

    public String getOriginUrl() {
        return originUrl;
    }

    public void setOriginUrl(String originUrl) {
        this.originUrl = originUrl;
    }

    public static class ImageUploadTaskFullscreen implements IFullScreenAvailableObject {


        private Image image;
        private String title;
        private String description;
        private String shareText;
        private ImageUploadTask task;

        public void setImageUploadTask(ImageUploadTask imageUploadTask) {
            this.task = imageUploadTask;

            image = new Image();
            Image.ImageVersion version = new Image.ImageVersion();
            version.setUrl(task.getFileUri());
            image.setMedium(version);
            image.setOriginal(version);
            image.setThumb(version);
            title = task.getTitle();
            description = "";
            shareText = task.getTitle();
        }

        public ImageUploadTaskFullscreen() {
        }

        @Override
        public Image getFSImage() {

            return image;
        }

        @Override
        public String getFSTitle() {
            return title;
        }

        @Override
        public String getFsDescription() {
            return description;
        }

        @Override
        public String getFsShareText() {
            return shareText;
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeParcelable(this.image, 0);
            dest.writeString(this.title);
            dest.writeString(this.description);
            dest.writeString(this.shareText);
        }

        private ImageUploadTaskFullscreen(Parcel in) {
            this.image = in.readParcelable(Image.class.getClassLoader());
            this.title = in.readString();
            this.description = in.readString();
            this.shareText = in.readString();
        }

        public static final Creator<IFullScreenAvailableObject> CREATOR = new Creator<IFullScreenAvailableObject>() {
            public IFullScreenAvailableObject createFromParcel(Parcel source) {
                return new ImageUploadTaskFullscreen(source);
            }

            public ImageUploadTaskFullscreen[] newArray(int size) {
                return new ImageUploadTaskFullscreen[size];
            }
        };

    }
}
