package beaux.thomas.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

public class ActivityManagement extends AppCompatActivity {
    //public String[] NEW_ACTIVITY = new String[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creation);
    }

    public void GatherStatInfo(View view){
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

        String check = ActivityName.getText().toString();
        String[] New_Act = new String[1];

        if(DatabaseHelper.getsInstance(getApplicationContext()).tablesInfo.containsKey(check) == true){
            Intent resultIntent = new Intent(this, HomeScreen.class);
            System.out.print("**********Already in DB************");
            // TODO Add extras or a data URI to this intent as appropriate.
            New_Act[0] = "PRINTING ERROR INTENT CODE ";
            resultIntent.putExtra("ACT", New_Act);
            setResult(666, resultIntent);
            finish();
        }

        New_Act[0] = ActivityName.getText().toString()+ ";"
                +StatName1.getText().toString()+ ";"
                +StatName2.getText().toString()+ ";"
                +StatName3.getText().toString()+ ";"
                +StatName4.getText().toString()+ ";"
                +StatName5.getText().toString()+ ";"
                +StatType1.getText().toString()+ ";"
                +StatType2.getText().toString()+ ";"
                +StatType3.getText().toString()+ ";"
                +StatType4.getText().toString()+ ";"
                +StatType5.getText().toString();

        Intent resultIntent = new Intent(this, HomeScreen.class);

        // TODO Add extras or a data URI to this intent as appropriate.
        resultIntent.putExtra("ACT",New_Act);

        setResult(1, resultIntent);
        finish();

    }

}
