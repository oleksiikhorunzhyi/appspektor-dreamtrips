package com.messenger.ui.adapter.holder;

import android.view.View;
import android.widget.ImageView;

import com.messenger.entities.DataUser;
import com.worldventures.dreamtrips.R;

import java.util.List;

import butterknife.InjectView;

public class TripConversationViewHolder extends BaseConversationViewHolder{

    @InjectView(R.id.conversation_group_pic)
    ImageView groupAvatarsView;

    public TripConversationViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    protected void setConversationPicture(List<DataUser> participants) {
        // nothing
    }

}
