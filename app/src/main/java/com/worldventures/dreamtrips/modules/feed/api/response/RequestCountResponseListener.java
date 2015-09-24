package com.worldventures.dreamtrips.modules.feed.api.response;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.InterceptingOkClient;

import java.util.List;

import retrofit.client.Header;
import timber.log.Timber;

public class RequestCountResponseListener implements InterceptingOkClient.ResponseHeaderListener {

    protected final SnappyRepository db;
    protected final String key;

    public RequestCountResponseListener(String key, SnappyRepository db) {
        this.key = key;
        this.db = db;
    }

    @Override
    public void onResponse(List<Header> headers) {
        saveHeaderCount(headers);
    }

    protected void saveHeaderCount(List<Header> headers) {
        Header header = Queryable.from(headers).firstOrDefault(element ->
                key.equals(element.getName()));
        if (header == null) return;
        int notificationsCount = 0;
        try {
            notificationsCount = Integer.parseInt(header.getValue());
        } catch (Exception e) {
            Timber.w(e, "Can't parse notification count");
        }
        db.saveCountFromHeader(key, notificationsCount);
    }

}
