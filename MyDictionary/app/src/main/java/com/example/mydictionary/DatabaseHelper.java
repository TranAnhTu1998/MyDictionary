package com.example.mydictionary;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    private String DB_PATH = null;
    private static String DB_NAME = "Dictionary.db";
    private SQLiteDatabase myDataBase;
    private final Context myContext;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, 1);
        myContext = context;
        DB_PATH = "/data/data/" + context.getPackageName() + "/" + "databases/";
        Log.e("Path 1",DB_PATH);
    }

    public void createDatabase() throws IOException{
        boolean dbExitst = checkDataBase();
        if(!dbExitst){
            this.getReadableDatabase();
            try {
                copyDataBase();
            }catch(IOException e){
                throw new Error("Error copying database");
            }
        }

    }

    public boolean checkDataBase(){
        SQLiteDatabase checkDB = null;
        try{
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
        }
        catch (SQLiteException e){
            //
        }
        if(checkDB != null){
            checkDB.close();
        }
        return checkDB != null ? true : false;
    }

    public void copyDataBase() throws IOException{
        InputStream myInput = myContext.getAssets().open(DB_NAME);
        String outFileName = DB_PATH + DB_NAME;
        OutputStream myOutput = new FileOutputStream(outFileName);
        byte[] buffer = new byte[1024];
        int length;
        System.out.println("1********Copy");
        while ((length = myInput.read(buffer)) > 0){
            myOutput.write(buffer, 0, length);
        }
        myOutput.flush();
        myOutput.close();
        myInput.close();
        Log.e("copyDataBase","Database copied");
    }

    public void openDataBase() throws SQLException{
        String myPath = DB_PATH + DB_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath,null, SQLiteDatabase.OPEN_READWRITE);
        Log.e("openDataBase","Database opend");
    }

    @Override
    public synchronized void close() {
        if(myDataBase != null){
            myDataBase.close();
        }
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        try {
            this.getReadableDatabase();
            myContext.deleteDatabase(DB_NAME);
            copyDataBase();
        }catch (IOException e){
            e.printStackTrace();
        }
    }


    public Cursor getMeaning(String text){
        Cursor cursor = myDataBase.rawQuery("SELECT description, pronounce FROM av WHERE word=='"+text+"'", null);
        return cursor;
    }

    public Cursor getSuggestion(String text){
        System.out.println("DatbaseHelper.cucur");
        Cursor cursor = myDataBase.rawQuery("SELECT _id, word FROM av WHERE word LIKE '"+text+"%' LIMIT 40", null);
        System.out.println("DatbaseHelper.cucur == null??" + (cursor == null));
        return cursor;
    }

    public void insertHistory(String text){
        myDataBase.execSQL("INSERT INTO history(word_history) VALUES('"+text+"')");
    }

    public Cursor getHistory(){
        Cursor cursor = myDataBase.rawQuery("SELECT DISTINCT word_history, description FROM history h JOIN av a ON h.word_history = a.word ORDER BY h._id DESC", null);
        return cursor;
    }

    public  void deleteHistory(){
        myDataBase.execSQL("DELETE FROM history");
    }

}
