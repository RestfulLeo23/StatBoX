package beaux.thomas.base;

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

//import static java.lang.Math.toIntExact;

/**
 * Created by Owner on 2/16/2018.
 */
public class DatabaseHelper extends SQLiteOpenHelper
{
    public static final String DATABASE_NAME = "statboxDB.db";
    public static final int DB_VERSION = 1;
    public static final String TABLE_ICONS = "ICONS";
    public static final String COL1 = "Activity";
    public static final String COL2 = "Picture";

    public static final String TABLE_METADATA = "StatType Metadata";
    public static final String COL_Activity = "Activity";
    public static final String COL_StatType = "StatType";
    public static final String COL_IsTimer  = "IsTimer";
    public static final String COL_IsGPS = "IsGPS";
    public static final String COL_Unit = "Unit";
    public static final String COL_Description = "Description";


    public static Hashtable<String, List<String>>tablesInfo;
    //We don't want activities with the same name,
    //that'll screw up my hash table, so the front end can
    //check tablesInfo to see if the desired activity is already a thing.
    //Then reject the activity name and prompt for a new one if it is.
    //OR I can use this to check if the activity is already a thing and send back an error code of some kind.
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
        String CREATE_TABLE_ICONS = "CREATE TABLE IF NOT EXISTS ICONS (Activity TEXT PRIMARY KEY, Picture TEXT)";//+ attribute + ” TEXT,”
        db.execSQL(CREATE_TABLE_ICONS);

        //String CREATE_TABLE_METADATA = "CREATE TABLE IF NOT EXISTS StatType_MetaData (Activity NOT NULL TEXT, StatType NOT NULL TEXT, IsTimer TEXT, IsGPS TEXT, Unit TEXT, Description TEXT, PRIMARY KEY(Activity, StatType))";
//        String CREATE_TABLE_METADATA = "CREATE TABLE IF NOT EXISTS StatType_MetaData (Activity NOT NULL TEXT PRIMARY KEY, StatType TEXT, IsTimer TEXT, IsGPS TEXT, Unit TEXT, Description TEXT)";
//        db.execSQL(CREATE_TABLE_METADATA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ICONS);
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_METADATA);
        onCreate(db);
    }

    private Hashtable<String, List<String>> restoreDBState() {
        return getExistingTables();
    }
    private Hashtable<String, List<String>> getExistingTables() {
        //query master table and return it
        Hashtable exists = new Hashtable<String, List<String>>();

        db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        List<String> tableNamesList = new ArrayList<String>();
        if (c.moveToFirst()) {
            while ( !c.isAfterLast() ) {
                tableNamesList.add( c.getString( c.getColumnIndex("name")) );
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
            String[] columnNames = dbCursor.getColumnNames();
            List<String> columnsList = Arrays.asList(columnNames);
            exists.put(tableNames[x], columnsList);
        }
        //this hashtable will have a few extra values in it: the ICONS table, the metadata table, & 2 built-in tables which we need to remove
        exists.remove("ICONS");
        exists.remove("android_metadata");
        exists.remove("sqlite_sequence");
//        exists.remove("TABLE_METADATA");
        return exists;
    }


    //array[0] is the table name, all proceeding indeces are the attributes
    //Attributes MUST be ONE single string. "Pages Read" should be PagesRead or Pages_Read, etc
    //array[0] MUST NOT be a duplicate activity name. This will crash the app.
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
        List<String> l = Arrays.asList(array); //in java there is no array[1:] but there is if it's a list.
        tablesInfo.put(array[0], l.subList(1, array.length)); //This creates an active list of all tables and their attributes
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    //Activity, Picture
    //DatabaseHelper.getsInstance(getApplicationContext()).updateIcons("Running", "some_address string");
    public void updateIcons(String activity, String address)
    {
        db = this.getWritableDatabase();
        db.beginTransaction();

        ContentValues values = new ContentValues();
        values.put("Activity", activity);
        values.put("Picture", address);


        db.insert("ICONS", null, values);
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }


    //Activity, StatType, IsTimer, IsGPS, Unit, Description     ***TO EVERYONE ELSE, STAT TYPE IS STAT NAME AND UNIT IS STAT TYPE**
    //example_array = {"Running", "Duration", "Yes", "No", "minutes", "The duration"}
//    public void updateMeta(String array[])
//    {
//            db = this.getWritableDatabase();
//            db.beginTransaction();
//
//            ContentValues values = new ContentValues();
//            values.put("Activity", array[0]);
//            values.put("StatType", array[1]);
//            values.put("IsTimer", array[2]);
//            values.put("IsGPS", array[3]);
//            values.put("Unit", array[4]);
//            values.put("Description", array[5]);
//
//            db.insert("StatType_MetaData", null, values);
//            db.setTransactionSuccessful();
//            db.endTransaction();
//            db.close();
//    }
//
//    public List<String> pullStatTypeMetadata(String activity, String stattype)
//    {
//        db = getReadableDatabase();
//        String [] columns = {"IsTimer", "IsGPS", "Unit", "Description"}; //these are the columns to be returned. We don't need Activity or StatType
//                                                                        //because those are known (they're what's being passed into this whole function).
//        Cursor cur = db.query("TABLE_METADATA", columns, "Activity = "+activity + " AND StatType = " + stattype, null, null, null, null, null);
//        List<String> theRow = new ArrayList<String>();
//        if (cur.moveToFirst())
//        {
//            while ( !cur.isAfterLast() )
//            {
//                theRow.add( cur.getString( cur.getColumnIndex("name")) );
//                cur.moveToNext();
//            }
//        }
//        db.close();
//        return theRow;
//    }


    //array[] is an entry that's being entered into a table. array[0] is the table in question. All proceeding indeces are the attributes' data
    //array[] must ordered the same as the table was created. It is also case sensitive.
    public void insertData(String array[])      //@('u')@
    {
        db = getReadableDatabase();
        Cursor dbCursor = db.query(array[0], null, null, null, null, null, null);
        String[] columnNames = dbCursor.getColumnNames();
        System.out.print(columnNames);

        db.beginTransaction();

        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        for(int x=1; x<array.length; x++)
        {
            values.put(columnNames[x], array[x]);    //arg1 is column name, arg2 is data
        }

        db.insert(array[0], null, values);
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    //grabActivity queries the database and returns all entries as a Hashtable
    //activity is the activity you want to query. It is case sensitive.
    //The returned Hashtable, the key is the ID primary key for each index.
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
