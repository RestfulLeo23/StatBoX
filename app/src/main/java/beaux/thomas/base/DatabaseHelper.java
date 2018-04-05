package beaux.thomas.base;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static android.support.v4.content.ContextCompat.startActivity;

//import static java.lang.Math.toIntExact;

/**
 * Created by David on 2/16/2018.
 */

public class DatabaseHelper extends SQLiteOpenHelper
{
    public static final String DATABASE_NAME = "statboxDB.db";
    public static final int DB_VERSION = 1;
    public static final String TABLE_ICONS = "ICONS";
    public static final String COL1 = "Activity";
    public static final String COL2 = "Picture";

    public static final String TABLE_METADATA = "StatType_Metadata";
    public static final String COL_Activity = "Activity";
    public static final String COL_StatType = "StatType";
    public static final String COL_IsTimer  = "IsTimer";//IsTimer TEXT
    public static final String COL_IsGPS = "IsGPS";
    public static final String COL_Unit = "Unit";
    public static final String COL_Description = "Description";


    public static Hashtable<String, List<String>>tablesInfo;
    //We don't want activities with the same name,
    //that'll screw up my hash table, so the front end can
    //check tablesInfo to see if the desired activity is already a thing.
    //Then reject the activity name and prompt for a new one if it is.

    //DatabaseHelper_Object.tablesInfo.containsKey(String key)

    //To get a list of all Activity names:
    //Set j =  DatabaseHelper.getsInstance(getApplicationContext()).tablesInfo.keySet();
    SQLiteDatabase db;

    //Singleton design pattern
    private static DatabaseHelper sInstance;
    public static synchronized DatabaseHelper getsInstance(Context context)
    {
        if (sInstance == null)
            sInstance = new DatabaseHelper(context.getApplicationContext());
        return sInstance;
    }

    //CONSTRUCTOR
    private DatabaseHelper(Context context)  //whenever this is called, the database will be initialized.
    {
        super(context, DATABASE_NAME, null, DB_VERSION);
        db = getWritableDatabase();
        tablesInfo = restoreDBState();

    }

    @Override
    public void onCreate(SQLiteDatabase db) //onCreate is called when the database file does not exist or has not been created yet.
    {
//        tablesInfo = new Hashtable<String, List<String>>();
//        String tables_string = "";
//        if(!tablesInfo.isEmpty())
//        {
//            for (String key : tablesInfo.keySet()) //for each key (the tables) in the hashtable, do an execSQL.
//            {
//                tables_string = "";
//                tables_string.concat("CREATE TABLE IF NOT EXISTS " + key + "(ID INTEGER PRIMARY KEY AUTOINCREMENT) ");
//                int valueLength = tablesInfo.get(key).size();
//                for(int x=0; x<valueLength; x++) //for each element in the value:
//                {
//                    tables_string.concat(tablesInfo.get(key).get(x) + " TEXT"); //the string looks something like CREATE TABLE " + TABLE_NAME+ "(ID...) + ATTRIBUTE + "TEXT"
//                }                                                               //and the attribute + "text" part is concatenated for each attribute
//                db.execSQL(tables_string);
//            }
//        }
        String CREATE_TABLE_ICONS = "CREATE TABLE IF NOT EXISTS ICONS (Activity TEXT PRIMARY KEY, Picture TEXT);";//+ attribute + ” TEXT,”
        db.execSQL(CREATE_TABLE_ICONS);

        String CREATE_TABLE_METADATA = "CREATE TABLE IF NOT EXISTS "+TABLE_METADATA+" (Activity TEXT, StatType TEXT, IsTimer TEXT, IsGPS TEXT, Unit TEXT, Description TEXT, PRIMARY KEY(Activity, StatType));";
        //String CREATE_TABLE_METADATA = "CREATE TABLE IF NOT EXISTS StatType_MetaData (\"Activity NOT NULL TEXT PRIMARY KEY, StatType TEXT, IsTimer TEXT, IsGPS TEXT, Unit TEXT, Description TEXT\");";
        db.execSQL(CREATE_TABLE_METADATA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ICONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_METADATA);
        onCreate(db);
    }


    private Hashtable<String, List<String>> restoreDBState() {
        return getExistingTables();
    }
    private Hashtable<String, List<String>> getExistingTables()
    {
        //query master table and return it
        Hashtable exists = new Hashtable<String, List<String>>();

        db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        List<String> tableNamesList = new ArrayList<String>();
        if (c.moveToFirst()) {
            while ( !c.isAfterLast() ) {
                tableNamesList.add( c.getString( c.getColumnIndex("name")) );
//System.out.println("REEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE " + c.getString(c.getColumnIndex("name")));
                c.moveToNext();
            }
        }
        String tableNames[] = new String[tableNamesList.size()];
        tableNamesList.toArray(tableNames);
        //so now, tablesNames is an array of all the table names in the database.
        //Now we have to extract the attributes for each of them, in order to fully construct the final hashtable
        for(int x = 0; x< tableNamesList.size(); x++)
        {
            Cursor dbCursor = db.query(tableNames[x], null, null, null, null, null, null);
            String [] columnNames = dbCursor.getColumnNames();
            List<String> columnsList = Arrays.asList(columnNames);
            exists.put(tableNames[x], columnsList.subList(1, columnsList.size()));
//System.out.println("tableNames[x] = " + tableNames[x] + " and " + exists.get(tableNames[x]));
        }
        //this hashtable will have a few extra values in it: the ICONS table, the metadata table, & 2 built-in tables which we need to remove
        exists.remove(TABLE_ICONS);
        exists.remove("android_metadata");
        exists.remove("sqlite_sequence");
        exists.remove(TABLE_METADATA);
//System.out.println("REEEEEEEEEE exists = " + exists);
        db.close();
        c.close();
        return exists;
    }


    //array[0] is the table name, all proceeding indeces are the attributes
    //Attributes MUST be ONE single string. "Pages Read" should be PagesRead or Pages_Read, etc
    //array[0] MUST NOT be a duplicate activity name. This will crash the app.
    //array[0] MUST follow standard java variable naming conventions, or this will crash the app. ie, Can't begin with a number or special character, etc.
    //String [] example_array = {"Running", "Duration",
    //DatabaseHelper.getsInstance(getApplicationContext()).createTable(example_array);
    public void createTable(String array[])     //@('u'@)
    {
        db = getWritableDatabase();
        String tableName = array[0];
        db.beginTransaction();
        db.execSQL("CREATE TABLE " + tableName+ " (ID INTEGER PRIMARY KEY AUTOINCREMENT)");
        for (int x = 1; x< array.length; x++)
        {
            db.execSQL("ALTER TABLE " + tableName +  " ADD COLUMN " +array[x]+ " STRING");
        }

        db.execSQL("ALTER TABLE " + tableName +  " ADD COLUMN " + "Date STRING");

        List<String> temp = Arrays.asList(array); //in java there is no array[1:] but there is if it's a list.
        tablesInfo.put(tableName, temp.subList(1, array.length)); //This creates an active list of all tables and their attributes, besides ID and Date_Time
//        System.out.println("GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG temp.sublist(1, array.length): " + temp.subList(1, array.length));
        //      System.out.println("FFFFFFFFFFFFFFFFFFFFFFFFFF full tablesInfo: " + tablesInfo);
//        System.out.println("keyset test: " + tablesInfo.keySet() + "\nget test with "+tableName+": " + tablesInfo.get(array[0]));
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
        //       System.out.println("FFFFFFFFFFFFFFFFFFFFFFFFFF full tablesInfo: " +tablesInfo);
        //System.out.println("keyset test: " + tablesInfo.keySet() + "\nget test with "+tableName+": " + tablesInfo.get(array[0]));
    }

    //Activity, Picture
    //DatabaseHelper.getsInstance(getApplicationContext()).addIcon("Running", "some_address string");
    public void addIcon(String activity, String address)
    {
        db = this.getWritableDatabase();
        db.beginTransaction();

        ContentValues values = new ContentValues();
        values.put(COL1, activity);
        values.put(COL2, address);


        db.insert("ICONS", null, values);
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }


    //Activity, StatType, IsTimer, IsGPS, Unit, Description             ***TO EVERYONE ELSE, STAT TYPE IS STAT NAME AND UNIT IS STAT TYPE**
    //example_array = {"Running", "Duration", "Yes", "No", "minutes", "The duration"}
    //DatabaseHelper.getsInstance(getApplicationContext()).updateMeta(example_array);
    public void updateMeta(String array[])
    {
        db = this.getWritableDatabase();
        db.beginTransaction();

        ContentValues values = new ContentValues();
        values.put("Activity", array[0]);
        values.put(COL_StatType, array[1]);
        values.put("IsTimer", array[2]);
        values.put("IsGPS", array[3]);
        values.put("Unit", array[4]);
        values.put("Description", array[5]);

//System.out.println("RRRRREEEEEEEEEEEEEEEEEEEEEE\narray = " + array.toString() + "\nvalues = " + values);

        db.insert(TABLE_METADATA, null, values);
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    //Given 2 strings: an activity and a statName, pullStatTypeMetadata() returns a List<String> object with the rest of that stat's metadata.
    //String act = "Running";
    //String stat = "Duration";
    //List<String> aStatData = DatabaseHelper.getsInstance(getApplicationContext()).pullStatTypeMetadata(act, stat);
    public List<String> pullStatTypeMetadata(String activity, String stattype)
    {
        db = getReadableDatabase();
        String [] columns = {COL_IsTimer, COL_IsGPS, COL_Unit, COL_Description}; //these are the columns to be returned. We don't need Activity or StatType
        //because those are known (they're what's being passed into this whole function).
        Cursor cur = db.query(TABLE_METADATA, columns, COL_Activity+ " = \""+activity + "\" AND "+ COL_StatType+" = \"" + stattype + "\"", null, null, null, null, null);
        List<String> theRow = new ArrayList<String>();
        if (cur.moveToFirst())
        {
            int x = 0;
            while ( !cur.isAfterLast() )
            {
                theRow.add( cur.getString( cur.getColumnIndex(columns[x])) );
                cur.moveToNext();
                x++;
            }
        }
        db.close();
        cur.close();
        return theRow;
    }


    //array[] is an entry that's being entered into its activity table. array[0] is the table in question. All proceeding indeces are the attributes' data.
    //array[] must ordered the same as the table was created. It is also case sensitive.
    //DatabaseHelper.getsInstance(getApplicationContext()).insertData(array);
    public void insertData(String array[])      //@('u')@
    {
        db = getReadableDatabase();
        String [] columns = {};
        Cursor dbCursor = db.query(array[0], null, null, null, null, null, null);
        String[] columnNames = dbCursor.getColumnNames();
        //System.out.print("RRRRREEEE4 " + Arrays.asList(columnNames));

        db.beginTransaction();

        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        // System.out.println("fffffffffffffffffffffffffffffff");
        for(int x=1; x<array.length; x++)
        {
            // System.out.println("stuff" + columnNames[x] + ", " + array[x]);
            values.put(columnNames[x], array[x]);    //arg1 is column name, arg2 is data
            //System.out.println("stuff2 " + values);
        }

        values.put("Date", getDateTime());
        //System.out.println("values = " + values);
        db.insert(array[0], null, values);
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }
    private String getDateTime()
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    //grabActivity queries the database and returns all entries as a Hashtable
    //activity is the activity you want to query. It is case sensitive.
    //The returned Hashtable, the key is the ID primary key for each index.
    public Hashtable<String, List<String>> grabActivity(String activity)
    {
        db = this.getReadableDatabase();

        Cursor dbCursor = db.query(activity, null, null, null, null, null, null);
        String[] columnNames = dbCursor.getColumnNames();//columnNames is an array with all columns for the activity
        dbCursor.close();
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


    //Given 2 strings: an activity and a statName, grabActivity_Stat() returns a List<String> object with the all entries of that attribute.
    //String act = "Running";
    //String stat = "Duration";
    //List<String> aStatData = DatabaseHelper.getsInstance(getApplicationContext()).grabActivity_Stat(act, stat);
    public List<String> grabActivity_Stat(String activity, String statname)
    {
        db = getReadableDatabase();
        String [] columns = {statname}; //these are the columns to be returned.

        Cursor cur = db.query(activity, columns, null, null, null, null, null, null);
        List<String> theRow = new ArrayList<String>();
        if (cur.moveToFirst())
        {
            while ( !cur.isAfterLast() )
            {
                theRow.add( cur.getString( cur.getColumnIndex(statname)) );
                cur.moveToNext();
            }
        }
        db.close();
        cur.close();
        return theRow;
    }

    //String [] ex_array = DatabaseHelper.getsInstance(getApplicationContext()).returnLastEntry("Running")
    public String [] returnLastEntry(String act)
    {
        db = getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM "+  act + " WHERE ID = (SELECT MAX(ID) FROM " +act+ ");", null);
        String[] columnNames = cur.getColumnNames();
        ArrayList<String> indeces = new ArrayList<String>();
        int x =1;
//        if (cur.moveToFirst())
//        {
//            while(!cur.isAfterLast())
//            {
//                indeces.add(cur.getString(x));
//                cur.moveToNext();
//                x++;
//            }
//        }
        if (cur.moveToFirst())
        {
            String temp;
            do
            {
                for (int i = 1; i < columnNames.length; i++)
                {
                    temp = cur.getString(i);
                    indeces.add(temp);
                }
                //questions.add(indeces);
            } while (cur.moveToNext());
        }
        db.close();
        cur.close();
        
        return indeces.subList(0, indeces.size()-1).toArray(new String[indeces.size()]);
    }

    //Given 2 strings: an activity and a statName, grabActivity_Stat_withDate() returns an array of lists with the all entries of that attribute
    //in the index 0 list and their dates in the index 1 list of the returned object.
    //String act = "Running";
    //String stat = "Duration";
    //List aStatData_withDate = DatabaseHelper.getsInstance(getApplicationContext()).grabActivity_Stat(act, stat);
    public List [] grabActivity_Stat_withDate(String activity, String statname)
    {
        db = getReadableDatabase();
        String [] columns = {statname, "Date"}; //these are the columns to be returned.

        Cursor cur = db.query(activity, columns, null, null, null, null, null, null);
        List<String> theRow = new ArrayList<String>();
        List<String> theRow2 = new ArrayList<String>();
        if (cur.moveToFirst())
        {
            while ( !cur.isAfterLast() )
            {
                theRow.add( cur.getString( cur.getColumnIndex(statname)) );
                theRow2.add( cur.getString( cur.getColumnIndex("Date")) );
                cur.moveToNext();
            }
        }
        db.close();
        cur.close();
        List [] theArray = {theRow, theRow2};
        return theArray;
    }

    //for testing purposes
    //DatabaseHelper.getsInstance(getApplicationContext()).death();
    public void death(Context context)
    {
        context.deleteDatabase(DATABASE_NAME);
    }
}
