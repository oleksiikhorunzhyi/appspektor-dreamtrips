package com.worldventures.dreamtrips.modules.tripsimages.view.fragment.temp;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.view.dialog.ShareDialog;
import com.worldventures.dreamtrips.modules.trips.event.TripImageAnalyticEvent;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.Inspiration;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen.InspirationFullscreenPresenter;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.fragment_fullscreen_inspiration_photo)
public class InspirePhotoFullscreenFragment extends FullScreenPhotoFragment<InspirationFullscreenPresenter, Inspiration> implements InspirationFullscreenPresenter.View {

    @InjectView(R.id.tv_description)
    TextView tvDescription;
    @InjectView(R.id.tv_see_more)
    TextView tvSeeMore;
    @InjectView(R.id.textViewInspireMeTitle)
    TextView textViewInspireMeTitle;
    @InjectView(R.id.iv_share)
    ImageView ivShare;
    @InjectView(R.id.actionPanel)
    LinearLayout actionContainer;

    @Override
    protected InspirationFullscreenPresenter createPresenter(Bundle savedInstanceState) {
        return new InspirationFullscreenPresenter((Inspiration) getArgs().getPhoto(), getArgs().getType());
    }

    @Override
    public void setContent(IFullScreenObject photo) {
        super.setContent(photo);
        tvDescription.setText(photo.getFSDescription());
        textViewInspireMeTitle.setText("- " + photo.getFSTitle());
    }

    @OnClick(R.id.iv_share)
    public void actionShare() {
        eventBus.post(new TripImageAnalyticEvent(getArgs().getPhoto().getFSId(), TrackingHelper.ATTRIBUTE_SHARE_IMAGE));
        new ShareDialog(getActivity(), type -> {
            getPresenter().onShare(type);
        }).show();
    }

    @OnClick(R.id.tv_see_more)
    protected void actionSeeMore() {
        actionContainer.setVisibility(View.VISIBLE);
        tvDescription.setSingleLine(false);

        tvSeeMore.setVisibility(View.GONE);
        if (tvDescription.getText().length() == 0) {
            tvDescription.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.actionPanel, R.id.description_container})
    public void actionSeeLess() {
        actionContainer.setVisibility(View.INVISIBLE);
        tvDescription.setSingleLine(true);
        tvDescription.setVisibility(View.VISIBLE);
        tvSeeMore.setVisibility(View.VISIBLE);
    }

}
