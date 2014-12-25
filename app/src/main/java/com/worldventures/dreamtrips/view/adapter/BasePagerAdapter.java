package com.worldventures.dreamtrips.view.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.worldventures.dreamtrips.utils.Logs;

import java.util.ArrayList;
import java.util.List;

public class BasePagerAdapter<T extends Fragment> extends FragmentPagerAdapter {
    private List<FragmentItem<? extends T>> fragmentItems = new ArrayList<>();

    public BasePagerAdapter(FragmentManager fm) {
        super(fm);

    }

    public void add(FragmentItem<? extends T> item) {
        fragmentItems.add(item);
    }

    private T getFragment(int i) {
        try {
            T value = fragmentItems.get(i).aClass.newInstance();
            setArgs(value);
            return value;
        } catch (Exception e) {
            Logs.e(e);
        }
        return null;
    }

    public void setArgs(T fragment) {
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

    public static class FragmentItem<T> {
        Class<? extends T> aClass;
        String title;

        public FragmentItem(Class<? extends T> aClass, String title) {
            this.aClass = aClass;
            this.title = title;
        }
    }

}