package com.worldventures.dreamtrips.modules.dtl_flow.parts.pilot;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.api_common.error.ErrorResponse;
import com.worldventures.dreamtrips.core.rx.RxBaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.dtl.bundle.ThrstPaymentBundle;
import com.worldventures.dreamtrips.modules.dtl.event.DtlThrstTransactionSucceedEvent;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Merchant;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlThrstThankYouScreenPresenter;
import com.worldventures.dreamtrips.social.ui.activity.SocialComponentActivity;

import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

@Layout(R.layout.include_thank_you_screen)
public class DtlThankYouScreenFragment extends RxBaseFragmentWithArgs<DtlThrstThankYouScreenPresenter, ThrstPaymentBundle> implements DtlThrstThankYouScreenPresenter.View {
   @InjectView(R.id.tv_thank_you_pilot) TextView mThankYouView;
   @InjectView(R.id.tv_payment_status_pilot) TextView mPaymentStatusView;
   @InjectView(R.id.iv_status_payment_pilot) ImageView mPaymentImage;
   @InjectView(R.id.tv_total_charged_text_pilot) TextView mPaymentCharged;
   @InjectView(R.id.tv_total_charged_value_pilot) TextView mMoneyCharged;
   @InjectView(R.id.tv_payment_resume_text_pilot) TextView mPaymentResumeView;
   @InjectView(R.id.tv_payment_sub_thank_you_message_pilot) TextView mSubThankYouMessage;

   private Merchant merchant;

   @Override
   public void onActivityCreated(Bundle savedInstanceState) {
      super.onActivityCreated(savedInstanceState);
      ThrstPaymentBundle thrstPaymentBundle = getArgs();
      merchant = thrstPaymentBundle.getMerchant();
      ((SocialComponentActivity) getActivity()).getSupportActionBar().setTitle(merchant.displayName());
   }

   @Override
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);
   }

   @Override
   protected DtlThrstThankYouScreenPresenter createPresenter(Bundle savedInstanceState) {
      return new DtlThrstThankYouScreenPresenter(getArgs());
   }

   @OnClick(R.id.payment_done_button)
   public void onDoneClick(){
      getPresenter().onDoneClick();
   }

   @Override
   public void goBack(boolean isPaid, String earnedPoints, String totalPoints) {
      if (isPaid) {
         EventBus.getDefault().postSticky(new DtlThrstTransactionSucceedEvent(earnedPoints, totalPoints));
      }
      getActivity().onBackPressed();
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

   private Drawable getDrawableFromResource(int id) { return ContextCompat.getDrawable(getContext(), id);}
}
