package com.graduate.seoil.sg_projdct;

import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.graduate.seoil.sg_projdct.Model.Category;
import com.graduate.seoil.sg_projdct.Model.CategoryChild;
import com.graduate.seoil.sg_projdct.Model.CategoryJSON;

import java.util.Arrays;
import java.util.List;

public class CategoryDataFactory {
  static List<Category> makeGenres(String category, List<CategoryJSON> subcategory) {
    return Arrays.asList(
        makeRockGenre(category, subcategory),
            makeJazzGenre(),
            makeClassicGenre(),
            makeBluegrassGenre());
  }

  public static Category makeRockGenre(String category, List<CategoryJSON> subcategory) {
    return new Category(category, makeRockArtists(subcategory), R.drawable.ic_under_arrow);
  }

  public static List<CategoryChild> makeRockArtists(List<CategoryJSON> clist) {
    CategoryChild queen = new CategoryChild("Queen", true);
    CategoryChild styx = new CategoryChild("Styx", false);
    CategoryChild reoSpeedwagon = new CategoryChild("REO Speedwagon", false);
    CategoryChild boston = new CategoryChild("Boston", true);

    return Arrays.asList(queen, styx, reoSpeedwagon, boston);
  }

  public static Category makeJazzGenre() {
    return new Category("Jazz", makeJazzArtists(), R.drawable.ic_under_arrow);
  }

  public static List<CategoryChild> makeJazzArtists() {
    CategoryChild milesDavis = new CategoryChild("Miles Davis", true);
    CategoryChild ellaFitzgerald = new CategoryChild("Ella Fitzgerald", true);
    CategoryChild billieHoliday = new CategoryChild("Billie Holiday", false);

    return Arrays.asList(milesDavis, ellaFitzgerald, billieHoliday);
  }

  public static Category makeClassicGenre() {
    return new Category("Classic", makeClassicArtists(), R.drawable.ic_under_arrow);
  }

  public static List<CategoryChild> makeClassicArtists() {
    CategoryChild beethoven = new CategoryChild("Ludwig van Beethoven", false);
    CategoryChild bach = new CategoryChild("Johann Sebastian Bach", true);
    CategoryChild brahms = new CategoryChild("Johannes Brahms", false);
    CategoryChild puccini = new CategoryChild("Giacomo Puccini", false);

    return Arrays.asList(beethoven, bach, brahms, puccini);
  }

  public static Category makeSalsaGenre() {
    return new Category("Salsa", makeSalsaArtists(), R.drawable.ic_under_arrow);
  }

  public static List<CategoryChild> makeSalsaArtists() {
    CategoryChild hectorLavoe = new CategoryChild("Hector Lavoe", true);
    CategoryChild celiaCruz = new CategoryChild("Celia Cruz", false);
    CategoryChild willieColon = new CategoryChild("Willie Colon", false);
    CategoryChild marcAnthony = new CategoryChild("Marc Anthony", false);

    return Arrays.asList(hectorLavoe, celiaCruz, willieColon, marcAnthony);
  }

  public static Category makeBluegrassGenre() {
    return new Category("Bluegrass", makeBluegrassArtists(), R.drawable.ic_under_arrow);
  }

  public static List<CategoryChild> makeBluegrassArtists() {
    CategoryChild billMonroe = new CategoryChild("Bill Monroe", false);
    CategoryChild earlScruggs = new CategoryChild("Earl Scruggs", false);
    CategoryChild osborneBrothers = new CategoryChild("Osborne Brothers", true);
    CategoryChild johnHartford = new CategoryChild("John Hartford", false);

    return Arrays.asList(billMonroe, earlScruggs, osborneBrothers, johnHartford);
  }

}

