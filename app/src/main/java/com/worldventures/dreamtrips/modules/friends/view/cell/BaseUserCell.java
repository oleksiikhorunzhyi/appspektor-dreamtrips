package com.worldventures.dreamtrips.modules.friends.view.cell;

import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.core.navigation.FragmentCompass;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.wrapper.NavigationWrapper;
import com.worldventures.dreamtrips.core.navigation.wrapper.NavigationWrapperFactory;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.friends.bundle.MutualFriendsBundle;
import com.worldventures.dreamtrips.modules.friends.events.UserClickedEvent;
import com.worldventures.dreamtrips.modules.friends.view.util.MutualStringUtil;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;

public abstract class BaseUserCell extends AbstractCell<User> {

    @Inject
    Presenter.TabletAnalytic tabletAnalytic;
    @Inject
    FragmentCompass fragmentCompass;
    @Inject
    ActivityRouter activityRouter;

    @InjectView(R.id.sdv_avatar)
    SimpleDraweeView sdvAvatar;
    @InjectView(R.id.tv_name)
    TextView tvName;
    @InjectView(R.id.tv_mutual)
    TextView tvMutual;
    @InjectView(R.id.tv_company)
    TextView tvCompany;
    @Optional
    @InjectView(R.id.tv_group)
    TextView tvGroup;

    protected MutualStringUtil mutualStringUtil;

    public BaseUserCell(View view) {
        super(view);
        mutualStringUtil = new MutualStringUtil(view.getContext());
    }

    @Override
    protected void syncUIStateWithModel() {
        sdvAvatar.setImageURI(Uri.parse(getModelObject().getAvatar().getThumb()));
        sdvAvatar.invalidate(); // workaround for samsung devices
        tvName.setText(getModelObject().getFullName());

        String mutual = createMutualString();
        tvMutual.setVisibility(TextUtils.isEmpty(mutual) ? View.GONE : View.VISIBLE);
        tvMutual.setText(mutual);

        String companyName = getModelObject().getCompany();
        tvCompany.setVisibility(TextUtils.isEmpty(companyName) ? View.GONE : View.VISIBLE);
        tvCompany.setText(companyName);
    }

    @Override
    public void prepareForReuse() {

    }

    @OnClick(R.id.tv_mutual)
    void onMutualClick() {
        createActionPanelNavigationWrapper().navigate(Route.MUTUAL_FRIENDS, new MutualFriendsBundle(getModelObject().getId()));
    }

    @OnClick(R.id.sdv_avatar)
    void onUserClicked() {
        getEventBus().post(new UserClickedEvent(getModelObject()));
    }

    protected String createMutualString() {
        return mutualStringUtil.createMutualString(getModelObject());
    }

    private NavigationWrapper createActionPanelNavigationWrapper() {
        return new NavigationWrapperFactory().componentOrDialogNavigationWrapper(
                activityRouter, fragmentCompass, tabletAnalytic
        );
    }
}
