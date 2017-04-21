package com.worldventures.dreamtrips.modules.facebook.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.facebook.CallbackManager;
import com.facebook.login.LoginManager;
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.custom.RecyclerItemClickListener;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.common.view.util.PhotoPickerDelegate;
import com.worldventures.dreamtrips.modules.facebook.model.FacebookAlbum;
import com.worldventures.dreamtrips.modules.facebook.presenter.FacebookAlbumPresenter;
import com.worldventures.dreamtrips.modules.facebook.view.cell.FacebookAlbumCell;
import com.worldventures.dreamtrips.modules.feed.view.util.GridAutofitLayoutManager;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;

@Layout(R.layout.dialog_facebook_select_album)
public class FacebookAlbumFragment extends BaseFragment<FacebookAlbumPresenter> implements FacebookAlbumPresenter.View {

   @InjectView(R.id.lv_items) protected RecyclerView lvItems;
   @InjectView(R.id.progress) protected ProgressBar progressBar;

   @Inject PhotoPickerDelegate photoPickerDelegate;

   private int previousTotal;
   private boolean loading;

   private BaseArrayListAdapter<FacebookAlbum> adapter;
   private GridLayoutManager layoutManager;

   private CallbackManager callbackManager;

   @Override
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);
      progressBar.setVisibility(View.VISIBLE);
      adapter = new BaseArrayListAdapter(getActivity(), this);
      adapter.registerCell(FacebookAlbum.class, FacebookAlbumCell.class);
      lvItems.setAdapter(adapter);

      lvItems.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), (view1, position) -> {
               String facebookId = (adapter.getItem(position)).getId();
               Bundle b = new Bundle();
               b.putString(FacebookPhotoFragment.BUNDLE_ALBUM_ID, facebookId);
               photoPickerDelegate.openFacebookPhoto(b);
            })

      );
      layoutManager = new GridAutofitLayoutManager(getContext(), getContext().getResources()
            .getDimension(R.dimen.photo_picker_size));

      lvItems.setLayoutManager(layoutManager);
      lvItems.addOnScrollListener(new RecyclerView.OnScrollListener() {
         @Override
         public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            int itemCount = layoutManager.getItemCount();
            int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
            scrolled(itemCount, lastVisibleItemPosition);
         }
      });
   }

   public void scrolled(int totalItemCount, int lastVisible) {
      if (totalItemCount > previousTotal) {
         loading = false;
         previousTotal = totalItemCount;
      }
      if (!loading && lastVisible == totalItemCount - 1) {
         getPresenter().requestAlbums(true);
         loading = true;
      }
   }

   @Override
   public void onResume() {
      super.onResume();
      photoPickerDelegate.attachScrollableView(lvItems);
      photoPickerDelegate.setSelectedPhotosProvider(null);
   }

   @Override
   public void setCallbackManager(CallbackManager callbackManager) {
      this.callbackManager = callbackManager;
   }

   @Override
   public void loginToFacebook(Collection<String> permissions) {
      LoginManager.getInstance().logInWithReadPermissions(this, permissions);
   }

   @Override
   public int getItemsCount() {
      return adapter.getItemCount();
   }

   @Override
   public void onDestroyView() {
      this.lvItems.setAdapter(null);
      super.onDestroyView();
   }

   @Override
   public void onActivityResult(int requestCode, int resultCode, Intent data) {
      super.onActivityResult(requestCode, resultCode, data);
      callbackManager.onActivityResult(requestCode, resultCode, data);
   }

   @Override
   protected FacebookAlbumPresenter createPresenter(Bundle savedInstanceState) {
      return new FacebookAlbumPresenter();
   }

   @Override
   public void showAlbums(List<FacebookAlbum> albums) {
      progressBar.setVisibility(View.GONE);
      adapter.addItems(albums);
      adapter.notifyDataSetChanged();
   }

   @Override
   public void back() {
      getActivity().onBackPressed();
   }
}
