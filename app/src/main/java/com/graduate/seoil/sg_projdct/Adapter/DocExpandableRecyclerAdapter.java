package com.graduate.seoil.sg_projdct.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.graduate.seoil.sg_projdct.CategoryActivity;
import com.graduate.seoil.sg_projdct.CategoryChildList;
import com.graduate.seoil.sg_projdct.CategoryParentList;
import com.graduate.seoil.sg_projdct.R;
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

import java.util.List;

/**
 * Created by baejanghun on 17/04/2019.
 */
public class DocExpandableRecyclerAdapter extends ExpandableRecyclerViewAdapter<DocExpandableRecyclerAdapter.MyParentViewHolder, DocExpandableRecyclerAdapter.MyChildViewHolder> {
    private Activity mActivity;
    public DocExpandableRecyclerAdapter(Activity mActivity, List<? extends ExpandableGroup> groups) {
        super(groups);
        this.mActivity = mActivity;
    }

    @Override
    public MyParentViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item_parent, parent, false);
        return new MyParentViewHolder(view);
    }

    @Override
    public MyChildViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item_child, parent, false);
        return new MyChildViewHolder(view);
    }

    @Override
    public void onBindChildViewHolder(MyChildViewHolder holder, int flatPosition, ExpandableGroup group, int childIndex) {
        final CategoryChildList childItem = ((CategoryParentList) group).getItems().get(childIndex);
        holder.onBind(childItem.getTitle());
        final String TitleChild = group.getTitle();
        holder.listChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra(CategoryActivity.RESULT_DATA, childItem.getTitle());
                mActivity.setResult(Activity.RESULT_OK, intent);
                mActivity.finish();
            }

        });
    }

    @Override
    public void onBindGroupViewHolder(MyParentViewHolder holder, int flatPosition, final ExpandableGroup group) {
        holder.setParentTitle(group);

        if (group.getItems() == null) {
            holder.listGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
        }
    }

    public class MyParentViewHolder extends GroupViewHolder {
        public TextView listGroup;

        public MyParentViewHolder(View itemView) {
            super(itemView);
            listGroup = itemView.findViewById(R.id.listParent);
        }
        public void setParentTitle(ExpandableGroup group) {
            listGroup.setText(group.getTitle());
        }
    }

    public class MyChildViewHolder extends ChildViewHolder {
        public TextView listChild;

        public MyChildViewHolder(View itemView) {
            super(itemView);
            listChild = itemView.findViewById(R.id.listChild);
        }

        public void onBind(String Sousdoc) {
            listChild.setText((Sousdoc));
        }
    }
}
