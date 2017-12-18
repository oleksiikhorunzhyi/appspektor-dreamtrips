package com.worldventures.core.ui.view.fragment;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.worldventures.core.janet.Injector;
import com.worldventures.core.ui.annotations.Layout;

import java.lang.reflect.Field;

import timber.log.Timber;

public final class FragmentHelper {

   private FragmentHelper() {
   }

   public static View onCreateView(LayoutInflater inflater, ViewGroup container, ConfigurableFragment configurableFragment) {
      Layout layout = configurableFragment.getClass().getAnnotation(Layout.class);

      if (layout == null) {
         throw new IllegalArgumentException("ConfigurableFragment should have Layout annotation");
      }

      View rootView = inflater.inflate(layout.value(), container, false);

      configurableFragment.afterCreateView(rootView);

      return rootView;
   }

   public static void inject(Activity activity, Object injectingFragment) {
      if (!(activity instanceof Injector)) {
         throw new IllegalArgumentException("InjectingFragment have to be attached to instance of Injector");
      }

      ((Injector) activity).inject(injectingFragment);
   }

   public static void resetChildFragmentManagerField(Fragment fragment) {
      try {
         Field fragmentManagerField = Fragment.class.getDeclaredField("mChildFragmentManager");
         fragmentManagerField.setAccessible(true);
         fragmentManagerField.set(fragment, null);
      } catch (Exception ex) {
         Timber.e(ex.toString());
      }
   }
}
