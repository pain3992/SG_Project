package com.graduate.seoil.sg_projdct.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.graduate.seoil.sg_projdct.Company;
import com.graduate.seoil.sg_projdct.CompanyViewHolder;
import com.graduate.seoil.sg_projdct.Model.Product;
import com.graduate.seoil.sg_projdct.ProductViewHolder;
import com.graduate.seoil.sg_projdct.R;
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

public class ProductAdapter extends ExpandableRecyclerViewAdapter<CompanyViewHolder, ProductViewHolder> {
    public ProductAdapter(List<? extends ExpandableGroup> groups) {
        super(groups);
    }

    @Override
    public CompanyViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_checkgoal,parent, false );
        return new CompanyViewHolder(v);
    }

    @Override
    public ProductViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_checkgoal_expandable,parent,false);
        return new ProductViewHolder(v);
    }

    @Override
    public void onBindChildViewHolder(ProductViewHolder holder, int flatPosition, ExpandableGroup group, int childIndex) {
        final Product product = (Product)group.getItems().get(childIndex);
        holder.bind(product);

    }

    @Override
    public void onBindGroupViewHolder(CompanyViewHolder holder, int flatPosition, ExpandableGroup group) {
        final Company company = (Company) group;
        holder.bind(company);

    }
}
