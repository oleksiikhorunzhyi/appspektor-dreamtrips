package com.worldventures.dreamtrips.social.ui.reptools.presenter;

import com.worldventures.dreamtrips.social.util.event_delegate.SearchFocusChangedDelegate;
import com.worldventures.core.model.session.Feature;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.view.viewpager.FragmentItem;
import com.worldventures.dreamtrips.social.ui.reptools.service.analytics.ApptentiveEnrolRepViewedAction;
import com.worldventures.dreamtrips.social.ui.reptools.service.analytics.ApptentiveTrainingVideosViewedAction;
import com.worldventures.dreamtrips.social.ui.reptools.service.analytics.ReptoolsInviteShareAction;
import com.worldventures.dreamtrips.social.ui.reptools.service.analytics.SuccessStoriesViewedAction;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class RepToolsPresenter extends Presenter<RepToolsPresenter.View> {

   private List<FragmentItem> screens;
   @Inject SearchFocusChangedDelegate searchFocusChangedDelegate;

   @Override
   public void onInjected() {
      super.onInjected();
      screens = provideScreens();
   }

   @Override
   public void takeView(View view) {
      super.takeView(view);
      view.setScreens(screens);
      searchFocusChangedDelegate.getObservable()
            .compose(bindViewToMainComposer())
            .subscribe(hasFocus -> view.toggleTabStripVisibility(!hasFocus));
      subscribeToErrorUpdates();
   }

   /**
    * We show single common connection overlay over the tabs content.
    * Subscribe to offline errors to be able to handle those happened in tabs and show it.
    */
   private void subscribeToErrorUpdates() {
      offlineErrorInteractor.offlineErrorCommandPipe()
            .observeSuccess()
            .compose(bindViewToMainComposer())
            .subscribe(command -> reportNoConnection());
   }

   public List<FragmentItem> provideScreens() {
      List<FragmentItem> items = new ArrayList<>();
      items.add(new FragmentItem(Route.TRAINING_VIDEOS, context.getString(R.string.training_videos)));
      items.add(new FragmentItem(Route.ENROLL_REP, context.getString(R.string.rep_enrollment)));
      items.add(new FragmentItem(Route.WV_ADVANTAGE, context.getString(R.string.wv_advantage)));
      items.add(new FragmentItem(Route.SUCCESS_STORY_LIST, context.getString(R.string.success_stories)));
      if (showInvite()) {
         items.add(new FragmentItem(Route.INVITE, context.getString(R.string.invite_and_share)));
      }
      return items;
   }

   public boolean showInvite() {
      return featureManager.available(Feature.REP_TOOLS);
   }

   public void trackState(int position) {
      FragmentItem item = screens.get(position);
      switch (item.route) {
         case TRAINING_VIDEOS:
            analyticsInteractor.analyticsActionPipe().send(new ApptentiveTrainingVideosViewedAction());
            break;
         case ENROLL_REP:
            analyticsInteractor.analyticsActionPipe().send(new ApptentiveEnrolRepViewedAction());
            break;
         case SUCCESS_STORY_LIST:
            analyticsInteractor.analyticsActionPipe().send(new SuccessStoriesViewedAction());
            break;
         case INVITE:
            analyticsInteractor.analyticsActionPipe().send(new ReptoolsInviteShareAction());
            break;
      }
   }

   public interface View extends Presenter.View {
      void setScreens(List<FragmentItem> items);

      void toggleTabStripVisibility(boolean isVisible);
   }
}
