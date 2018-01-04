package com.worldventures.wallet.ui.settings.help.video.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.worldventures.core.modules.video.model.VideoLocale;
import com.worldventures.wallet.R;

import java.util.List;

public class VideoLocaleAdapter extends ArrayAdapter<VideoLocale> {

   private final LayoutInflater layoutInflater;
   private final List<VideoLocale> data;

   public VideoLocaleAdapter(@NonNull Context context, List<VideoLocale> data) {
      super(context, R.layout.item_wallet_video_locale, data);
      layoutInflater = LayoutInflater.from(context);
      this.data = data;
   }

   @Override
   public View getDropDownView(int position, @Nullable View view, @NonNull ViewGroup parent) {
      ViewHolder holder;
      if (view == null) {
         view = layoutInflater.inflate(R.layout.item_wallet_video_locale, parent, false);

         holder = new ViewHolder();
         holder.tvName = view.findViewById(R.id.tv_name);
         holder.ivFlag = view.findViewById(R.id.iv_flag);

         view.setTag(holder);
      } else {
         holder = (ViewHolder) view.getTag();
      }

      final VideoLocale videoLocale = data.get(position);

      holder.tvName.setTextColor(Color.BLACK);
      holder.tvName.setText(videoLocale.getTitle());
      holder.ivFlag.setImageURI(videoLocale.getImage());

      return view;
   }

   @NonNull
   @Override
   public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
      View view = getDropDownView(position, convertView, parent);
      ((ViewHolder) view.getTag()).tvName.setTextColor(Color.WHITE);
      return view;
   }

   private static class ViewHolder {
      TextView tvName;
      SimpleDraweeView ivFlag;
   }
}
