package com.messenger.messengerservers.model;

public class MessengerUser {
   private boolean online;
   private final String name;
   private String type;

   public MessengerUser(String name) {
      this.name = name;
   }

   public String getName() {
      return name;
   }

   @Deprecated
   public String getId() {
      return name;
   }

   public boolean isOnline() {
      return online;
   }

   public void setOnline(boolean online) {
      this.online = online;
   }

   public String getType() {
      return type;
   }

   public void setType(String type) {
      this.type = type;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      MessengerUser messengerUser = (MessengerUser) o;

      return name != null ? name.equals(messengerUser.name) : messengerUser.name == null;

   }

   @Override
   public int hashCode() {
      return name != null ? name.hashCode() : 0;
   }
}
