package com.worldventures.dreamtrips.modules.feed.view.fragment;

import android.os.Bundle;
import android.os.Parcelable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.seppius.i18n.plurals.PluralResources;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.NavigationBuilder;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.feed.bundle.FeedEntityDetailsBundle;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityEditClickEvent;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.presenter.FeedEntityDetailsPresenter;
import com.worldventures.dreamtrips.modules.feed.view.custom.FeedActionPanelView;
import com.worldventures.dreamtrips.modules.feed.view.util.FeedActionPanelViewActionHandler;
import com.worldventures.dreamtrips.modules.feed.view.util.FeedEntityContentFragmentFactory;
import com.worldventures.dreamtrips.modules.feed.view.util.FeedItemHeaderHelper;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;
import timber.log.Timber;

@Layout(R.layout.fragment_feed_entity_details)
public class FeedEntityDetailsFragment extends BaseFragmentWithArgs<FeedEntityDetailsPresenter, FeedEntityDetailsBundle> implements FeedEntityDetailsPresenter.View {

    @Inject
    FeedEntityContentFragmentFactory fragmentFactory;

    @Inject
    FeedActionPanelViewActionHandler feedActionHandler;

    @InjectView(R.id.actionView)
    FeedActionPanelView actionView;

    @InjectView(R.id.feedDetailsRootView)
    ViewGroup feedDetailsRootView;

    @Optional
    @InjectView(R.id.user_who_liked)
    TextView usersWhoLiked;

    FeedItemHeaderHelper feedItemHeaderHelper = new FeedItemHeaderHelper();

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        ButterKnife.inject(feedItemHeaderHelper, rootView);
        if (!getArgs().isSlave()) {
            int space = getResources().getDimensionPixelSize(R.dimen.tablet_details_spacing);
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) feedDetailsRootView.getLayoutParams();
            lp.rightMargin = space;
            lp.leftMargin = space;
            feedDetailsRootView.setLayoutParams(lp);
        }

    }

    @Override
    public void setHeader(FeedItem feedItem) {
        fragmentCompass.setContainerId(R.id.entity_content_container);
        fragmentCompass.setSupportFragmentManager(getChildFragmentManager());
        Pair<Route, Parcelable> routeParcelablePair = fragmentFactory.create(feedItem);
        NavigationBuilder.create().with(fragmentCompass).data(routeParcelablePair.second).move(routeParcelablePair.first);
        setupView(feedItem);
    }

    @Override
    public void updateHeader(FeedItem feedItem) {
        setupView(feedItem);
    }

    private void setupView(FeedItem feedItem) {
        actionView.setState(feedItem, isForeignItem(feedItem));
        feedActionHandler.init(actionView);
        feedItemHeaderHelper.set(feedItem, getContext(), getPresenter().getAccount().getId());
        feedItemHeaderHelper.setOnEditClickListener(v -> eventBus.post(new FeedEntityEditClickEvent(feedItem, v)));
        setupLikersPanel(feedItem);
    }

    private void setupLikersPanel(FeedItem feedItem) {
        int likesCount = feedItem.getItem().getLikesCount();
        if (likesCount > 0) {
            String firstUserName = feedItem.getItem().getFirstUserLikedItem();
            if (usersWhoLiked != null && firstUserName != null && !TextUtils.isEmpty(firstUserName)) {
                usersWhoLiked.setVisibility(View.VISIBLE);
                int stringRes = R.plurals.users_who_liked_with_name;
                String appeal = firstUserName;
                if (feedItem.getItem().isLiked()) {
                    stringRes = R.plurals.account_who_liked_item;
                    appeal = getResources().getString(R.string.you);
                }
                Spanned text = null;
                try {
                    text = Html.fromHtml(new PluralResources(getResources()).getQuantityString(stringRes, likesCount - 1, appeal, likesCount - 1));
                } catch (NoSuchMethodException e) {
                    Timber.e("", e);
                }

                usersWhoLiked.setText(text);
            } else {
                usersWhoLiked.setVisibility(View.GONE);
            }
        } else {
            usersWhoLiked.setVisibility(View.GONE);
        }
    }


    @Override
    protected FeedEntityDetailsPresenter createPresenter(Bundle savedInstanceState) {
        return new FeedEntityDetailsPresenter(getArgs());
    }

    private boolean isForeignItem(FeedItem feedItem) {
        return feedItem.getItem().getUser() == null
                || getPresenter().getAccount().getId() == feedItem.getItem().getUser().getId();
    }
}
