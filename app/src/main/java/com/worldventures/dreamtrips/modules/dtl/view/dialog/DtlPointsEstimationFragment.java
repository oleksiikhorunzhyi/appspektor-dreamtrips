package com.worldventures.dreamtrips.modules.dtl.view.dialog;

import android.os.Bundle;
import android.support.annotation.StringRes;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.service.DialogNavigatorInteractor;
import com.worldventures.dreamtrips.core.navigation.service.command.CloseDialogCommand;
import com.worldventures.dreamtrips.core.rx.RxBaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.common.view.util.TextWatcherAdapter;
import com.worldventures.dreamtrips.modules.dtl.bundle.PointsEstimationDialogBundle;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.Currency;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlPointsEstimationPresenter;
import com.worldventures.dreamtrips.modules.dtl.view.custom.CurrencyDTEditText;

import java.util.Locale;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.fragment_dtl_points_estimation)
public class DtlPointsEstimationFragment extends RxBaseFragmentWithArgs<DtlPointsEstimationPresenter, PointsEstimationDialogBundle> implements DtlPointsEstimationPresenter.View {

   @InjectView(R.id.inputPoints) CurrencyDTEditText inputPoints;
   @InjectView(R.id.calculateButton) Button calculateButton;
   @InjectView(R.id.pointsEstimated) TextView pointsEstimated;
   @InjectView(R.id.progressBar) ProgressBar progressBar;
   @InjectView(R.id.info) TextView info;
   @InjectView(R.id.currency) TextView currency;
   @Inject DialogNavigatorInteractor dialogNavigatorInteractor;

   private TextWatcherAdapter textWatcherAdapter = new TextWatcherAdapter() {
      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
         //work around for disappearing background of button
         calculateButton.setTextColor(s.length() > 0 ? getResources().getColor(R.color.black) : getResources().getColor(R.color.tripButtonDisabled));
      }
   };

   @Override
   protected DtlPointsEstimationPresenter createPresenter(Bundle savedInstanceState) {
      return new DtlPointsEstimationPresenter(getArgs().getMerchant());
   }

   @Override
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);
      pointsEstimated.setText(R.string.dtl_points_estimation_default_result);
      inputPoints.addTextChangedListener(textWatcherAdapter);
      inputPoints.setOnEditorActionListener((v, actionId, event) -> {
         if (actionId == getResources().getInteger(R.integer.dtl_keyboard_point_estimator_action_id)) {
            calculateClicked();
            return true;
         }
         return false;
      });
   }

   @Override
   public void showCurrency(Currency currency) {
      this.currency.setText(currency.getCurrencyHint());
      inputPoints.setCurrencySymbol(currency.prefix());
   }

   @Override
   public void setMinimalAmount(double minimalAmount, Currency currency) {
      String minAmount = String.format(Locale.US, "%.2f", minimalAmount);
      info.setText(getString(R.string.dtl_estimator_explanation_to_earn_points, String.format("%s%s %s", currency.prefix(),
            minAmount, currency.suffix())));
      info.append("\n");
   }

   @Override
   public void onDestroyView() {
      inputPoints.removeTextChangedListener(textWatcherAdapter);
      inputPoints.setOnEditorActionListener(null);
      super.onDestroyView();
   }

   @Override
   public void showProgress() {
      // clear possible previous result
      pointsEstimated.setText(R.string.dtl_points_estimation_default_result);
      progressBar.setVisibility(View.VISIBLE);
      calculateButton.setVisibility(View.INVISIBLE);
   }

   @Override
   public void hideProgress() {
      progressBar.setVisibility(View.INVISIBLE);
      calculateButton.setVisibility(View.VISIBLE);
   }

   @Override
   public void showError(@StringRes int errorRes) {
      hideProgress();
      inputPoints.setError(getString(errorRes));
   }

   @Override
   public void showError(String error) {
      hideProgress();
      inputPoints.setError(error);
   }

   @Override
   public void showEstimatedPoints(int value) {
      hideProgress();
      pointsEstimated.setText(getString(R.string.dtl_dt_points, value));
   }

   @OnClick(R.id.infoToggle)
   void infoToggle() {
      info.setVisibility(info.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
   }

   @OnClick(R.id.calculateButton)
   void calculateClicked() {
      if (inputPoints.getText().length() > 0 && inputPoints.validate()) {
         tryHideSoftInput();
      }
      getPresenter().onCalculateClicked(inputPoints.getText().toString());
   }

   @OnClick(R.id.button_cancel)
   void onCancel() {
      dialogNavigatorInteractor.closeDialogActionPipe().send(new CloseDialogCommand());
   }
}
