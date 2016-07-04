package com.cnbs.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.cnbs.fragment.Fragment0;
import com.cnbs.fragment.Fragment1;
import com.cnbs.fragment.Fragment2;
import com.cnbs.fragment.Fragment3;
import com.cnbs.hanxirailway.R;
import com.cnbs.viewpager.IconPagerAdapter;

/**
 * Created by Administrator on 2015/10/26.
 */
public class MainAdapter extends FragmentPagerAdapter implements
        IconPagerAdapter {

    private Fragment0 framgent0;
    private Fragment1 framgent1;
    private Fragment2 framgent2;
    private Fragment3 framgent3;

    private String[] TITLE;
//    private static final int[] ICONS = new int[]{
//            R.drawable.main_menu0_selector, R.drawable.main_menu1_selector, R.drawable.main_menu2_selector, R.drawable.main_menu1_selector};


    public MainAdapter(Context context, FragmentManager fm) {
        super(fm);
        // TODO Auto-generated constructor stub
        TITLE = context.getResources().getStringArray(R.array.main_menu);
    }

    @Override
    public Fragment getItem(int arg0) {
        // TODO Auto-generated method stub
        switch (arg0) {
            case 0:
                if (framgent0 == null) {
                    framgent0 = Fragment0.newInstance();
                    return framgent0;
                } else {
                    return framgent0;
                }
            case 1:
                if (framgent1 == null) {
                    framgent1 = Fragment1.newInstance();
                    return framgent1;
                } else {
                    return framgent1;
                }
            case 2:
                if (framgent2 == null) {
                    framgent2 = Fragment2.newInstance();
                    return framgent2;
                } else {
                    return framgent2;
                }
            case 3:
                if (framgent3 == null) {
                    framgent3 = Fragment3.newInstance();
                    return framgent3;
                } else {
                    return framgent3;
                }
        }
        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // TODO Auto-generated method stub
        return TITLE[position % TITLE.length];
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return TITLE.length;
    }

    @Override
    public int getIconResId(int index) {
        return 0;
    }


}