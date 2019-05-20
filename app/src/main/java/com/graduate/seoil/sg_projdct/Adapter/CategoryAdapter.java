package com.graduate.seoil.sg_projdct.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.graduate.seoil.sg_projdct.Model.Category;
import com.graduate.seoil.sg_projdct.Model.CategoryChild;
import com.graduate.seoil.sg_projdct.R;
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

import java.util.List;

import static android.view.animation.Animation.RELATIVE_TO_SELF;

/**
 * Created by baejanghun on 16/04/2019.
 */
public class CategoryAdapter extends ExpandableRecyclerViewAdapter<CategoryAdapter.CategoryViewHolder, CategoryAdapter.GenreChildViewHolder> {

    public CategoryAdapter(List<? extends ExpandableGroup> groups) {
        super(groups);
    }

    @Override
    public CategoryViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_genre, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public GenreChildViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_genre_child, parent, false);
        return new GenreChildViewHolder(view);
    }

    @Override
    public void onBindChildViewHolder(GenreChildViewHolder holder, int flatPosition, ExpandableGroup group, int childIndex) {
        final CategoryChild categoryChild = ((Category) group).getItems().get(childIndex);
        holder.setArtistName(categoryChild.getName());
    }

    @Override
    public void onBindGroupViewHolder(CategoryViewHolder holder, int flatPosition, ExpandableGroup group) {
        holder.setGenreTitle(group);
    }

    public class CategoryViewHolder extends GroupViewHolder {

        private TextView genreName;
        private ImageView arrow;
        private ImageView icon;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            genreName = itemView.findViewById(R.id.list_item_genre_name);
            arrow = itemView.findViewById(R.id.list_item_genre_arrow);
            icon = itemView.findViewById(R.id.list_item_genre_icon);
        }

        public void setGenreTitle(ExpandableGroup category) {
            if (category instanceof Category) {
                genreName.setText(category.getTitle());
                icon.setBackgroundResource(((Category) category).getIconResId());
            }
        }

        @Override
        public void expand() {
            animateExpand();
        }

        @Override
        public void collapse() {
            animateCollapse();
        }

        private void animateExpand() {
            RotateAnimation rotate = new RotateAnimation(360, 180, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
            rotate.setDuration(300);
            rotate.setFillAfter(true);
            arrow.setAnimation(rotate);
        }

        private void animateCollapse() {
            RotateAnimation rotate = new RotateAnimation(180, 360, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
            rotate.setDuration(300);
            rotate.setFillAfter(true);
            arrow.setAnimation(rotate);
        }
    }

    public class GenreChildViewHolder extends ChildViewHolder {

        private TextView childTextView;

        public GenreChildViewHolder(View itemView) {
            super(itemView);
            childTextView = itemView.findViewById(R.id.list_item_genre_child_name);
        }

        public void setArtistName(String name) {
            childTextView.setText(name);
        }
    }
}
