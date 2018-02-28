package com.example.owner.mystatbox;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

import static java.lang.Math.toIntExact;

/**
 * Created by Owner on 2/16/2018.
 */
public class DatabaseHelper extends SQLiteOpenHelper
{
    public static final String DATABASE_NAME = "statboxDB.db";
    public static final int DB_VERSION = 1;
    public static final String TABLE_NAME = "whatever";


    Hashtable<String, List<String>>tablesInfo = new Hashtable<String, List<String>>(); //We don't want activities with the same name,
                                                                                        //that'll screw up my hash table, so the front end can
                                                                                        //check tablesInfo to see if the desired activity is already a thing.
                                                                                        //Then reject the activity name and prompt for a new one if it is.
                                                                                        //DatabaseHelper_Object.tablesInfo.containsKey(String key)
    SQLiteDatabase db;
    //CONSTRUCTOR
    public DatabaseHelper(Context context)  //whenever this is called, the database will be initialized.
    {
        super(context, DATABASE_NAME, null, DB_VERSION);
        db = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) //onCreate is called when the database file does not exist or has not been created yet.
    {
        String tables_string = "";
        if(!tablesInfo.isEmpty())
        {
            for (String key : tablesInfo.keySet()) //for each key (the tables) in the hashtable, do an execSQL.
            {
                tables_string = "";
                tables_string.concat("CREATE TABLE " + key + "(ID INTEGER PRIMARY KEY AUTOINCREMENT) ");
                int valueLength = tablesInfo.get(key).size();
                for(int x=0; x<valueLength; x++) //for each element in the value:
                {
                    tables_string.concat(tablesInfo.get(key).get(x) + " TEXT"); //the string looks something like CREATE TABLE " + TABLE_NAME+ "(ID...) + ATTRIBUTE + "TEXT"
                }                                                               //and the attribute + "text" part is concatenated for each attribute
                db.execSQL(tables_string);
            }
        }
        //String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME+ "(ID INTEGER PRIMARY KEY AUTOINCREMENT)";//+ attribute + ” TEXT,”
        //db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    //array[0] is the table name, all proceeding indeces are the attributes
    //Attributes MUST be ONE single string. "Pages Read" should be PagesRead or Pages_Read, etc
    public void createTable(String array[])     //@('u'@)
    {
        db = getWritableDatabase();
        String tableName = array[0];
        db.execSQL("CREATE TABLE " + tableName+ " (ID INTEGER PRIMARY KEY AUTOINCREMENT)");
        for (int x = 1; x< array.length; x++)
        {
            db.execSQL("ALTER TABLE " + tableName +  " ADD COLUMN " +array[x]+ " STRING");
        }
        List<String> l = Arrays.asList(array); //in java there is no array[1:] but there is if it's a list.
        tablesInfo.put(array[0], l.subList(1, array.length-1)); //This creates an active list of all tables and their attributes
    }

    //array[] is an entry that's being entered into a table. array[0] is the table in question. All proceeding indeces are the attributes' data
    //array[] must ordered the same as the table was created. It is also case sensitive.
    public void insertData(String array[])      //@('u')@
    {
        db = getReadableDatabase();
        Cursor dbCursor = db.query(array[0], null, null, null, null, null, null);
        String[] columnNames = dbCursor.getColumnNames();
        System.out.print(columnNames);

        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        for(int x=1; x<array.length; x++)
        {
            values.put(columnNames[x], array[x]);    //arg1 is column name, arg2 is data
        }

        db.insert(array[0], null, values);
        db.close();
    }

    //grabActivity queries the database and returns all entries as a table
    //activity is the activity you want to query. It is case sensitive.
    //The returned table, there's an extra index to the list: the first element is the ID primary key for easier sorting once returned
    public Hashtable<String, List<String>> grabActivity(String activity)
    {
        db = this.getReadableDatabase();

        Cursor dbCursor = db.query(activity, null, null, null, null, null, null);
        String[] columnNames = dbCursor.getColumnNames();//columnNames is an array with all columns for the activity

        long numberOfRows = DatabaseUtils.queryNumEntries(db, activity);
        Hashtable<String, List<String>> doubles = new Hashtable<String, List<String>>();
        Cursor cur;
        int x2;

        for(int x=0; x < numberOfRows; x++)//runs once per row/entry
        {
            x2=x+1;
            //SQLiteDatabase db = getWritableDatabase();
            cur = db.rawQuery("SELECT * FROM " + activity+ " WHERE ID = " +x2, null);
            if (cur.moveToFirst())
            {
                String [] yData = new String[columnNames.length];

                for (int y = 1; y < columnNames.length; y++) //starting at y=1 to skip over attribute ID
                    yData[y-1] = cur.getString(cur.getColumnIndex(columnNames[y]));
                doubles.put(cur.getString(cur.getColumnIndex(columnNames[0])), Arrays.asList(yData));
            }
        }
        db.close();
        return doubles;
    }

}
