package com.graduate.seoil.sg_projdct;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

/**
 * Created by baejanghun on 17/04/2019.
 */
public class CategoryParentList extends ExpandableGroup<CategoryChildList> {
    public CategoryParentList(String title, List<CategoryChildList> items) {
        super(title, items);
    }
}
