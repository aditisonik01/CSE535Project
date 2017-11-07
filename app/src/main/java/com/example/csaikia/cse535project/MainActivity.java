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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    EditText userName;
    String user;
    String user_table = "user_table";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        File file = new File(Environment.getExternalStorageDirectory() + File.separator+"Android/data/MCProject_data");
        final SQLiteDatabase user_db = SQLiteDatabase.openDatabase(file.toString()+"/user_database",null, SQLiteDatabase.CREATE_IF_NECESSARY);
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
                    File userFile = new File(Environment.getExternalStorageDirectory() + File.separator+"Android/data/MCProject_data"+user);
                    if (check_if_user_exists_in_server(userFile)) {
                        Toast.makeText(MainActivity.this, "This user already exists. Please register as another user", Toast.LENGTH_LONG).show();
                    } else {
                        int hashForuser = user.hashCode();
                        Random r = new Random();
                        int i = r.nextInt(11-1)+1;
                        String CSVFile = getCSVData(i);
                        user_db.execSQL("insert into "+user_table+" (user,csvfile) values ("
                        + hashForuser+","+CSVFile+"_test.csv);");

                        // Create the file which I need to upload to the remote server and fog server
                        File src = new File(Environment.getExternalStorageDirectory() + File.separator+"Android/data/MCProject_data/dataset/"+CSVFile+"_train.csv");
                        File target = new File(Environment.getExternalStorageDirectory() + File.separator+"Android/data/MCProject_data/dataset/"+user+".csv");
                        try {
                            copy(src,target);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        // TODO write code to upload this file to server

                        // Now delete the target file
                        try {
                            target.delete();
                        } catch (Exception e) {
                            e.printStackTrace();
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
                    // TODO check if that user is registered
                    // TODO check which server to pick
                    // TODO create data for the user
                    // TODO Match signature and unlock message printed
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

    // TODO write this
    private boolean check_if_user_exists_in_server (File file) {
        return false;
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

    // Reference: https://www.journaldev.com/861/java-copy-file
    private static void copy(File source, File dest) throws IOException {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(source);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            is.close();
            os.close();
        }
    }
}
