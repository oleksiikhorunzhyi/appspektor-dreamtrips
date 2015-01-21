package com.worldventures.dreamtrips.core.model;

public class BucketItem extends BaseEntity {
    String type;
    String created;
    String name;
    String img_uri;
    String description;
    String featured;
    String frequency;
    String globalLikesCount;
    String globalRatingCount;
    String globalRatingCountAverage;
    String globalSaveCount;
    String globalSharesCount;
    String language;
    String nodeId;

    public String getPublisherContent() {
        return publisherContent;
    }

    public void setPublisherContent(String publisherContent) {
        this.publisherContent = publisherContent;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg_uri() {
        return img_uri;
    }

    public void setImg_uri(String img_uri) {
        this.img_uri = img_uri;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFeatured() {
        return featured;
    }

    public void setFeatured(String featured) {
        this.featured = featured;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getGlobalLikesCount() {
        return globalLikesCount;
    }

    public void setGlobalLikesCount(String globalLikesCount) {
        this.globalLikesCount = globalLikesCount;
    }

    public String getGlobalRatingCount() {
        return globalRatingCount;
    }

    public void setGlobalRatingCount(String globalRatingCount) {
        this.globalRatingCount = globalRatingCount;
    }

    public String getGlobalRatingCountAverage() {
        return globalRatingCountAverage;
    }

    public void setGlobalRatingCountAverage(String globalRatingCountAverage) {
        this.globalRatingCountAverage = globalRatingCountAverage;
    }

    public String getGlobalSaveCount() {
        return globalSaveCount;
    }

    public void setGlobalSaveCount(String globalSaveCount) {
        this.globalSaveCount = globalSaveCount;
    }

    public String getGlobalSharesCount() {
        return globalSharesCount;
    }

    public void setGlobalSharesCount(String globalSharesCount) {
        this.globalSharesCount = globalSharesCount;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    String publisherContent;


}
