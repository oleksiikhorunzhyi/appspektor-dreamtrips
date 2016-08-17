package com.worldventures.dreamtrips.modules.membership.view.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.techery.spares.ui.fragment.InjectingDialogFragment;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.adapter.FilterableArrayListAdapter;
import com.worldventures.dreamtrips.modules.membership.view.util.WrapContentLinearLayoutManager;
import com.worldventures.dreamtrips.modules.reptools.model.VideoLanguage;
import com.worldventures.dreamtrips.modules.reptools.model.VideoLocale;
import com.worldventures.dreamtrips.modules.reptools.view.cell.VideoLanguageCell;
import com.worldventures.dreamtrips.modules.reptools.view.cell.VideoLocaleCell;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.ButterKnife;
import butterknife.InjectView;
import icepick.Icepick;
import icepick.State;


public class FilterLanguageDialogFragment extends InjectingDialogFragment {

   @InjectView(R.id.list_country) RecyclerView listCountry;
   @InjectView(R.id.locale_search) SearchView search;
   @InjectView(R.id.filter_title) TextView title;

   @State ArrayList<VideoLocale> locales;

   private FilterableArrayListAdapter adapter;
   private SelectionListener selectionListener;
   private VideoLocale selectedLocale;

   public Dialog onCreateDialog(Bundle savedInstanceState) {
      final Dialog dialog = super.onCreateDialog(savedInstanceState);
      dialog.getWindow().getAttributes().windowAnimations = R.style.BottomSheetDialog;
      dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
      dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
      dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
      return dialog;
   }

   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      Icepick.restoreInstanceState(this, savedInstanceState);
      View v = inflater.inflate(R.layout.dialog_choose_locale, null);
      ButterKnife.inject(this, v);
      listCountry.setLayoutManager(new WrapContentLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
      adapter = new FilterableArrayListAdapter<>(getActivity(), this);
      adapter.registerCell(VideoLocale.class, VideoLocaleCell.class);
      adapter.registerDelegate(VideoLocale.class, new CellDelegate<VideoLocale>() {
         @Override
         public void onCellClicked(VideoLocale videoLocale) {
            onVideoLocaleSelected(videoLocale);
         }
      });
      adapter.registerCell(VideoLanguage.class, VideoLanguageCell.class);
      adapter.registerDelegate(VideoLanguage.class, new CellDelegate<VideoLanguage>() {
         @Override
         public void onCellClicked(VideoLanguage videoLanguage) {
            onVideoLanguageSelected(videoLanguage);
         }
      });
      adapter.setItems(new ArrayList<>(locales));
      listCountry.setAdapter(adapter);
      search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
         @Override
         public boolean onQueryTextSubmit(String s) {
            return false;
         }

         @Override
         public boolean onQueryTextChange(String s) {
            adapter.setFilter(s);
            return false;
         }
      });
      search.setIconifiedByDefault(false);

      return v;
   }

   @Override
   public void onSaveInstanceState(Bundle outState) {
      super.onSaveInstanceState(outState);
      Icepick.saveInstanceState(this, outState);
   }

   public void setData(ArrayList<VideoLocale> locales) {
      this.locales = locales;
   }

   public void setSelectionListener(SelectionListener selectionListener) {
      this.selectionListener = selectionListener;
   }

   public void onVideoLocaleSelected(VideoLocale videoLocale) {
      selectedLocale = videoLocale;
      title.setText(R.string.filter_video_title_language);
      search.setVisibility(View.GONE);
      adapter.setFilter("");
      adapter.clear();
      adapter.setItems(Arrays.asList(selectedLocale.getLanguage()));
   }

   public void onVideoLanguageSelected(VideoLanguage videoLanguage) {
      if (selectionListener != null) selectionListener.onSelected(selectedLocale, videoLanguage);
      dismiss();
   }

   public interface SelectionListener {
      void onSelected(VideoLocale locale, VideoLanguage language);
   }
}
