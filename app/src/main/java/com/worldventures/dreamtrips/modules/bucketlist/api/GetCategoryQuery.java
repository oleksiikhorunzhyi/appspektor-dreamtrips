package com.worldventures.dreamtrips.modules.bucketlist.api;

import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.modules.bucketlist.model.CategoryItem;

import java.util.ArrayList;

public class GetCategoryQuery extends Query<ArrayList<CategoryItem>> {

    public GetCategoryQuery() {
        super((Class<ArrayList<CategoryItem>>) new ArrayList<CategoryItem>().getClass());
    }

    @Override
    public ArrayList<CategoryItem> loadDataFromNetwork() throws Exception {
        return getService().getCategories();
    }
}
