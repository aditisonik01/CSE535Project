package com.example.csaikia.cse535project;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    EditText userName;
    String user;
    String user_table = "user_table";
    SQLiteDatabase user_db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("chaynika","******ONCREATE STARTS******");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        File file = new File(Environment.getExternalStorageDirectory() + File.separator+"Android/data/MCProject_data");
        user_db = SQLiteDatabase.openDatabase(file.toString()+"/user_database",null, SQLiteDatabase.CREATE_IF_NECESSARY);
        user_db.beginTransaction();

        if (!tableExists(user_db,user_table)) {
            try {
                user_db.execSQL("create table "+ user_table + " ("
                        + "recID integer PRIMARY KEY autoincrement, "
                        + "user INTEGER, "
                        + "csvfile VARCHAR);"

                );
                user_db.setTransactionSuccessful();
            } catch (SQLiteException e) {

            } finally {
                user_db.endTransaction();
            }
        }

        userName = (EditText) findViewById(R.id.user);
        Button registerButton = (Button) findViewById(R.id.register);
        registerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("chaynika", "Register clicked");
                user = (userName.getText().toString());
                if (!checkUser(user)) {
                    Toast.makeText(MainActivity.this, "Enter Name", Toast.LENGTH_LONG).show();
                } else {
                    // Create a database for the user

                    //My EEG dataset is in directory /sdcard/Android/data/MCProject_data
                    // TODO: Check if that user already exists in server. If it does, then tell the person
                    // If already registered user, it does not allow u to register again
                    // TODO @chaynika needs to change this line

                    if (check_if_user_exists_in_server(user)) {
                        Toast.makeText(MainActivity.this, "This user already exists. Please register as another user", Toast.LENGTH_LONG).show();
                    } else {
                        int hashForuser = user.hashCode();
                        Random r = new Random();
                        int i = r.nextInt(10 - 1+1) + 1;
                        String CSVFile = getCSVData(i);
                        Log.d("chaynika", "User data: "+ hashForuser + " "+CSVFile);
                        try {
                            String string = "insert into " + user_table + " (user,csvfile) values ("
                                    + hashForuser + ",'" + CSVFile + "_test.csv');";
                            Log.d("chaynika","SQLITE query is "+string);
                            user_db.beginTransaction();
                            user_db.execSQL("insert into " + user_table + " (user,csvfile) values ("
                                    + hashForuser + ",'" + CSVFile + "_test.csv');");
                            user_db.setTransactionSuccessful();
                            // Create the file which I need to upload to the remote server and fog server
                            File src = new File(Environment.getExternalStorageDirectory() + File.separator + "Android/data/MCProject_data/dataset/" + CSVFile + "_train.csv");
                            File target = new File(Environment.getExternalStorageDirectory() + File.separator + "Android/data/MCProject_data/dataset/" + user + ".csv");
                            try {
                                copyFile(src, target);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            // TODO write code to upload this file to server

                            // Now delete the target file
                            try {
                                // TODO uncomment this later
//                                if (!target.delete()) {
//                                    throw new IOException();
//                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } catch (SQLiteException e) {

                        } finally {
                            //user_db.close();
                            user_db.endTransaction();
                        }
                    }
                }
            }
        });

        Button unlockButton = (Button) findViewById(R.id.unlock);
        unlockButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("chaynika", "Unlock clicked");
                String user = (userName.getText().toString());
                if (!checkUser(user)) {
                    Toast.makeText(MainActivity.this, "Enter Name", Toast.LENGTH_LONG).show();
                } else {
                    user = (userName.getText().toString());
                    // TODO check which server to pick
                    if (check_if_user_exists_in_server(user)) {
                        int hashForuser = user.hashCode();
                        String query_find_csv = "select csvfile from "+user_table+" where user="+hashForuser;
                        Cursor cursor= user_db.rawQuery(query_find_csv, null);
                        String csv_file_for_test = "";
                        if (cursor.moveToFirst()){
                            do {
                                csv_file_for_test = cursor.getString(0);
                            } while(cursor.moveToNext());
                        }
                        cursor.close();
                        user_db.close();
                        // TODO send the csv_file_for_test file to the cloud for matching
                        // TODO Match signature and unlock message printed
                    } else {
                        Toast.makeText(MainActivity.this, "User not authorized", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

    }

    private boolean checkUser(String user) {
        if (user.length() == 0) {
            return false;
        }
        return true;
    }

    private boolean check_if_user_exists_in_server (String user) {
        String query ="SELECT * FROM "+user_table+" WHERE user="+user.hashCode();
        Cursor cursor= user_db.rawQuery(query,null);
        if(cursor.getCount()>0){
            user_db.close();
            cursor.close();
            return true;
        }else{
            user_db.close();
            cursor.close();
            return false;
        }
    }

    // Reference: https://stackoverflow.com/questions/1601151/how-do-i-check-in-sqlite-whether-a-table-exists
    boolean tableExists(SQLiteDatabase db, String tableName)
    {
        if (tableName == null || db == null || !db.isOpen())
        {
            return false;
        }
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM sqlite_master WHERE type = ? AND name = ?", new String[] {"table", tableName});
        if (!cursor.moveToFirst())
        {
            cursor.close();
            return false;
        }
        int count = cursor.getInt(0);
        cursor.close();
        return count > 0;
    }

    String getCSVData(int i) {
        String csvfile = "";
        if (i<10) {
            csvfile = "S00"+i+"R14";
        } else {
            csvfile = "S010R14";
        }
        return csvfile;
    }

    // Reference: https://stackoverflow.com/questions/106770/standard-concise-way-to-copy-a-file-in-java
    public static void copyFile(File src, File dest) throws IOException {
        if(!dest.exists()) {
            dest.createNewFile();
        }

        FileChannel source_channel = null;
        FileChannel destination_channel = null;

        try {
            source_channel = new FileInputStream(src).getChannel();
            destination_channel = new FileOutputStream(src).getChannel();
            destination_channel.transferFrom(source_channel, 0, source_channel.size());
        }
        finally {
            if(source_channel != null) {
                source_channel.close();
            }
            if(destination_channel != null) {
                destination_channel.close();
            }
        }
    }
}
