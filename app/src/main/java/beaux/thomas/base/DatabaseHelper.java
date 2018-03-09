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

    //tablesInfo is not being saved. It's being reset each time the system starts again. This is causing the crash
    public static Hashtable<String, List<String>>tablesInfo;
    //We don't want activities with the same name,
    //that'll screw up my hash table, so the front end can
    //check tablesInfo to see if the desired activity is already a thing.
    //Then reject the activity name and prompt for a new one if it is.
    //OR I can use this to check if the activity is already a thing and send back an error code of some kind.
    //DatabaseHelper_Object.tablesInfo.containsKey(String key)
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
        String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS ICONS (Activity TEXT PRIMARY KEY, Picture TEXT)";//+ attribute + ” TEXT,”
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ICONS);
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
        //this hashtable will have a few extra values in it: the ICONS table, and the 2 built-in tables
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