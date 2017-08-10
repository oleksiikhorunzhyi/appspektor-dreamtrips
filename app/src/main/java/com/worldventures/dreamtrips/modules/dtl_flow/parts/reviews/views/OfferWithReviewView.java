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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews.ReviewImages;
import com.worldventures.dreamtrips.modules.dtl_flow.FlowUtil;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.detailReview.DtlDetailReviewPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.adapter.ReviewAdapter;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.model.ReviewObject;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.recycler.MarginDecoration;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.recycler.PaginationScrollListener;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.recycler.RecyclerClickListener;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.recycler.RecyclerTouchListener;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import flow.Flow;

public class OfferWithReviewView extends LinearLayout {

   private RecyclerView recyclerAdapter;
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

   private boolean isFirstLoad = true;

   private boolean isLoading = false;

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
      recyclerAdapter = (RecyclerView) v.findViewById(R.id.recycler_adapter);
      ratingBar2 = (RatingBar) v.findViewById(R.id.ratingBar2);
      tvReviewCount = (TextView) v.findViewById(R.id.tv_review_count);

      initRecycler();
      initAdapter();
      initListener();

   }

   public void addBundle(Bundle bundle) {
      if(isFirstLoad)
         isFirstLoad = false;
      else {
         mAdapter.removeLoadingFooter();
         isLoading = false;
      }

      //List<ReviewObject> mockArray = getMockObjects();
      List<ReviewObject> mockArray = bundle.<ReviewObject>getParcelableArrayList(ARRAY);
      mAdapter.addItems(mockArray);

      mRatingMerchant = bundle.getFloat(RATING_MERCHANT, 0f);
      mCountReview = bundle.getInt(COUNT_REVIEW, 0);
      mMerchantName = bundle.getString(MERCHANT_NAME, "");
      mIsFromListReview = bundle.getBoolean(IS_FROM_LIST_REVIEW, false);

      setUpInfo();
   }

   private List<ReviewObject> getMockObjects(){
      List<ReviewObject> items = new ArrayList<>();

      for(int i=0; i<10; i++){
         List<ReviewImages> urlReviewImages = new ArrayList<>();
         items.add(new ReviewObject("aaa", "aaa", "aaa", 3.2f, "aaa", "aaa", true, urlReviewImages));
      }

      return items;
   }

   public void showNoComments() {
      removeAllViews();
      LayoutInflater.from(getContext()).inflate(R.layout.offer_details_no_review, this, true);
   }

   private void initListener() {
      RecyclerView.OnItemTouchListener onItemTouchListener = new RecyclerTouchListener(getContext(), recyclerAdapter,
            new RecyclerClickListener() {
               @Override
               public void onClick(View view, int position) {
                  DtlDetailReviewPath path = new DtlDetailReviewPath(FlowUtil.currentMaster(getContext()), mMerchantName, mAdapter.getAllItems()
                        .get(position), mAdapter.getAllItems()
                        .get(position)
                        .getReviewId(), mIsFromListReview);
                  Flow.get(getContext()).set(path);
               }

               @Override
               public void onLongClick(View view, int position) {

               }
            });
      recyclerAdapter.addOnItemTouchListener(onItemTouchListener);
   }

   private void setUpInfo() {
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

   private void initRecycler() {
      LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

      recyclerAdapter.setLayoutManager(linearLayoutManager);
      recyclerAdapter.addItemDecoration(new MarginDecoration(getContext()));
      recyclerAdapter.setHasFixedSize(false);

      recyclerAdapter.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
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
      });
   }

   private void getMoreReviewItems(){
      int lastIndex = getNextItemValue();
      mEventListener.onEventAccured(lastIndex);
   }

   private int getNextItemValue() { return mAdapter.getItemCount() + 1;}

   private void initAdapter() {
      recyclerAdapter.setAdapter(mAdapter);
   }

   public interface IMyEventListener {
      void onEventAccured(int indexOf);
   }

   public void setEventListener(IMyEventListener mEventListener) {
      this.mEventListener = mEventListener;
   }
}
