package com.graduate.seoil.sg_projdct.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.graduate.seoil.sg_projdct.Model.Goal;

import java.util.List;

/**
 * Created by baejanghun on 03/05/2019.
 */
public class StatisticsAdapter extends BaseAdapter {
    Context mContext;
    List<Goal> mGoals;

    public StatisticsAdapter(Context mContext, List<Goal> mGoals) {
        this.mContext = mContext;
        this.mGoals = mGoals;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }

    @Override
    public int getCount() {
        return mGoals.size();
    }
}
