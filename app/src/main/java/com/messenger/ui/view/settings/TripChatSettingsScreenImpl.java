package com.messenger.ui.view.settings;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.messenger.entities.DataConversation;
import com.messenger.entities.DataUser;
import com.worldventures.dreamtrips.R;

import java.util.List;

import butterknife.InjectView;

public class TripChatSettingsScreenImpl extends GroupChatSettingsScreenImpl<TripSettingsPath> {

    @InjectView(R.id.chat_settings_trip_pic)
    ImageView tripIconView;

    public TripChatSettingsScreenImpl(Context context) {
        super(context);
    }

    public TripChatSettingsScreenImpl(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setParticipants(DataConversation conversation, List<DataUser> participants) {
        super.setParticipants(conversation, participants);
        groupAvatarsView.setVisibility(GONE);
        tripIconView.setVisibility(VISIBLE);
    }

}
