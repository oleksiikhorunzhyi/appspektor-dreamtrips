package com.messenger.ui.model;

import com.messenger.entities.DataUser;

public class SelectableDataUser {
   private DataUser dataUser;
   private boolean selected;
   private boolean selectionEnabled;

   public SelectableDataUser() {
   }

   public SelectableDataUser(DataUser dataUser, boolean selected, boolean selectionEnabled) {
      this.selected = selected;
      this.dataUser = dataUser;
      this.selectionEnabled = selectionEnabled;
   }

   public DataUser getDataUser() {
      return dataUser;
   }

   public void setDataUser(DataUser dataUser) {
      this.dataUser = dataUser;
   }

   public boolean isSelected() {
      return selected;
   }

   public void setSelected(boolean selected) {
      this.selected = selected;
   }

   public boolean isSelectionEnabled() {
      return selectionEnabled;
   }
}
