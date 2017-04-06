package com.worldventures.dreamtrips.api.http.executor;

import com.worldventures.dreamtrips.api.http.provider.BaseJanetProvider;
import com.worldventures.dreamtrips.api.http.provider.SimpleHttpServiceProvider;
import com.worldventures.dreamtrips.api.http.provider.SimpleJanetProvider;
import com.worldventures.dreamtrips.api.http.provider.SystemEnvProvider;

public class SimpleActionExecutor extends BaseActionExecutor<BaseJanetProvider> {

    public SimpleActionExecutor() {
        super(new SimpleJanetProvider(new SimpleHttpServiceProvider(new SystemEnvProvider().provide()).provide()));
    }
}
