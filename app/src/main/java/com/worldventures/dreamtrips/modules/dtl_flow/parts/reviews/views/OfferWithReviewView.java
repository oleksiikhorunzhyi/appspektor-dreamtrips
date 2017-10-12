package com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.views;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.dtl_flow.FlowUtil;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.detailReview.DtlDetailReviewPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.adapter.ReviewAdapter;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.model.ReviewObject;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.recycler.MarginDecoration;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.recycler.PaginationScrollListener;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.recycler.RecyclerClickListener;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.recycler.RecyclerTouchListener;

import java.util.List;

import flow.Flow;

public class OfferWithReviewView extends LinearLayout {

   private RecyclerView recyclerView;
   private RatingBar ratingBar2;
   private TextView tvReviewCount;
   private ReviewAdapter mAdapter;

   public static final String ARRAY = "arrayList";
   public static final String RATING_MERCHANT = "ratingMerchant";
   public static final String COUNT_REVIEW = "countReview";
   public static final String MERCHANT_NAME = "merchantName";
   public static final String IS_FROM_LIST_REVIEW = "isFromListReview";

   private float mRatingMerchant;
   private int mCountReview;
   private String mMerchantName;
   private boolean mIsFromListReview = false;

   private boolean firstPageLoading = true;
   private boolean isLoading = false;

   private LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
   private RecyclerView.OnItemTouchListener onItemTouchListener;
   private RecyclerView.OnScrollListener scrollingListener;
   private IMyEventListener mEventListener;

   public OfferWithReviewView(Context context) {
      this(context, null);
   }

   public OfferWithReviewView(Context context, @Nullable AttributeSet attrs) {
      super(context, attrs);
      init();
   }

   private void init() {
      mAdapter = new ReviewAdapter(getContext());
      final View v = LayoutInflater.from(getContext()).inflate(R.layout.activity_offer_with_review, this, true);
      recyclerView = (RecyclerView) v.findViewById(R.id.recycler_adapter);
      ratingBar2 = (RatingBar) v.findViewById(R.id.ratingBar2);
      tvReviewCount = (TextView) v.findViewById(R.id.tv_review_count);

      initRecycler();
   }

   public void loadData(Bundle bundle) {
      mAdapter.removeLoadingFooter();
      loadPage(bundle);
   }

   public void loadFirstPage() {
      if (mEventListener != null) mEventListener.onStartFistPageLoading();
      resetViewData();
      getMoreReviewItems();
   }

   private void loadPage(Bundle bundle) {
      if (firstPageLoading) {
         firstPageLoading = false;
         if (mEventListener != null) mEventListener.onFinishFistPageLoading();
      }

      List<ReviewObject> reviewObjects = bundle.getParcelableArrayList(ARRAY);

      if (!validReceivedData(reviewObjects)) {
         return;
      }

      mAdapter.addItems(reviewObjects);

      setUpInfo(bundle);

      isLoading = false;

   }

   public void resetViewData() {
      mAdapter.getAllItems().clear();
      mAdapter.notifyDataSetChanged();
   }

   public void showNoComments() {
      removeAllViews();
      LayoutInflater.from(getContext()).inflate(R.layout.offer_details_no_review, this, true);
   }

   private void setUpInfo(Bundle bundle) {
      mRatingMerchant = bundle.getFloat(RATING_MERCHANT, 0f);
      mCountReview = bundle.getInt(COUNT_REVIEW, 0);
      mMerchantName = bundle.getString(MERCHANT_NAME, "");
      mIsFromListReview = bundle.getBoolean(IS_FROM_LIST_REVIEW, false);

      setUpRating();
      setUpCommentReview();
   }

   private void setUpCommentReview() {
      if (null != tvReviewCount && mCountReview > 0) {
         tvReviewCount.setText(getTextReview(mCountReview));
      }
   }

   private String getTextReview(int mCountReview) {
      return mCountReview == 1 ? String.format(getContext().getResources()
            .getString(R.string.format_review_text), mCountReview) : String.format(getContext().getResources()
            .getString(R.string.format_reviews_text), mCountReview);
   }

   private void setUpRating() {
      ratingBar2.setRating(mRatingMerchant);
   }

   private void getMoreReviewItems() {
      int lastIndex = getNextItemValue();
      recyclerView.postDelayed(new Runnable() {
         @Override
         public void run() {
            if (mEventListener != null) mEventListener.onEventOccurred(lastIndex);
         }
      }, 1000);
   }

   private int getNextItemValue() {
      return mAdapter.isEmpty() ? 0 : mAdapter.getItemCount() - 1;
   }

   public interface IMyEventListener {
      void onStartFistPageLoading();
      void onFinishFistPageLoading();
      void onEventOccurred(int indexOf);
   }

   public void setEventListener(IMyEventListener mEventListener) {
      this.mEventListener = mEventListener;
   }

   public void removeLoadingActions() {
      if (isLoading) mAdapter.removeLoadingFooter();
      recyclerView.removeOnScrollListener(scrollingListener);
   }

   private boolean validReceivedData(List<ReviewObject> reviewObjects) {
      List<ReviewObject> currentItems = mAdapter.getAllItems();
      if (!currentItems.isEmpty() && !reviewObjects.isEmpty()) {
         ReviewObject lastItem = currentItems.get(currentItems.size() - 1);
         ReviewObject lastReceivedItem = reviewObjects.get(reviewObjects.size() - 1);
         if (lastItem.getReviewId().equals(lastReceivedItem.getReviewId())) return false;
      }

      return true;
   }

   private void initRecycler() {
      recyclerView.setLayoutManager(linearLayoutManager);
      recyclerView.addItemDecoration(new MarginDecoration(getContext()));
      recyclerView.setHasFixedSize(false);

      onItemTouchListener = new RecyclerTouchListener(getContext(), recyclerView,
            new RecyclerClickListener() {
               @Override
               public void onClick(View view, int position) {
                  DtlDetailReviewPath path = new DtlDetailReviewPath(FlowUtil.currentMaster(getContext()), mMerchantName, mAdapter
                        .getAllItems()
                        .get(position), mAdapter.getAllItems()
                        .get(position)
                        .getReviewId(), mIsFromListReview);
                  Flow.get(getContext()).set(path);
               }

               @Override
               public void onLongClick(View view, int position) {

               }
            });
      recyclerView.addOnItemTouchListener(onItemTouchListener);

      scrollingListener = new PaginationScrollListener(linearLayoutManager) {
         @Override
         protected void loadMoreItems() {
            mAdapter.addLoadingFooter();
            isLoading = true;
            getMoreReviewItems();
         }

         @Override
         public boolean isLoading() {
            return isLoading;
         }
      };

      recyclerView.addOnScrollListener(scrollingListener);

      recyclerView.setAdapter(mAdapter);

   }

}
