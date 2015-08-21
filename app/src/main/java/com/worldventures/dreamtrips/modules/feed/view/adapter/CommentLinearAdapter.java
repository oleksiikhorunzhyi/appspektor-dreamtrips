package com.worldventures.dreamtrips.modules.feed.view.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.modules.feed.model.comment.Comment;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class CommentLinearAdapter extends BaseAdapter {

    private List<Comment> comments;
    private LayoutInflater inflater;

    public CommentLinearAdapter(List<Comment> contentItems, Context context) {
        this.comments = contentItems;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return comments.size();
    }

    @Override
    public Comment getItem(int position) {
        return comments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return Long.parseLong(comments.get(position).getUid());
    }

    @Override
    public View getView(int position, View contentView, ViewGroup parent) {
        ViewHolder holder;
        View view = contentView;

        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            view = inflater.inflate(R.layout.adapter_item_comment_preview, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }

        Comment comment = getItem(position);
        holder.text.setText(comment.getMessage());
        holder.userName.setText(comment.getOwner().getFullName());

        holder.userPhoto.setImageURI(Uri.parse(comment.getOwner().getAvatar().getThumb()));
        CharSequence relativeTimeSpanString = DateTimeUtils.getRelativeTimeSpanString(view.getResources(),
                comment.getCreatedAt().getTime());
        holder.date.setText(relativeTimeSpanString);

        return view;
    }

    static class ViewHolder {
        @InjectView(R.id.text)
        protected TextView text;
        @InjectView(R.id.user_photo)
        protected SimpleDraweeView userPhoto;
        @InjectView(R.id.user_name)
        protected TextView userName;
        @InjectView(R.id.date)
        protected TextView date;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
