package beaux.thomas.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import static beaux.thomas.base.HomeScreen.EXTRA_MESSAGE;

public class InputStat extends AppCompatActivity {

    public String StatNAME;
    public String timestat = "No Record";
    public String distance = "No Record";
    public static final int GET_TIMER = 3;
    public static final int GET_GPS = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.stat);
        Intent intent = getIntent();
        StatNAME = intent.getStringExtra(EXTRA_MESSAGE);
        UpdateInputStat();

    }
     public void Input_Stat(View view){
        System.out.println("INPUT STAT START");
        EditText Statv1 = (EditText) findViewById(R.id.StatVal1);
        EditText Statv2 = (EditText) findViewById(R.id.StatVal2);
        EditText Statv3 = (EditText) findViewById(R.id.StatVal3);
        EditText Statv4 = (EditText) findViewById(R.id.StatVal4);
        EditText Statv5 = (EditText) findViewById(R.id.StatVal5);

        String[] New_Stat = new String[1];
        New_Stat[0]= Statv1.getText().toString()+";"+
                Statv2.getText().toString()+";"+
                Statv3.getText().toString()+";"+
                Statv4.getText().toString()+";"+
                Statv5.getText().toString();

        New_Stat[0] = New_Stat[0] + ";" + timestat;
        New_Stat[0] = New_Stat[0] + ";" + distance;
        System.out.println("INPUT STAT MIDDLE");
        //New_Stat[1]= StatV.getText().toString();
        Intent resultIntent = new Intent(this, ViewActivity.class);
        resultIntent.putExtra("STAT",New_Stat);
        setResult(1,resultIntent);
        System.out.println("INPUT STAT END");
        finish();

     }

     public void UpdateInputStat(){
        List<String> Pull = DatabaseHelper.getsInstance(getApplicationContext()).tablesInfo.get(StatNAME);
        String[] t = new String[4];
        int n = Pull.size();
        String arr[] = new String[n];
        arr = Pull.toArray(arr);
        int size = arr.length;
        String dcheck = Pull.get(Pull.size() - 1);
        System.out.println("****THIS IS THE LAST ENTRY IN PULL BEFORE toARRAY:  "+dcheck+"   ****");


        if (dcheck.equals("Date")){

            size = Pull.size()-1;
            System.out.println("****THIS IS THE SIZE OF THE TABLES INFO PULL AFTER toARRAY this is reallt a good line so greattttttttttttt:  "+size+"   ****");

        }

        TextView Stat1 = (TextView) findViewById(R.id.StatName1);
        TextView Stat2 = (TextView) findViewById(R.id.StatName2);
        TextView Stat3 = (TextView) findViewById(R.id.StatName3);
        TextView Stat4 = (TextView) findViewById(R.id.StatName4);
        TextView Stat5 = (TextView) findViewById(R.id.StatName5);

        TextView[] Stats = new TextView[5];
        Stats[0]= Stat1;
        Stats[1]= Stat2;
        Stats[2]= Stat3;
        Stats[3]= Stat4;
        Stats[4]= Stat5;

        EditText Statv1 = (EditText) findViewById(R.id.StatVal1);
        EditText Statv2 = (EditText) findViewById(R.id.StatVal2);
        EditText Statv3 = (EditText) findViewById(R.id.StatVal3);
        EditText Statv4 = (EditText) findViewById(R.id.StatVal4);
        EditText Statv5 = (EditText) findViewById(R.id.StatVal5);

        EditText[] Inputs = new EditText[5];
        Inputs[0]= Statv1;
        Inputs[1]= Statv2;
        Inputs[2]= Statv3;
        Inputs[3]= Statv4;
        Inputs[4]= Statv5;

         Button button = findViewById(R.id.timer);
         t = DatabaseHelper.getsInstance(getApplicationContext()).pullStatTypeMetadata(StatNAME, arr[0]).toArray(t);


         if (t[1].equals("0")){
             button.setVisibility(View.GONE);
             TextView timer = (TextView) findViewById(R.id.timerstat);
             timer.setVisibility(View.GONE);
             TextView recordedtime = (TextView) findViewById(R.id.time);
             recordedtime.setVisibility(View.GONE);
         }
         if (t[1].equals("1")){
             button.setVisibility(View.VISIBLE);
             TextView timer = (TextView) findViewById(R.id.timerstat);
             timer.setVisibility(View.VISIBLE);

         }

        if (size == 0) {
            for (int i = 0; i<5 ; i++){
                Stats[i].setVisibility(View.GONE);
                Inputs[i].setVisibility(View.GONE);
            }
        }
        else{
            for (int i = 0; i<size ; i++){
                Stats[i].setVisibility(View.VISIBLE);
                Stats[i].setText(arr[i]);
                Inputs[i].setVisibility(View.VISIBLE);
                if (t[1].equals("1")){
                    button.setVisibility(View.VISIBLE);
                    TextView timer = (TextView) findViewById(R.id.timerstat);
                    timer.setVisibility(View.VISIBLE);

                }
            }
            if (t[1].equals("1")) {
                for (int j = size-1; j < 5; j++) {
                    Stats[j].setVisibility(View.GONE);
                    Inputs[j].setVisibility(View.GONE);
                }


            }
            else{
                for (int j = size; j < 5; j++) {
                    Stats[j].setVisibility(View.GONE);
                    Inputs[j].setVisibility(View.GONE);
                }
            }
        }
    }

    public void openTimer(View view){
        Intent intent = new Intent(this, Timer.class);
        startActivityForResult(intent, GET_TIMER);
    }
    public void openGPS(View view){
        Intent intent = new Intent(this, GPS.class);
        startActivityForResult(intent, GET_GPS);


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (GET_TIMER): {
                if (resultCode == 1) {
                    System.out.println(data.getStringExtra("TIMER"));
                    TextView timer = (TextView) findViewById(R.id.time);
                    timer.setText(data.getStringExtra("TIMER"));
                    timestat = data.getStringExtra("TIMER");
                    break;
                }
            }
            case (GET_GPS): {
                if (resultCode == 1) {
                    System.out.println(data.getStringExtra("GPS"));
                    TextView gps = (TextView) findViewById(R.id.GPS);
                    gps.setText(data.getStringExtra("GPS"));
                    distance = data.getStringExtra("GPS");
                    break;
                }
            }
        }
    }

}
