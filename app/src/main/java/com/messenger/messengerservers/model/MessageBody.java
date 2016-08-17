package com.messenger.messengerservers.model;

import java.util.List;

public class MessageBody {
   private String text;
   private String locale; // we should send locale to support release/1.6.0
   private List<AttachmentHolder> attachments;

   public MessageBody() {
   }

   private MessageBody(Builder builder) {
      this.text = builder.text;
      this.locale = builder.locale;
      this.attachments = builder.attachments;
   }

   public MessageBody(String text, List<AttachmentHolder> attachments) {
      this.text = text;
      this.attachments = attachments;
   }

   public void setText(String text) {
      this.text = text;
   }

   public String getText() {
      return text;
   }

   public void setLocale(String locale) {
      this.locale = locale;
   }

   public String getLocale() {
      return locale;
   }

   public List<AttachmentHolder> getAttachments() {
      return attachments;
   }

   public void setAttachments(List<AttachmentHolder> attachments) {
      this.attachments = attachments;
   }

   public static final class Builder {
      private String text;
      private String locale; // we should send locale to support release/1.6.0
      private int version;
      private List<AttachmentHolder> attachments;

      public Builder() {
      }

      public Builder text(String text) {
         this.text = text;
         return this;
      }

      public Builder locale(String locale) {
         this.locale = locale;
         return this;
      }

      public Builder attachments(List<AttachmentHolder> attachments) {
         this.attachments = attachments;
         return this;
      }

      public MessageBody build() {
         return new MessageBody(this);
      }
   }
}