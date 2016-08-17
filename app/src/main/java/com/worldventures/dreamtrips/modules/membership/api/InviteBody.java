package com.worldventures.dreamtrips.modules.membership.api;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.modules.membership.model.InviteTemplate;

import java.util.List;

public class InviteBody {
   private static final String EMAIL = "email";
   private static final String SMS = "sms";

   @SerializedName("filled_template_id") int templateId;
   @SerializedName("type") String type;
   @SerializedName("contacts") List<String> contacts;


   public int getTemplateId() {
      return templateId;
   }

   public void setTemplateId(int templateId) {
      this.templateId = templateId;
   }

   public String getType() {
      return type;
   }

   public void setType(InviteTemplate.Type type) {
      this.type = type.name().toLowerCase();
   }

   public List<String> getContacts() {
      return contacts;
   }

   public void setContacts(List<String> contacts) {
      this.contacts = contacts;
   }
}
