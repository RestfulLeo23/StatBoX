package beaux.thomas.base;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.drive.CreateFileActivityOptions;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveClient;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.OpenFileActivityOptions;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.SpreadsheetProperties;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


public class GoogleDriveAPI extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    /**
     * Google Drive and Spreadsheet Credentials
     */
    private GoogleAccountCredential mCredential;
    private static final String TAG = "StatBoXDriveAPI";
    private DriveId driveId;
    private DriveClient mDriveClient;
    private DriveResourceClient mDriveResourceClient;
    private GoogleSignInClient mGoogleSignInClient;

    /**
     * Request codes for both APIs
     */
    private static final int DRIVE_REQUEST_CODE_SIGN_IN = 0;
    private static final int DRIVE_REQUEST_CODE_PICKER = 2;
    static final int SHEETS_REQUEST_ACCOUNT_PICKER = 1000;
    static final int SHEETS_REQUEST_AUTHORIZATION = 1001;
    static final int SHEETS_REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int SHEETS_REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    /**
     * Drive and Spreadsheet API's user credential properties
     */
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = { SheetsScopes.SPREADSHEETS_READONLY, Drive.SCOPE_FILE.toString()};

    /**
     * System fields
     */
    public String actNAME;
    public String[] Entry= new String[5];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        actNAME = intent.getStringExtra("Activity");

        // Initialize Google credentials and service object.
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());

        if(actNAME != null){
            Export();
        }
        else{
            Import();
        }
    }

    /**
     * Updates the GoogleDriveAPI UI with the Spreadsheets API export_activity.xml
     */
    private void Export(){
        setContentView(R.layout.activity_export);
        TextView textView = findViewById(R.id.act_name);
        textView.setText(actNAME);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        updateScreen();
    }

    /**
     * Starts the StatBoX Import process for the Drive API and Spreadsheets API if there was not a
     * StatBoX Activity name sent to us
     */
    private void Import(){
        setContentView(R.layout.activity_import);
        driveSignIn();
    }

    /**
     * Starts the StatBoX Export process for the Spreadsheets API when the export button
     * from the activity_export.xml file was pressed
     * @param view View passed to us by the export button from the activity_export.xml
     */
    public void spreadsheetExport(View view){
        getResultsFromApi();
    }

    /**
     * Attempt to call the API, after verifying that all the preconditions are
     * satisfied. The preconditions are: Google Play Services installed, an
     * account was selected and the device currently has online access. If any
     * of the preconditions are not satisfied, the app will prompt the user as
     * appropriate.
     */
    private void getResultsFromApi() {
        if (! isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (! isDeviceOnline()) {
            Toast.makeText(getApplicationContext(),"No network connection available.",Toast.LENGTH_LONG).show();
        } else {
            new GoogleDriveAPI.MakeExportRequestTask(mCredential).execute();
        }
    }

    /**
     * Attempts to set the account used with the API credentials. If an account
     * name was previously saved it will use that one; otherwise an account
     * picker dialog will be shown to the user. Note that the setting the
     * account to use with the credentials object requires the app to have the
     * GET_ACCOUNTS permission, which is requested here if it is not already
     * present. The AfterPermissionGranted annotation indicates that this
     * function will be rerun automatically whenever the GET_ACCOUNTS permission
     * is granted.
     */
    @AfterPermissionGranted(SHEETS_REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, android.Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        SHEETS_REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    SHEETS_REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }

    /** Start the Drive sign in activity. */
    private void driveSignIn() {
        Log.i(TAG, "Start sign in");
        mGoogleSignInClient = buildGoogleDriveSignInClient();
        startActivityForResult(mGoogleSignInClient.getSignInIntent(), DRIVE_REQUEST_CODE_SIGN_IN);
    }

    /** Build a Google SignIn client. */
    private GoogleSignInClient buildGoogleDriveSignInClient() {
        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestScopes(Drive.SCOPE_FILE)
                        .build();
        return GoogleSignIn.getClient(this, signInOptions);
    }

    /**
     * Respond to requests for permissions at runtime for API 23 and above.
     * @param requestCode The request code passed in
     *     requestPermissions(android.app.Activity, String, int, String[])
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    /**
     * Callback for when a permission is granted using the EasyPermissions
     * library.
     * @param requestCode The request code associated with the requested
     *         permission
     * @param list The requested permission list. Never null.
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Callback for when a permission is denied using the EasyPermissions
     * library.
     * @param requestCode The request code associated with the requested
     *         permission
     * @param list The requested permission list. Never null.
     */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Checks whether the device currently has a network connection.
     * @return true if the device has a network connection, false otherwise.
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * Check that Google Play services APK is installed and up to date.
     * @return true if Google Play Services is available and up to
     *     date on this device; false otherwise.
     */
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    /**
     * Attempt to resolve a missing, out-of-date, invalid or disabled Google
     * Play Services installation via a user dialog, if possible.
     */
    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }

    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     * @param connectionStatusCode code describing the presence (or lack of)
     *     Google Play Services on this device.
     */
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                GoogleDriveAPI.this,
                connectionStatusCode,
                SHEETS_REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    /**
     * An asynchronous task that handles the Google Sheets API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private class MakeExportRequestTask extends AsyncTask<Void, Void, Spreadsheet> {
        private Sheets mService = null;

        private Exception mLastError = null;

        MakeExportRequestTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.sheets.v4.Sheets.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Google Sheets API Android Quickstart")
                    .build();
        }

        /**
         * Background task to call Google Sheets API.
         * @param params no parameters needed for this task.
         */
        @Override
        protected Spreadsheet doInBackground(Void... params) {
            try {
                if(actNAME != null) {
                    Spreadsheet sheet = generateSpreadsheet();
                    updateActivitySpreadsheet(sheet);
                    return sheet;
                }
                else{
                    retrieveSpreadsheetContents(driveId);
                    return null;
                }
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        /**
         * Create a blank StatBoX Activity Google Spreadsheet
         * @return Google Spreadsheet
         * @throws IOException
         */
        private Spreadsheet generateSpreadsheet() throws IOException {
            Spreadsheet requestBody = new Spreadsheet();
            SpreadsheetProperties properties = new SpreadsheetProperties();
            properties.setTitle(actNAME);
            requestBody.setProperties(properties);

            Sheets.Spreadsheets.Create request = mService.spreadsheets().create(requestBody);
            return request.execute();
        }

        /**
         * Update a GoogleSpreadsheet with a StatBoX Activity
         * @param sheet Google Spreadsheet object
         */
        private void updateActivitySpreadsheet(Spreadsheet sheet){
            // Catching IO exception from updating Spreadsheet with new information.
            try {
                // Set the parameters of the sheet and acquire activity information from DatabaseHelper
                String writeRange = "Sheet1!A1:G";
                String id = sheet.getSpreadsheetId();
                Hashtable<String, List<String>> activityEntries = DatabaseHelper.getsInstance(getApplicationContext()).grabActivity(actNAME);
                List<String> activityInfo = DatabaseHelper.getsInstance(getApplicationContext()).tablesInfo.get(actNAME);

                // Create column headers using activity stat's
                List<List<Object>> values = new ArrayList<>();
                List<Object> columnHeaderDataRow = new ArrayList<>();
                for (int i = 0; i < activityInfo.size(); i++) {
                    List<String> statType = DatabaseHelper.getsInstance(getApplicationContext()).pullStatTypeMetadata(actNAME, activityInfo.get(i));
                    columnHeaderDataRow.add(activityInfo.get(i) + " (" + statType.get(2).toLowerCase() + ")");
                }
                columnHeaderDataRow.add("Date (yyyy-mm-dd)");
                values.add(columnHeaderDataRow);

                // Populate rows starting at row 1 with StatBoX entries
                Set<String> keys = activityEntries.keySet();
                List sortedKeys = new ArrayList<>(keys);
                Collections.sort(sortedKeys);
                for (int j = sortedKeys.size()-1; j >= 0; j--) {
                    List<Object> entryRow = new ArrayList<>();
                    List<String> entryList = activityEntries.get(sortedKeys.toArray()[j]);
                    System.out.println(entryList);
                    for (int i = 0; i < entryList.size(); i++) {
                        entryRow.add(entryList.get(i));
                    }
                    values.add(entryRow);
                }

                // Generate spreadsheet payload and send it off to update the spreadsheet
                ValueRange vr = new ValueRange().setValues(values).setMajorDimension("ROWS");
                mService.spreadsheets().values()
                        .update(id, writeRange, vr)
                        .setValueInputOption("RAW")
                        .execute();
            } catch (Exception e){
                System.out.println(e);
            }
        }

        /**
         * Retrieves the contents of a user selected spreadsheet and imports it into the system
         * as a StatBoX Activity.
         * @param driveId DriveId of the file selected by the user.
         */
        private void retrieveSpreadsheetContents(DriveId driveId){
            try{
                /**
                 * Acquire the sheet and it's properties using the Sheets API sent by the Drive API
                 */
                String range = "Sheet1!A1:G";
                ValueRange result = mService.spreadsheets().values().get(driveId.getResourceId(), range).execute();
                int numRows = result.getValues() != null ? result.getValues().size() : 0;
                String activityName = mService.spreadsheets().get(driveId.getResourceId())
                        .execute().getProperties().getTitle();
                activityName = activityName.replaceAll("\\s+","");

                /**
                 * Parse the Spreadsheet finding the column header row
                 */
                List<String> colHeaders = new ArrayList<String>();
                List<List<Object>> values = result.getValues();
                if (values != null) {
                    for (int i = 0; i < values.get(0).size(); i++) {
                        colHeaders.add(values.get(0).get(i).toString());
                    }
                }
                int numCols = colHeaders.size();

                /**
                 * Parse the column header row looking for statNames by capturing everything
                 * that is not within parenthesis.
                 */
                String[] activityData = new String[numCols];
                Pattern statName = Pattern.compile("([A-Z])\\w+");
                activityData[0] = activityName;
                for(int i = 0; i < numCols-1; i ++){
                    Matcher m = statName.matcher(colHeaders.get(i).toString());
                    if(m.find())
                        activityData[i+1] = m.group().subSequence(0, m.group().length()).toString();
                }

                DatabaseHelper.getsInstance(getApplicationContext()).createTable(activityData);

                /**
                 * Parse the column header row again looking for statTypes by capturing everything
                 * within parenthesis.
                 */
                String[] statTypes = new String[numCols-1];
                Pattern statType = Pattern.compile("(?=\\().+?(?=\\))");
                for(int i = 0; i < numCols-1; i ++){
                    Matcher m = statType.matcher(colHeaders.get(i).toString());
                    if(m.find())
                        statTypes[i] = m.group().subSequence(1, m.group().length()).toString();
                }

                /**
                 * Create the metaDataEntry for each statName and update the metadata table to accurately
                 * reflect the metaData of the activity just imported.
                 */
                for(int i = 0; i < statTypes.length; i++){
                    String[] metaDataEntry = new String[6];
                    metaDataEntry[0] = activityName; // Activity name
                    metaDataEntry[1] = activityData[i+1].toString(); // Stat name
                    metaDataEntry[2] = "No"; // GPS
                    metaDataEntry[3] = "No"; // Timer
                    metaDataEntry[4] = statTypes[i].toString(); // Stat type for the specified Stat name
                    metaDataEntry[5] = activityName+" habit"; // Description of the activity
                    DatabaseHelper.getsInstance(getApplicationContext()).updateMeta(metaDataEntry);
                }

                /**
                 * Insert entries without Dates
                 * Change Dates after row has been entered
                 */
                int counter = 0;
                for(int i = numRows-1; i >= 1; i--){
                    String[] entry = new String[numCols+1];
                    entry[0] = activityName;
                    for(int j = 1; j < numCols; j++){
                        entry[j] = values.get(i).get(j-1).toString();
                    }
                    counter += 1;
                    DatabaseHelper.getsInstance(getApplicationContext()).insertData(entry);
                    DatabaseHelper.getsInstance(getApplicationContext()).changeDate(activityName, values.get(i).get(numCols-1).toString(),counter);
                }
                System.out.println();
            }catch(Exception e){
                Log.e(TAG, "Unable to read contents", e);
            }
        }

        /**
         * Generate UI for background task.
         */
        @Override
        protected void onPreExecute() {
        }

        /**
         * Update UI with status of api call
         * @param input Spreadsheet
         */
        @Override
        protected void onPostExecute(Spreadsheet input) {
            if ((input == null || input.size() == 0) && actNAME != null) {
                Toast.makeText(getApplicationContext(),"Spreadsheet was not generated successfully.",Toast.LENGTH_LONG).show();
                finish();
            } else if (actNAME != null){
                Toast.makeText(getApplicationContext(),"Spreadsheet successfully created",Toast.LENGTH_LONG).show();
                finish();
            } else if (actNAME == null){
                Toast.makeText(getApplicationContext(),"Spreadsheet successfully imported.",Toast.LENGTH_LONG).show();
                setResult(RESULT_OK);
                finish();
            }
        }

        @Override
        protected void onCancelled() {
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            GoogleDriveAPI.SHEETS_REQUEST_AUTHORIZATION);
                } else {
                    Toast.makeText(getApplicationContext(),"The following error occurred:\n"
                            + mLastError.getMessage(),Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getApplicationContext(),"Request cancelled.",Toast.LENGTH_LONG).show();
            }
        }
    }

    /** Create a new file and save it to Drive. */
    private void openDrivePicker() {
        // Start by creating a new contents, and setting a callback.
        Log.i(TAG, "Creating new contents.");

        mDriveResourceClient
                .createContents()
                .continueWithTask(
                        new Continuation<DriveContents, Task<Void>>() {
                            @Override
                            public Task<Void> then(@NonNull Task<DriveContents> task) {
                                return openFileIntentPicker();
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Failed to create new contents.", e);
                            }
                        });
    }

    /**
     * Creates an {@link IntentSender} to start a dialog activity with configured {@link
     * CreateFileActivityOptions} for user to create a new photo in Drive.
     */
    private Task<Void> openFileIntentPicker() {
        Log.i(TAG, "New contents created.");

        OpenFileActivityOptions openFileActivityOptions =
                new OpenFileActivityOptions.Builder().build();

        return mDriveClient.newOpenFileActivityIntentSender(openFileActivityOptions).continueWith(new Continuation<IntentSender, Void>() {
            @Override
            public Void then(@NonNull Task<IntentSender> task) throws Exception {
                startIntentSenderForResult(task.getResult(), DRIVE_REQUEST_CODE_PICKER, null, 0, 0, 0);
                return null;
            }
        });
    }

    public void updateScreen(){
        List<String> Pull = DatabaseHelper.getsInstance(getApplicationContext()).tablesInfo.get(actNAME);
        int n = Pull.size();
        String arr[] = new String[n];
        arr = Pull.toArray(arr);
        int size = arr.length;

        String[] ex_array = DatabaseHelper.getsInstance(getApplicationContext()).returnLastEntry(actNAME);

        TextView[] Stats = new TextView[5];
        TextView stat1 = findViewById(R.id.statN1);
        TextView stat2 = findViewById(R.id.statN2);
        TextView stat3 = findViewById(R.id.statN3);
        TextView stat4 = findViewById(R.id.statN4);
        TextView stat5 = findViewById(R.id.statN5);
        Stats[0] = stat1;
        Stats[1] = stat2;
        Stats[2] = stat3;
        Stats[3] = stat4;
        Stats[4] = stat5;

        TextView[] Entries = new TextView[5];
        TextView R1 = findViewById(R.id.entry1);
        TextView R2 = findViewById(R.id.entry2);
        TextView R3 = findViewById(R.id.entry3);
        TextView R4 = findViewById(R.id.entry4);
        TextView R5 = findViewById(R.id.entry5);
        Entries[0] = R1;
        Entries[1] = R2;
        Entries[2] = R3;
        Entries[3] = R4;
        Entries[4] = R5;
        for (int i = 0; i<size; i++) {
            List<String> P = DatabaseHelper.getsInstance(getApplicationContext()).grabActivity_Stat(actNAME, arr[i]);
            int c = P.size();
            String a[] = new String[c];
            a = P.toArray(a);
            Entry[i] ="";
            for (String j : a) {
                //System.out.println("THIS IS THE ENTRY: "+j);
                Entry[i] = j +"\n" + Entry[i];
            }
            //System.out.println(Entry[i]);
        }
        if (size == 0) {
            for (int i = 0; i<5 ; i++){
                Stats[i].setVisibility(View.GONE);
            }
        }
        else{
            for (int i = 0; i<size ; i++){
                Stats[i].setVisibility(View.VISIBLE);
                Stats[i].setText(arr[i]);

                if (ex_array.length != 0) {
                    Entries[i].setVisibility(View.VISIBLE);
                    Entries[i].setText(Entry[i]);
                }

            }
            for (int j = size; j < 5 ; j++){
                Stats[j].setVisibility(View.GONE);
                Entries[j].setVisibility(View.GONE);
            }
        }
    }

    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode code indicating the result of the incoming
     *     activity result.
     * @param data Intent (containing result data) returned by incoming
     *     activity result.
     */
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case SHEETS_REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    Toast.makeText(getApplicationContext(),"This app requires Google Play Services. Please install " +
                            "Google Play Services on your device and relaunch this app.",Toast.LENGTH_LONG).show();

                } else {
                    Log.i(TAG, "REQUEST_GOOGLE_PLAY_SERVICES");
                    getResultsFromApi();
                }
                break;
            case SHEETS_REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getResultsFromApi();
                        Log.i(TAG, "REQUEST_ACCOUNT_PICKER");
                    }
                }
                break;
            case SHEETS_REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
                    Log.i(TAG, "REQUEST_AUTHORIZATION");
                }
                break;

            case DRIVE_REQUEST_CODE_SIGN_IN:
                Log.i(TAG, "Sign in request code");
                // Called after user is signed in.
                if (resultCode == RESULT_OK) {
                    Log.i(TAG, "Signed in successfully.");
                    // Use the last signed in account here since it already have a Drive scope.
                    mDriveClient = Drive.getDriveClient(this, GoogleSignIn.getLastSignedInAccount(this));
                    // Build a drive resource client.
                    mDriveResourceClient =
                            Drive.getDriveResourceClient(this, GoogleSignIn.getLastSignedInAccount(this));
                    openDrivePicker();
                }
                Log.i(TAG, "REQUEST_CODE_SIGN_IN");
                break;

            case DRIVE_REQUEST_CODE_PICKER:
                Log.i(TAG, "creator request code");
                // Called after a file is saved to Drive.
                if (resultCode == RESULT_OK) {
                    Log.i(TAG, "Image successfully saved.");
                    driveId = data.getParcelableExtra(OpenFileActivityOptions.EXTRA_RESPONSE_DRIVE_ID);
                    getResultsFromApi();
                }
                //finish();
                break;
        }
    }
}
