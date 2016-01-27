package com.messenger.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.messenger.di.MessengerModule;
import com.messenger.flow.path.StyledPath;
import com.messenger.flow.util.FlowActivityHelper;
import com.messenger.flow.util.GsonParceler;
import com.messenger.ui.view.chat.ChatPath;
import com.messenger.ui.view.conversation.ConversationsPath;
import com.techery.spares.annotations.Layout;
import com.techery.spares.utils.ui.SoftInputUtil;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.component.ComponentDescription;
import com.worldventures.dreamtrips.core.component.RootComponentsProvider;
import com.worldventures.dreamtrips.modules.common.view.activity.BaseActivity;
import com.worldventures.dreamtrips.modules.navdrawer.NavigationDrawerPresenter;
import com.worldventures.dreamtrips.modules.navdrawer.NavigationDrawerViewImpl;

import javax.inject.Inject;

import butterknife.InjectView;
import flow.Flow;
import flow.History;
import flow.path.Path;
import flow.path.PathContainerView;

@Layout(R.layout.activity_base_messenger)
public class MessengerActivity extends BaseActivity implements Flow.Dispatcher {

    public static final String EXTRA_CHAT_CONVERSATION_ID = "MessengerActivity#EXTRA_CHAT_CONVERSATION_ID";

    @Inject
    protected RootComponentsProvider rootComponentsProvider;
    @Inject
    protected Gson gson;
    @Inject
    protected NavigationDrawerPresenter navigationDrawerPresenter;

    @InjectView(R.id.drawer)
    protected DrawerLayout drawerLayout;
    @InjectView(R.id.drawer_layout)
    protected NavigationDrawerViewImpl navDrawer;
    @InjectView(R.id.root_container)
    protected PathContainerView container;

    private FlowActivityHelper flowActivityHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //
        String conversationId = getIntent().getStringExtra(EXTRA_CHAT_CONVERSATION_ID);
        //
        initNavDrawer();
        initFlow(conversationId);
        //
        navigationDrawerPresenter.setCurrentComponent(rootComponentsProvider
                .getComponentByKey(MessengerModule.MESSENGER));
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        flowActivityHelper.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        flowActivityHelper.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        flowActivityHelper.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        flowActivityHelper.onNewIntent(intent);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        flowActivityHelper.onSaveState(outState, (View) container);
    }

    @Override
    protected void onDestroy() {
        flowActivityHelper = null;
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (!flowActivityHelper.handleBack())
            super.onBackPressed();
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return flowActivityHelper.provideNonConfigurationInstance();
    }

    private void initNavDrawer() {
        navigationDrawerPresenter = new NavigationDrawerPresenter();
        inject(navigationDrawerPresenter);
        navigationDrawerPresenter.attachView(navDrawer, rootComponentsProvider.getActiveComponents());
        navigationDrawerPresenter.setOnItemReselected(this::itemReseleted);
        navigationDrawerPresenter.setOnItemSelected(this::itemSelected);
        navigationDrawerPresenter.setOnLogout(this::logout);
    }

    private void initFlow(String conversationId) {
        // Init flow
        History defaultBackstack = History.single(provideDefaultScreen());

        if (!TextUtils.isEmpty(conversationId)) {
            defaultBackstack = defaultBackstack
                    .buildUpon()
                    .push(new ChatPath(conversationId))
                    .build();
        }

        flowActivityHelper = new FlowActivityHelper(this, this,
                defaultBackstack, new GsonParceler(gson));

    }

    private Path provideDefaultScreen() {
        return ConversationsPath.MASTER_PATH;
    }

    private void itemSelected(ComponentDescription component) {
        router.openMainWithComponent(component.getKey());
    }

    private void itemReseleted(ComponentDescription route) {
        //
    }

    private void logout() {
        new MaterialDialog.Builder(this)
                .title(getString(R.string.logout_dialog_title))
                .content(getString(R.string.logout_dialog_message))
                .positiveText(getString(R.string.logout_dialog_positive_btn))
                .negativeText(getString(R.string.logout_dialog_negative_btn))
                .positiveColorRes(R.color.theme_main_darker)
                .negativeColorRes(R.color.theme_main_darker)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        //TrackingHelper.logout();
                        //getPresentationModel().logout();
                    }
                }).show();
    }

    @Override
    public Object getSystemService(String name) {
        Object service = null;
        if (flowActivityHelper != null) service = flowActivityHelper.getSystemService(name);
        if (service == null) service = super.getSystemService(name);
        return service;

    }

    //TODO refactor after merge with social and update social router
    public static void startMessenger(Context context) {
        context.startActivity(new Intent(context, MessengerActivity.class));
    }

    //TODO refactor after merge with social and update social router
    public static void startMessengerWithConversation(Context context, String conversationId) {
        Intent resultIntent = new Intent(context, MessengerActivity.class);
        //set args to pending intent
        resultIntent.putExtra(MessengerActivity.EXTRA_CHAT_CONVERSATION_ID, conversationId);
        //
        context.startActivity(resultIntent);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Flow
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void dispatch(Flow.Traversal traversal, Flow.TraversalCallback callback) {
        SoftInputUtil.hideSoftInputMethod(this);
        //
        Path path = traversal.destination.top();
        setNavigation(path);
        //
        container.dispatch(traversal, callback);
    }

    void setNavigation(Path path) {
        boolean enabled = false;
        if (path instanceof StyledPath) {
            StyledPath.PathAttrs attrs = ((StyledPath) path).getAttrs();
            enabled = attrs.isDrawerEnabled();
        }
        //
        drawerLayout.setDrawerLockMode(enabled ?
                DrawerLayout.LOCK_MODE_UNLOCKED :
                DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

}
