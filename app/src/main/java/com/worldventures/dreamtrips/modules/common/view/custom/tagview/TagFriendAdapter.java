package com.worldventures.dreamtrips.modules.common.view.custom.tagview;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.model.User;

import java.util.Collections;
import java.util.List;

public class TagFriendAdapter extends ArrayAdapter<User> {

    private Context context;
    private List<User> friendList;
    private TagCreationActionsListener tagListener;

    public TagFriendAdapter(Context context, List<User> items) {
        super(context, 0, 0, items);
        this.context = context;
        this.friendList = items;
    }

    public void setTagListener(TagCreationActionsListener tagListener) {
        this.tagListener = tagListener;
    }

    public void setFriendList(List<User> friendList) {
        this.friendList = friendList;
    }

    @Override
    public User getItem(int position) {
        return friendList.get(position);
    }

    @Override
    public int getCount() {
        return friendList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FriendHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.adapter_item_tag_dropdown, parent, false);

            holder = new FriendHolder();
            holder.friendName = (TextView) convertView.findViewById(R.id.friend_name);
            holder.friendPhoto = (SimpleDraweeView) convertView.findViewById(R.id.friend_photo);

            convertView.setTag(holder);
        } else {
            holder = (FriendHolder) convertView.getTag();
        }

        User user = friendList.get(position);
        holder.friendName.setText(user.getFullName());
        holder.friendPhoto.setImageURI(Uri.parse(user.getAvatar().getThumb()));
        holder.friendPhoto.invalidate();

        return convertView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                return new FilterResults();
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (constraint != null && constraint.toString().trim().length() > 2)
                    tagListener.onQueryChanged(constraint.toString());
                else
                    setFriendList(Collections.emptyList());
            }
        };
    }

    static class FriendHolder {
        TextView friendName;
        SimpleDraweeView friendPhoto;
    }
}
