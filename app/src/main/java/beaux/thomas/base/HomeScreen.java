package beaux.thomas.base;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Set;

import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;



public class HomeScreen extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "beaux.thomas.base.MESSAGE";
    //public DatabaseHelper StatboX_Database;
    public static final int GET_NEW_ACT = 2;
    public static final int OK = 1;
    public boolean clicked = false;

    private static final int DRIVE_IMPORT_FINISH = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homescreen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
       // Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        updateHomeScreen();
    }


    /** Called when the user taps the Send button */
    public void InputStatMode(View view) {
        Intent intent = new Intent(this, InputStat.class);
        startActivity(intent);
    }

    /** Called when the user taps the Send button */
    public void ViewActivityMode(View view) {

        LinearLayout first = ((LinearLayout)view);
        View view1 = first.getChildAt(0);

        LinearLayout second = ((LinearLayout)view1);
        View view2 = second.getChildAt(1);

        LinearLayout third = ((LinearLayout)view2);
        TextView view3 = (TextView) third.getChildAt(0);
        //TextView tv = findViewById(R.id.)

        Intent intent = new Intent(this, ViewActivity.class);
        System.out.println(view3.getText().toString());
        intent.putExtra(EXTRA_MESSAGE, view3.getText().toString());
        //String message = b.getText().toString();
        //Intent intent = new Intent(this, ViewActivity.class);
        //intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    public void GoogleDriveAPIImport(){
        Intent intent = new Intent(this, GoogleDriveAPI.class);
        startActivityForResult(intent,DRIVE_IMPORT_FINISH);
    }

    /** Called when the user taps the Send button */
    public void ActivityManagementMode(View view) {
        System.out.println("################CLICKED ON FOB#########################");

        Set<String> act_set = DatabaseHelper.getsInstance(getApplicationContext()).tablesInfo.keySet();
        int n = act_set.size();
        if (n >=6){
            Toast.makeText(getApplicationContext(),"Max Number of Activities",Toast.LENGTH_SHORT).show();
        }
        else {
            Intent intent = new Intent(this, ActivityManagement.class);
            startActivityForResult(intent, GET_NEW_ACT);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (GET_NEW_ACT): {
                if (resultCode == OK) {
                    System.out.println("OK RESULT CODE");
                    // TODO Extract the data returned from the child Activity.
                    String[] returnValue = data.getStringArrayExtra("ACT");
                    String[] ACTS = ParseString(returnValue[0]);
                    String[] sType = ParseString(returnValue[1]);
                    String[] meta = ParseString(returnValue[2]);
                    String filepath = returnValue[3];
                    DatabaseHelper.getsInstance(getApplicationContext()).createTable(ACTS);

                    for (int i = 0; i<sType.length; i++){
                        String[] ACTDB = new String[6];
                        ACTDB[0]= ACTS[0];
                        ACTDB[1]= ACTS[i+1];
                        ACTDB[2]= meta[0];
                        ACTDB[3]= meta[1];
                        ACTDB[4]= sType[i];
                        ACTDB[5]= meta[2];
                        DatabaseHelper.getsInstance(getApplicationContext()).updateMeta(ACTDB);
                    }

                    List<String> Pull = DatabaseHelper.getsInstance(getApplicationContext()).tablesInfo.get(ACTS[0]);
                    System.out.println("** THESE ARE THE STATS: "+Pull.toString()+"***");
                    DatabaseHelper.getsInstance(getApplicationContext()).addIcon(ACTS[0],filepath);
                    updateHomeScreen();
                    break;
                }
                if (resultCode == 666) {
                    System.out.print("ERROR CODE");
                    // TODO Extract the data returned from the child Activity.
                    String[] returnValue = data.getStringArrayExtra("ACT");
                    String a = returnValue[0];
                    String[] act = ParseString(a);
                    for (String W : act){
                        System.out.println("ERROR CODE" + W);
                    }
                    break;
                }
            }
            case (DRIVE_IMPORT_FINISH): {
                if (resultCode == RESULT_OK) {
                    System.out.println("We made it");
                    updateHomeScreen();
                }
            }
        }
    }

    public String[] ParseString(String s){
        String[] result = s.split(";+");
        return result;
    }

    public void updateHomeScreen() {
        ImageButton icon1 = findViewById(R.id.icon1);
        ImageButton icon2 = findViewById(R.id.icon2);
        ImageButton icon3 = findViewById(R.id.icon3);
        ImageButton icon4 = findViewById(R.id.icon4);
        ImageButton icon5 = findViewById(R.id.icon5);
        ImageButton icon6 = findViewById(R.id.icon6);
        TextView name1 = findViewById(R.id.STATNAME1);
        TextView name2 = findViewById(R.id.STATNAME2);
        TextView name3 = findViewById(R.id.STATNAME3);
        TextView name4 = findViewById(R.id.STATNAME4);
        TextView name5 = findViewById(R.id.STATNAME5);
        TextView name6 = findViewById(R.id.STATNAME6);
        TextView EntryNum1 = findViewById(R.id.entryNumber1);
        TextView EntryNum2 = findViewById(R.id.entryNumber2);
        TextView EntryNum3 = findViewById(R.id.entryNumber3);
        TextView EntryNum4 = findViewById(R.id.entryNumber4);
        TextView EntryNum5 = findViewById(R.id.entryNumber5);
        TextView EntryNum6 = findViewById(R.id.entryNumber6);
        TextView statsNum1 = findViewById(R.id.statsNumber1);
        TextView statsNum2 = findViewById(R.id.statsNumber2);
        TextView statsNum3 = findViewById(R.id.statsNumber3);
        TextView statsNum4 = findViewById(R.id.statsNumber4);
        TextView statsNum5 = findViewById(R.id.statsNumber5);
        TextView statsNum6 = findViewById(R.id.statsNumber6);
        TextView date1 = findViewById(R.id.recentDate1);
        TextView date2 = findViewById(R.id.recentDate2);
        TextView date3 = findViewById(R.id.recentDate3);
        TextView date4 = findViewById(R.id.recentDate4);
        TextView date5 = findViewById(R.id.recentDate5);
        TextView date6 = findViewById(R.id.recentDate6);
        TableRow row1 = findViewById(R.id.Table1);
        TableRow row2 = findViewById(R.id.Table2);
        TableRow row3 = findViewById(R.id.Table3);
        TableRow row4 = findViewById(R.id.Table4);
        TableRow row5 = findViewById(R.id.Table5);
        TableRow row6 = findViewById(R.id.Table6);
        TextView timer1 = findViewById(R.id.timer1);
        TextView timer2 = findViewById(R.id.timer2);
        TextView timer3 = findViewById(R.id.timer3);
        TextView timer4 = findViewById(R.id.timer4);
        TextView timer5 = findViewById(R.id.timer5);
        TextView timer6 = findViewById(R.id.timer6);
        TextView gps1 = findViewById(R.id.gps1);
        TextView gps2 = findViewById(R.id.gps2);
        TextView gps3 = findViewById(R.id.gps3);
        TextView gps4 = findViewById(R.id.gps4);
        TextView gps5 = findViewById(R.id.gps5);
        TextView gps6 = findViewById(R.id.gps6);
        TableRow[] rows = new TableRow[6];
        ImageButton[] IMAGES = new ImageButton[6];
        TextView[] names = new TextView[6];
        TextView[] dates = new TextView[6];
        TextView[] stats = new TextView[6];
        TextView[] entries = new TextView[6];
        TextView[] gps = new TextView[6];
        TextView[] timer = new TextView[6];

        IMAGES[0] = icon1;
        IMAGES[1] = icon2;
        IMAGES[2] = icon3;
        IMAGES[3] = icon4;
        IMAGES[4] = icon5;
        IMAGES[5] = icon6;
        names[0]=name1;
        names[1]=name2;
        names[2]=name3;
        names[3]=name4;
        names[4]=name5;
        names[5]=name6;
        rows[0] = row1;
        rows[1] = row2;
        rows[2] = row3;
        rows[3] = row4;
        rows[4] = row5;
        rows[5] = row6;
        dates[0] = date1;
        dates[1] = date2;
        dates[2] = date3;
        dates[3] = date4;
        dates[4] = date5;
        dates[5] = date6;
        entries[0] = EntryNum1;
        entries[1] = EntryNum2;
        entries[2] = EntryNum3;
        entries[3] = EntryNum4;
        entries[4] = EntryNum5;
        entries[5] = EntryNum6;
        stats[0] = statsNum1;
        stats[1] = statsNum2;
        stats[2] = statsNum3;
        stats[3] = statsNum4;
        stats[4] = statsNum5;
        stats[5] = statsNum6;
        gps[0] = gps1;
        gps[1] = gps2;
        gps[2] = gps3;
        gps[3] = gps4;
        gps[4] = gps5;
        gps[5] = gps6;
        timer[0] = timer1;
        timer[1] = timer2;
        timer[2] = timer3;
        timer[3] = timer4;
        timer[4] = timer5;
        timer[5] = timer6;

        Set<String> set_of_activities = DatabaseHelper.getsInstance(getApplicationContext()).tablesInfo.keySet();
        int n = set_of_activities.size();

        String ActArray[] = new String[n];
        ActArray = set_of_activities.toArray(ActArray);
        int size = ActArray.length;

        if (size == 0 || size > 6)
        {
            for (int i = 0; i<6 ; i++){
                names[i].setVisibility(View.GONE);
                IMAGES[i].setVisibility(View.GONE);
                rows[i].setVisibility(View.GONE);
            }
        }

        else {

            for (int i = 0; i<size ; i++){

                List<String> list_of_stats = DatabaseHelper.getsInstance(getApplicationContext()).tablesInfo.get(ActArray[i]);
                int sizeofstats = list_of_stats.size();
                String[] ArrayStats = new String[sizeofstats];
                ArrayStats = list_of_stats.toArray(ArrayStats);
                int size_of_stats = ArrayStats.length;

                List<String> list_Entry = DatabaseHelper.getsInstance(getApplicationContext()).grabActivity_Stat(ActArray[i],ArrayStats[0] );
                int size_of_entries = list_Entry.size();

                rows[i].setVisibility(View.VISIBLE);
                names[i].setText(ActArray[i]);

                String[] metadata = new String[4];
                metadata = DatabaseHelper.getsInstance(getApplicationContext()).pullStatTypeMetadata(ActArray[i],ArrayStats[0]).toArray(metadata);
                dates[i].setText(metadata[3]);

                String path = DatabaseHelper.getsInstance(getApplicationContext()).pullIcon(ActArray[i]);
                Uri targetUri = Uri.parse(path);
                Bitmap bitmap;
                try {
                    bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(targetUri));
                    IMAGES[i].setImageBitmap(bitmap);

                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                IMAGES[i].setVisibility(View.VISIBLE);
                names[i].setVisibility(View.VISIBLE);
                entries[i].setText(Integer.toString(size_of_entries));
                stats[i].setText(Integer.toString(size_of_stats));
                if (metadata[0].equals("1")){
                    timer[i].setVisibility(View.VISIBLE);
                }
                if (metadata[1].equals("1")){
                    gps[i].setVisibility(View.VISIBLE);
                }
                if (metadata[0].equals("0")){
                    timer[i].setVisibility(View.GONE);
                }
                if (metadata[1].equals("0")){
                    gps[i].setVisibility(View.GONE);
                }
            }
            for (int j =size; j < 6 ; j++){
                System.out.println("########((&&&****   "+j);
                IMAGES[j].setVisibility(View.GONE);
                names[j].setVisibility(View.GONE);
                rows[j].setVisibility(View.GONE);
            }
        }
        System.out.println("@@@@@@@@@@@@@@@@UPDATE HOMESCREEN CLOSE@@@@@@@@@@@@@@@@@@");
    }


    public void deleteSettingDB(){
        DatabaseHelper.getsInstance(getApplicationContext()).death(this);
        android.os.Process.killProcess(android.os.Process.myPid());
        finish();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_input, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_settings:
                System.out.println("THIS IS HS ASOIAUDINIMIUIHYHJKIJUHGYHBJNKUHGYVHBJUYGTGFVBHJYGVBHJNUHYGV BNJUHGBV%%%%%%");
                return true;
            case R.id.deletedb:
                deleteSettingDB();
                return true;
            case R.id.import_act:
                GoogleDriveAPIImport();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.

    }

}
