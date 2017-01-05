package com.techery.spares.module;

import com.techery.spares.utils.delegate.CloseDialogEventDelegate;
import com.techery.spares.utils.delegate.DrawerOpenedEventDelegate;
import com.techery.spares.utils.delegate.EditCommentCloseDelegate;
import com.techery.spares.utils.delegate.EntityDeletedEventDelegate;
import com.techery.spares.utils.delegate.ImagePresenterClickEventDelegate;
import com.techery.spares.utils.delegate.NotificationCountEventDelegate;
import com.techery.spares.utils.delegate.ScreenChangedEventDelegate;
import com.techery.spares.utils.delegate.SearchFocusChangedDelegate;
import com.techery.spares.utils.delegate.StoryLikedEventDelegate;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.delegate.ReplayEventDelegatesWiper;
import com.worldventures.dreamtrips.modules.membership.delegate.MembersSelectedEventDelegate;
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
   CloseDialogEventDelegate provideCloseDialogEventDelegate() {
      return new CloseDialogEventDelegate();
   }

   @Provides
   @Singleton
   EditCommentCloseDelegate provideEditCommentCloseDelegate() {
      return new EditCommentCloseDelegate();
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

   @Provides
   @Singleton
   ReplayEventDelegatesWiper provideReplayEventDelegatesWiper() {
      return new ReplayEventDelegatesWiper();
   }

   @Provides
   @Singleton
   EntityDeletedEventDelegate provideEntityDeletedEventDelegate(ReplayEventDelegatesWiper wiper) {
      return new EntityDeletedEventDelegate(wiper);
   }

   @Provides
   @Singleton
   MembersSelectedEventDelegate provideMembersSelectedEventDelegate(ReplayEventDelegatesWiper wiper) {
      return new MembersSelectedEventDelegate(wiper);
   }
}
