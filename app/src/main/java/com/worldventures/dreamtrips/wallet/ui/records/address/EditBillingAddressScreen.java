package com.worldventures.dreamtrips.wallet.ui.records.address;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;

import com.innahema.collections.query.queriables.Queryable;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.trello.rxlifecycle.RxLifecycle;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.domain.entity.AddressInfo;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableAddressInfo;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.delegate.DialogOperationScreen;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class EditBillingAddressScreen extends WalletLinearLayout<EditBillingAddressPresenter.Screen, EditBillingAddressPresenter, EditBillingAddressPath>
      implements EditBillingAddressPresenter.Screen {

   @InjectView(R.id.toolbar) Toolbar toolbar;
   @InjectView(R.id.address1) EditText etAddress1;
   @InjectView(R.id.address2) EditText etAddress2;
   @InjectView(R.id.city) EditText etCityField;
   @InjectView(R.id.state) EditText etStateField;
   @InjectView(R.id.zip) EditText etZipField;
   @InjectView(R.id.confirm_button) Button confirmButton;

   private DialogOperationScreen dialogOperationScreen;

   public EditBillingAddressScreen(Context context) {
      super(context);
   }

   public EditBillingAddressScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @NonNull
   @Override
   public EditBillingAddressPresenter createPresenter() {
      return new EditBillingAddressPresenter(getContext(), getInjector(), getPath().getRecord());
   }

   @Override
   protected void onFinishInflate() {
      super.onFinishInflate();
      if (isInEditMode()) return;
      toolbar.setNavigationOnClickListener(v -> presenter.goBack());
   }

   @Override
   public void address(AddressInfo addressInfo) {
      etAddress1.setText(addressInfo.address1());
      etAddress1.setSelection(etAddress1.length());

      etAddress2.setText(addressInfo.address2());
      etAddress2.setSelection(etAddress2.length());

      etCityField.setText(addressInfo.city());
      etCityField.setSelection(etCityField.length());

      etStateField.setText(addressInfo.state());
      etStateField.setSelection(etStateField.length());

      etZipField.setText(addressInfo.zip());
      etZipField.setSelection(etZipField.length());

      Observable.combineLatest(RxTextView.afterTextChangeEvents(etAddress1), RxTextView.afterTextChangeEvents(etAddress2),
            RxTextView.afterTextChangeEvents(etCityField), RxTextView.afterTextChangeEvents(etStateField),
            RxTextView.afterTextChangeEvents(etZipField),
            (address1TextChangeEvent, address2TextChangeEvent, cityTextChangeEvent, stateTextChangeEvent, zipTextChangeEvent) ->
                  Queryable.from(address1TextChangeEvent, cityTextChangeEvent, stateTextChangeEvent, zipTextChangeEvent)
                        .count(event -> event.editable().length() == 0) == 0 &&
                        (!addressInfo.address1().equals(address1TextChangeEvent.editable().toString()) ||
                              !addressInfo.address2().equals(address2TextChangeEvent.editable().toString()) ||
                              !addressInfo.city().equals(cityTextChangeEvent.editable().toString()) ||
                              !addressInfo.state().equals(stateTextChangeEvent.editable().toString()) ||
                              !addressInfo.zip().equals(zipTextChangeEvent.editable().toString())))
            .compose(RxLifecycle.bindView(this))
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(requiredFieldsNotEmptyAndChanged -> confirmButton.setEnabled(requiredFieldsNotEmptyAndChanged));
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      if (dialogOperationScreen == null) dialogOperationScreen = new DialogOperationScreen(this);
      return dialogOperationScreen;
   }

   @OnEditorAction({R.id.city, R.id.state, R.id.zip})
   boolean interceptDoneButton(int actionId) {
      if (actionId == EditorInfo.IME_ACTION_DONE) {
         onConfirmButtonClicked();
         return true;
      }
      return false;
   }

   @OnClick(R.id.confirm_button)
   public void onConfirmButtonClicked() {
      AddressInfo addressInfo = ImmutableAddressInfo.builder()
            .address1(etAddress1.getText().toString().trim())
            .address2(etAddress2.getText().toString().trim())
            .city(etCityField.getText().toString().trim())
            .state(etStateField.getText().toString().trim())
            .zip(etZipField.getText().toString().trim())
            .build();

      getPresenter().onCardAddressConfirmed(addressInfo);
   }
}
