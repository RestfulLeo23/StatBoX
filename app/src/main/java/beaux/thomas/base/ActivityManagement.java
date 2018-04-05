package beaux.thomas.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

public class ActivityManagement extends AppCompatActivity {
    //public String[] NEW_ACTIVITY = new String[3];
    public int istimer;
    public int isGPS;
    int statCount = 0;
    @Override


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creation_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Switch switch1 = (Switch)findViewById(R.id.timer);
        Switch switch2 = (Switch)findViewById(R.id.GPS);

        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                   istimer=1;
                }else{
                    istimer=0;
                }
            }
        });
        switch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    isGPS=1;


                }else{
                    isGPS=0;
                }
            }
        });
        addMorestat(switch1);
    }

    public void addMorestat(View view){
        if (statCount<5) {
            statCount += 1;
            EditText StatName1 = (EditText) findViewById(R.id.StatName);
            EditText StatName2 = (EditText) findViewById(R.id.StatName2);
            EditText StatName3 = (EditText) findViewById(R.id.StatName3);
            EditText StatName4 = (EditText) findViewById(R.id.StatName4);
            EditText StatName5 = (EditText) findViewById(R.id.StatName5);

            EditText StatType1 = (EditText) findViewById(R.id.StatType);
            EditText StatType2 = (EditText) findViewById(R.id.StatType2);
            EditText StatType3 = (EditText) findViewById(R.id.StatType3);
            EditText StatType4 = (EditText) findViewById(R.id.StatType4);
            EditText StatType5 = (EditText) findViewById(R.id.StatType5);

            EditText[] names = new EditText[5];

            names[0] = StatName1;
            names[1] = StatName2;
            names[2] = StatName3;
            names[3] = StatName4;
            names[4] = StatName5;

            EditText[] types = new EditText[5];

            types[0] = StatType1;
            types[1] = StatType2;
            types[2] = StatType3;
            types[3] = StatType4;
            types[4] = StatType5;

            if (statCount == 0) {
                StatName1.setVisibility(View.GONE);
                for (int i = 0; i < 5; i++) {
                    names[i].setVisibility(View.GONE);
                }
            } else {
                for (int i = 0; i < statCount; i++) {
                    types[i].setVisibility(View.VISIBLE);
                    names[i].setVisibility(View.VISIBLE);
                }
                for (int j = statCount; j < 5; j++) {
                    types[j].setVisibility(View.GONE);
                    names[j].setVisibility(View.GONE);
                }
            }
        }
        else{
            Toast.makeText(getApplicationContext(),"Max Number of Stat Types",Toast.LENGTH_SHORT).show();
        }
    }

    public void GatherStatInfo(View view) {

        //System.out.println("ACTIVITY CREATION");
        EditText ActivityName = (EditText) findViewById(R.id.ActivityName);

        EditText StatName1 = (EditText) findViewById(R.id.StatName);
        EditText StatName2 = (EditText) findViewById(R.id.StatName2);
        EditText StatName3 = (EditText) findViewById(R.id.StatName3);
        EditText StatName4 = (EditText) findViewById(R.id.StatName4);
        EditText StatName5 = (EditText) findViewById(R.id.StatName5);

        EditText StatType1 = (EditText) findViewById(R.id.StatType);
        EditText StatType2 = (EditText) findViewById(R.id.StatType2);
        EditText StatType3 = (EditText) findViewById(R.id.StatType3);
        EditText StatType4 = (EditText) findViewById(R.id.StatType4);
        EditText StatType5 = (EditText) findViewById(R.id.StatType5);

        EditText Descript = (EditText) findViewById(R.id.des);

        String check = ActivityName.getText().toString();
        String[] New_Act = new String[3];

        if (!DatabaseHelper.getsInstance(getApplicationContext()).tablesInfo.containsKey(check)) {
            New_Act[0] = ActivityName.getText().toString() + ";"
                    + StatName1.getText().toString() + ";"
                    + StatName2.getText().toString() + ";"
                    + StatName3.getText().toString() + ";"
                    + StatName4.getText().toString() + ";"
                    + StatName5.getText().toString();

            New_Act[1] = StatType1.getText().toString() + ";"
                    + StatType2.getText().toString() + ";"
                    + StatType3.getText().toString() + ";"
                    + StatType4.getText().toString() + ";"
                    + StatType5.getText().toString();

            New_Act[2] = Integer.toString(istimer) + ";"
                    + Integer.toString(isGPS)+ ";"
                    + Descript.getText().toString();

            Intent resultIntent = new Intent(this, HomeScreen.class);

            // TODO Add extras or a data URI to this intent as appropriate.
            resultIntent.putExtra("ACT", New_Act);
            setResult(1, resultIntent);
            finish();
        }
        else {
            Toast.makeText(getApplicationContext(),"Stat Name Already in Database",Toast.LENGTH_SHORT).show();
        }
    }

}