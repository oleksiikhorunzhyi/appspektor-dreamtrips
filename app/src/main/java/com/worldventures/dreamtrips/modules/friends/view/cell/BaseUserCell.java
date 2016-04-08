package com.worldventures.dreamtrips.modules.friends.view.cell;

import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForActivity;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.router.Router;
import com.worldventures.dreamtrips.core.navigation.wrapper.NavigationWrapper;
import com.worldventures.dreamtrips.core.navigation.wrapper.NavigationWrapperFactory;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.view.custom.SmartAvatarView;
import com.worldventures.dreamtrips.modules.friends.bundle.MutualFriendsBundle;
import com.worldventures.dreamtrips.modules.friends.events.UserClickedEvent;
import com.worldventures.dreamtrips.modules.friends.view.util.MutualFriendsUtil;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;

public abstract class BaseUserCell extends AbstractCell<User> {

    @Inject
    Presenter.TabletAnalytic tabletAnalytic;
    @Inject
    FragmentManager fragmentManager;
    @Inject
    Router router;
    @Inject
    @ForActivity
    Provider<Injector> injectorProvider;

    @InjectView(R.id.sdv_avatar)
    SmartAvatarView sdvAvatar;
    @InjectView(R.id.tv_name)
    TextView tvName;
    @InjectView(R.id.tv_mutual)
    TextView tvMutual;
    @InjectView(R.id.tv_company)
    TextView tvCompany;
    @Optional
    @InjectView(R.id.tv_group)
    TextView tvGroup;

    protected MutualFriendsUtil mutualFriendsUtil;

    public BaseUserCell(View view) {
        super(view);
        mutualFriendsUtil = new MutualFriendsUtil(view.getContext());
    }

    @Override
    protected void syncUIStateWithModel() {
        sdvAvatar.setImageURI(Uri.parse(getModelObject().getAvatar().getThumb()));
        sdvAvatar.setup(getModelObject(), injectorProvider.get());
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
        if (!mutualFriendsUtil.hasMutualFriends(getModelObject())) return;
        //
        createActionPanelNavigationWrapper()
                .navigate(Route.MUTUAL_FRIENDS, new MutualFriendsBundle(getModelObject().getId()));
    }

    @OnClick(R.id.sdv_avatar)
    void onUserClicked() {
        getEventBus().post(new UserClickedEvent(getModelObject()));
    }

    protected String createMutualString() {
        return mutualFriendsUtil.createMutualString(getModelObject());
    }

    private NavigationWrapper createActionPanelNavigationWrapper() {
        return new NavigationWrapperFactory().componentOrDialogNavigationWrapper(
                router, fragmentManager, tabletAnalytic
        );
    }
}
