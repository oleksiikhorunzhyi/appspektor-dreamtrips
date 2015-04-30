package com.worldventures.dreamtrips.modules.membership.model;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.modules.common.model.BaseEntity;

import java.util.Date;

public class History extends BaseEntity {
    @SerializedName("invitation_filled_template_id")
    int templateId;
    @SerializedName("contact")
    String contact;
    @SerializedName("type")
    InviteTemplate.Type type;
    @SerializedName("date")
    Date date;


    public int getTemplateId() {
        return templateId;
    }

    public void setTemplateId(int templateId) {
        this.templateId = templateId;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public InviteTemplate.Type getType() {
        return type;
    }

    public void setType(InviteTemplate.Type type) {
        this.type = type;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
