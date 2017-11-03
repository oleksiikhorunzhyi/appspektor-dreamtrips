package com.worldventures.dreamtrips.social.ui.reptools.presenter;

import com.techery.spares.utils.delegate.SearchFocusChangedDelegate;
import com.worldventures.core.model.session.Feature;
import com.worldventures.dreamtrips.R;

import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.view.viewpager.FragmentItem;
import com.worldventures.dreamtrips.social.ui.infopages.view.fragment.staticcontent.EnrollRepFragment;
import com.worldventures.dreamtrips.social.ui.infopages.view.fragment.staticcontent.WVAdvantageFragment;
import com.worldventures.dreamtrips.social.ui.membership.view.fragment.InviteFragment;
import com.worldventures.dreamtrips.social.ui.reptools.service.analytics.ApptentiveEnrolRepViewedAction;
import com.worldventures.dreamtrips.social.ui.reptools.service.analytics.ApptentiveTrainingVideosViewedAction;
import com.worldventures.dreamtrips.social.ui.reptools.service.analytics.ReptoolsInviteShareAction;
import com.worldventures.dreamtrips.social.ui.reptools.service.analytics.SuccessStoriesViewedAction;
import com.worldventures.dreamtrips.social.ui.reptools.view.fragment.SuccessStoryListFragment;
import com.worldventures.dreamtrips.social.ui.reptools.view.fragment.TrainingVideosFragment;

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
      items.add(new FragmentItem(TrainingVideosFragment.class, context.getString(R.string.training_videos)));
      items.add(new FragmentItem(EnrollRepFragment.class, context.getString(R.string.rep_enrollment)));
      items.add(new FragmentItem(WVAdvantageFragment.class, context.getString(R.string.wv_advantage)));
      items.add(new FragmentItem(SuccessStoryListFragment.class, context.getString(R.string.success_stories)));
      if (showInvite()) {
         items.add(new FragmentItem(InviteFragment.class, context.getString(R.string.invite_and_share)));
      }
      return items;
   }

   public boolean showInvite() {
      return featureManager.available(Feature.REP_TOOLS);
   }

   public void trackState(int position) {
      FragmentItem item = screens.get(position);
      if (item.getFragmentClazz().equals(TrainingVideosFragment.class)) {
         analyticsInteractor.analyticsActionPipe().send(new ApptentiveTrainingVideosViewedAction());
      } else if (item.getFragmentClazz().equals(EnrollRepFragment.class)) {
         analyticsInteractor.analyticsActionPipe().send(new ApptentiveEnrolRepViewedAction());
      } else if (item.getFragmentClazz().equals(SuccessStoryListFragment.class)) {
         analyticsInteractor.analyticsActionPipe().send(new SuccessStoriesViewedAction());
      } else if (item.getFragmentClazz().equals(InviteFragment.class)) {
         analyticsInteractor.analyticsActionPipe().send(new ReptoolsInviteShareAction());
      }
   }

   public interface View extends Presenter.View {
      void setScreens(List<FragmentItem> items);

      void toggleTabStripVisibility(boolean isVisible);
   }
}
