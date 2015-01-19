package com.worldventures.dreamtrips.core.model.response;

import com.worldventures.dreamtrips.core.model.Trip;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Edward on 19.01.15.
 * response that contains dream trips
 */
public class ListTripResponse {

    private List<Trip> data;

    public List<Trip> getData() {
        if (data == null) return new ArrayList<>();
        return data;
    }

}
