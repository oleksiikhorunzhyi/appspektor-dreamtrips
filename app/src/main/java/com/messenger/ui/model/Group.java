package com.messenger.ui.model;

import java.util.Collection;

public class Group<T> {
   public final String groupName;
   public final Collection<T> items;

   public Group(String groupName, Collection<T> items) {
      this.groupName = groupName;
      this.items = items;
   }

   @Override
   public String toString() {
      return "Group{" +
            "groupName='" + groupName + '\'' +
            ", items=" + items +
            '}';
   }
}
