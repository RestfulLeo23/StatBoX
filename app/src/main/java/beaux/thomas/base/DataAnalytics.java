package beaux.thomas.base;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.PointsGraphSeries;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class DataAnalytics extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // Get the activity and stattype passed in through the intent
        Intent intent = getIntent();
        String activityName = intent.getStringExtra("Activity");
        final String statTypeName = intent.getStringExtra("Stat");

        // Get the activity stattypes from the database
        List<String> statTypes = DatabaseHelper.getsInstance(getApplicationContext()).tablesInfo.get(activityName);
        List<String> a = DatabaseHelper.getsInstance(getApplicationContext()).tablesInfo.get(activityName);
        List<String> statTypesPlusDate = new ArrayList<String>();
        List<String> chartTypes = new ArrayList<>();
        chartTypes.add("Bar Graph");
        chartTypes.add("Scatter Plot Graph");
        statTypesPlusDate.add("Date");
        for(int i = 0; i < a.size(); i++)
            statTypesPlusDate.add(a.get(i));

        //populate xaxis and yaxis spinners
        final Spinner yAxisSpinner = (Spinner) findViewById(R.id.yAxisSpinner);
        final Spinner xAxisSpinner = (Spinner) findViewById(R.id.xAxisSpinner);
        final Spinner chartSpinner = (Spinner) findViewById(R.id.chartSpinner);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> yadapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item, statTypes);
        ArrayAdapter<CharSequence> xadapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item, statTypesPlusDate);
        ArrayAdapter<CharSequence> chartadapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item, chartTypes);

        // Specify the layout to use when the list of choices appears
        xadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        chartadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        xAxisSpinner.setAdapter(xadapter);
        yAxisSpinner.setAdapter(yadapter);
        chartSpinner.setAdapter(chartadapter);

        // Set Spinner base value
        yAxisSpinner.setSelection(statTypes.indexOf(statTypeName));

        // Set spinner selection behaviors
        xAxisSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if("Bar Graph".equals(chartSpinner.getSelectedItem().toString())){
                    displayBarGraph(xAxisSpinner.getSelectedItem().toString(), yAxisSpinner.getSelectedItem().toString());
                }else{
                    displayScatterGraph(xAxisSpinner.getSelectedItem().toString(), yAxisSpinner.getSelectedItem().toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
        yAxisSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if("Bar Graph".equals(chartSpinner.getSelectedItem().toString())){
                    displayBarGraph(xAxisSpinner.getSelectedItem().toString(), yAxisSpinner.getSelectedItem().toString());
                }else{
                    displayScatterGraph(xAxisSpinner.getSelectedItem().toString(), yAxisSpinner.getSelectedItem().toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
        chartSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if("Bar Graph".equals(chartSpinner.getSelectedItem().toString())){
                    displayBarGraph(xAxisSpinner.getSelectedItem().toString(), yAxisSpinner.getSelectedItem().toString());
                }else{
                    displayScatterGraph(xAxisSpinner.getSelectedItem().toString(), yAxisSpinner.getSelectedItem().toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        //Set x axis to Date by default
        displayBarGraph("Date", statTypeName);
        //displayScatterGraph("Date", statTypeName);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void displayBarGraph(String xAxisName, String yAxisName){
        // Get the activity and stattype passed in through the intent
        Intent intent = getIntent();
        String activityName = intent.getStringExtra("Activity");
        SimpleDateFormat timerFormat = new SimpleDateFormat("hh:mm:ss:SS");

        // Special graph created given 'Date' on the x-axis
        if ("Date".equals(xAxisName)) {
            //Obtain tuple list from database containing
            List<String>[] tupList = DatabaseHelper.getsInstance(getApplicationContext()).grabActivity_Stat_withDate(activityName, yAxisName);

            GraphView graph = (GraphView) findViewById(R.id.graph);
            graph.removeAllSeries();
            // convert the list of data tuples into a map with key= each unique X axis element and value= sum of Y axis elements sharing X axis element
            Map<Date, Integer> map = new TreeMap<Date, Integer>();
            SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-dd-MM");
            //this is for test purposes
            //tupList[1].set(0, "2018-03-05");
            //tupList[1].set(1, "2018-02-05");
            //tupList[1].set(2, "2018-07-05");
            for (int i = 0; i < tupList[0].size(); i++) {
                String y_axis_el = tupList[0].get(i);
                int y_axis_int;
                if ("Timer".equals(yAxisName)){
                    try {
                        Date y_axis_time = timerFormat.parse(tupList[0].get(i));
                        y_axis_int = (int) y_axis_time.getTime() / 1000;
                    } catch(ParseException e){
                        e.printStackTrace();
                        y_axis_int = 0;
                    }
                }else{
                    y_axis_int = Integer.parseInt(y_axis_el);
                }
                try {
                    Date x_axis_el = dFormat.parse(tupList[1].get(i));
                    // If the map already has seen this x-axis element, increase the existing value in map
                    if (map.containsKey(x_axis_el)) {
                        map.put(x_axis_el, map.get(x_axis_el) + y_axis_int);
                    }
                    // Otherwise, the x-axis element has been seen; make a new map entry
                    else {
                        map.put(x_axis_el, y_axis_int);
                    }
                } catch (java.text.ParseException e) {
                    e.printStackTrace();
                }
            }

            BarGraphSeries<DataPoint> series = new BarGraphSeries<>();
            //new DataPoint(4, 6)
            // For each entry in map, create an datapoint in the series
            for (Map.Entry<Date, Integer> entry : map.entrySet()) {
                // add 12 hours to each date to center them
                Date key = entry.getKey();
                Calendar cal = Calendar.getInstance();
                cal.setTime(key);
                cal.add(Calendar.HOUR_OF_DAY, 12);

                int value = entry.getValue();
                series.appendData(new DataPoint(cal.getTime(), value), true, map.size());
            }
            graph.addSeries(series);

            // styling
            series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
                @Override
                public int get(DataPoint data) {
                    return Color.rgb((int) data.getX() * 255 / 4, (int) Math.abs(data.getY() * 255 / 6), 100);
                }
            });

            // set date label formatter
            graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this));

            // set manual x bounds to have nice steps
            graph.getViewport().setMinX(series.getLowestValueX() - (series.getHighestValueX() - series.getLowestValueX()) / 5);
            graph.getViewport().setMaxX(series.getHighestValueX() + (series.getHighestValueX() - series.getLowestValueX()) / 5);
            graph.getViewport().setXAxisBoundsManual(true);

            // set manual y bounds
            graph.getViewport().setMinY(0);
            graph.getViewport().setMaxY(series.getHighestValueY());
            graph.getViewport().setYAxisBoundsManual(true);
            graph.getGridLabelRenderer().setNumHorizontalLabels(3);
            graph.getGridLabelRenderer().setNumVerticalLabels(3);

            // as we use dates as labels, the human rounding to nice readable numbers
            // is not necessary
            graph.getGridLabelRenderer().setHumanRounding(false);

            series.setSpacing(50);

            // set axis labels for graph
            GridLabelRenderer gridLabel = graph.getGridLabelRenderer();
            gridLabel.setHorizontalAxisTitle(xAxisName);
            gridLabel.setVerticalAxisTitle(yAxisName);
            gridLabel.setPadding(50);

            // Make graph scrollable
            graph.getViewport().setScalable(true);
        }else{ // x-axis is not 'Date', do regular graph display
            //Obtain tuple list from database containing
            List<String> tupListx = DatabaseHelper.getsInstance(getApplicationContext()).grabActivity_Stat(activityName, xAxisName);
            List<String> tupListy = DatabaseHelper.getsInstance(getApplicationContext()).grabActivity_Stat(activityName, yAxisName);

            GraphView graph = (GraphView) findViewById(R.id.graph);
            graph.removeAllSeries();
            // convert the list of data tuples into a map with key= each unique X axis element and value= sum of Y axis elements sharing X axis element
            Map<Integer, Integer> map = new TreeMap<Integer, Integer>();

            for (int i = 0; i < tupListx.size(); i++) {
                String x_axis_el = tupListx.get(i);
                String y_axis_el = tupListy.get(i);
                int y_axis_int, x_axis_int;
                if ("Timer".equals(yAxisName)){
                    try {
                        Date y_axis_time = timerFormat.parse(tupListy.get(i));
                        y_axis_int = (int) y_axis_time.getTime() / 1000;
                    } catch(ParseException e){
                        e.printStackTrace();
                        y_axis_int = 0;
                    }
                }else{
                    y_axis_int = Integer.parseInt(y_axis_el);
                }
                if ("Timer".equals(xAxisName)){
                    try {
                        Date y_axis_time = timerFormat.parse(tupListx.get(i));
                        x_axis_int = (int) y_axis_time.getTime() / 1000;
                    } catch(ParseException e){
                        e.printStackTrace();
                        x_axis_int = 0;
                    }
                }else{
                    x_axis_int = Integer.parseInt(x_axis_el);
                }
                // If the map already has seen this x-axis element, increase the existing value in map
                if (map.containsKey(x_axis_int) ){
                    map.put(x_axis_int, x_axis_int + y_axis_int);
                }
                // Otherwise, the x-axis element has been seen; make a new map entry
                else {
                    map.put(x_axis_int, y_axis_int);
                }
            }

            BarGraphSeries<DataPoint> series = new BarGraphSeries<>();
            //new DataPoint(4, 6)
            // For each entry in map, create an datapoint in the series
            for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
                int key = entry.getKey();
                int value = entry.getValue();
                series.appendData(new DataPoint(key, value), true, map.size());
            }
            graph.addSeries(series);

            // styling
            series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
                @Override
                public int get(DataPoint data) {
                    return Color.rgb((int) data.getX() * 255 / 4, (int) Math.abs(data.getY() * 255 / 6), 100);
                }
            });

            graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter());

            // set manual x bounds to have nice steps
            graph.getViewport().setMinX(series.getLowestValueX() - (series.getHighestValueX() - series.getLowestValueX()) / 5);
            graph.getViewport().setMaxX(series.getHighestValueX() + (series.getHighestValueX() - series.getLowestValueX()) / 5);
            graph.getViewport().setXAxisBoundsManual(true);

            // set manual y bounds
            graph.getViewport().setMinY(0);
            graph.getViewport().setMaxY(series.getHighestValueY());
            graph.getViewport().setYAxisBoundsManual(true);

            // as we use dates as labels, the human rounding to nice readable numbers
            // is not necessary
            graph.getGridLabelRenderer().setHumanRounding(true);

            series.setSpacing(50);

            // set axis labels for graph
            GridLabelRenderer gridLabel = graph.getGridLabelRenderer();
            gridLabel.setHorizontalAxisTitle(xAxisName);
            gridLabel.setVerticalAxisTitle(yAxisName);
            gridLabel.setPadding(50);

            // Make graph scrollable
            graph.getViewport().setScalable(true);
            graph.getGridLabelRenderer().setNumHorizontalLabels(3);
            graph.getGridLabelRenderer().setNumVerticalLabels(3);
        }
    }

    private void displayScatterGraph(String xAxisName, String yAxisName){
        // Get the activity and stattype passed in through the intent
        Intent intent = getIntent();
        String activityName = intent.getStringExtra("Activity");
        SimpleDateFormat timerFormat = new SimpleDateFormat("hh:mm:ss:SS");

        // Special graph created given 'Date' on the x-axis
        if ("Date".equals(xAxisName)) {
            //Obtain tuple list from database containing
            List<String>[] tupList = DatabaseHelper.getsInstance(getApplicationContext()).grabActivity_Stat_withDate(activityName, yAxisName);

            GraphView graph = (GraphView) findViewById(R.id.graph);
            graph.removeAllSeries();
            // convert the list of data tuples into a map with key= each unique X axis element and value= sum of Y axis elements sharing X axis element
            Map<Date, Integer> map = new TreeMap<Date, Integer>();
            SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-dd-MM");


            //this is for test purposes
            //tupList[1].set(0, "2018-03-05");
            //tupList[1].set(1, "2018-02-05");
            //tupList[1].set(2, "2018-07-05");
            for (int i = 0; i < tupList[0].size(); i++) {
                String y_axis_el = tupList[0].get(i);
                int y_axis_int;
                if ("Timer".equals(yAxisName)){
                    try {
                        Date y_axis_time = timerFormat.parse(tupList[0].get(i));
                        y_axis_int = (int) y_axis_time.getTime() / 1000;
                    } catch(ParseException e){
                        e.printStackTrace();
                        y_axis_int = 0;
                    }
                }else{
                    y_axis_int = Integer.parseInt(y_axis_el);
                }
                try {
                    Date x_axis_el = dFormat.parse(tupList[1].get(i));
                    // If the map already has seen this x-axis element, increase the existing value in map
                    if (map.containsKey(x_axis_el)) {
                        map.put(x_axis_el, map.get(x_axis_el) + y_axis_int);
                    }
                    // Otherwise, the x-axis element has been seen; make a new map entry
                    else {
                        map.put(x_axis_el, y_axis_int);
                    }
                } catch (java.text.ParseException e) {
                    e.printStackTrace();
                }
            }

            PointsGraphSeries<DataPoint> series = new PointsGraphSeries<>();
            //new DataPoint(4, 6)
            // For each entry in map, create an datapoint in the series
            for (Map.Entry<Date, Integer> entry : map.entrySet()) {
                // add 12 hours to each date to center them
                Date key = entry.getKey();
                int value = entry.getValue();
                series.appendData(new DataPoint(key, value), true, map.size());
            }
            graph.addSeries(series);

            // set date label formatter
            graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this));

            // set manual x bounds to have nice steps
            graph.getViewport().setMinX(series.getLowestValueX() - (series.getHighestValueX() - series.getLowestValueX()) / 5);
            graph.getViewport().setMaxX(series.getHighestValueX() + (series.getHighestValueX() - series.getLowestValueX()) / 5);
            graph.getViewport().setXAxisBoundsManual(true);

            // set manual y bounds
            graph.getViewport().setMinY(0);
            graph.getViewport().setMaxY(series.getHighestValueY());
            graph.getViewport().setYAxisBoundsManual(true);
            graph.getGridLabelRenderer().setNumHorizontalLabels(3);
            graph.getGridLabelRenderer().setNumVerticalLabels(3);

            // as we use dates as labels, the human rounding to nice readable numbers
            // is not necessary
            graph.getGridLabelRenderer().setHumanRounding(false);

            // set axis labels for graph
            GridLabelRenderer gridLabel = graph.getGridLabelRenderer();
            gridLabel.setHorizontalAxisTitle(xAxisName);
            gridLabel.setVerticalAxisTitle(yAxisName);
            gridLabel.setPadding(50);

            // Make graph scrollable
            graph.getViewport().setScalable(true);
        }else{ // x-axis is not 'Date', do regular graph display
            //Obtain tuple list from database containing
            List<String> tupListx = DatabaseHelper.getsInstance(getApplicationContext()).grabActivity_Stat(activityName, xAxisName);
            List<String> tupListy = DatabaseHelper.getsInstance(getApplicationContext()).grabActivity_Stat(activityName, yAxisName);

            GraphView graph = (GraphView) findViewById(R.id.graph);
            graph.removeAllSeries();
            // convert the list of data tuples into a map with key= each unique X axis element and value= sum of Y axis elements sharing X axis element
            Map<Integer, Integer> map = new TreeMap<Integer, Integer>();

            for (int i = 0; i < tupListx.size(); i++) {
                String x_axis_el = tupListx.get(i);
                String y_axis_el = tupListy.get(i);
                int y_axis_int, x_axis_int;
                if ("Timer".equals(yAxisName)){
                    try {
                        Date y_axis_time = timerFormat.parse(tupListy.get(i));
                        y_axis_int = (int) y_axis_time.getTime() / 1000;
                    } catch(ParseException e){
                        e.printStackTrace();
                        y_axis_int = 0;
                    }
                }else{
                    y_axis_int = Integer.parseInt(y_axis_el);
                }
                if ("Timer".equals(xAxisName)){
                    try {
                        Date y_axis_time = timerFormat.parse(tupListx.get(i));
                        x_axis_int = (int) y_axis_time.getTime() / 1000;
                    } catch(ParseException e){
                        e.printStackTrace();
                        x_axis_int = 0;
                    }
                }else{
                    x_axis_int = Integer.parseInt(x_axis_el);
                }
                // If the map already has seen this x-axis element, increase the existing value in map
                if (map.containsKey(x_axis_int)) {
                    map.put(x_axis_int, x_axis_int + y_axis_int);
                }
                // Otherwise, the x-axis element has been seen; make a new map entry
                else {
                    map.put(x_axis_int, y_axis_int);
                }
            }

            PointsGraphSeries<DataPoint> series = new PointsGraphSeries<>();
            //new DataPoint(4, 6)
            // For each entry in map, create an datapoint in the series
            for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
                int key = entry.getKey();
                int value = entry.getValue();
                series.appendData(new DataPoint(key, value), true, map.size());
            }
            graph.addSeries(series);

            graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter());
            //graph.getGridLabelRenderer().setNumHorizontalLabels(map.size());

            // set manual x bounds to have nice steps
            graph.getViewport().setMinX(series.getLowestValueX() - (series.getHighestValueX() - series.getLowestValueX()) / 5);
            graph.getViewport().setMaxX(series.getHighestValueX() + (series.getHighestValueX() - series.getLowestValueX()) / 5);
            graph.getViewport().setXAxisBoundsManual(true);

            // set manual y bounds
            graph.getViewport().setMinY(0);
            graph.getViewport().setMaxY(series.getHighestValueY());
            graph.getViewport().setYAxisBoundsManual(true);

            // as we use dates as labels, the human rounding to nice readable numbers
            // is not necessary
            graph.getGridLabelRenderer().setHumanRounding(true);

            // set axis labels for graph
            GridLabelRenderer gridLabel = graph.getGridLabelRenderer();
            gridLabel.setHorizontalAxisTitle(xAxisName);
            gridLabel.setVerticalAxisTitle(yAxisName);
            gridLabel.setPadding(50);
            graph.getGridLabelRenderer().setNumHorizontalLabels(3);
            graph.getGridLabelRenderer().setNumVerticalLabels(3);

            // Make graph scrollable
            graph.getViewport().setScalable(true);
        }
    }

    public void StatisticsPage(View view){
        Intent intent = getIntent();
        String activityName = intent.getStringExtra("Activity");
        String statTypeName = intent.getStringExtra("Stat");

        Intent newIntent = new Intent(this, StatisticsPage.class);
        newIntent.putExtra("Activity", activityName);
        newIntent.putExtra("Stat", statTypeName);
        this.startActivity(newIntent);
    }
}
