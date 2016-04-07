package com.worldventures.dreamtrips.modules.common.api;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.modules.common.model.AvailableLocale;

import java.util.ArrayList;

public class GetLocaleQuery extends Query<ArrayList<AvailableLocale>> {

    public GetLocaleQuery() {
        super((Class<ArrayList<AvailableLocale>>) new ArrayList<AvailableLocale>().getClass());
    }

    @Override
    public ArrayList<AvailableLocale> loadDataFromNetwork() throws Exception {
        return getService().getLocales();
    }

    @Override
    public int getErrorMessage() {
        return R.string.error_fail_to_locales;
    }
}
