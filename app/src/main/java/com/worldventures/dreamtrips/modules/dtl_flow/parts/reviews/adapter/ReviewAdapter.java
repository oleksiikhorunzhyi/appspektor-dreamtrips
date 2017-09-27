package com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.model.CSTConverter;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.model.ReviewObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class ReviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

   private ArrayList<ReviewObject> mItems = new ArrayList<>();
   private Context context;

   public ReviewAdapter(Context context) {
      this.context = context;
   }

   // View Types
   private static final int ITEM = 0;
   private static final int LOADING = 1;

   @Override
   public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      RecyclerView.ViewHolder viewHolder = null;
      LayoutInflater inflater = LayoutInflater.from(parent.getContext());

      switch (viewType) {
         case ITEM:
            View viewItem = inflater.inflate(R.layout.item_review, parent, false);
            viewHolder = new RecyclerViewHolder(viewItem);
            break;
         case LOADING:
            View viewLoading = inflater.inflate(R.layout.view_dtl_item_loading, parent, false);
            viewHolder = new LoadingVH(viewLoading);
            break;
      }
      return viewHolder;
   }

   @Override
   public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
      switch (getItemViewType(position)) {
         case ITEM:
            final RecyclerViewHolder recyclerViewHolder = (RecyclerViewHolder) holder;
            recyclerViewHolder.bind(position);
            break;
      }
   }

   @Override
   public int getItemCount() {
      return mItems.size();
   }

   @Override
   public int getItemViewType(int position) {
      String reviewId = getItem(position).getReviewId();
      return reviewId == null || reviewId.length() == 0 ? LOADING : ITEM;
   }

     /*
        Helpers - Pagination
   _________________________________________________________________________________________________
    */

   public void add(ReviewObject r) {
      mItems.add(r);
      notifyItemInserted(mItems.size() - 1);
   }

   public void addItems(List<ReviewObject> reviewResults) {
      for (ReviewObject review : reviewResults) {
         add(review);
      }
   }

   public List<ReviewObject> getAllItems() {
      return mItems;
   }

   public void remove(ReviewObject r) {
      int position = mItems.indexOf(r);
      if (position > -1) {
         mItems.remove(position);
         notifyItemRemoved(position);
      }
   }

   public void clear() {
      while (getItemCount() > 0) {
         remove(getItem(0));
      }
   }

   public boolean isEmpty() {
      return getItemCount() == 0;
   }

   public void addLoadingFooter() {
      add(new ReviewObject());
   }

   public void removeLoadingFooter() {
      if (mItems == null || mItems.isEmpty()) return;

      int position = mItems.size() - 1;

      if (getItemViewType(position) == ITEM) return;

      ReviewObject reviewObject = getItem(position);

      if (reviewObject != null) {
         mItems.remove(position);
         notifyItemRemoved(position);
      }
   }

   public ReviewObject getItem(int position) {
      return mItems.get(position);
   }

   /**
    * Main list's content ViewHolder
    */
   protected class RecyclerViewHolder extends RecyclerView.ViewHolder {
      private SimpleDraweeView mAvatar;
      private TextView mUserName;
      private TextView mCommentWrote;
      private RatingBar mRating;
      private TextView mComment;
      private TextView mTvVerifiedReview;
      private ImageView mIvVerifiedReview;
      private LinearLayout mPhotosIndicatorLayout;
      private TextView mPhotosNumber;

      public RecyclerViewHolder(View itemView) {
         super(itemView);
         mAvatar = (SimpleDraweeView) itemView.findViewById(R.id.ivItemReview);
         mUserName = (TextView) itemView.findViewById(R.id.tvUserName);
         mCommentWrote = (TextView) itemView.findViewById(R.id.tvCommentWrote);
         mComment = (TextView) itemView.findViewById(R.id.tvComment);
         mRating = (RatingBar) itemView.findViewById(R.id.rbRating);
         mTvVerifiedReview = (TextView) itemView.findViewById(R.id.tv_verified_buyer);
         mIvVerifiedReview = (ImageView) itemView.findViewById(R.id.iv_verified_buyer);
         mPhotosIndicatorLayout = (LinearLayout) itemView.findViewById(R.id.photos_indicator_layout);
         mPhotosNumber = (TextView) itemView.findViewById(R.id.pics_number_tv);
      }

      public void bind(int position) {
         String urlImage = mItems.get(position).getUrlImageUser();
         if (urlImage != null && !urlImage.equalsIgnoreCase("null")) {
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

         if (mItems.get(position).getUrlReviewImages().size() > 0) {
            mPhotosNumber.setText(String.valueOf(mItems.get(position).getUrlReviewImages().size()));
         } else {
            mPhotosIndicatorLayout.setVisibility(View.INVISIBLE);
         }
      }

      private void setVerifiedReview(boolean isVerified) {
         if (isVerified) {
            changeVisibility(mTvVerifiedReview, View.VISIBLE);
            changeVisibility(mIvVerifiedReview, View.VISIBLE);
         } else {
            changeVisibility(mTvVerifiedReview, View.INVISIBLE);
            changeVisibility(mIvVerifiedReview, View.INVISIBLE);
         }
      }

      private void changeVisibility(@NonNull View view, int type) {
         view.setVisibility(type);
      }
   }

   protected class LoadingVH extends RecyclerView.ViewHolder {
      public LoadingVH(View itemView) {
         super(itemView);
      }
   }
}
