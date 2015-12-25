package com.messenger.ui.widget.inappnotification.messanger;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.messenger.messengerservers.entities.User;
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

    private List<User> chatParticipantsList;

    public void setChatParticipantsList(List<User> chatParticipantsList){
        this.chatParticipantsList = chatParticipantsList;
        avatarViewGroup.updateAvatars(chatParticipantsList);
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
        titleTextView.setText(title);
    }

    @Override
    public void setText(String text) {
        this.text = text;
        textTextView.setText(text);
    }

    public InAppNotificationViewGroup(Context context) {
        super(context);
    }

    public InAppNotificationViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InAppNotificationViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public InAppNotificationViewGroup(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void initialize(){
        ButterKnife.inject(this,
                LayoutInflater.from(getContext()).inflate(R.layout.layout_in_app_notification_chat,
                        this, true));
        super.initialize();
    }
}
