package com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews;

import android.content.Context;
import android.support.annotation.StringRes;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.error.ErrorResponse;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlLayout;

import butterknife.InjectView;

/**
 * Created by yair.carreno on 2/1/2017.
 */

public class DtlReviewsScreenImpl extends DtlLayout<DtlReviewsScreen, DtlReviewsPresenter, DtlReviewsPath>
        implements DtlReviewsScreen {

    @InjectView(R.id.toolbar_actionbar) Toolbar toolbar;

    public DtlReviewsScreenImpl(Context context) {
        super(context);
    }

    public DtlReviewsScreenImpl(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onPostAttachToWindowView() {
        inflateToolbarMenu(toolbar);
        toolbar.setTitle("Reviews");
        toolbar.setNavigationIcon(R.drawable.back_icon);
        toolbar.setNavigationOnClickListener(view -> {
            getPresenter().onBackPressed();
            getActivity().onBackPressed();
        });
    }

    @Override
    public boolean isTabletLandscape() {
        return false;
    }

    @Override
    public boolean onApiError(ErrorResponse errorResponse) {
        return false;
    }

    @Override
    public void onApiCallFailed() {

    }

    @Override
    public void informUser(@StringRes int stringId) {

    }

    @Override
    public void informUser(String message) {

    }

    @Override
    public void showBlockingProgress() {

    }

    @Override
    public void hideBlockingProgress() {

    }

    @Override
    public DtlReviewsPresenter createPresenter() {
        return new DtlReviewsPresenterImpl(getContext(), injector);
    }
}
