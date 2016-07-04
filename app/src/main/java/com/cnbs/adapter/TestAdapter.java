package com.cnbs.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.cnbs.entity.Question;
import com.cnbs.entity.TestQS;
import com.cnbs.fragment.TestChooseFragment;
import com.cnbs.fragment.TestMultChooseFragment;
import com.cnbs.fragment.TestTofFragment;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/1/13.
 */
public class TestAdapter extends FragmentStatePagerAdapter {

    private ArrayList<Question> data;

    public TestAdapter(FragmentManager fm, ArrayList<Question> data) {
        super(fm);
        this.data = data;
    }

    @Override
    public Fragment getItem(int position) {
        int type = data.get(position).getType();
        if (type == 1) {
            return TestChooseFragment.newInstance(data.get(position),position);
        } else if (type == 2) {
            return TestMultChooseFragment.newInstance(data.get(position),position);
        } else {
            return TestTofFragment.newInstance(data.get(position),position);
        }
    }

    @Override
    public int getCount() {
        return data.size();
    }
}
