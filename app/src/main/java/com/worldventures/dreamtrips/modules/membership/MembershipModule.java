package com.worldventures.dreamtrips.modules.membership;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.component.ComponentDescription;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.infopages.view.fragment.staticcontent.StaticInfoFragment;
import com.worldventures.dreamtrips.modules.membership.api.PhoneContactRequest;
import com.worldventures.dreamtrips.modules.membership.presenter.EditTemplatePresenter;
import com.worldventures.dreamtrips.modules.membership.presenter.InvitePresenter;
import com.worldventures.dreamtrips.modules.membership.presenter.InviteTemplateSelectorPresenter;
import com.worldventures.dreamtrips.modules.membership.presenter.MembershipPresenter;
import com.worldventures.dreamtrips.modules.membership.presenter.PresentationVideosPresenter;
import com.worldventures.dreamtrips.modules.membership.presenter.SelectTemplatePresenter;
import com.worldventures.dreamtrips.modules.membership.view.activity.InviteTemplateSelectorActivity;
import com.worldventures.dreamtrips.modules.membership.view.activity.PreviewTemplateActivity;
import com.worldventures.dreamtrips.modules.membership.view.cell.InviteTemplateCell;
import com.worldventures.dreamtrips.modules.membership.view.cell.MemberCell;
import com.worldventures.dreamtrips.modules.membership.view.cell.TemplatePhotoCell;
import com.worldventures.dreamtrips.modules.membership.view.dialog.FilterLanguageDialogFragment;
import com.worldventures.dreamtrips.modules.membership.view.fragment.EditTemplateFragment;
import com.worldventures.dreamtrips.modules.membership.view.fragment.InviteFragment;
import com.worldventures.dreamtrips.modules.membership.view.fragment.MembershipFragment;
import com.worldventures.dreamtrips.modules.membership.view.fragment.PresentationVideosFragment;
import com.worldventures.dreamtrips.modules.membership.view.fragment.PreviewTemplateFragment;
import com.worldventures.dreamtrips.modules.membership.view.fragment.SelectTemplateFragment;
import com.worldventures.dreamtrips.modules.reptools.view.cell.VideoLanguageCell;
import com.worldventures.dreamtrips.modules.reptools.view.cell.VideoLocaleCell;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
                MembershipPresenter.class,
                PresentationVideosPresenter.class,
                MembershipFragment.class,
                PresentationVideosFragment.class,
                StaticInfoFragment.EnrollFragment.class,
                InviteFragment.class,
                InvitePresenter.class,
                MemberCell.class,
                PhoneContactRequest.class,
                SelectTemplateFragment.class,
                SelectTemplatePresenter.class,
                InviteTemplateCell.class,
                EditTemplateFragment.class,
                EditTemplatePresenter.class,
                InviteTemplateSelectorPresenter.class,
                PreviewTemplateFragment.class,
                TemplatePhotoCell.class,
                VideoLocaleCell.class,
                VideoLanguageCell.class,
                FilterLanguageDialogFragment.class

        },
        complete = false,
        library = true
)
public class MembershipModule {
    @Provides(type = Provides.Type.SET)
    ComponentDescription provideMembershipComponent() {
        return new ComponentDescription(Route.MEMBERSHIP.name(), R.string.membership, R.string.membership, R.drawable.ic_membership, MembershipFragment.class);
    }
}
