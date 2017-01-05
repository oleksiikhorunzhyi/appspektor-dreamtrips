package com.worldventures.dreamtrips.modules.common.view.fragment;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.view.View;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.BackStackDelegate;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.modules.common.presenter.MediaPickerPresenter;
import com.worldventures.dreamtrips.modules.common.view.bundle.PickerBundle;
import com.worldventures.dreamtrips.modules.common.view.custom.PhotoPickerLayout;

import javax.inject.Inject;

import butterknife.InjectView;
import timber.log.Timber;

@Layout(R.layout.fragment_picker)
public class MediaPickerFragment extends BaseFragmentWithArgs<MediaPickerPresenter, PickerBundle> implements MediaPickerPresenter.View {

   private static final int DEFAULT_CONTAINER_ID = R.id.picker_container;

   @Inject BackStackDelegate backStackDelegate;

   @InjectView(R.id.photo_picker) PhotoPickerLayout photoPickerLayout;
   @InjectView(R.id.transparent_view) View transparentView;

   @Override
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);
      rootView.setClickable(false);
      inject(photoPickerLayout);
      setupPicker();
      boolean multipickEnabled = false;
      int pickLimit = 1;
      if (getArgs() != null) {
         multipickEnabled = getArgs().isMultipickEnabled();
         pickLimit = getArgs().getPickLimit();
      }
      photoPickerLayout.showPanel(multipickEnabled, pickLimit);
      photoPickerLayout.setPhotoPickerListener(new PhotoPickerLayout.PhotoPickerListener() {
         @Override
         public void onClosed() {
         }

         @Override
         public void onOpened() {
         }
      });
      photoPickerLayout.setTransparentView(transparentView);

      photoPickerLayout.setOnDoneClickListener((chosenImages, type) -> getPresenter().attachImages(chosenImages, type));
   }

   @Override
   public void onDestroyView() {
      super.onDestroyView();
      if (photoPickerLayout != null) {
         photoPickerLayout.setOnDoneClickListener(null);
      }
   }

   @Override
   public void onResume() {
      super.onResume();
      backStackDelegate.setListener(this::onBackPressed);
   }

   @Override
   public void onPause() {
      super.onPause();
      backStackDelegate.setListener(null);
   }

   @Override
   protected MediaPickerPresenter createPresenter(Bundle savedInstanceState) {
      return new MediaPickerPresenter(getArgs().getRequestId());
   }

   private boolean onBackPressed() {
      if (getChildFragmentManager().getBackStackEntryCount() > 1) {
         getChildFragmentManager().popBackStack();
         return true;
      }
      return back();
   }

   private void setupPicker() {
      photoPickerLayout.setup(getChildFragmentManager());
   }

   @Override
   public boolean back() {
      // TODO Revise operations with stack and binding to view when returning processed images from presenter
      try {
         photoPickerLayout.hidePanel();
         @IdRes int containerId = (getView() != null && getView().getParent() != null) ? ((View) getView().getParent()).getId() : DEFAULT_CONTAINER_ID;
         router.moveTo(Route.MEDIA_PICKER, NavigationConfigBuilder.forRemoval()
               .containerId(containerId)
               .fragmentManager(getFragmentManager())
               .build());
      } catch (Exception ex) {
         Timber.w(ex, "Could not hide photo picker");
      }
      return true;
   }
}
