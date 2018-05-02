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
import android.widget.TextView;
import android.widget.Toast;



public class HomeScreen extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "beaux.thomas.base.MESSAGE";
    //public DatabaseHelper StatboX_Database;
    public static final int GET_NEW_ACT = 2;
    public static final int OK = 1;
    public String[] Acts = new String[20];
    public boolean clicked = false;

    private static final int DRIVE_IMPORT_FINISH = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homescreen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        updateHomeScreen();
    }


    public void GoogleDriveAPI(View view){
        //System.out.println("I HAVE CLICKED ON THE Export Button");
        Intent intent = new Intent(this, GoogleDriveAPI.class);
        intent.putExtra("Activity", "");
        startActivity(intent);

    /** Called when the user taps the Send button */
    public void InputStatMode(View view) {
        Intent intent = new Intent(this, InputStat.class);
        startActivity(intent);
    }

    public void TimerMode(View view) {
        Intent intent = new Intent(this, Timer.class);
        startActivity(intent);
    }

    public void GraphViewMode(View view) {
        Intent intent = new Intent(this, DataAnalytics.class);
        startActivity(intent);
    }

    public void ImageMode(View view) {
        System.out.println("IMAGE MORE CLICKED");
        Intent intent = new Intent(this, ImagePicker.class);
        System.out.println("IMAGE MORE CLICKED");
        startActivity(intent);
        System.out.println("IMAGE MORE CLICKED");
>>>>>>> origin/DriveAPIMerge
    }

    /** Called when the user taps the Send button */
    public void ViewActivityMode(View view) {
        TextView tv = (TextView) ((LinearLayout) view).getChildAt(1);
        Intent intent = new Intent(this, ViewActivity.class);
        System.out.println(tv.getText().toString());
        intent.putExtra(EXTRA_MESSAGE, tv.getText().toString());
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

        System.out.println("################UPDATE HOMESCREEN#########################");

        Set<String> act_set = DatabaseHelper.getsInstance(getApplicationContext()).tablesInfo.keySet();

        int n = act_set.size();

        String arr[] = new String[n];
        arr = act_set.toArray(arr);
        int size = arr.length;
        System.out.println("################SIZE n: "+size+"#########################");
        for (String i:arr){
            System.out.println("^^^UHS^^: DATABASE PULL: "+ i +"^^^");
        }

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


        ImageButton[] IMAGES = new ImageButton[6];
        TextView[] names = new TextView[6];

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


        System.out.println("################SIZE n: "+size+"#########################");
        if (size == 0 || size > 6)
        {
            for (int i = 0; i<6 ; i++){
                names[i].setVisibility(View.GONE);
                IMAGES[i].setVisibility(View.GONE);
            }
        }

        else {
            System.out.println("##########$$$$$$$$$$$$$$$$$$#########################");
            for (int i = 0; i<size ; i++){
                System.out.println("########((&&&         "+i);
                names[i].setText(arr[i]);
                String path = DatabaseHelper.getsInstance(getApplicationContext()).pullIcon(arr[i]);
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
            }
            for (int j =size; j < 6 ; j++){
                System.out.println("########((&&&****   "+j);
                IMAGES[j].setVisibility(View.GONE);
                names[j].setVisibility(View.GONE);
            }
        }
        System.out.println("@@@@@@@@@@@@@@@@UPDATE HOMESCREEN CLOSE@@@@@@@@@@@@@@@@@@");
    }

    public void deleteDB(View view){
        System.out.println("DELETING DB");
        DatabaseHelper.getsInstance(getApplicationContext()).death(this);
        updateHomeScreen();
        android.os.Process.killProcess(android.os.Process.myPid());
        finish();
    }
    public void deleteSettingDB(){
        System.out.println("DELETING DB");
        DatabaseHelper.getsInstance(getApplicationContext()).death(this);
        updateHomeScreen();
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
