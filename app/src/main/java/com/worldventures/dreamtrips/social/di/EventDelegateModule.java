package com.worldventures.dreamtrips.social.di;

import com.worldventures.core.modules.auth.api.command.LogoutAction;
import com.worldventures.dreamtrips.modules.common.delegate.ReplayEventDelegatesWiper;
import com.worldventures.dreamtrips.social.util.event_delegate.DrawerOpenedEventDelegate;
import com.worldventures.dreamtrips.social.util.event_delegate.ImagePresenterClickEventDelegate;
import com.worldventures.dreamtrips.social.util.event_delegate.ScreenChangedEventDelegate;
import com.worldventures.dreamtrips.social.util.event_delegate.SearchFocusChangedDelegate;
import com.worldventures.dreamtrips.social.util.event_delegate.StoryLikedEventDelegate;

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

   @Provides(type = Provides.Type.SET)
   LogoutAction provideReplayEventDelegatesWiperLogoutAction(ReplayEventDelegatesWiper replayEventDelegatesWiper) {
      return replayEventDelegatesWiper::clearReplays;
   }
}
