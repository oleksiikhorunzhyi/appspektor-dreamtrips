package com.messenger.messengerservers.model;

import com.messenger.messengerservers.constant.AttachmentType;

public class AttachmentHolder {
    private String type;
    private Attachment item;

    public AttachmentHolder() {
    }

    public static AttachmentHolder newImageAttachment(String url) {
        return new AttachmentHolder(AttachmentType.IMAGE, new ImageAttachment(url));
    }

    public static AttachmentHolder newLocationAttachment(double lat, double lng){
        return new AttachmentHolder(AttachmentType.LOCATION, new LocationAttachment(lat, lng));
    }

    protected AttachmentHolder(@AttachmentType.Type String type, Attachment item) {
        this.type = type;
        this.item = item;
    }

    @AttachmentType.Type
    public String getType() {
        return type;
    }

    public void setType(@AttachmentType.Type String type) {
        this.type = type;
    }

    public Attachment getItem() {
        return item;
    }

    public void setItem(Attachment item) {
        this.item = item;
    }
}
