package com.worldventures.dreamtrips.modules.bucketlist.presenter;

import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.modules.bucketlist.service.BucketInteractor;
import com.worldventures.dreamtrips.modules.bucketlist.service.command.BucketListCommand;
import com.worldventures.dreamtrips.modules.bucketlist.service.command.GetCategoriesCommand;
import com.worldventures.dreamtrips.modules.bucketlist.service.command.RecentlyAddedBucketsFromPopularCommand;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import io.techery.janet.ActionPipe;
import io.techery.janet.Command;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.Observable;

import static com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem.BucketType;
import static com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem.BucketType.ACTIVITY;
import static com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem.BucketType.DINING;
import static com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem.BucketType.LOCATION;

public class BucketTabsPresenter extends Presenter<BucketTabsPresenter.View> {
   @Inject SnappyRepository db;

   @Inject BucketInteractor bucketInteractor;

   @Override
   public void takeView(View view) {
      super.takeView(view);
      setTabs();
      loadCategories();
      loadBucketList();
   }

   @Override
   public void onResume() {
      super.onResume();

      view.bind(recentTabCountObservable().map(Command::getResult)).subscribe(bucketTypeListPair -> {
         view.setRecentBucketItemCountByType(bucketTypeListPair.first, bucketTypeListPair.second.size());
      });
   }

   @Override
   public void dropView() {
      super.dropView();
      db.saveOpenBucketTabType(null);
   }

   private void loadCategories() {
      bucketInteractor.getCategoriesPipe()
            .createObservable(new GetCategoriesCommand())
            .compose(bindView())
            .subscribe(new ActionStateSubscriber<GetCategoriesCommand>()
                  .onSuccess(getCategoriesCommand -> db.putList(SnappyRepository.CATEGORIES,
                        getCategoriesCommand.getResult()))
                  .onFail(this::handleError)
            );
   }

   private void loadBucketList() {
      bucketInteractor.bucketListActionPipe()
            .createObservable(BucketListCommand.fetch(getUser().getId(), false))
            .compose(bindViewToMainComposer())
            .concatMap(state -> state.action.isFromCache() ? bucketInteractor.bucketListActionPipe()
                  .createObservable(BucketListCommand.fetch(getUser().getId(), true)) : Observable.just(state))
            .subscribe(new ActionStateSubscriber<BucketListCommand>()
                  .onFail(this::handleError));
   }

   public void setTabs() {
      view.setTypes(Arrays.asList(LOCATION, ACTIVITY, DINING));
      view.updateSelection();
   }

   public void onTabChange(BucketType type) {
      bucketInteractor.recentlyAddedBucketsFromPopularCommandPipe()
            .send(RecentlyAddedBucketsFromPopularCommand.clear(type));

      db.saveOpenBucketTabType(type.name());
   }

   private Observable<RecentlyAddedBucketsFromPopularCommand> recentTabCountObservable() {
      ActionPipe<RecentlyAddedBucketsFromPopularCommand> recentPipe = bucketInteractor.recentlyAddedBucketsFromPopularCommandPipe();
      return Observable.merge(recentPipe.createObservableResult(RecentlyAddedBucketsFromPopularCommand.get(LOCATION)), recentPipe
            .createObservableResult(RecentlyAddedBucketsFromPopularCommand.get(ACTIVITY)), recentPipe.createObservableResult(RecentlyAddedBucketsFromPopularCommand
            .get(DINING)));
   }

   protected User getUser() {
      return getAccount();
   }

   public interface View extends RxView {
      void setTypes(List<BucketType> type);

      void setRecentBucketItemCountByType(BucketType type, int count);

      void updateSelection();
   }
}
