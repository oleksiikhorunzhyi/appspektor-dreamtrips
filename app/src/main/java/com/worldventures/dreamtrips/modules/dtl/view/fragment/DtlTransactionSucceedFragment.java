package com.worldventures.dreamtrips.modules.dtl.view.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.NavigationBuilder;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.common.view.bundle.ShareBundle;
import com.worldventures.dreamtrips.modules.common.view.dialog.ShareDialog;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.dtl.event.CloseDialogEvent;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlace;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlaceMedia;
import com.worldventures.dreamtrips.modules.dtl.model.DtlTransactionResult;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlTransactionSucceedPresenter;

import butterknife.InjectView;
import butterknife.OnClick;
import io.techery.properratingbar.ProperRatingBar;

@Layout(R.layout.fragment_transaction_succeed)
public class DtlTransactionSucceedFragment extends BaseFragmentWithArgs<DtlTransactionSucceedPresenter, DtlPlace>
        implements DtlTransactionSucceedPresenter.View {

    @InjectView(R.id.total)
    TextView total;
    @InjectView(R.id.earned)
    TextView earned;

    @InjectView(R.id.rating_bar)
    ProperRatingBar properRatingBar;

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        properRatingBar.setListener(stars -> getPresenter().rate(stars));
    }

    @Override
    public void setCongratulations(DtlTransactionResult result) {
        total.setText(String.valueOf((int) result.getTotal()));
        earned.setText(String.valueOf((int) result.getEarnedPoints()));
    }

    @OnClick(R.id.share)
    void onShareClicked() {
        getPresenter().share();
    }

    @OnClick(R.id.done)
    void onDoneClicked() {
        //TODO think about dismissing dialog from fragment
        eventBus.post(new CloseDialogEvent());
    }

    @Override
    public void showShareDialog(int amount, DtlPlace place) {
        new ShareDialog(activityRouter.getContext(), type -> {
            ShareBundle shareBundle = new ShareBundle();
            shareBundle.setShareType(type);
            shareBundle.setText(getString(R.string.dtl_details_share_title_earned, amount, place.getName()));
            shareBundle.setShareUrl(place.getWebsite());
            DtlPlaceMedia media = Queryable.from(place.getMediaList()).firstOrDefault();
            if (media != null) shareBundle.setImageUrl(media.getImagePath());
            eventBus.post(new CloseDialogEvent());
            NavigationBuilder.create()
                    .with(activityRouter)
                    .data(shareBundle)
                    .move(Route.SHARE);
        }).show();
    }

    @Override
    protected DtlTransactionSucceedPresenter createPresenter(Bundle savedInstanceState) {
        return new DtlTransactionSucceedPresenter(getArgs());
    }

}
