package com.worldventures.dreamtrips.wallet.util;


import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

public class NonCopyPastSelectionMode implements ActionMode.Callback {

   @Override
   public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
      return false;
   }

   @Override
   public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
      return false;
   }

   @Override
   public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
      return false;
   }

   @Override
   public void onDestroyActionMode(ActionMode actionMode) {

   }
}
