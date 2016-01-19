package com.messenger.ui.widget.inappnotification.messanger;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.messenger.ui.widget.AvatarView;
import com.worldventures.dreamtrips.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class InAppNotificationViewChat extends InAppMessengerNotificationView {

    @InjectView(R.id.in_app_notif_avatar)
    AvatarView avatarView;
    @InjectView(R.id.in_app_notif_title)
    TextView titleTextView;
    @InjectView(R.id.in_app_notif_text)
    TextView textTextView;

    public InAppNotificationViewChat(Context context) {
        super(context);
    }

    public InAppNotificationViewChat(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InAppNotificationViewChat(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setAvatarUrl(String avatarUrl) {
        avatarView.setImageURI(Uri.parse(avatarUrl));
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
    protected void initialize(){
        ButterKnife.inject(this,
                LayoutInflater.from(getContext()).inflate(R.layout.layout_in_app_notification_chat,
                        this, true));

        super.initialize();
    }
}
