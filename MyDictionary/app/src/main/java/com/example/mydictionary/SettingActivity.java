package com.example.mydictionary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.SQLException;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class SettingActivity extends AppCompatActivity {
    private  DatabaseHelper myDbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        //System.out.println("************2");
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_setting);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Strings");

        toolbar.setNavigationIcon(R.drawable.icon_back);

        /**Sự kiện khi người dùng ấn vào nút clear all history thì
         * Sẽ hiện lên một thông báo "bạn có chắc sẽ xóa hết lịch sử từ đã tra"
         * Ấn yes thì chương trình sẽ xóa hế từ trong bảng history
         * Ấn no thì chương trình sẽ hủy thao tác coi như chưa có chuyện gì cả.
         */
        TextView clearHistory = (TextView)findViewById(R.id.clear_history);
        clearHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Mở database và kết nối với biến myDbHelper;
                myDbHelper = new DatabaseHelper(SettingActivity.this);
                try{
                    myDbHelper.openDataBase();
                }catch (SQLException e){
                    e.printStackTrace();
                }
                showAlertDialog();
            }
        });

    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this, R.style.MyDialogTheme);
        builder.setTitle("Are you sure?");
        builder.setMessage("All the history will be deleted");
        String positiveText = "Yes";
        builder.setPositiveButton(positiveText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                myDbHelper.deleteHistory();
            }
        });
        String negativeText = "No";
        builder.setNegativeButton(negativeText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Không làm gì cả.
            }
        });
        AlertDialog dialog = builder.create();
        //Hiện thị của sổ thông báo
        dialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
