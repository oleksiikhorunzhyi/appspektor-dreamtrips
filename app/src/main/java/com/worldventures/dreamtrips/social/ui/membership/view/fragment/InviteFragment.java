package com.worldventures.dreamtrips.social.ui.membership.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.ui.util.permission.PermissionUtils;
import com.worldventures.core.ui.view.DividerItemDecoration;
import com.worldventures.core.ui.view.adapter.BaseDelegateAdapter;
import com.worldventures.core.ui.view.recycler.RecyclerViewStateDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.modules.common.view.adapter.BaseDiffUtilCallback;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragmentWithArgs;
import com.worldventures.dreamtrips.social.domain.entity.Contact;
import com.worldventures.dreamtrips.social.domain.entity.InviteType;
import com.worldventures.dreamtrips.social.ui.membership.bundle.ShareBundle;
import com.worldventures.dreamtrips.social.ui.membership.presenter.InvitePresenter;
import com.worldventures.dreamtrips.social.ui.membership.view.adapter.SimpleImageArrayAdapter;
import com.worldventures.dreamtrips.social.ui.membership.view.cell.ContactCell;
import com.worldventures.dreamtrips.social.ui.membership.view.dialog.AddContactDialog;

import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;

import static com.worldventures.core.ui.util.permission.PermissionConstants.READ_PHONE_CONTACTS;

@Layout(R.layout.fragment_invite)
public class InviteFragment extends BaseFragmentWithArgs<InvitePresenter, ShareBundle> implements InvitePresenter.View, SearchView.OnQueryTextListener {

   @Inject PermissionUtils permissionUtils;

   @InjectView(R.id.search) SearchView search;
   @InjectView(R.id.contactsList) RecyclerView contactsList;
   @InjectView(R.id.buttonContinue) Button buttonContinue;
   @InjectView(R.id.selectionTypeSpinner) Spinner selectionTypeSpinner;
   @InjectView(R.id.containerTemplates) FrameLayout containerTemplates;
   @InjectView(R.id.textViewSelectedCount) TextView textViewSelectedCount;
   @InjectView(R.id.selectedCountContainer) LinearLayout selectedCountContainer;

   private BaseDelegateAdapter<Contact> adapter;
   private RecyclerViewStateDelegate stateDelegate;

   private boolean inhibitSpinner = true;

   @Override
   protected InvitePresenter createPresenter(Bundle savedInstanceState) {
      return new InvitePresenter(getArgs());
   }

   @Override
   public void onCreate(@Nullable Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      stateDelegate = new RecyclerViewStateDelegate();
      stateDelegate.onCreate(savedInstanceState);
   }

   @Override
   public void onSaveInstanceState(Bundle outState) {
      super.onSaveInstanceState(outState);
      stateDelegate.saveStateIfNeeded(outState);
   }

   @Override
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);
      containerTemplates.setVisibility(isTabletLandscape() ? View.VISIBLE : View.GONE);
      stateDelegate.setRecyclerView(contactsList);
      contactsList.setLayoutManager(new LinearLayoutManager(getActivity()));
      contactsList.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
      adapter = new BaseDelegateAdapter<>(getActivity(), this);
      adapter.registerCell(Contact.class, ContactCell.class);
      adapter.registerDelegate(Contact.class, getPresenter()::onMemberCellSelected);
      contactsList.setAdapter(adapter);
      contactsList.addOnScrollListener(new RecyclerView.OnScrollListener() {
         @Override
         public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            search.clearFocus();
         }
      });

      selectionTypeSpinner.setAdapter(new SimpleImageArrayAdapter(getActivity(),
            new Integer[]{R.drawable.ic_invite_mail, R.drawable.ic_invite_phone}));
      selectionTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
         @Override
         public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
            //TODO refactor so that selection is not tied to enum position
            if (inhibitSpinner) {
               inhibitSpinner = false;
               return;
            }
            getPresenter().onTypeSelected(InviteType.values()[position]);
         }

         @Override
         public void onNothingSelected(AdapterView<?> adapterView) { }
      });

      search.setOnQueryTextListener(this);
      search.clearFocus();
      search.setIconifiedByDefault(false);
      search.setOnClickListener(v -> getPresenter().onSearchStart());

      if (isTabletLandscape()) {
         router.moveTo(Route.SELECT_INVITE_TEMPLATE, NavigationConfigBuilder.forFragment()
               .backStackEnabled(false)
               .fragmentManager(getChildFragmentManager())
               .containerId(R.id.containerTemplates)
               .build());
      }
   }

   @Override
   protected void trackViewFromViewPagerIfNeeded() {
      getPresenter().track();
   }

   @OnClick(R.id.textViewDeselectAll)
   public void deselectOnClick() {
      getPresenter().deselectAll();
   }

   @Override
   public void onDestroyView() {
      contactsList.setAdapter(null);
      stateDelegate.onDestroyView();
      search.setOnQueryTextListener(null);
      search.setOnQueryTextFocusChangeListener(null);
      selectionTypeSpinner.setOnItemSelectedListener(null);
      super.onDestroyView();
   }

   @Override
   public void setSelectedCount(int count) {
      textViewSelectedCount.setText(String.format(getString(R.string.selected), count));
   }

   @OnClick(R.id.addContactButton)
   public void addContact() {
      getPresenter().addContactRequired();
   }

   @Override
   public void openTemplateView() {
      router.moveTo(Route.SELECT_INVITE_TEMPLATE, NavigationConfigBuilder.forActivity().build());
   }

   @Override
   public boolean onQueryTextChange(String newText) {
      getPresenter().onQuery(newText);
      return false;
   }

   @Override
   public boolean onQueryTextSubmit(String query) {
      // adapter already has items filtered, nothing to do
      search.clearFocus();
      return false;
   }

   @OnClick(R.id.buttonContinue)
   public void continueAction() {
      getPresenter().continueAction();
   }

   @Override
   public void showNextStepButtonVisibility(boolean isVisible) {
      selectedCountContainer.setVisibility(isVisible ? View.VISIBLE : View.GONE);
      buttonContinue.setVisibility(!isTabletLandscape() && isVisible ? View.VISIBLE : View.GONE);
   }

   @Override
   public void showPermissionDenied(String[] permissions) {
      Snackbar.make(getView(), permissionUtils.equals(permissions, READ_PHONE_CONTACTS) ?
            R.string.no_permission_to_read_contacts : R.string.no_permission_to_write_contacts, Snackbar.LENGTH_SHORT)
            .show();
   }

   @Override
   public void showPermissionExplanationText(String[] permissions) {
      int contentRes = permissionUtils.equals(permissions, READ_PHONE_CONTACTS)
            ? R.string.permission_to_read_contacts
            : R.string.permission_to_write_contacts;
      new MaterialDialog.Builder(getContext())
            .content(contentRes)
            .positiveText(R.string.dialog_ok)
            .negativeText(R.string.dialog_cancel)
            .onPositive((materialDialog, dialogAction) -> getPresenter().onExplanationShown(permissions))
            .onNegative((materialDialog, dialogAction) -> getPresenter().onPermissionDenied(permissions))
            .cancelable(false)
            .show();
   }

   @Override
   public void showAddContactDialog() {
      new AddContactDialog(getActivity()).show(getPresenter()::addMember);
   }

   public void setContactsList(@NonNull List<Contact> contactsList) {
      DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new BaseDiffUtilCallback(adapter.getItems(), contactsList));
      adapter.setItemsNoNotify(contactsList);
      diffResult.dispatchUpdatesTo(adapter);
   }

   @Override
   public void shareLink(@NonNull Intent intent) {
      startActivity(Intent.createChooser(intent, getActivity().getString(R.string.action_share)));
   }
}
