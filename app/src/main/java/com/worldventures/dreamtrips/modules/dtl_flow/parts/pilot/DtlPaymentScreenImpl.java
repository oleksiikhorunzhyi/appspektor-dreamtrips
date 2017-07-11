package com.worldventures.dreamtrips.modules.dtl_flow.parts.pilot;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.error.ErrorResponse;
import com.worldventures.dreamtrips.modules.common.view.custom.ImageryDraweeView;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlLayout;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.detailReview.DtlDetailReviewPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.detailReview.DtlDetailReviewPresenter;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.detailReview.DtlDetailReviewPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.detailReview.DtlDetailReviewScreen;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.adapter.ReviewImagesAdapter;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.model.CSTConverter;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.model.ReviewObject;

import java.text.ParseException;

import butterknife.InjectView;
import cn.pedant.SweetAlert.SweetAlertDialog;
import flow.Flow;

public class DtlPaymentScreenImpl extends DtlLayout<DtlPaymentScreen, DtlPaymentPresenter, DtlPaymentPath>
        implements DtlPaymentScreen {

    @InjectView(R.id.toolbar_actionbar) Toolbar toolbar;
    @InjectView(R.id.emptyView) View emptyView;
    @InjectView(R.id.errorView) View errorView;

    @InjectView(R.id.tv_thank_you_pilot) TextView mThankYouView;
    @InjectView(R.id.tv_payment_status_pilot) TextView mPaymentStatusView;
    @InjectView(R.id.tv_total_charged_value_pilot) TextView mPaymentValueView;
    @InjectView(R.id.tv_total_charged_text_pilot) TextView mPaymentTotalChargedView;
    @InjectView(R.id.tv_payment_resume_text_pilot) TextView mPaymentResumeView;
    @InjectView(R.id.iv_status_payment_pilot) ImageView mPaymentImage;

    public DtlPaymentScreenImpl(Context context) {
        super(context);
    }

    public DtlPaymentScreenImpl(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public DtlPaymentPresenter createPresenter() {
        return new DtlPaymentPresenterImpl(getContext(), getPath().isPaid());
    }

    @Override
    protected void onPostAttachToWindowView() {
        inflateToolbarMenu(toolbar);
        toolbar.setNavigationIcon(R.drawable.back_icon);
        toolbar.setNavigationOnClickListener(view ->
                Flow.get(getContext()).goBack());
        //toolbar.setTitle(getPath().getMerchant());
        if (getPath().isPaid()) {
            showThankYouText();
            setSuccessPaymentText();
            setPaymentSuccessImage();
            setPaymentValue("15.59");
            showTotalChargedText();
            setSuccessResume("31");
        } else {
            hideThankYouText();
            setFailurePaymentText();
            setPaymentFailureImage();
            setPaymentValue("15.59");
            hideTotalChargedText();
            setFailureResume();
        }
    }

    @Override
    public void hideThankYouText() {
        mThankYouView.setVisibility(View.GONE);
    }

    @Override
    public void showThankYouText() {
        mThankYouView.setVisibility(View.VISIBLE);
    }

    @Override
    public void setSuccessPaymentText() {
        mPaymentStatusView.setText(getTextFromResource(R.string.payment_success_status_pilot));
    }

    @Override
    public void setFailurePaymentText() {
        mPaymentStatusView.setText(getTextFromResource(R.string.payment_error_status_pilot));
    }

    @Override
    public void setPaymentValue(@NonNull String value) {
        mPaymentValueView.setText(value);
    }

    @Override
    public void showTotalChargedText() {
        mPaymentTotalChargedView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideTotalChargedText() {
        mPaymentTotalChargedView.setVisibility(View.GONE);
    }

    @Override
    public void setSuccessResume(String points) {
        String text = String.format(getTextFromResource(R.string.payment_resume_success_pilot), points);
        mPaymentResumeView.setText(text);
    }

    @Override
    public void setFailureResume() {
        mPaymentResumeView.setText(getTextFromResource(R.string.payment_resume_failure_pilot));
    }

    @Override
    public void setPaymentSuccessImage() {
        mPaymentImage.setImageDrawable(getDrawableFromResource(R.drawable.ic_action_accept));
    }

    @Override
    public void setPaymentFailureImage() {
        mPaymentImage.setImageDrawable(getDrawableFromResource(R.drawable.ic_action_cancel));
    }

    private String getTextFromResource(int id) { return getContext().getString(id);}

    private Drawable getDrawableFromResource(int id) { return getContext().getDrawable(id);}
}