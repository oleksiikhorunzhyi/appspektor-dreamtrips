package com.worldventures.dreamtrips.modules.common.view.viewpager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.techery.spares.adapter.IRoboSpiceAdapter;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class BasePagerAdapter<T extends FragmentItem> extends FragmentPagerAdapter implements IRoboSpiceAdapter<T> {

    protected List<T> fragmentItems = new ArrayList<>();

    public BasePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public void addItems(List<T> baseItemClasses) {
        fragmentItems.addAll(baseItemClasses);
    }

    public void add(T item) {
        fragmentItems.add(item);
    }

    public void remove(int index) {
        fragmentItems.remove(index);
    }

    public T getFragmentItem(int i) {
        return fragmentItems.get(i);
    }

    @Override
    public void clear() {
        fragmentItems.clear();
    }

    private Fragment getFragment(int i) {
        try {
            Fragment value = fragmentItems.get(i).route.getClazz().newInstance();
            setArgs(i, value);
            return value;
        } catch (Exception e) {
            Timber.e(e, "");
        }
        return null;
    }

    public void setArgs(int position, Fragment fragment) {
    }


    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentItems.get(position).title;
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
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
}