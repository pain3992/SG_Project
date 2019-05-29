package com.graduate.seoil.sg_projdct;

import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.graduate.seoil.sg_projdct.Adapter.CategoryAdapter;
import com.graduate.seoil.sg_projdct.Adapter.DocExpandableRecyclerAdapter;
import com.graduate.seoil.sg_projdct.Model.Category;
import com.graduate.seoil.sg_projdct.Model.CategoryJSON;

import java.util.ArrayList;
import java.util.List;

import static com.graduate.seoil.sg_projdct.CategoryDataFactory.makeGenres;

public class CategoryActivity extends AppCompatActivity {
    public static final String RESULT_DATA = "ResultData";

    public CategoryAdapter categoryAdapter;
    private DatabaseReference reference;

    private RecyclerView recycler_view;
    private EditText category_search;

//    private List<CategoryChildList> child;
//    private List<CategoryParentList> parent;
//    private List<CategoryChildList> clist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        recycler_view = findViewById(R.id.recycler_expand);
        recycler_view.setLayoutManager(new LinearLayoutManager(this));
//        parent = new ArrayList<>();
//        child = new ArrayList<>();
//        clist = new ArrayList<>();

        category_search = findViewById(R.id.et_search_category);
        category_search.setSelected(false);

        //Initialize your Firebase app
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        // Reference to your entire Firebase database
        DatabaseReference parentReference = database.getReference("Category");

        parentReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final List<CategoryParentList> parent = new ArrayList<>();

                for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    final String parentKey = snapshot.getKey();
                    snapshot.child("titre").getValue();

                    DatabaseReference childReference = FirebaseDatabase.getInstance().getReference("Category").child(parentKey);
                    childReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            final List<CategoryChildList> child = new ArrayList<>();
                            for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
                                final String childVal = snapshot1.getValue().toString();
                                snapshot1.child("titre").getValue();
                                child.add(new CategoryChildList(childVal));
                            }
                            parent.add(new CategoryParentList(parentKey, child));
                            DocExpandableRecyclerAdapter adapter = new DocExpandableRecyclerAdapter(CategoryActivity.this, parent);
                            recycler_view.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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
