package com.mdweb.tunnumerique.ui.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    public List<Fragment> getmFragmentList() {
        return mFragmentList;
    }

    private final List<Fragment> mFragmentList = new ArrayList<>();//list of fragment in table layout

    public ViewPagerAdapter(FragmentManager manager) {
        super(manager,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFrag(Fragment fragment)
    {

        mFragmentList.add(fragment);

    }



}