package com.worldventures.dreamtrips.modules.dtl.view.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.modules.common.view.bundle.ShareBundle;
import com.worldventures.dreamtrips.modules.common.view.dialog.ShareDialog;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.dtl.event.CloseDialogEvent;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantMedia;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.DtlTransactionResult;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlTransactionSucceedPresenter;

import butterknife.InjectView;
import butterknife.OnClick;
import io.techery.properratingbar.ProperRatingBar;

@Layout(R.layout.fragment_transaction_succeed)
public class DtlTransactionSucceedFragment extends BaseFragmentWithArgs<DtlTransactionSucceedPresenter, DtlMerchant>
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
        properRatingBar.setListener(ratingBar -> getPresenter().rate(ratingBar.getRating()));
    }

    @Override
    public void setCongratulations(DtlTransactionResult result) {
        total.setText(String.valueOf((int) result.getTotal()));
        earned.setText(String.format("+%d", Double.valueOf(result.getEarnedPoints()).intValue()));
    }

    @OnClick(R.id.share)
    void onShareClicked() {
        getPresenter().share();
    }

    @OnClick(R.id.done)
    void onDoneClicked() {
        getPresenter().done();
        //TODO think about dismissing dialog from fragment
        eventBus.post(new CloseDialogEvent());
    }

    @Override
    public void showShareDialog(int amount, DtlMerchant merchant) {
        new ShareDialog(activityRouter.getContext(), type -> {
            getPresenter().trackSharing(type);
            ShareBundle shareBundle = new ShareBundle();
            shareBundle.setShareType(type);
            shareBundle.setText(getString(R.string.dtl_details_share_title_earned, amount, merchant.getDisplayName()));
            shareBundle.setShareUrl(merchant.getWebsite());
            DtlMerchantMedia media = Queryable.from(merchant.getImages()).firstOrDefault();
            if (media != null) shareBundle.setImageUrl(media.getImagePath());
            router.moveTo(Route.SHARE, NavigationConfigBuilder.forActivity()
                    .data(shareBundle)
                    .build());
        }).show();
    }

    @Override
    protected DtlTransactionSucceedPresenter createPresenter(Bundle savedInstanceState) {
        return new DtlTransactionSucceedPresenter(getArgs());
    }

}
