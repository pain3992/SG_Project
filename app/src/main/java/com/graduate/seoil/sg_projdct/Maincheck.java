package com.graduate.seoil.sg_projdct;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.graduate.seoil.sg_projdct.Adapter.ProductAdapter;
import com.graduate.seoil.sg_projdct.Model.Product;

import java.util.ArrayList;

public class Maincheck extends AppCompatActivity {
    private String name;
    private String category;

    @Override
    protected void  onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkmain);

        Intent intent = getIntent();
        category = intent.getStringExtra("category");
        Toast.makeText(this, category, Toast.LENGTH_SHORT).show();

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ArrayList<Company> companies = new ArrayList<>();

        ArrayList<Product> googleProduct = new ArrayList<>();
        googleProduct.add(new Product(name ="Google AdSense"));
        googleProduct.add(new Product(name ="Drive"));
        googleProduct.add(new Product(name ="Mail"));
        googleProduct.add(new Product(name ="Doc"));
        googleProduct.add(new Product(name ="Android"));

        Company google = new Company("Google", googleProduct);
        companies.add(google);

        ArrayList<Product> microsoftProduct = new ArrayList<>();
        microsoftProduct.add(new Product("Windows"));
        microsoftProduct.add(new Product("SkyDrive"));
        microsoftProduct.add(new Product("Microsoft Store"));
        microsoftProduct.add(new Product("Microsoft Office" ));

        Company microsoft = new Company("Microsoft",microsoftProduct);
        companies.add(microsoft);

        ProductAdapter adapter = new ProductAdapter(companies);
        recyclerView.setAdapter(adapter);





    }
}
