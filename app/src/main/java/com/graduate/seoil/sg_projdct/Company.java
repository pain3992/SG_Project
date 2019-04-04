package com.graduate.seoil.sg_projdct;

import com.graduate.seoil.sg_projdct.Model.Product;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

public class Company extends ExpandableGroup<Product> {
    // 승연

    public Company(String title, List<Product> items) {
        super(title, items);
    }
}
