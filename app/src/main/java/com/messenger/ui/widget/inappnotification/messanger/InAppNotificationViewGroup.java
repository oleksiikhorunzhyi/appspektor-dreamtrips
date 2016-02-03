package com.messenger.ui.widget.inappnotification.messanger;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.messenger.entities.DataUser;
import com.messenger.ui.widget.GroupAvatarsView;
import com.worldventures.dreamtrips.R;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class InAppNotificationViewGroup extends InAppMessengerNotificationView {

    @InjectView(R.id.in_app_notif_avatar_group)
    GroupAvatarsView avatarViewGroup;
    @InjectView(R.id.in_app_notif_title)
    TextView titleTextView;
    @InjectView(R.id.in_app_notif_text)
    TextView textTextView;

    public InAppNotificationViewGroup(Context context) {
        super(context);
    }

    public InAppNotificationViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InAppNotificationViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setChatParticipants(List<DataUser> chatParticipants) {
        avatarViewGroup.updateAvatars(chatParticipants);
    }

    @Override
    public void setTitle(String title) {
        titleTextView.setText(title);
    }

    @Override
    public void setText(String text) {
        textTextView.setText(text);
    }

    @Override
    protected void initialize() {
        ButterKnife.inject(this,
                LayoutInflater.from(getContext()).inflate(R.layout.layout_in_app_notification_group,
                        this, true));
        super.initialize();
    }
}
