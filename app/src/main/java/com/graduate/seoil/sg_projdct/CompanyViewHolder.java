package com.graduate.seoil.sg_projdct;

import android.view.View;
import android.widget.TextView;

import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

public class CompanyViewHolder extends GroupViewHolder {
    private TextView mTextView;

    public CompanyViewHolder(View itemView) {
        super(itemView);

        mTextView = itemView.findViewById(R.id.textView);

    }
    public  void  bind(Company company){
        mTextView.setText(company.getTitle());

    }
}
