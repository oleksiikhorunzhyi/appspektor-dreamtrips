package com.worldventures.dreamtrips.modules.dtl_flow.parts.pilot;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlLayout;

import butterknife.InjectView;
import butterknife.OnClick;
import flow.Flow;

public class DtlPaymentScreenImpl extends DtlLayout<DtlPaymentScreen, DtlPaymentPresenter, DtlPaymentPath>
        implements DtlPaymentScreen {

    @InjectView(R.id.toolbar_actionbar) Toolbar toolbar;
    @InjectView(R.id.emptyView) View emptyView;
    @InjectView(R.id.errorView) View errorView;

    @InjectView(R.id.tv_thank_you_pilot) TextView mThankYouView;
    @InjectView(R.id.tv_payment_status_pilot) TextView mPaymentStatusView;
    @InjectView(R.id.iv_status_payment_pilot) ImageView mPaymentImage;
    @InjectView(R.id.tv_total_charged_text_pilot) TextView mPaymentCharged;
    @InjectView(R.id.tv_total_charged_value_pilot) TextView mMoneyCharged;
    @InjectView(R.id.tv_payment_resume_text_pilot) TextView mPaymentResumeView;
    @InjectView(R.id.tv_payment_sub_thank_you_message_pilot) TextView mSubThankYouMessage;

    public DtlPaymentScreenImpl(Context context) {
        super(context);
    }

    public DtlPaymentScreenImpl(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public DtlPaymentPresenter createPresenter() {
        return new DtlPaymentPresenterImpl(getContext(), getPath().isPaid(), getPath().getTotalAmount());
    }

    @Override
    public void initToolbar(){
        inflateToolbarMenu(toolbar);
        toolbar.setNavigationIcon(R.drawable.back_icon);
        toolbar.setNavigationOnClickListener(view ->
              getPresenter().onBackPressed());
        toolbar.setTitle(getPath().getMerchantName());
    }

    @OnClick(R.id.payment_done_button)
    public void onDoneClick(){
        getPresenter().onBackPressed();
    }

    @Override
    public void goBack() {
        Flow.get(getContext()).goBack();
    }

    @Override
    public void thankYouSuccessfulText() {
        mThankYouView.setText(getTextFromResource(R.string.thank_you_thrst_pilot));
    }

    @Override
    public void thankYouFailureText() {
        mThankYouView.setText(getTextFromResource(R.string.first_failure_text_thrst_pilot));
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
    public void showTotalChargedText() {
    }

    @Override
    public void hideTotalChargedText() {
    }

    @Override
    public void setSuccessResume() {
        mPaymentCharged.setText(getTextFromResource(R.string.total_amount_charged_pilot));
    }

    @Override
    public void setFailureResume() {
        mPaymentCharged.setText(getTextFromResource(R.string.payment_amount_due_pilot));
    }

    @Override
    public void setChargeMoney(String money){
        mMoneyCharged.setText(String.format(getTextFromResource(R.string.payment_money_charged), money));
    }

    @Override
    public void setPaymentSuccessImage() {
        mPaymentImage.setImageDrawable(getDrawableFromResource(R.drawable.check_succes_pilot));
    }

    @Override
    public void setPaymentFailureImage() {
       mPaymentImage.setImageDrawable(getDrawableFromResource(R.drawable.check_error_pilot));
    }

    @Override
    public void setShowScreenSuccessMessage() {
        mPaymentResumeView.setText(getTextFromResource(R.string.payment_resume_success_pilot));
    }

    @Override
    public void setShowScreenFailureMessage() {
        mPaymentResumeView.setText(getTextFromResource(R.string.payment_resume_failure_pilot));
    }

    @Override
    public void showSubThankYouMessage() {
        mSubThankYouMessage.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideSubThankYouMessage() {
        mSubThankYouMessage.setVisibility(View.GONE);
    }

    private String getTextFromResource(int id) { return getContext().getString(id);}

    private Drawable getDrawableFromResource(int id) { return getContext().getDrawable(id);}
}