package com.cnbs.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.cnbs.entity.Question;
import com.cnbs.fragment.PractiseChooseFragment;
import com.cnbs.fragment.PractiseMultChooseFragment;
import com.cnbs.fragment.PractiseTofFragment;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/1/7.
 */
public class QuestionAdapter extends FragmentStatePagerAdapter {//FragmentPagerAdapter

    private ArrayList<Question> data;

    public QuestionAdapter(FragmentManager fm, ArrayList<Question> data) {
        super(fm);
        this.data = data;
    }

    @Override
    public Fragment getItem(int position) {
        int type = data.get(position).getType();
        if (type == 1) {
            return PractiseChooseFragment.newInstance(data.get(position));
        } else if (type == 2) {
            return PractiseMultChooseFragment.newInstance(data.get(position));
        } else {
            return PractiseTofFragment.newInstance(data.get(position));
        }
    }

    @Override
    public int getCount() {
        return data.size();
    }

}
