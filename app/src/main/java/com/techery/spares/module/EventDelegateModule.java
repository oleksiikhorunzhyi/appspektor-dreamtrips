package com.techery.spares.module;

import com.techery.spares.utils.delegate.ImagePresenterClickEventDelegate;
import com.techery.spares.utils.delegate.NotificationCountEventDelegate;
import com.techery.spares.utils.delegate.ScreenChangedEventDelegate;
import com.techery.spares.utils.delegate.SearchFocusChangedDelegate;
import com.techery.spares.utils.delegate.DrawerOpenedEventDelegate;
import com.techery.spares.utils.delegate.StoryLikedEventDelegate;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.trips.delegate.ResetFilterEventDelegate;
import com.worldventures.dreamtrips.modules.trips.delegate.TripFilterEventDelegate;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(library = true, complete = false)
public class EventDelegateModule {

   @Provides
   @Singleton
   SearchFocusChangedDelegate provideSearchFocusChangedDelegate() {
      return new SearchFocusChangedDelegate();
   }

   @Provides
   @Singleton
   ScreenChangedEventDelegate provideScreenChangedDelegate() {
      return new ScreenChangedEventDelegate();
   }

   @Provides
   @Singleton
   StoryLikedEventDelegate provideStoryLikedEventDelegate() {
      return new StoryLikedEventDelegate();
   }

   @Provides
   @Singleton
   NotificationCountEventDelegate provideNotificationCountDelegate() {
      return new NotificationCountEventDelegate();
   }

   @Provides
   @Singleton
   TripFilterEventDelegate provideTripFilterEventDelegate(SnappyRepository snappyRepository) {
      return new TripFilterEventDelegate(snappyRepository);
   }

   @Provides
   @Singleton
   ResetFilterEventDelegate provideResetFilterEventDelegate() {
      return new ResetFilterEventDelegate();
   }

   @Provides
   @Singleton
   ImagePresenterClickEventDelegate provideImagePresenterEventDelegate() {
      return new ImagePresenterClickEventDelegate();
   }

   @Provides
   @Singleton
   DrawerOpenedEventDelegate provideSideNavigationItemPressedDelegate() {
      return new DrawerOpenedEventDelegate();
   }
}
