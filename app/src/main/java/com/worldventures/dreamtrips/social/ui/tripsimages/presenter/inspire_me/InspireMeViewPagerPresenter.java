package com.worldventures.dreamtrips.social.ui.tripsimages.presenter.inspire_me;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.modules.common.view.viewpager.FragmentItem;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.Inspiration;
import com.worldventures.dreamtrips.social.ui.tripsimages.presenter.BaseImageViewPagerPresenter;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.TripImagesInteractor;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.GetInspireMePhotosCommand;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.GetYSBHPhotosCommand;
import com.worldventures.dreamtrips.social.ui.tripsimages.view.args.InspireMeViewPagerArgs;
import com.worldventures.dreamtrips.social.ui.tripsimages.view.fragment.inspire_me.FullscreenInspireMeFragment;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.helper.ActionStateSubscriber;

public class InspireMeViewPagerPresenter extends BaseImageViewPagerPresenter<BaseImageViewPagerPresenter.View> {

   double randomSeed;
   List<Inspiration> currentItems;

   @Inject TripImagesInteractor tripImagesInteractor;

   public InspireMeViewPagerPresenter(InspireMeViewPagerArgs args) {
      super(args.isLastPageReached(), args.getCurrentItemPosition());
      randomSeed = args.getRandomSeed();
      currentItems = args.getCurrentItems();
   }

   @Override
   public void onViewTaken() {
      super.onViewTaken();
      subscribeToNewItems();
   }

   @Override
   protected void initItems() {
      tripImagesInteractor.inspireMePhotosPipe()
            .createObservableResult(GetInspireMePhotosCommand.cachedCommand())
            .map(Command::getResult)
            .subscribe(items -> {
               currentItems = items;
               super.initItems();
            });
   }

   void subscribeToNewItems() {
      tripImagesInteractor.inspireMePhotosPipe()
            .observe()
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<GetInspireMePhotosCommand>()
                  .onStart(command -> loading = true)
                  .onFail((inspireMePhotosCommand, throwable) -> {
                     loading = false;
                     handleError(inspireMePhotosCommand, throwable);
                  }).onSuccess(inspireMePhotosCommand -> {
                     loading = false;
                     lastPageReached = inspireMePhotosCommand.lastPageReached();
                     currentItems.addAll(inspireMePhotosCommand.getResult());
                     view.setItems(getItems());
                  }));
   }

   @Override
   protected List<FragmentItem> getItems() {
      return Queryable.from(currentItems)
            .map(entity -> new FragmentItem(FullscreenInspireMeFragment.class, "", entity))
            .toList();
   }

   @Override
   protected int getCurrentItemsSize() {
      return currentItems.size();
   }

   @Override
   protected void loadMore() {
      tripImagesInteractor.inspireMePhotosPipe()
            .send(GetInspireMePhotosCommand.forPage(randomSeed, (currentItems.size() / GetYSBHPhotosCommand.PER_PAGE) + 1));
   }
}
