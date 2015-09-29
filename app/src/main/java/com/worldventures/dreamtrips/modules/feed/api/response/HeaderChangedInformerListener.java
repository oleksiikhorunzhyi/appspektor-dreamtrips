
package com.worldventures.dreamtrips.modules.feed.api.response;

import android.os.Handler;

import com.worldventures.dreamtrips.modules.common.event.HeaderCountChangedEvent;

import java.util.List;

import de.greenrobot.event.EventBus;
import retrofit.client.Header;

import static com.worldventures.dreamtrips.core.utils.InterceptingOkClient.ResponseHeaderListener;

public class HeaderChangedInformerListener implements ResponseHeaderListener {

    private static final long DELAY = 1000L;
    private EventBus eventBus;
    //
    private Handler handler = new Handler();
    private Runnable runnable = () -> eventBus.post(new HeaderCountChangedEvent());

    public HeaderChangedInformerListener(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void onResponse(List<Header> headers) {
        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, DELAY);
    }


}
