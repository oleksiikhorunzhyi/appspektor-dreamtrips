package com.messenger.messengerservers.model;

import android.support.annotation.Nullable;

import com.messenger.messengerservers.constant.MessageStatus;
import com.messenger.messengerservers.constant.MessageType;

public class Message {
   private String id;
   private String fromId;
   private String toId;
   private String deleted;

   @Nullable private MessageBody messageBody;
   // ms
   private long date;
   private String conversationId;
   private int status;
   private @MessageType.Type String type;

   public Message() {
   }

   public String getId() {
      return id;
   }

   public void setId(String id) {
      this.id = id;
   }

   public String getFromId() {
      return fromId;
   }

   public void setFromId(String fromId) {
      this.fromId = fromId;
   }

   public String getToId() {
      return toId;
   }

   public void setToId(String toId) {
      this.toId = toId;
   }

   @Nullable
   public MessageBody getMessageBody() {
      return messageBody;
   }

   public void setMessageBody(@Nullable MessageBody messageBody) {
      this.messageBody = messageBody;
   }

   public long getDate() {
      return date;
   }

   public void setDate(long date) {
      this.date = date;
   }

   public String getConversationId() {
      return conversationId;
   }

   public void setConversationId(String conversationId) {
      this.conversationId = conversationId;
   }

   @MessageStatus.Status
   public int getStatus() {
      return status;
   }

   public void setStatus(@MessageStatus.Status int status) {
      this.status = status;
   }

   public String getDeleted() {
      return deleted;
   }

   public void setDeleted(String deleted) {
      this.deleted = deleted;
   }

   public String getType() {
      return type;
   }

   public void setType(String type) {
      this.type = type;
   }

   public static final class Builder {
      private Message message;

      public Builder() {
         message = new Message();
      }

      public Builder id(String id) {
         message.setId(id);
         return this;
      }

      public Builder fromId(String fromId) {
         message.setFromId(fromId);
         return this;
      }

      public Builder toId(String toId) {
         message.setToId(toId);
         return this;
      }

      public Builder messageBody(MessageBody messageBody) {
         message.setMessageBody(messageBody);
         return this;
      }

      public Builder date(long date) {
         message.setDate(date);
         return this;
      }

      public Builder conversationId(String conversationId) {
         message.setConversationId(conversationId);
         return this;
      }

      public Builder status(@MessageStatus.Status int status) {
         message.setStatus(status);
         return this;
      }

      public Builder deleted(String deleted) {
         message.setDeleted(deleted);
         return this;
      }

      public Builder type(@MessageType.Type String type) {
         message.setType(type);
         return this;
      }

      public Message build() {
         return message;
      }
   }
}
