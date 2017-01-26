package com.worldventures.dreamtrips.wallet.ui.wizard.edit_card;

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
import com.worldventures.dreamtrips.wallet.domain.entity.AddressInfoWithLocale;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableAddressInfo;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.delegate.DialogOperationScreen;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class EditCardDetailsScreen extends WalletLinearLayout<EditCardDetailsPresenter.Screen, EditCardDetailsPresenter, EditCardDetailsPath>
      implements EditCardDetailsPresenter.Screen {

   @InjectView(R.id.toolbar) Toolbar toolbar;
   @InjectView(R.id.address1) EditText address1Field;
   @InjectView(R.id.address2) EditText address2Field;
   @InjectView(R.id.city) EditText cityField;
   @InjectView(R.id.state) EditText stateField;
   @InjectView(R.id.zip) EditText zipField;
   @InjectView(R.id.confirm_button) Button confirmButton;

   private DialogOperationScreen dialogOperationScreen;

   public EditCardDetailsScreen(Context context) {
      super(context);
   }

   public EditCardDetailsScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @NonNull
   @Override
   public EditCardDetailsPresenter createPresenter() {
      return new EditCardDetailsPresenter(getContext(), getInjector(), getPath().getBankCard());
   }

   @Override
   protected void onFinishInflate() {
      super.onFinishInflate();
      if (isInEditMode()) return;
      toolbar.setNavigationOnClickListener(v -> presenter.goBack());

      Observable.combineLatest(RxTextView.afterTextChangeEvents(address1Field), RxTextView.afterTextChangeEvents(cityField),
            RxTextView.afterTextChangeEvents(stateField), RxTextView.afterTextChangeEvents(zipField),
            (addressTextChangeEvent, cityTextChangeEvent, stateTextChangeEvent, zipTextChangeEvent) ->
                  Queryable.from(addressTextChangeEvent, cityTextChangeEvent, stateTextChangeEvent, zipTextChangeEvent)
                        .count(event -> event.editable().length() == 0) > 0)
            .compose(RxLifecycle.bindView(this))
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(someRequiredFieldIsEmpty -> confirmButton.setEnabled(!someRequiredFieldIsEmpty));
   }

   @Override
   public void address(AddressInfoWithLocale defaultAddress) {
      AddressInfo addressInfo = defaultAddress.addressInfo();
      address1Field.setText(addressInfo.address1());
      address2Field.setText(addressInfo.address2());
      cityField.setText(addressInfo.city());
      stateField.setText(addressInfo.state());
      zipField.setText(addressInfo.zip());
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
            .address1(address1Field.getText().toString().trim())
            .address2(address2Field.getText().toString().trim())
            .city(cityField.getText().toString().trim())
            .state(stateField.getText().toString().trim())
            .zip(zipField.getText().toString().trim())
            .build();

      getPresenter().onCardAddressConfirmed(addressInfo);
   }

   @Override
   protected boolean hasToolbar() {
      return true;
   }
}
