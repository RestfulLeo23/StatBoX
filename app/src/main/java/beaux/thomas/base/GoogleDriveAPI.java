package beaux.thomas.base;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;
import android.content.Intent;
import android.util.Log;
import android.app.Activity;
import android.widget.Button;

import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.*;
import com.google.android.gms.tasks.*;
import android.support.annotation.*;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class GoogleDriveAPI extends AppCompatActivity {

    private static final String TAG = "drive-quickstart";
    private static final int REQUEST_CODE_SIGN_IN = 0;
    private static final int REQUEST_CODE_CAPTURE_IMAGE = 1;
    private static final int REQUEST_CODE_CREATOR = 2;

    private DriveClient mDriveClient;
    private DriveResourceClient mDriveResourceClient;
    private GoogleSignInClient mGoogleSignInClient;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_drive_api);
        mGoogleSignInClient = buildGoogleSignInClient();
        startActivityForResult(mGoogleSignInClient.getSignInIntent(), REQUEST_CODE_SIGN_IN);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.back_arrow));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private GoogleSignInClient buildGoogleSignInClient() {
        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestScopes(Drive.SCOPE_FILE)
                        .build();
        return GoogleSignIn.getClient(this, signInOptions);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        Toast.makeText(this.getApplicationContext(),"Step 1",Toast.LENGTH_LONG).show();
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_SIGN_IN:
                Toast.makeText(this.getApplicationContext(),"Step 2",Toast.LENGTH_LONG).show();
                Log.i(TAG, "Sign in request code");
                // Called after user is signed in.
                if (resultCode == RESULT_OK) {
                    Toast.makeText(this.getApplicationContext(),"Step 3",Toast.LENGTH_LONG).show();
                    Log.i(TAG, "Signed in successfully.");
                    // Use the last signed in account here since it already have a Drive scope.
                    mDriveClient = Drive.getDriveClient(this, GoogleSignIn.getLastSignedInAccount(this));
                    // Build a drive resource client.
                    mDriveResourceClient = Drive.getDriveResourceClient(this, GoogleSignIn.getLastSignedInAccount(this));
                    Toast.makeText(this.getApplicationContext(),"Step 4",Toast.LENGTH_LONG).show();

                    // Create File
                    final Task<DriveFolder> rootFolderTask = mDriveResourceClient.getRootFolder();
                    final Task<DriveContents> createContentsTask = mDriveResourceClient.createContents();
                    Tasks.whenAll(rootFolderTask, createContentsTask)
                            .continueWithTask(new Continuation<Void, Task<DriveFile>>() {
                                @Override
                                public Task<DriveFile> then(@NonNull Task<Void> task) throws Exception {
                                    DriveFolder parent = rootFolderTask.getResult();
                                    DriveContents contents = createContentsTask.getResult();
                                    OutputStream outputStream = contents.getOutputStream();
                                    try (Writer writer = new OutputStreamWriter(outputStream)) {
                                        writer.write("Hello World!");
                                    }

                                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                            .setTitle("HelloWorld.txt")
                                            .setMimeType("text/plain")
                                            .setStarred(true)
                                            .build();

                                    return mDriveResourceClient.createFile(parent, changeSet, contents);
                                }
                            })
                            .addOnSuccessListener(this,
                                    new OnSuccessListener<DriveFile>() {
                                        @Override
                                        public void onSuccess(DriveFile driveFile) {
                                            Log.i(TAG, "Successful file creation");
                                            finish();
                                        }
                                    })
                            .addOnFailureListener(this, new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e(TAG, "Unable to create file", e);
                                    finish();
                                }
                            });
                }
                break;
            case REQUEST_CODE_CAPTURE_IMAGE:
                Log.i(TAG, "capture image request code");
                // Called after a photo has been taken.
                if (resultCode == Activity.RESULT_OK) {

                }
                break;
            case REQUEST_CODE_CREATOR:
                Log.i(TAG, "creator request code");
                // Called after a file is saved to Drive.
                if (resultCode == RESULT_OK) {

                }
                break;
        }
    }

    public void signOutClick(View view) {
        signOut();
        Toast.makeText(this.getApplicationContext(),"Signed Out",Toast.LENGTH_LONG).show();
    }

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.i(TAG, "signed out user");
                    }
                });
    }
}
