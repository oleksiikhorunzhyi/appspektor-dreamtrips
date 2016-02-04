package com.messenger.di;

import android.content.Context;
import android.text.TextUtils;

import com.messenger.entities.User;
import com.messenger.storage.dao.AttachmentDAO;
import com.messenger.storage.dao.ConversationsDAO;
import com.messenger.storage.dao.MessageDAO;
import com.messenger.storage.dao.ParticipantsDAO;
import com.messenger.storage.dao.UsersDAO;
import com.messenger.util.RxContentResolver;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.techery.spares.module.qualifier.ForApplication;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        complete = false,
        library = true
)
public class MessengerStorageModule {

    public static final String DB_FLOW_RX_RESOLVER = "db_flow_rx_resolver";

    @Provides
    @Singleton
    @Named(DB_FLOW_RX_RESOLVER)
    RxContentResolver providedRxContentResolver(@ForApplication Context context) {
        return new RxContentResolver(context.getContentResolver(),
                query -> {
                    StringBuilder builder = new StringBuilder(query.selection);
                    if (!TextUtils.isEmpty(query.sortOrder)) {
                        builder.append(" ").append(query.sortOrder);
                    }
                    return FlowManager.getDatabaseForTable(User.class).getWritableDatabase()
                            .rawQuery(builder.toString(), query.selectionArgs);
                });
    }

    @Provides
    @Singleton
    ConversationsDAO provideConversationsDAO(@Named(DB_FLOW_RX_RESOLVER) RxContentResolver rxContentResolver, @ForApplication Context context) {
        return new ConversationsDAO(context, rxContentResolver);
    }

    @Provides
    @Singleton
    ParticipantsDAO provideParticipantsDAO(@Named(DB_FLOW_RX_RESOLVER) RxContentResolver rxContentResolver, @ForApplication Context context) {
        return new ParticipantsDAO(rxContentResolver, context);
    }

    @Provides
    @Singleton
    UsersDAO provideUsersDAO(@Named(DB_FLOW_RX_RESOLVER) RxContentResolver rxContentResolver, @ForApplication Context context) {
        return new UsersDAO(rxContentResolver, context);
    }

    @Provides
    @Singleton
    MessageDAO provideMessageDAO(@Named(DB_FLOW_RX_RESOLVER) RxContentResolver rxContentResolver, @ForApplication Context context) {
        return new MessageDAO(rxContentResolver, context);
    }

    @Provides
    @Singleton
    AttachmentDAO provideAttachmentDAO(@Named(DB_FLOW_RX_RESOLVER) RxContentResolver rxContentResolver, @ForApplication Context context) {
        return new AttachmentDAO(context, rxContentResolver);
    }
}
