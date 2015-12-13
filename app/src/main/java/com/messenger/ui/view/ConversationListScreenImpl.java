package com.messenger.ui.view;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.worldventures.dreamtrips.R;
import com.messenger.model.ChatConversation;
import com.messenger.ui.adapter.ConversationListAdapter;
import com.messenger.ui.presenter.ConversationListScreenPresenter;
import com.messenger.ui.presenter.ConversationListScreenPresenterImpl;
import com.messenger.ui.presenter.ToolbarPresenter;
import com.messenger.ui.util.recyclerview.VerticalDivider;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import butterknife.ButterKnife;
import butterknife.InjectViews;

public class ConversationListScreenImpl extends BaseViewStateLinearLayout<ConversationListScreen,
        ConversationListScreenPresenter> implements ConversationListScreen {

    private static final int TABS_COUNT = 2;
    private static final int TAB_POSITION_ALL_CHATS = 0;
    private static final int TAB_POSITION_GROUP_CHATS = 1;

    @InjectView(R.id.conversation_list_content_view) View contentView;
    @InjectView(R.id.conversation_list_loading_view) View loadingView;
    @InjectView(R.id.conversation_list_error_view) View errorView;

    @InjectView(R.id.conversation_list_toolbar)
    Toolbar toolbar;
    @InjectView(R.id.conversation_tab_layout)
    TabLayout tabLayout;

    @InjectView(R.id.conversation_list_pager)
    ViewPager viewPager;
    @InjectViews({R.id.conversation_list_all_chats_recycler_view, R.id.conversation_list_group_chats_recycler_view})
    List<RecyclerView> recyclerViews;

    @InjectView(R.id.conversation_list_search_edit_text) EditText searchEditText;

    private ToolbarPresenter toolbarPresenter;

    private List<ConversationListAdapter> recyclerViewsAdapters;

    public ConversationListScreenImpl(Context context) {
        super(context);
        initUi();
    }

    public ConversationListScreenImpl(Context context, AttributeSet attrs) {
        super(context, attrs);
        initUi();
    }

    private void initUi() {
        setOrientation(LinearLayout.VERTICAL);
        setBackgroundColor(getResources().getColor(R.color.main_background));

        LayoutInflater.from(getContext()).inflate(R.layout.screen_conversation_list, this, true);
        ButterKnife.inject(this, this);

        toolbarPresenter = new ToolbarPresenter(toolbar, (AppCompatActivity) getContext());
        toolbarPresenter.enableUpNavigationButton();
        toolbarPresenter.setTitle(R.string.conversation_list_title);

        tabLayout.addTab(tabLayout.newTab().setText(R.string.conversation_list_tab_all_chats), 0);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.conversation_list_tab_group_chats), 1);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition(), true);
            }

            @Override public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        recyclerViewsAdapters = new ArrayList<>();
        for (int i = 0; i < TABS_COUNT; i++) {
            RecyclerView recyclerView = recyclerViews.get(i);
            recyclerView.setSaveEnabled(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.addItemDecoration(new VerticalDivider(getResources().getDrawable(R.drawable.divider_list)));
            ConversationListAdapter adapter = new ConversationListAdapter(getContext());
            adapter.setClickListener(conversation -> getPresenter().onConversationSelected(conversation));
            recyclerView.setAdapter(adapter);
            recyclerViewsAdapters.add(adapter);
        }
        viewPager.setAdapter(new TabsAdapter());
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }

    @Override public void showLoading() {
        contentView.setVisibility(View.GONE);
        loadingView.setVisibility(View.VISIBLE);
        errorView.setVisibility(View.GONE);
    }

    @Override public void showContent() {
        contentView.setVisibility(View.VISIBLE);
        loadingView.setVisibility(View.GONE);
        errorView.setVisibility(View.GONE);
    }

    @Override public void showError(Throwable e) {
        contentView.setVisibility(View.GONE);
        loadingView.setVisibility(View.GONE);
        errorView.setVisibility(View.VISIBLE);
    }

    @Override public void setConversationList(List<ChatConversation> chatConversationList) {
        recyclerViewsAdapters.get(TAB_POSITION_ALL_CHATS).setConversationList(chatConversationList, false);
        recyclerViewsAdapters.get(TAB_POSITION_GROUP_CHATS).setConversationList(chatConversationList, true);
    }

    private class TabsAdapter extends PagerAdapter {
        @Override public Object instantiateItem(ViewGroup container, int position) {
            return recyclerViews.get(position);
        }

        @Override public int getCount() {
            return TABS_COUNT;
        }

        @Override public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

    @Override public ConversationListScreenPresenter createPresenter() {
        return new ConversationListScreenPresenterImpl();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Activity related
    ///////////////////////////////////////////////////////////////////////////

    @Override public AppCompatActivity getActivity() {
        return (AppCompatActivity)getContext();
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        return presenter.onCreateOptionsMenu(menu);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        return presenter.onOptionsItemSelected(item);
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        presenter.onActivityResult(requestCode, resultCode, data);
    }

    @Override public void onDestroy() {
        presenter.onDestroy();
    }
}
