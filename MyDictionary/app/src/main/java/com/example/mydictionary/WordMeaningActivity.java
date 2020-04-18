package com.example.mydictionary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewParent;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.mydictionary.fragments.FragmentAntonyms;
import com.example.mydictionary.fragments.FragmentDefinition;
import com.example.mydictionary.fragments.FragmentExample;
import com.example.mydictionary.fragments.FragmentSynonyms;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class WordMeaningActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private String word;
    DatabaseHelper myDbHelper;
    Cursor cursor = null;

    public String vnDefinition;
    public String enPronounce;
    public String enExample;
    public String enSynonyms;
    public  String enAntonyms;

    TextToSpeech tts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_meaning);

        //Đưa dữ liệu lên người dùng.
        Bundle bundle = getIntent().getExtras();

        word = bundle.getString("word");

        myDbHelper = new DatabaseHelper(this);

        try {
            myDbHelper.openDataBase();
        }catch (SQLException e_sql){
            throw e_sql;
        }

        cursor = myDbHelper.getMeaning(word);

        if(cursor.moveToFirst()){
            vnDefinition = cursor.getString(cursor.getColumnIndex("description"));
            enPronounce = cursor.getString(cursor.getColumnIndex("pronounce"));
            enSynonyms = null;
            enAntonyms = null;
            enExample = null;
            //description, pronounce
        }

        try {
            myDbHelper.openDataBase();
        }catch (SQLException e_sql){
            throw e_sql;
        }
        myDbHelper.insertHistory(word);
        //
        //Đồng thời thêm từ vào bảng "History"
        myDbHelper.insertHistory(word);

        ///
        ImageButton bntSpeak = (ImageButton)findViewById(R.id.bntSpeak) ;
        bntSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tts = new TextToSpeech(WordMeaningActivity.this, new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int i) {
                        if(i == TextToSpeech.SUCCESS){
                            int resul = tts.setLanguage(Locale.getDefault());
                            if(resul == TextToSpeech.LANG_MISSING_DATA || resul == TextToSpeech.LANG_NOT_SUPPORTED){
                                Log.e("error", "This Language is not supported");
                            }else{
                                tts.speak(word,TextToSpeech.QUEUE_FLUSH,null);
                            }
                        }else{
                            Log.e("error", "Initialization Failed!!!");
                        }
                    }
                });
            }
        });


        Toolbar toolbar = (Toolbar)findViewById(R.id.mToolber);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(word);

        toolbar.setNavigationIcon(R.drawable.icon_back);

        viewPager = (ViewPager) findViewById(R.id.tab_viewpager);
        if(viewPager != null){
            setViewPager(viewPager);
        }

        TabLayout tabLayout = (TabLayout)findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
    private class ViewPagerAdapter extends FragmentPagerAdapter{
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();
        ViewPagerAdapter(FragmentManager manager){
            super(manager);
        }

        void addFrag(Fragment fragment, String title){
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }
        @Override
        public CharSequence getPageTitle(int position){
            return mFragmentTitleList.get(position);
        }
    }

    private void setViewPager(ViewPager viewPager){
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        System.out.println("WordMeaningActivity-vnDefinition = " + vnDefinition);
        viewPagerAdapter.addFrag(new FragmentDefinition(this), "Description");
        viewPagerAdapter.addFrag(new FragmentSynonyms(enSynonyms), "Synonyms");
        viewPagerAdapter.addFrag(new FragmentAntonyms(enAntonyms), "Antonyms");
        viewPagerAdapter.addFrag(new FragmentExample(enExample),"Example");
        viewPager.setAdapter(viewPagerAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
