package com.example.mydictionary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import java.io.IOException;
import java.util.ArrayList;

import androidx.cursoradapter.widget.CursorAdapter;
import androidx.cursoradapter.widget.SimpleCursorAdapter;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

//import android.support.v7.app.ActionBarActivity;
public class MainActivity extends AppCompatActivity {
    SearchView search;
    static DatabaseHelper myDbHelper;
    static boolean databaseOpened=false;
    private SimpleCursorAdapter suggestionAdapter;

    ArrayList<History> historyList;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    //RecyclerView.Adapter historyAdapter;
    RecyclerViewAdapterHistory historyAdapter;
    RelativeLayout emptyHistory;
    Cursor cursorHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        search = (SearchView) findViewById(R.id.search_view);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search.setIconified(false);

            }
        });

        myDbHelper = new DatabaseHelper(this);
        /*MainActivity.this.deleteDatabase("Dictionary.db");
        try {
            myDbHelper.copyDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }
        LoadDatabaseAsync  task = new LoadDatabaseAsync(MainActivity.this);
        task.execute();*/
        if(myDbHelper.checkDataBase()){
            openDatabase();
        }
        else{
            LoadDatabaseAsync  task = new LoadDatabaseAsync(MainActivity.this);
            task.execute();
        }


        //
        final String[] from = new String[]{"word"};
        final int[] to = new int[]{R.id.suggestion_text};

        suggestionAdapter = new SimpleCursorAdapter(MainActivity.this, R.layout.suggestion_row, null, from, to, 0){
            @Override
            public void changeCursor(Cursor cursor) {
                super.swapCursor(cursor);
            }
        };
        search.setSuggestionsAdapter(suggestionAdapter);

        //Sử lý sự kiện click vào từng phần từ và ngõ từ.
        search.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {

                return true;
            }

            //Sử lý sự kiện khi ấn vào từng phần tử
            @Override
            public boolean onSuggestionClick(int position) {
                //Them click khi go
                CursorAdapter cursorAdapter = search.getSuggestionsAdapter();
                Cursor cursor = cursorAdapter.getCursor();
                cursor.moveToPosition(position);
                String click_word = cursor.getString(cursor.getColumnIndex("word"));
                System.out.println("main-click_word : " + click_word);
                search.setQuery(click_word, false);

                //Seach
                search.clearFocus();
                search.setFocusable(false);

                Intent intent = new Intent(MainActivity.this, WordMeaningActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("word", click_word);
                intent.putExtras(bundle);
                startActivity(intent);
                return true;
            }
        });
        //Seach khi ấn nút enter
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Lấy text từ thanh công cụ search;
                String text = search.getQuery().toString();
                Cursor cursor = myDbHelper.getMeaning(text);
                System.out.println("Cursor cursor = myDbHelper.getMeaning(text) - WordMeaning = ??0 : " +  cursor.getCount() );
                if(cursor.getCount() == 0){
                    search.setQuery("",false);
                    //Hiện thanh thông báo không tìm được từ cần tra
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.MyDialogTheme);
                    builder.setTitle("Word Not Found");
                    builder.setMessage("Please search again");

                    String positiveText = getString(android.R.string.ok);
                    builder.setPositiveButton(positiveText, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    String negativeText = getString(android.R.string.cancel);

                    builder.setNegativeButton(negativeText, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            search.clearFocus();
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else{
                    System.out.println("Vào else - WordMeaningActivity");
                    search.clearFocus();
                    search.setFocusable(false);

                    Intent intent = new Intent(MainActivity.this, WordMeaningActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("word", text);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                search.setIconifiedByDefault(false);
                Cursor cursorSuggestion = myDbHelper.getSuggestion(newText);
                suggestionAdapter.changeCursor(cursorSuggestion);
                return false;
            }
        });

        //Phần triển khai giao diện lịch sử người dùng.
        emptyHistory = (RelativeLayout)findViewById(R.id.empty_history);
        //Recycler View
        recyclerView = (RecyclerView)findViewById(R.id.recycler_view_history);
        layoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(layoutManager);

        fetch_history();

    }

    protected static void openDatabase() {
        try {
            myDbHelper.openDataBase();
            databaseOpened=true;
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the menu_main, this adds items to the action bar if it is present
        //System.out.println("*************----1");
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //Handle action bar item click here. The action bar wil automatically bandle clicks on the home/Up button
        //System.out.println("*************0");
        int id = item.getItemId();
        if(id == R.id.action_settings){
            //Action
            //System.out.println("*************1");
            Intent intent = new Intent(MainActivity.this,SettingActivity.class);
            startActivity(intent);
            return true;

        }

        if(id == R.id.action_exit){
            System.exit(0);
            return true;
        }
        return super.onOptionsItemSelected(item);

    }
    //
    @Override
    protected void onResume() {
        super.onResume();
        fetch_history();
    }

    //

    private  void fetch_history(){
        historyList = new ArrayList<>();
        History h;
        if(databaseOpened){
            cursorHistory = myDbHelper.getHistory();
            if(cursorHistory.moveToFirst()){
                //Nạp dữ liệu lên giao diện người dùng.
                do{
                    System.out.println("1.Main.cusorHistory" +  cursorHistory);
                    System.out.println("2.Main.cusorHistory == null??"+ (cursorHistory == null));
                    h = new History(cursorHistory.getString(cursorHistory.getColumnIndex("word_history")), cursorHistory.getString(cursorHistory.getColumnIndex("description")));
                    //h = new History(string);
                    System.out.println("main.h: " + h.getWord());
                    historyList.add(h);
                }while (cursorHistory.moveToNext());
            }
            historyAdapter = new RecyclerViewAdapterHistory(this, historyList);
            recyclerView.setAdapter(historyAdapter);
            historyAdapter.notifyDataSetChanged();
            if(historyAdapter.getItemCount() == 0){
                emptyHistory.setVisibility(View.VISIBLE);
            }else {
                emptyHistory.setVisibility(View.GONE);
            }
        }
    }
}
