package com.worldventures.dreamtrips.social.ui.membership.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.worldventures.core.di.qualifier.ForActivity;
import com.worldventures.core.janet.Injector;
import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.ui.annotations.MenuResource;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragmentWithArgs;
import com.worldventures.dreamtrips.social.ui.membership.bundle.TemplateBundle;
import com.worldventures.dreamtrips.social.ui.membership.bundle.UrlBundle;
import com.worldventures.dreamtrips.social.ui.membership.presenter.EditTemplatePresenter;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.InjectView;

@Layout(R.layout.fragment_edit_template)
@MenuResource(R.menu.menu_edit_template)
public class EditTemplateFragment extends BaseFragmentWithArgs<EditTemplatePresenter, TemplateBundle> implements EditTemplatePresenter.View {

   public static final int REQUEST_CODE = 228;

   @Inject @ForActivity Provider<Injector> injector;

   @InjectView(R.id.tv_from) TextView fromContact;
   @InjectView(R.id.tv_to) TextView toContact;
   @InjectView(R.id.tv_subj) TextView subject;
   @InjectView(R.id.wv_preview) WebView preview;
   @InjectView(R.id.et_personal_message) MaterialEditText message;
   @InjectView(R.id.ll_progress) View progressView;

   @Override
   protected EditTemplatePresenter createPresenter(Bundle savedInstanceState) {
      return new EditTemplatePresenter(getArgs());
   }

   @Override
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);
      preview.getSettings().setLoadWithOverviewMode(true);
      preview.getSettings().setUseWideViewPort(true);
      preview.getSettings().setJavaScriptEnabled(true);
      progressView.setVisibility(View.GONE);
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId()) {
         case R.id.action_preview:
            getPresenter().previewAction();
            break;
         case R.id.action_send:
            getPresenter().shareRequest(message.getText().toString());
            break;
      }
      return super.onOptionsItemSelected(item);
   }

   @Override
   public void onActivityResult(int requestCode, int resultCode, Intent data) {
      if (requestCode == REQUEST_CODE) {
         getActivity().finish();
      }
   }

   @Override
   public void setFrom(@NonNull String from) {
      fromContact.setText(from);
   }

   @Override
   public void setSubject(@NonNull String title) {
      subject.setText(title);
   }

   @Override
   public void setTo(@NonNull String to) {
      toContact.setText(to);
   }

   @Override
   public void setWebViewContent(String content) {
      preview.loadData(content, "text/html; charset=UTF-8", null);
   }

   @Override
   public void startLoading() {
      progressView.setVisibility(View.VISIBLE);
   }

   @Override
   public void finishLoading() {
      progressView.setVisibility(View.GONE);
   }

   @Override
   public void openPreviewTemplate(@NonNull UrlBundle bundle) {
      router.moveTo(Route.PREVIEW_TEMPLATE, NavigationConfigBuilder.forActivity().data(bundle).build());
   }

   @Override
   public void openShare(@NonNull Intent intent) {
      startActivityForResult(Intent.createChooser(intent, getActivity().getString(R.string.action_share)), REQUEST_CODE);
   }

   @Override
   public void onResume() {
      preview.onResume();
      super.onResume();
   }

   @Override
   public void onPause() {
      super.onPause();
      preview.onPause();
   }

@Override
   public void onDestroy() {
      super.onDestroy();
      if (preview != null) {
         preview.destroy();
         preview = null;
      }
   }

}
