package com.worldventures.dreamtrips.core.rx.viewbinding;

import android.support.v4.view.ViewPager;

import rx.Observable;
import rx.Subscriber;
import rx.android.MainThreadSubscription;

import static com.jakewharton.rxbinding.internal.Preconditions.checkUiThread;

public class ViewPagerPageSelectedOnSubscribe implements Observable.OnSubscribe<Integer> {

    final ViewPager viewPager;

    public ViewPagerPageSelectedOnSubscribe(ViewPager viewPager) {
        this.viewPager = viewPager;
    }

    @Override
    public void call(Subscriber<? super Integer> subscriber) {
        checkUiThread();
        //
        ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(position);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        };
        //
        viewPager.addOnPageChangeListener(onPageChangeListener);
        //
        subscriber.add(new MainThreadSubscription() {
            @Override protected void onUnsubscribe() {
                viewPager.removeOnPageChangeListener(onPageChangeListener);
            }
        });
    }
}
