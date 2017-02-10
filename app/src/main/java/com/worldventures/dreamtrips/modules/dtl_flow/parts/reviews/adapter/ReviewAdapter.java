package com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.model.ReviewObject;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.media.ImageLoaderHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

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
           String urlImage = mItems.get(position).getUrlImageUser();
           try{
               if (!urlImage.equalsIgnoreCase("null")){
                   imageLoaderHelper.getLoader().load(
                         urlImage,
                         mAvatar);
               } else {
                   mAvatar.setImageResource(R.drawable.noavatar_small);
               }
           } catch (Exception e){
               e.printStackTrace();
               mAvatar.setImageResource(R.drawable.noavatar_small);
           }
            mUserName.setText(String.valueOf(mItems.get(position).getNameUser()));
           try {
               mCommentWrote.setText(getCorrectTimeWrote(mItems.get(position).getTimeWrote()));
           } catch (ParseException e) {
               e.printStackTrace();
           }
           mComment.setText(mItems.get(position).getComment());
            mRating.setRating((mItems.get(position).getRatingCommentUser()) );
        }

        private String getCorrectTimeWrote(String timeWrote) throws ParseException {
            int time = 0;
            String info = "";

            Calendar calendar = getCorrectTime(timeWrote);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy MMM dd HH:mm:ss");
            Calendar localCalendar = Calendar.getInstance();


            time = getDifferenceTime(localCalendar, calendar, Calendar.YEAR);
            //Year
            if (time > 0){
                info = time + " years ago";
            } else {
                //Month
                time = getDifferenceTime(localCalendar, calendar, Calendar.MONTH);
                if (time > 0){
                    info = time + " months ago";
                } else {
                    //days
                    time = getDifferenceTime(localCalendar, calendar, Calendar.DAY_OF_MONTH);
                    if (time > 0){
                        info = time + " days ago";
                    } else {
                        //hours
                        time = getDifferenceTime(localCalendar, calendar, Calendar.HOUR_OF_DAY);
                        if (time > 0){
                            info = time + " hours ago";
                        } else {
                            //min
                            time = getDifferenceTime(localCalendar, calendar, Calendar.MINUTE);
                            if (time > 0){
                                info = time + " min ago";
                            } else {
                                //seg
                                time = getDifferenceTime(localCalendar, calendar, Calendar.SECOND);
                                if (time > 0){
                                    info = time + " sec ago";
                                }
                            }
                        }
                    }
                }
            }
            return info;
        }

        private Calendar getCorrectTime(@NonNull String dateToConvert){
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            df.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = null;
            try {
                date = df.parse(dateToConvert);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            df.setTimeZone(TimeZone.getDefault());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return calendar;
        }

        private int getDifferenceTime(@NonNull Calendar localTime, @NonNull Calendar commentTime, int typeToCompare){
            return localTime.get(typeToCompare) - commentTime.get(typeToCompare);
        }
    }
}
