package beaux.thomas.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.List;
import java.util.Set;

import android.widget.Toast;



public class HomeScreen extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "beaux.thomas.base.MESSAGE";
    //public DatabaseHelper StatboX_Database;
    public static final int GET_NEW_ACT = 2;
    public static final int OK = 1;
    public String[] Acts = new String[20];
    public boolean clicked = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homescreen);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        updateHomeScreen();
    }



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
    }

    /** Called when the user taps the Send button */
    public void ViewActivityMode(View view) {
        Button b = (Button) view;
        String message = b.getText().toString();
        Intent intent = new Intent(this, ViewActivity.class);
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    public void GoogleDriveAPIImport(){
        Intent intent = new Intent(this, GoogleDriveAPI.class);
        startActivity(intent);
        updateHomeScreen();
    }

    /** Called when the user taps the Send button */
    public void ActivityManagementMode(View view) {
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
                    //System.out.println("OK RESULT CODE");
                    // TODO Extract the data returned from the child Activity.
                    String[] returnValue = data.getStringArrayExtra("ACT");
                    String[] ACTS = ParseString(returnValue[0]);
                    String[] sType = ParseString(returnValue[1]);
                    String[] meta = ParseString(returnValue[2]);

                    DatabaseHelper.getsInstance(getApplicationContext()).createTable(ACTS);
                    for (int i = 0; i<sType.length; i++){
                        String[] ACTDB = new String[6];
                        ACTDB[0]= ACTS[0];
                        ACTDB[1]= ACTS[i+1];
                        ACTDB[2]= meta[1];
                        ACTDB[3]= meta[0];
                        ACTDB[4]= sType[i];
                        ACTDB[5]= meta[2];
                        DatabaseHelper.getsInstance(getApplicationContext()).updateMeta(ACTDB);
                    }
                    List<String> Pull = DatabaseHelper.getsInstance(getApplicationContext()).tablesInfo.get(ACTS[0]);
                    System.out.println(Pull.toString());
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
        }
    }

    public String[] ParseString(String s){
        String[] result = s.split(";+");
        return result;
    }

    public void updateHomeScreen() {
        Set<String> act_set = DatabaseHelper.getsInstance(getApplicationContext()).tablesInfo.keySet();
        int n = act_set.size();
        String arr[] = new String[n];
        arr = act_set.toArray(arr);
        int size = arr.length;

        Button Act1 = findViewById(R.id.act1);
        Button Act2 = findViewById(R.id.act2);
        Button Act3 = findViewById(R.id.act3);
        Button Act4 = findViewById(R.id.act4);
        Button Act5 = findViewById(R.id.act5);
        Button Act6 = findViewById(R.id.act6);
        Button[] BUTTS = new Button[6];
        BUTTS[0] = Act1;
        BUTTS[1] = Act2;
        BUTTS[2] = Act3;
        BUTTS[3] = Act4;
        BUTTS[4] = Act5;
        BUTTS[5] = Act6;

        if (size == 0 || size > 6)
        {
            for (int i = 0; i<6 ; i++){
                BUTTS[i].setVisibility(View.GONE);
            }
        }
        else {
            for (int i = 0; i<size ; i++){
                BUTTS[i].setVisibility(View.VISIBLE);
                BUTTS[i].setText(arr[i]);
            }
            for (int j =size; j < 6 ; j++){
                BUTTS[j].setVisibility(View.GONE);
            }
        }
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
                // TODO Plugin GoogleDriveAPI Import
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
        outState.putStringArray("Acts", Acts);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.
        Acts = savedInstanceState.getStringArray("Acts");
    }

}
