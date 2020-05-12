 package com.example.loginggfirebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

 public class MainActivity extends AppCompatActivity  {

    private SignInButton signInButton;
    private GoogleSignInClient googleSignInClient;
    private String TAG = "Test";
    private  FirebaseAuth mAuth;
    private Button btnSignOut;
    private int RC_SIGN_IN;
    FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnSignOut = findViewById(R.id.sign_out_button);
        mAuth = FirebaseAuth.getInstance();
        signInButton = findViewById(R.id.sign_in_button);


        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleSignInClient.signOut();
//                Toast.makeText(MainActivity.this, "You Are Logged Out", Toast.LENGTH_SHORT).show();
                btnSignOut.setVisibility(View.INVISIBLE);
                signOut();
//                revokeAccess();
                deleteUser();
            }
        });

    }
    private  void signIn ()
     {
         Intent signInIntent = googleSignInClient.getSignInIntent();
         startActivityForResult(signInIntent, RC_SIGN_IN);
     }

     @Override
     protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
         super.onActivityResult(requestCode, resultCode, data);
         if(requestCode == RC_SIGN_IN)
         {
             Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
             handleSignInResult(task);
         }
     }

     private void handleSignInResult(Task<GoogleSignInAccount> completes) {
        try {
            GoogleSignInAccount googleSignInAccount = completes.getResult(ApiException.class);
            Toast.makeText(this, "Signed In Successfully", Toast.LENGTH_SHORT).show();
            assert googleSignInAccount != null;
            FreebaseGoogleAuth(googleSignInAccount);
        }
        catch (ApiException e)
        {
            Toast.makeText(this, "Sign In Failure", Toast.LENGTH_SHORT).show();
           FreebaseGoogleAuth( null);
        }

     }
     private void FreebaseGoogleAuth(GoogleSignInAccount googleSignInAccount1)
     {
         AuthCredential authCredential = GoogleAuthProvider.getCredential(googleSignInAccount1.getIdToken(),null);
         mAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
             @Override
             public void onComplete(@NonNull Task<AuthResult> task) {
                 if (task.isSuccessful())
                 {
                     Toast.makeText(MainActivity.this, "Signed In Successfully", Toast.LENGTH_SHORT).show();
                     FirebaseUser user = mAuth.getCurrentUser();
                     updateUI(user);
                 }
                 else {
                     Toast.makeText(MainActivity.this, "Sign In Failure", Toast.LENGTH_SHORT).show();
                     updateUI(null);
                 }
             }
         });
     }
     private void updateUI(FirebaseUser firebaseUser)
     {
         btnSignOut.setVisibility(View.VISIBLE);
         GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
         if (googleSignInAccount != null)
         {
             String personName = googleSignInAccount.getDisplayName();
             String personGivenName = googleSignInAccount.getGivenName();
             String personEmail = googleSignInAccount.getEmail();
             String personFamily = googleSignInAccount.getFamilyName();
             String personId = googleSignInAccount.getId();
             Uri personPhoto = googleSignInAccount.getPhotoUrl();
             Toast.makeText(this, personName+ " " + personEmail, Toast.LENGTH_SHORT).show();
         }

     }
     private void signOut() {
         googleSignInClient.signOut()
                 .addOnCompleteListener( this, new OnCompleteListener<Void>() {
                     @Override
                     public void onComplete(@NonNull Task<Void> task) {
                         Toast.makeText( MainActivity.this, "Log Out Successfully", Toast.LENGTH_SHORT ).show();
                     }
                 } );

//     private  void revokeAccess()
//     {
//         googleSignInClient.revokeAccess()
//                 .addOnCompleteListener( this, new OnCompleteListener<Void>() {
//                     @Override
//                     public void onComplete(@NonNull Task<Void> task) {
//                         Toast.makeText( MainActivity.this, "Log out ok", Toast.LENGTH_SHORT ).show();
//                     }
//                 } );
//     }

     }
     private void deleteUser()
     {
         final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
         user.delete()
                 .addOnCompleteListener( new OnCompleteListener<Void>() {
                     @Override
                     public void onComplete(@NonNull Task<Void> task) {
                         if (task.isSuccessful())
                         {
                             Log.d(TAG,"user acc deleted");
                             Toast.makeText( MainActivity.this, "user deleted", Toast.LENGTH_SHORT ).show();
                         }
                     }
                 } );
     }
 }
