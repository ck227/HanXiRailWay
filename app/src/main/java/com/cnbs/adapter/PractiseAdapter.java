package com.cnbs.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.cnbs.fragment.PractiseFragment;

/**
 * Created by Administrator on 2016/1/7.
 */
public class PractiseAdapter extends FragmentPagerAdapter {

    private PractiseFragment allFragment, singleFragment, mulFragment, tofFragment;

    public PractiseAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                if (allFragment == null) {
                    allFragment = PractiseFragment.newInstance(0);
                    return allFragment;
                } else {
                    return allFragment;
                }
            case 1:
                if (singleFragment == null) {
                    singleFragment = PractiseFragment.newInstance(1);
                    return singleFragment;
                } else {
                    return singleFragment;
                }
            case 2:
                if (mulFragment == null) {
                    mulFragment = PractiseFragment.newInstance(2);
                    return mulFragment;
                } else {
                    return mulFragment;
                }
            case 3:
                if (tofFragment == null) {
                    tofFragment = PractiseFragment.newInstance(3);
                    return tofFragment;
                } else {
                    return tofFragment;
                }
        }
        return null;
    }


    @Override
    public int getCount() {
        return 4;
    }

    public PractiseFragment getAllFragment() {
        return allFragment;
    }

    public PractiseFragment getSingleFragment() {
        return singleFragment;
    }

//    public PractiseFragment getMulFragment() {
//        return mulFragment;
//    }
//
//    public PractiseFragment getTofFragment() {
//        return tofFragment;
//    }

}
