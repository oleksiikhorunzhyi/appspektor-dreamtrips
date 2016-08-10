package com.worldventures.dreamtrips.modules.feed.model;

import com.worldventures.dreamtrips.modules.common.model.Coordinates;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup.newio.model.PhotoTag;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CreatePhotoEntity {

    private List<PhotoEntity> photos;

    public CreatePhotoEntity() {
        photos = new ArrayList<>();
    }

    public void addPhoto(PhotoEntity photoEntity) {
        photos.add(photoEntity);
    }

    public boolean isEmpty() {
        return photos.isEmpty();
    }

    public static class PhotoEntity {

        private String originUrl;
        private String title;
        private Date shotAt;
        private int width;
        private int height;
        private Coordinates coordinates;
        private String locationName;
        private List<PhotoTag> photoTags;

        public void setOriginUrl(String originUrl) {
            this.originUrl = originUrl;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setShotAt(Date shotAt) {
            this.shotAt = shotAt;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public void setCoordinates(Coordinates coordinates) {
            this.coordinates = coordinates;
        }

        public void setLocationName(String locationName) {
            this.locationName = locationName;
        }

        public void setPhotoTags(List<PhotoTag> photoTags) {
            this.photoTags = photoTags;
        }

        public static class Builder {

            private PhotoEntity photoEntity;

            public Builder() {
                photoEntity = new PhotoEntity();
            }

            public Builder originUrl(String originUrl) {
                photoEntity.originUrl = originUrl;
                return this;
            }

            public Builder title(String title) {
                photoEntity.title = title;
                return this;
            }

            public Builder date(Date shotAt) {
                photoEntity.shotAt = shotAt;
                return this;
            }

            public Builder width(int width) {
                photoEntity.width = width;
                return this;
            }

            public Builder height(int height) {
                photoEntity.height = height;
                return this;
            }

            public Builder coordinates(Coordinates coordinates) {
                photoEntity.coordinates = coordinates;
                return this;
            }

            public Builder locationName(String locationName) {
                photoEntity.locationName = locationName;
                return this;
            }

            public Builder photoTags(List<PhotoTag> photoTags) {
                photoEntity.photoTags = photoTags;
                return this;
            }

            public PhotoEntity build() {
                return photoEntity;
            }

        }
    }

}