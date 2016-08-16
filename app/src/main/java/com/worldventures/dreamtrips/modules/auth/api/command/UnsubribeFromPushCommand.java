package com.worldventures.dreamtrips.modules.auth.api.command;

import android.content.Context;
import android.text.TextUtils;

import com.google.android.gms.iid.InstanceID;
import com.worldventures.dreamtrips.api.push_notifications.UnsubscribeFromPushNotificationsHttpAction;
import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import rx.schedulers.Schedulers;
import timber.log.Timber;

@CommandAction
public class UnsubribeFromPushCommand extends Command<Void> implements InjectableAction {

    @Inject @Named(JanetModule.JANET_API_LIB)Janet janet;
    @Inject SnappyRepository snappyRepository;
    @Inject Context context;

    @Override
    protected void run(CommandCallback<Void> callback) throws Throwable {
        String token = snappyRepository.getGcmRegToken();

        if (TextUtils.isEmpty(token)) callback.onSuccess(null);
        else janet.createPipe(UnsubscribeFromPushNotificationsHttpAction.class, Schedulers.io())
                .createObservableResult(new UnsubscribeFromPushNotificationsHttpAction(token))
                .doOnNext(action -> {
                    try {
                        InstanceID.getInstance(context).deleteInstanceID();
                    } catch (IOException e) {
                        Timber.e(e, "Failed to delete instance ID");
                    }
                })
                .subscribe(action -> callback.onSuccess(null), callback::onFail);
    }
}
