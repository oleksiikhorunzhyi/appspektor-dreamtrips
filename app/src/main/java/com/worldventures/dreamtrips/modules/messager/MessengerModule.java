package com.worldventures.dreamtrips.modules.messager;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.component.ComponentDescription;

import dagger.Module;
import dagger.Provides;

@Module(complete = false,
        library = true)
public class MessengerModule {
    public static final String MESSENGER = "Messenger";

    @Provides(type = Provides.Type.SET)
    ComponentDescription provideMessengerComponent() {
        return new ComponentDescription(MESSENGER, R.string.messenger, R.string.messenger, R.drawable.ic_dtl, true, null);
    }
}
