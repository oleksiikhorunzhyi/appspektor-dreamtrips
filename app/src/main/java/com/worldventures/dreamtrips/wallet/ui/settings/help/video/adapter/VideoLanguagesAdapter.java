package com.worldventures.dreamtrips.wallet.ui.settings.help.video.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.video.model.VideoLanguage;

import java.util.List;

public class VideoLanguagesAdapter extends ArrayAdapter<VideoLanguage> {

   private final LayoutInflater layoutInflater;
   private final List<VideoLanguage> data;

   public VideoLanguagesAdapter(@NonNull Context context, List<VideoLanguage> data) {
      super(context, R.layout.adapter_item_video_locale_wallet, data);
      this.layoutInflater = LayoutInflater.from(context);
      this.data = data;
   }

   @NonNull
   @Override
   public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
      ViewHolder holder;
      if (view == null) {
         view = layoutInflater.inflate(R.layout.adapter_item_video_locale_wallet, null);

         holder = new ViewHolder();
         holder.tvName = (TextView) view.findViewById(R.id.tv_name);

         view.setTag(holder);
      } else {
         holder = (ViewHolder) view.getTag();
      }

      holder.tvName.setText(data.get(position).getTitle());

      return view;
   }

   private static class ViewHolder {
      TextView tvName;
   }
}
