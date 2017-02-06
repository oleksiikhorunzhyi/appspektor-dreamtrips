package com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.model.ReviewObject;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.media.ImageLoaderHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andres Rubiano Del Chiaro on 19/09/2016.
 */

public class ReviewAdapter
        extends RecyclerView.Adapter<ReviewAdapter.RecyclerViewHolder>
{

    private ArrayList<ReviewObject> mItems = new ArrayList<>();
    private ImageLoaderHelper imageLoaderHelper;
    private Context context;

    public ReviewAdapter(Context context) {
        this.context = context;
        this.imageLoaderHelper= new ImageLoaderHelper(ImageLoaderHelper.GLIDE);
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        return new RecyclerViewHolder(v);
    }

    public void addAll(List<ReviewObject> items) {
        int pos = getItemCount();
        mItems.addAll(items);
        notifyItemRangeInserted(pos, mItems.size());
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }


    class RecyclerViewHolder extends RecyclerView.ViewHolder {
        private ImageView mAvatar;
        private TextView mUserName;
        private TextView mCommentWrote;
        private RatingBar mRating;
        private TextView mComment;


        public RecyclerViewHolder(View itemView) {
            super(itemView);
            mAvatar = (ImageView) itemView.findViewById(R.id.ivItemReview);
            mUserName = (TextView) itemView.findViewById(R.id.tvUserName);
            mCommentWrote = (TextView) itemView.findViewById(R.id.tvCommentWrote);
            mComment = (TextView) itemView.findViewById(R.id.tvComment);
            mRating = (RatingBar) itemView.findViewById(R.id.rbRating);
        }

       public void bind(int position) {
            imageLoaderHelper.getLoader().load(
                                        mItems.get(position).getUrlImageUser(),
                                        mAvatar);
            mUserName.setText(String.valueOf(mItems.get(position).getNameUser()));
            mCommentWrote.setText(mItems.get(position).getTimeWrote());
            mComment.setText(mItems.get(position).getComment());
            mRating.setRating((mItems.get(position).getRatingCommentUser()) );
        }
    }
}
