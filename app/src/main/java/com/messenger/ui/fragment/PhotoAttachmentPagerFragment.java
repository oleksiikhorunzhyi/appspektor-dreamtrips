package com.messenger.ui.fragment;

import android.os.Bundle;

import com.innahema.collections.query.queriables.Queryable;
import com.messenger.entities.PhotoAttachment;
import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.common.view.viewpager.FragmentItem;
import com.worldventures.dreamtrips.social.ui.tripsimages.presenter.BaseImageViewPagerPresenter;
import com.worldventures.dreamtrips.social.ui.tripsimages.view.fragment.BaseImageViewPagerFragment;

import java.util.List;

@Layout(R.layout.fragment_full_screen_photo_wrapper)
public class PhotoAttachmentPagerFragment extends BaseImageViewPagerFragment<PhotoAttachmentPagerFragment.Presenter, PhotoAttachmentPagerArgs> {

   @Override
   protected Presenter createPresenter(Bundle savedInstanceState) {
      return new Presenter(true, getArgs().getCurrentItemPosition(), getArgs().getCurrentItems());
   }

   public static class Presenter extends BaseImageViewPagerPresenter<BaseImageViewPagerPresenter.View> {
      private List<PhotoAttachment> photoAttachments;

      public Presenter(boolean lastPageReached, int selectedPosition, List<PhotoAttachment> photoAttachments) {
         super(lastPageReached, selectedPosition);
         this.photoAttachments = photoAttachments;
      }

      @Override
      protected List<FragmentItem> getItems() {
         return Queryable.from(photoAttachments)
               .map(entity -> new FragmentItem(Route.MESSAGE_IMAGE_FULLSCREEN, "", entity))
               .toList();
      }
   }
}
