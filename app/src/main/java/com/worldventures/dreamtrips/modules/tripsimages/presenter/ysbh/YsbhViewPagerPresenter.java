package com.worldventures.dreamtrips.modules.tripsimages.presenter.ysbh;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.common.view.viewpager.FragmentItem;
import com.worldventures.dreamtrips.modules.tripsimages.model.YSBHPhoto;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.BaseImageViewPagerPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.service.TripImagesInteractor;
import com.worldventures.dreamtrips.modules.tripsimages.service.command.GetYSBHPhotosCommand;
import com.worldventures.dreamtrips.modules.tripsimages.view.args.YsbhPagerArgs;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.helper.ActionStateSubscriber;

public class YsbhViewPagerPresenter extends BaseImageViewPagerPresenter<BaseImageViewPagerPresenter.View> {

   List<YSBHPhoto> currentItems;

   @Inject TripImagesInteractor tripImagesInteractor;

   public YsbhViewPagerPresenter(YsbhPagerArgs args) {
      super(args.isLastPageReached(), args.getCurrentItemPosition());
      currentItems = args.getCurrentItems();
   }

   @Override
   public void onViewTaken() {
      super.onViewTaken();
      subscribeToNewItems();
   }

   @Override
   protected void initItems() {
      tripImagesInteractor.ysbhPhotosPipe()
            .createObservableResult(GetYSBHPhotosCommand.cachedCommand())
            .map(Command::getResult)
            .subscribe(items -> {
               currentItems = items;
               super.initItems();
            });
   }

   void subscribeToNewItems() {
      tripImagesInteractor.ysbhPhotosPipe()
            .observe()
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<GetYSBHPhotosCommand>()
                  .onStart(command -> loading = true)
                  .onFail((getYSBHPhotosCommand, throwable) -> {
                     loading = false;
                     handleError(getYSBHPhotosCommand, throwable);
                  })
                  .onSuccess(getYSBHPhotosCommand -> {
                     loading = false;
                     lastPageReached = getYSBHPhotosCommand.lastPageReached();
                     if (getYSBHPhotosCommand.getPage() == 1) currentItems.clear();
                     currentItems.addAll(getYSBHPhotosCommand.getResult());
                     view.setItems(getItems());
                  }));
   }

   @Override
   protected List<FragmentItem> getItems() {
      return Queryable.from(currentItems)
            .map(item -> new FragmentItem(Route.YSBH_FULLSCREEN_IMAGE, "", item))
            .toList();
   }

   @Override
   protected int getCurrentItemsSize() {
      return currentItems.size();
   }

   @Override
   protected void loadMore() {
      tripImagesInteractor.ysbhPhotosPipe()
            .send(GetYSBHPhotosCommand.commandForPage((currentItems.size() / GetYSBHPhotosCommand.PER_PAGE) + 1));
   }
}
