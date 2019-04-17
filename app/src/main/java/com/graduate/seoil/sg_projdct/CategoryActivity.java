package com.graduate.seoil.sg_projdct;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.graduate.seoil.sg_projdct.Adapter.CategoryAdapter;
import com.graduate.seoil.sg_projdct.Model.Category;
import com.graduate.seoil.sg_projdct.Model.CategoryJSON;

import java.util.List;

import static com.graduate.seoil.sg_projdct.CategoryDataFactory.makeGenres;

public class CategoryActivity extends AppCompatActivity {

    public CategoryAdapter categoryAdapter;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        RecyclerView.ItemAnimator animator = recyclerView.getItemAnimator();
        if (animator instanceof DefaultItemAnimator) {
            ((DefaultItemAnimator) animator).setSupportsChangeAnimations(false);
        }

        DatabaseReference reference;
        reference = FirebaseDatabase.getInstance().getReference("Category");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    System.out.println("snapshot --> " + snapshot.getValue());

//                    CategoryJSON category = snapshot.getValue(CategoryJSON.class);
//                    categoryAdapter = new CategoryAdapter(makeGenres(snapshot.getKey(), (List<CategoryJSON>) category));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//        categoryAdapter = new CategoryAdapter(makeGenres());
//        System.out.println("makeGenre --> " + makeGenres());
//        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.setAdapter(categoryAdapter);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        categoryAdapter.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        categoryAdapter.onRestoreInstanceState(savedInstanceState);
    }

}
