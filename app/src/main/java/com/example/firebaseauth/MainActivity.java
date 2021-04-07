package com.example.firebaseauth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    // Tag Variable For FireBase Database
    static final int GOOGLE_SIGNIN = 123;
    private static final String TAG = "MainActivity";

    // Firebase Variable
    FirebaseAuth mauth;
    GoogleSignInClient mgoogleSignInClient;

    //XML Variables
    Button Login,Logout;
    TextView textView;
    ImageView imageView;
    ProgressBar mprogressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize XML Values
        Login = findViewById(R.id.button_login);
        Logout = findViewById(R.id.button_logout);
        textView = findViewById(R.id.text);
        imageView = findViewById(R.id.imageview);
        mprogressBar = findViewById(R.id.progress_bar);

        // firebase Authentication method call
        mauth = FirebaseAuth.getInstance();


        // googleSigninoption Method
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder()
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mgoogleSignInClient = GoogleSignIn.getClient(this,googleSignInOptions);

        // Button action declear after finish work on (SignIn/ID Logout) method
        Login.setOnClickListener( v -> SignInGoogle());
        Logout.setOnClickListener( v -> IDLogout());

        //After Registrasion UI Update command
        if (mauth.getCurrentUser() != null){
            FirebaseUser user = mauth.getCurrentUser();
            UpadateUI(user);
        }

    }


    void SignInGoogle(){
        //progress_bar Status
        mprogressBar.setVisibility(View.VISIBLE);

        // Intent for Gmail Id taken
        Intent SignInIntent =mgoogleSignInClient.getSignInIntent();
       startActivityForResult(SignInIntent,GOOGLE_SIGNIN);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // request code Check get result to using try/catch method
        if (requestCode == GOOGLE_SIGNIN){
            Task <GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "FireBaseauthWithGoogle :"+ account.getId());

                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);

                mauth.signInWithCredential(credential).addOnCompleteListener(this, task1 -> {

                            if (task1.isSuccessful()){
                                mprogressBar.setVisibility(View.INVISIBLE);
                                Log.d(TAG, "Sign In SuccessFull");
                                FirebaseUser user = mauth.getCurrentUser();
                                UpadateUI(user);
                            }else {
                                mprogressBar.setVisibility(View.INVISIBLE);
                                Log.w(TAG, "SignIn Failed", task1.getException());
                                Toast.makeText(MainActivity.this, "SignIn Failed", Toast.LENGTH_SHORT).show();
                                UpadateUI(null);

                            }
                        });



            }catch (ApiException e){
                e.printStackTrace();
            }
        }
    }

    private void UpadateUI(FirebaseUser user) {

        if (user !=null){

            // after Registration User Information get
          //  String name = user.getDisplayName();
            String name =String.valueOf(user.getDisplayName());
            String email = user.getEmail();
            String photo = String.valueOf(user.getPhotoUrl());


            // Information Show
            textView.setText("Info : \n");
            textView.append(name + "\n");
            textView.append(email);
            Picasso.get().load(photo).into(imageView);

            //Button Visibility Setup
            Login.setVisibility(View.INVISIBLE);
            Logout.setVisibility(View.VISIBLE);

        }else {

            // Request User To try login Again
            textView.setText(getText(R.string.header));
            Picasso.get().load(R.drawable.ic_firebase).into(imageView);
            Login.setVisibility(View.VISIBLE);
            Logout.setVisibility(View.INVISIBLE);
        }

    }

    void IDLogout (){

        // User Logout Method
        FirebaseAuth.getInstance().signOut();
        mgoogleSignInClient.signOut().addOnCompleteListener(this, task -> UpadateUI(null));

    }


}


