package com.worldventures.dreamtrips.modules.feed.view.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.feed.bundle.EditEntityBundle;
import com.worldventures.dreamtrips.modules.feed.presenter.EditEntityPresenter;

import butterknife.InjectView;

@Layout(R.layout.layout_post)
public class EditEntityFragment extends ActionEntityFragment<EditEntityPresenter, EditEntityBundle> {

    @InjectView(R.id.cancel_action)
    ImageView cancel;

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        cancel.setVisibility(View.GONE);
    }

    @Override
    protected int getPostButtonText() {
        return R.string.update;
    }

    @Override
    protected Route getRoute() {
        return Route.ENTITY_EDIT;
    }

    @Override
    protected EditEntityPresenter createPresenter(Bundle savedInstanceState) {
        return new EditEntityPresenter(getArgs().getEntity(), getArgs().getType());
    }
}
