package beaux.thomas.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;



import android.widget.TextView;

public class HomeScreen extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "beaux.thomas.base.MESSAGE";
    //public DatabaseHelper StatboX_Database;

    public static final int GET_NEW_ACT = 2;
    public static final int OK = 1;

    public String[] Acts = new String[20];



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homescreen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        updateHomeScreen();
    }

    /** Called when the user taps the Send button */
    public void InputStatMode(View view) {
        Intent intent = new Intent(this, InputStat.class);
        startActivity(intent);
    }

    public void GraphViewMode(View view) {
        Intent intent = new Intent(this, GraphView.class);
        startActivity(intent);
    }

    /** Called when the user taps the Send button */
    public void ViewActivityMode(View view) {
        Intent intent = new Intent(this, ViewActivity.class);
        String message = Acts[0];
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    /** Called when the user taps the Send button */
    public void ActivityManagementMode(View view) {
        Intent intent = new Intent(this, ActivityManagement.class);
        startActivityForResult(intent,GET_NEW_ACT);
        updateHomeScreen();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (GET_NEW_ACT): {
                if (resultCode == OK) {
                    // TODO Extract the data returned from the child Activity.
                    String[] returnValue = data.getStringArrayExtra("ACT");
                    String a = returnValue[0];
                    String[] act = ParseString(a);
                    for (String W : act){
                        System.out.println("OK CODE: "+W);
                    }

                    DatabaseHelper.getsInstance(getApplicationContext()).createTable(act);
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
        // very early on
        Set<String> act_set = DatabaseHelper.getsInstance(getApplicationContext()).tablesInfo.keySet();
        int n = act_set.size();
        String arr[] = new String[n];
        arr = act_set.toArray(arr);
        int size = arr.length;
        for (String x : arr)
            System.out.println("UPDATE SCREEN" + x);

        Button GraphView = findViewById(R.id.Graphview);
        Button Act1 = findViewById(R.id.act1);
        Button Act2 = findViewById(R.id.act2);
        Button Act3 = findViewById(R.id.act3);
        Button Act4 = findViewById(R.id.act4);

        if (size == 0) {
            if (GraphView.getVisibility() == View.VISIBLE) {
                GraphView.setVisibility(View.GONE);
                Act1.setVisibility(View.GONE);
                Act2.setVisibility(View.GONE);
                Act3.setVisibility(View.GONE);
                Act4.setVisibility(View.GONE);
            }
        }
        if (size == 1) {
            if (GraphView.getVisibility() == View.VISIBLE) {
                GraphView.setVisibility(View.VISIBLE);
                Act1.setVisibility(View.VISIBLE);
                Act2.setVisibility(View.GONE);
                Act3.setVisibility(View.GONE);
                Act4.setVisibility(View.GONE);
                Button act1 = findViewById(R.id.act1);
                act1.setText(arr[0]);
            }

        }
        if (size == 2) {
            if (GraphView.getVisibility() == View.VISIBLE) {
                GraphView.setVisibility(View.VISIBLE);
                Act1.setVisibility(View.VISIBLE);
                Act2.setVisibility(View.VISIBLE);
                Act3.setVisibility(View.GONE);
                Act4.setVisibility(View.GONE);

                Button act2 = findViewById(R.id.act2);
                act2.setText(arr[0]);

                Button act1 = findViewById(R.id.act1);
                act1.setText(arr[1]);
            }
        }

        if (size == 3) {
            if (GraphView.getVisibility() == View.VISIBLE) {
                GraphView.setVisibility(View.VISIBLE);
                Act1.setVisibility(View.VISIBLE);
                Act2.setVisibility(View.VISIBLE);
                Act3.setVisibility(View.VISIBLE);
                Act4.setVisibility(View.GONE);

                Button act3 = findViewById(R.id.act3);
                act3.setText(arr[2]);

                Button act2 = findViewById(R.id.act2);
                act2.setText(arr[1]);

                Button act1 = findViewById(R.id.act1);
                act1.setText(arr[0]);
            }
        }
        if (size == 4) {
            if (GraphView.getVisibility() == View.VISIBLE) {
                GraphView.setVisibility(View.VISIBLE);
                Act1.setVisibility(View.VISIBLE);
                Act2.setVisibility(View.VISIBLE);
                Act3.setVisibility(View.VISIBLE);
                Act4.setVisibility(View.VISIBLE);
                Button act4 = findViewById(R.id.act4);
                act4.setText(arr[3]);
                Button act3 = findViewById(R.id.act3);
                act3.setText(arr[2]);
                Button act2 = findViewById(R.id.act2);
                act2.setText(arr[1]);
                Button act1 = findViewById(R.id.act1);
                act1.setText(arr[0]);
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_input, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
