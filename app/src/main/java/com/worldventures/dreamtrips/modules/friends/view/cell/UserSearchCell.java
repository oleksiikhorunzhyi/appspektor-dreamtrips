package com.worldventures.dreamtrips.modules.friends.view.cell;

import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.friends.events.AddUserRequestEvent;
import com.worldventures.dreamtrips.modules.friends.events.UserClickedEvent;
import com.worldventures.dreamtrips.modules.friends.view.util.MutualStringUtil;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_user_search)
public class UserSearchCell extends AbstractCell<User> {

    @InjectView(R.id.avatar)
    SimpleDraweeView avatar;
    @InjectView(R.id.company)
    TextView companyName;
    @InjectView(R.id.name)
    TextView name;
    @InjectView(R.id.info)
    TextView tvMutual;

    private MutualStringUtil mutualStringUtil;

    public UserSearchCell(View view) {
        super(view);
        mutualStringUtil = new MutualStringUtil(view.getContext());
    }

    @Override
    protected void syncUIStateWithModel() {
        name.setText(getModelObject().getFullName());
        if (!TextUtils.isEmpty(getModelObject().getCompany())) {
            companyName.setText(getModelObject().getCompany());
            companyName.setVisibility(View.VISIBLE);
        } else {
            companyName.setVisibility(View.GONE);
        }
        String mutual = mutualStringUtil.createMutualString(getModelObject().getMutualFriends());
        tvMutual.setVisibility(TextUtils.isEmpty(mutual) ? View.GONE : View.VISIBLE);
        tvMutual.setText(mutual);
        avatar.setImageURI(Uri.parse(getModelObject().getAvatar().getMedium()));
    }

    @OnClick(R.id.avatar)
    void onUserClicked() {
        getEventBus().post(new UserClickedEvent(getModelObject()));
    }

    @OnClick(R.id.add)
    void onAccept() {
        getEventBus().post(new AddUserRequestEvent(getModelObject()));
    }

    @Override
    public void prepareForReuse() {

    }
}
