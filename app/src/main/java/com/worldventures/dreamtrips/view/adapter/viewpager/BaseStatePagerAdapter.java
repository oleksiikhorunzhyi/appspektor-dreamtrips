package com.worldventures.dreamtrips.view.adapter.viewpager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class BaseStatePagerAdapter<T extends Fragment> extends FragmentStatePagerAdapter {
    private List<FragmentItem<? extends T>> fragmentItems = new ArrayList<>();

    public BaseStatePagerAdapter(FragmentManager fm) {
        super(fm);

    }

    public void add(FragmentItem<? extends T> item) {
        fragmentItems.add(item);
    }

    public void remove(int index) {
        fragmentItems.remove(index);
    }

    private T getFragment(int i) {
        try {
            T value = fragmentItems.get(i).aClass.newInstance();
            setArgs(i, value);
            return value;
        } catch (Exception e) {
            Timber.e(e, "");
        }
        return null;
    }

    public void setArgs(int position, T fragment) {
    }

    @Override
    public Fragment getItem(int i) {
        return getFragment(i);
    }


    @Override
    public int getCount() {
        return fragmentItems.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentItems.get(position).title;
    }

}