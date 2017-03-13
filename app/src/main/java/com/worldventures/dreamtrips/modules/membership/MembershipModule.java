package com.worldventures.dreamtrips.modules.membership;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.component.ComponentDescription;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.infopages.presenter.EnrollMemberPresenter;
import com.worldventures.dreamtrips.modules.infopages.view.fragment.staticcontent.EnrollMemberFragment;
import com.worldventures.dreamtrips.modules.infopages.view.fragment.staticcontent.StaticInfoFragment.EnrollMerchantFragment;
import com.worldventures.dreamtrips.modules.membership.presenter.EditTemplatePresenter;
import com.worldventures.dreamtrips.modules.membership.presenter.InvitePresenter;
import com.worldventures.dreamtrips.modules.membership.presenter.MembershipPresenter;
import com.worldventures.dreamtrips.modules.membership.presenter.PodcastsPresenter;
import com.worldventures.dreamtrips.modules.membership.presenter.SelectTemplatePresenter;
import com.worldventures.dreamtrips.modules.membership.view.cell.InviteTemplateCell;
import com.worldventures.dreamtrips.modules.membership.view.cell.MemberCell;
import com.worldventures.dreamtrips.modules.membership.view.cell.PodcastCell;
import com.worldventures.dreamtrips.modules.membership.view.dialog.FilterLanguageDialogFragment;
import com.worldventures.dreamtrips.modules.membership.view.fragment.EditTemplateFragment;
import com.worldventures.dreamtrips.modules.membership.view.fragment.InviteFragment;
import com.worldventures.dreamtrips.modules.membership.view.fragment.MembershipFragment;
import com.worldventures.dreamtrips.modules.membership.view.fragment.PodcastsFragment;
import com.worldventures.dreamtrips.modules.membership.view.fragment.PreviewTemplateFragment;
import com.worldventures.dreamtrips.modules.membership.view.fragment.SelectTemplateFragment;

import dagger.Module;
import dagger.Provides;

@Module(
      injects = {
            MembershipPresenter.class,
            MembershipFragment.class,
            EnrollMemberFragment.class,
            EnrollMerchantFragment.class,
            InviteFragment.class,
            InvitePresenter.class,
            PodcastsFragment.class,
            PodcastsPresenter.class,
            PodcastCell.class,
            MemberCell.class,
            SelectTemplateFragment.class,
            SelectTemplatePresenter.class,
            InviteTemplateCell.class,
            EditTemplateFragment.class,
            EditTemplatePresenter.class,
            EnrollMemberPresenter.class,
            PreviewTemplateFragment.class,
            FilterLanguageDialogFragment.class
      },
      complete = false,
      library = true)
public class MembershipModule {

   @Provides(type = Provides.Type.SET)
   ComponentDescription provideMembershipComponent() {
      return new ComponentDescription(Route.MEMBERSHIP.name(), R.string.membership, R.string.membership, R.drawable.ic_membership, MembershipFragment.class);
   }
}
