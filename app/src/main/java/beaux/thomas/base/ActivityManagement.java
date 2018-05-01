package beaux.thomas.base;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;

public class ActivityManagement extends AppCompatActivity {
    //public String[] NEW_ACTIVITY = new String[3];
    public int istimer;
    public int isGPS;
    int statCount = 0;
    TextView textTargetUri;
    ImageButton targetImage;
    String FILEPATH;
    @Override


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_creation_view);
        targetImage = (ImageButton) findViewById(R.id.icon);
        textTargetUri = (TextView) findViewById(R.id.filepath);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        CheckBox switch1 = (CheckBox) findViewById(R.id.timer);
        CheckBox switch2 = (CheckBox) findViewById(R.id.Distance);
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if(isChecked){
                    istimer=1;
                }else{
                    istimer=0;
                }
            }
        }

        );

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
        String[] New_Act = new String[4];

        if (DatabaseHelper.getsInstance(getApplicationContext()).tablesInfo.containsKey(check)!=true) {

            New_Act[0] = ActivityName.getText().toString() + ";"
                    + StatName1.getText().toString() + ";"
                    + StatName2.getText().toString() + ";"
                    + StatName3.getText().toString() + ";"
                    + StatName4.getText().toString() + ";"
                    + StatName5.getText().toString();

            if (istimer == 1 ){
                System.out.println(istimer+"               TIMER");
                New_Act[0] = New_Act[0] + ";"
                        + "Timer";
            }
            if (isGPS == 1 ){
                New_Act[0] = New_Act[0] + ";"
                        + "GPS";
            }
            New_Act[1] = StatType1.getText().toString() + ";"
                    + StatType2.getText().toString() + ";"
                    + StatType3.getText().toString() + ";"
                    + StatType4.getText().toString() + ";"
                    + StatType5.getText().toString();

            if (istimer == 1 ){
                New_Act[1] = New_Act[1] + ";"
                        + "(H:M:S:ms)";
            }
            if (isGPS == 1 ){
                New_Act[1] = New_Act[1] + ";"
                        + "Distance";
            }

            New_Act[2] = Integer.toString(istimer) + ";"
                    + Integer.toString(isGPS)+ ";"
                    + Descript.getText().toString();

            Intent resultIntent = new Intent(this, HomeScreen.class);
            New_Act[3] = FILEPATH;
            // TODO Add extras or a data URI to this intent as appropriate.
            resultIntent.putExtra("ACT", New_Act);
            setResult(1, resultIntent);
            finish();
        }
        else {
            Toast.makeText(getApplicationContext(),"Stat Name Already in Database",Toast.LENGTH_SHORT).show();
        }
    }

    public void PickImage(View view){
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            Uri targetUri = data.getData();
            textTargetUri.setText(targetUri.toString());
            FILEPATH = targetUri.toString();
            Bitmap bitmap;
            try {
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(targetUri));
                targetImage.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void TimerCheck(View v){
        istimer = 1;
    }
    public void GpsCheck(View v){
        isGPS = 1;
    }
}