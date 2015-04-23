package com.worldventures.dreamtrips.modules.membership.view;

import android.os.Bundle;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.membership.presenter.InvitePresenter;

@Layout(R.layout.fragment_invite)
public class InviteFragment extends BaseFragment<InvitePresenter> {
    @Override
    protected InvitePresenter createPresenter(Bundle savedInstanceState) {
        return new InvitePresenter(this);
    }
}
