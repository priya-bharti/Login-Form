package com.knowurcodes.musicplayer;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.ContentValues.TAG;

public class SignIn extends Fragment {

    //UI components
    TextInputEditText email, pass;
    Button signIn;

    //Tag
    String TAG = "Sign In Fragment";


    //Declare Firebase authorization variables
    private FirebaseAuth firebaseAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);

        email = view.findViewById(R.id.Enter_Email_Edit_Text2);
        pass = view.findViewById(R.id.Enter_Password_Edit_Text2);
        signIn = view.findViewById(R.id.Sign_In_Button);

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userEmail = email.getText().toString();
                String userPass = pass.getText().toString();

                // Checking Validation - @Vaibhav
                if (!isValidPassword(userPass))
                {


                    pass.setError("Invalid Password");

                }

                if (!isValidEmail(userEmail)){

                    email.setError("Invalid E-Mail");


                }

                if ((isValidEmail(userEmail)) && (isValidPassword(userPass))){

                    Sign_In(userEmail, userPass);
                }

            }
        });

        return view;
    }

    private void Sign_In(String userEmail, String userPass)
    {
        if(!validateForm(userEmail, userPass))
        {
            return;
        }
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(userEmail,userPass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            //Start music player
                            Toast.makeText(getContext(), "Successfully signed in", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(getContext(), "SignIn Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "SignIn failed with exception: " +e);
                if(e.toString().contains("InvalidUser"))
                    email.setError("Please check the username");
                if(e.toString().contains("InvalidCredentials"))
                    pass.setError("Enter the correct password");
            }
        });

    }

    private boolean validateForm(String userName, String userPass)
    {
        boolean flag = true;
        if(userName.length() == 0)
        {
            email.setError("Required");
            flag = false;
        }
        if(userPass.length() == 0)
        {
            pass.setError("Required");
            flag = false;
        }

        return flag;
    }

    // Added Password & Email Validation - @vaibhav
    private boolean isValidPassword(String passwordValid) {


        String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";

        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        Matcher matcher = pattern.matcher(passwordValid);

        return matcher.matches();


    }

    private boolean isValidEmail(String mailValid) {

        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(mailValid);
        return matcher.matches();
    }


}
