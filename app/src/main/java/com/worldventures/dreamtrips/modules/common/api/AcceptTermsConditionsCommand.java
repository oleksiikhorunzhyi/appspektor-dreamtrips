package com.worldventures.dreamtrips.modules.common.api;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.request.Command;

public class AcceptTermsConditionsCommand extends Command<Void> {

    private String text;

    public AcceptTermsConditionsCommand(String text) {
        super(Void.class);
        this.text = text;
    }

    @Override
    public Void loadDataFromNetwork() throws Exception {
        return getService().acceptTermsConditions(text);
    }

    @Override
    public int getErrorMessage() {
        return R.string.error_failed_to_accept_terms_and_conditions;
    }
}
