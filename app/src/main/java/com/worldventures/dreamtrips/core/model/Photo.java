package com.worldventures.dreamtrips.core.model;


import java.util.List;

public class Photo extends BaseEntity {
    String title;
    int userId;
    String shotAt;
    String locationName;
    Coordinate coordinates;
    List<String> tags;
    Image url;
    boolean liked;
    int likeCount;

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Coordinate getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinate coordinates) {
        this.coordinates = coordinates;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getShotAt() {
        return shotAt;
    }

    public void setShotAt(String shotAt) {
        this.shotAt = shotAt;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Image getUrl() {
        return url;
    }

    public void setUrl(Image url) {
        this.url = url;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    @Override
    public String toString() {
        return "{" +
                "title='" + title + '\'' +
                ", userId=" + userId +
                ", shotAt='" + shotAt + '\'' +
                ", locationName='" + locationName + '\'' +
                ", coordinates=" + coordinates +
                ", tags=" + tags +
                ", url=" + url +
                '}';
    }
}
