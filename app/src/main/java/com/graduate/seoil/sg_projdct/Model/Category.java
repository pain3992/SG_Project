package com.graduate.seoil.sg_projdct.Model;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

/**
 * Created by baejanghun on 16/04/2019.
 */
public class Category extends ExpandableGroup<CategoryChild> {
    private int iconResId;

    public Category(String title, List<CategoryChild> items, int iconResId) {
        super(title, items);
        this.iconResId = iconResId;
    }

    public int getIconResId() {
        return iconResId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Category)) return false;

        Category genre = (Category) o;

        return getIconResId() == genre.getIconResId();

    }

    @Override
    public int hashCode() {
        return getIconResId();
    }
}
