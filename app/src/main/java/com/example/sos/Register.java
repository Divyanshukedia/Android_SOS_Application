package com.example.sos;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class Register extends AppCompatActivity {
    Button b1,b2,b3;
    EditText e1;
    ListView listview;
    SQLiteOpenHelper s1;
    SQLiteDatabase sqLiteDatabase;
    Database myDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        e1 = findViewById(R.id.phone);
        b1 = findViewById(R.id.add);
        b2 = findViewById(R.id.delete);
        b3 = findViewById(R.id.view);

        myDB = new Database( this);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sr = e1.getText().toString();
                addData(sr);
                Toast.makeText(Register.this, "Data added", Toast.LENGTH_SHORT).show();
                e1.setText("");
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sqLiteDatabase = myDB.getWritableDatabase();
                String x = e1.getText().toString();
                DeleteData(x);
                Toast.makeText(Register.this, "Data Deleted", Toast.LENGTH_SHORT).show();
            }
        });

        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadData();
            }
        });

    }

    private void LoadData() {
        ArrayList<String> thelist = new ArrayList<>();
        Cursor data = myDB.getListContents();
        if(data.getCount()==0){
            Toast.makeText(this, "There is no content", Toast.LENGTH_SHORT).show();
        }
        else {
            while (data.moveToNext()){
                thelist.add(data.getString(1));
                ListAdapter listAdaptor = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,thelist);
                listview.setAdapter(listAdaptor);
            }
        }
    }

    private boolean DeleteData(String x) {
        return sqLiteDatabase.delete(Database.TABLE_NAME,Database.COL2+ "=?",new String[]{x})>0;
    }

    private void addData(String newEntry) {
        boolean insertData = myDB.addData( newEntry);

        if (insertData==true){

            Toast.makeText(this, "Data added", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, "Unsuccessful", Toast.LENGTH_SHORT).show();
        }
    }
}