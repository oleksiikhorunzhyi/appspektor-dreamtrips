package com.worldventures.dreamtrips.modules.common.view.dialog;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.worldventures.core.ui.view.fragment.InjectingDialogFragment;

import butterknife.ButterKnife;

public class BaseDialogFragment extends InjectingDialogFragment {

   private final static String TAG = BaseDialogFragment.class.getName();

   public BaseDialogFragment show(FragmentManager manager) {
      this.show(manager, TAG);
      return this;
   }

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      final View rootView = super.onCreateView(inflater, container, savedInstanceState);
      ButterKnife.inject(this, rootView);
      return rootView;
   }

   @Override
   public void show(@NonNull FragmentManager manager, String tag) {
      dismissIfShown(manager, tag);
      super.show(manager, TAG);
   }

   protected void dismissIfShown(FragmentManager fragmentManager) {
      dismissIfShown(fragmentManager, TAG);
   }

   /**
    * Method that detaches fragment by tag if already present.
    * Note: two simultaneous calls want dismiss each other, as fragment want get to manager yet.
    *
    * @param fragmentManager FragmentManager to operate on during transaction
    * @param tag
    */
   protected void dismissIfShown(FragmentManager fragmentManager, String tag) {
      Fragment frag = fragmentManager.findFragmentByTag(tag);
      if (frag != null) {
         if (frag instanceof DialogFragment) {
            ((DialogFragment) frag).dismiss();
            fragmentManager.beginTransaction().remove(frag).commit();
         }
      }
   }
}
