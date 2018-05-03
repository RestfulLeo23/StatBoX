package beaux.thomas.base;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.view.Menu;

import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;

import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;
import java.util.List;

import static beaux.thomas.base.HomeScreen.EXTRA_MESSAGE;

public class ViewActivity extends AppCompatActivity {
    public static final int GET_NEW_STAT = 2;
    public static final int OK = 1;
    final Context context = this;
    private Button button;
    public String ActivityNAME;
    public String Des;
    public String editName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String message = intent.getStringExtra(EXTRA_MESSAGE);
        ActivityNAME = message;
        // Capture the layout's TextView and set the string as its text
        TextView textView = findViewById(R.id.StatName);
        textView.setText(message);
        updateStatScreen();
        registerForContextMenu(findViewById(R.id.Row1));
        registerForContextMenu(findViewById(R.id.Row2));
        registerForContextMenu(findViewById(R.id.Row3));
        registerForContextMenu(findViewById(R.id.Row4));
        registerForContextMenu(findViewById(R.id.Row5));
    }

    /**
     * Called when the user taps the Send button
     */
    public void InputStatMode(View view) {
        Intent intent = new Intent(this, Input.class);
        intent.putExtra(EXTRA_MESSAGE, ActivityNAME);
        startActivityForResult(intent, GET_NEW_STAT);
    }

    public void DataAnalytics(View view) {
        TextView tv = (TextView) ((LinearLayout) view).getChildAt(0);
        Intent intent = new Intent(this, DataAnalytics.class);
        intent.putExtra("Activity", ActivityNAME);
        intent.putExtra("Stat", tv.getText().toString());
        context.startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case (GET_NEW_STAT): {
                if (resultCode == OK) {

                    List<String> Pull = DatabaseHelper.getsInstance(getApplicationContext()).tablesInfo.get(ActivityNAME);
                    int n = Pull.size();
                    //System.out.println("OK ACTIVITY RESULT");
                    // TODO Extract the data returned from the child Activity.
                    String[] returnValue = ParseString(data.getStringArrayExtra("STAT")[0]);
                    System.out.println(returnValue.length);
                    String[] newstat = new String[n + 1];
                    int o = 1;
                    newstat[0] = ActivityNAME;
                        for (String i : returnValue) {
                            System.out.println("Retrun Valuses: "+i);
                            newstat[o] = i;
                            //  System.out.println("NEW STATS AT BEGINING"+newstat[o]+" INDEX:"+o);
                            o = o + 1;
                        }
                        DatabaseHelper.getsInstance(getApplicationContext()).insertData(newstat);
                        // Ideally here is where ill add the stat to the activity and then store it in
                        // the DB.
                        //System.out.println("before stat pull");
                        //System.out.println(DatabaseHelper.getsInstance(getApplicationContext())
                        updateStatScreen();
                        break;
                    }
                }
            }
        }

    public void updateStatScreen() {

        TextView ActivityName = findViewById(R.id.StatName);
        TextView Description = findViewById(R.id.Des);
        TextView stat1 = findViewById(R.id.Stat1);
        TextView stat2 = findViewById(R.id.Stat2);
        TextView stat3 = findViewById(R.id.Stat3);
        TextView stat4 = findViewById(R.id.Stat4);
        TextView stat5 = findViewById(R.id.Stat5);
        TextView stat6 = findViewById(R.id.Stat6);
        TextView T1 = findViewById(R.id.type1);
        TextView T2 = findViewById(R.id.type2);
        TextView T3 = findViewById(R.id.type3);
        TextView T4 = findViewById(R.id.type4);
        TextView T5 = findViewById(R.id.type5);
        TextView T6 = findViewById(R.id.type6);
        TextView R1 = findViewById(R.id.recent1);
        TextView R2 = findViewById(R.id.recent2);
        TextView R3 = findViewById(R.id.recent3);
        TextView R4 = findViewById(R.id.recent4);
        TextView R5 = findViewById(R.id.recent5);
        TextView R6 = findViewById(R.id.recent6);
        LinearLayout Timer = findViewById(R.id.Row6);
        LinearLayout GPS = findViewById(R.id.Row7);

        TextView[] Types = new TextView[6];
        String[] t = new String[4];
        String[] types = new String[5];
        TextView[] Rs = new TextView[6];
        TextView[] Stats = new TextView[6];
        LinearLayout[] LL = new LinearLayout[5];

        LL[0] = findViewById(R.id.Row1);
        LL[1] = findViewById(R.id.Row2);
        LL[2] = findViewById(R.id.Row3);
        LL[3] = findViewById(R.id.Row4);
        LL[4] = findViewById(R.id.Row5);
        Stats[0] = stat1;
        Stats[1] = stat2;
        Stats[2] = stat3;
        Stats[3] = stat4;
        Stats[4] = stat5;
        Stats[5] = stat6;
        Types[0] = T1;
        Types[1] = T2;
        Types[2] = T3;
        Types[3] = T4;
        Types[4] = T5;
        Types[5] = T6;
        Rs[0] = R1;
        Rs[1] = R2;
        Rs[2] = R3;
        Rs[3] = R4;
        Rs[4] = R5;
        Rs[5] = R6;

        List<String> list_of_stats = DatabaseHelper.getsInstance(getApplicationContext()).tablesInfo.get(ActivityName.getText().toString());
        int sizeofStats = list_of_stats.size();
        String[] StatArray = new String[sizeofStats];
        StatArray = list_of_stats.toArray(StatArray);
        int sizeofStatsArray = StatArray.length;

        String dcheck = list_of_stats.get(list_of_stats.size() - 1);
        if (dcheck.equals("Date")) {
            sizeofStatsArray = list_of_stats.size() - 1;
        }

        for (int j = 0; j < sizeofStatsArray; j++) {
            t = DatabaseHelper.getsInstance(getApplicationContext()).pullStatTypeMetadata(ActivityName.getText().toString(), StatArray[j]).toArray(t);
            types[j] = t[2];
            Des = t[3];

            //System.out.println("THIS IS THE TYPE"+types[j]);

        }

        String[] ex_array = DatabaseHelper.getsInstance(getApplicationContext()).returnLastEntry(ActivityNAME);


        if (sizeofStatsArray == 0) {
            for (int i = 0; i < 6; i++) {
                Stats[i].setVisibility(View.GONE);

            }

        }
        else {

            for (int i = 0; i < sizeofStatsArray; i++) {
                Stats[i].setVisibility(View.VISIBLE);
                Stats[i].setText(StatArray[i]);

                Types[i].setVisibility(View.VISIBLE);
                Types[i].setText(types[i]);

                Description.setText(Des);

                if (ex_array.length != 0) {
                    Rs[i].setVisibility(View.VISIBLE);
                    Rs[i].setText(ex_array[i]);

                }
                if (ex_array.length == 0) {
                    Rs[i].setVisibility(View.INVISIBLE);

                }

            }
            for (int j = sizeofStatsArray; j < 6; j++) {
                Stats[j].setVisibility(View.GONE);
                Types[j].setVisibility(View.GONE);
                Rs[j].setVisibility(View.GONE);
                //LL[j].setVisibility(View.GONE);

            }

        }

    }




    public String[] ParseString(String s) {
        String[] result = s.split(";+");
        return result;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_settings:
                //System.out.println("THIS IS HS ASOIAUDINIMIUIHYHJKIJUHGYHBJNKUHGYVHBJUYGTGFVBHJYGVBHJNUHYGV BNJUHGBV%%%%%%");
                return true;
            case R.id.menu_export:
                Intent intent = new Intent(this, GoogleDriveAPI.class);
                intent.putExtra("Activity", ActivityNAME);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View
            v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, v.getId(), 0, "Edit");
        TextView tv = (TextView) ((LinearLayout) v).getChildAt(0);
        editName = tv.getText().toString();
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        try {
            if (item.getTitle() == "Edit") {
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Edit Column");
                alert.setMessage("Enter a New Name for " + editName);
                // Set an EditText view to get user input

                final EditText input = new EditText(this);

                alert.setView(input);
                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        DatabaseHelper.getsInstance(getApplicationContext()).changeColumnName(ActivityNAME, editName, input.getText().toString());
                        finish();
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });

                alert.show();
            } else {
                return false;
            }
            return true;
        } catch (Exception e) {
            return true;
        }
    }
}
