package com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.adapter;

import android.content.Context;
import android.net.Uri;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.model.CSTConverter;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.model.ReviewObject;
import com.facebook.drawee.view.SimpleDraweeView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import timber.log.Timber;


public class ReviewAdapter
      extends RecyclerView.Adapter<ReviewAdapter.RecyclerViewHolder> {

   private ArrayList<ReviewObject> mItems = new ArrayList<>();
   private Context context;

   public ReviewAdapter(Context context) {
      this.context = context;
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
      private SimpleDraweeView mAvatar;
      private TextView mUserName;
      private TextView mCommentWrote;
      private RatingBar mRating;
      private TextView mComment;
      private TextView mTvVerifiedReview;
      private ImageView mIvVerifiedReview;


      public RecyclerViewHolder(View itemView) {
         super(itemView);
         mAvatar = (SimpleDraweeView) itemView.findViewById(R.id.ivItemReview);
         mUserName = (TextView) itemView.findViewById(R.id.tvUserName);
         mCommentWrote = (TextView) itemView.findViewById(R.id.tvCommentWrote);
         mComment = (TextView) itemView.findViewById(R.id.tvComment);
         mRating = (RatingBar) itemView.findViewById(R.id.rbRating);
         mTvVerifiedReview = (TextView) itemView.findViewById(R.id.tv_verified_buyer);
         mIvVerifiedReview = (ImageView) itemView.findViewById(R.id.iv_verified_buyer);
      }

      public void bind(int position) {
         String urlImage = mItems.get(position).getUrlImageUser();
         if (!urlImage.equalsIgnoreCase("null")) {
            mAvatar.setImageURI(Uri.parse(urlImage));
         }
         mUserName.setText(String.valueOf(mItems.get(position).getNameUser()));
         try {
            CSTConverter converter = new CSTConverter();
            mCommentWrote.setText(converter.getCorrectTimeWrote(context, mItems.get(position).getTimeWrote()));
         } catch (ParseException e) {
            Timber.e(e.getMessage());
         }
         mComment.setText(mItems.get(position).getComment());
         mRating.setRating((mItems.get(position).getRatingCommentUser()));

         setVerifiedReview(mItems.get(position).isVerifiedReview());
      }

      private void setVerifiedReview(boolean isVerified) {
         if (isVerified){
            changeVisibility(mTvVerifiedReview, View.VISIBLE);
            changeVisibility(mIvVerifiedReview, View.VISIBLE);
         } else {
            changeVisibility(mTvVerifiedReview, View.INVISIBLE);
            changeVisibility(mIvVerifiedReview, View.INVISIBLE);
         }
      }

      private void changeVisibility(@NonNull View view, int type){
         view.setVisibility(type);
      }


   }
}
