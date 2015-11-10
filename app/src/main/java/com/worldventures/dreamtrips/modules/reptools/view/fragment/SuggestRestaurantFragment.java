package com.worldventures.dreamtrips.modules.reptools.view.fragment;

import android.os.Bundle;
import android.support.design.widget.Snackbar;

import com.techery.spares.annotations.MenuResource;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.dtl.validator.EmptyValidator;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.SuggestPlaceBaseFragment;
import com.worldventures.dreamtrips.modules.reptools.presenter.SuggestRestaurantPresenter;

@MenuResource(R.menu.menu_suggest_merchant)
public class SuggestRestaurantFragment extends SuggestPlaceBaseFragment<SuggestRestaurantPresenter>
        implements SuggestRestaurantPresenter.View {

    @Override
    protected SuggestRestaurantPresenter createPresenter(Bundle savedInstanceState) {
        return new SuggestRestaurantPresenter();
    }

    @Override
    protected void addValidators() {
        super.addValidators();
        restaurantName.addValidator(new EmptyValidator(getString(R.string.dtl_field_validation_empty_input_error)));
        city.addValidator(new EmptyValidator(getString(R.string.dtl_field_validation_empty_input_error)));
    }

    @Override
    protected boolean validateInput() {
        return restaurantName.validate() && city.validate() && super.validateInput();
    }

    @Override
    public String getRestaurantName() {
        return restaurantName.getText().toString();
    }

    @Override
    public String getCity() {
        return city.getText().toString();
    }

    @Override
    public void clearInput() {
        restaurantName.setText("");
        city.setText("");
        contactName.setText("");
        phoneNumber.setText("");
        toDate.setError(null);
        foodRatingBar.setRating(3);
        serviceRatingBar.setRating(3);
        cleanlinessRatingBar.setRating(3);
        uniquenessRatingBar.setRating(3);
        additionalInfo.setText("");
        restaurantName.requestFocus();
    }
}
