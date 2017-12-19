package com.knowurcodes.musicplayer;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SignUp extends Fragment
{
    private static final String TAG = "SignUpFragment";

    //Declare the components
    private EditText name, pass, number, email;
    private Button submit;
    private TextView forgotPass, signIn;

    //Declare Firebase authorization variables
    private FirebaseAuth firebaseAuth;

    //Declare Firebase database variables
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseRef;

    //User details
    private SignUpDetails newUser;
    String userName, userPass ,userEmail;
    long userNumber;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //Defining firebase variables
        firebaseAuth = FirebaseAuth.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        //Get the id's of the components
        name = view.findViewById(R.id.Enter_Name_Edit_Text);
        pass = view.findViewById(R.id.Enter_Password_Edit_Text);
        email = view.findViewById(R.id.Enter_Email_Edit_Text);
        number = view.findViewById(R.id.Enter_Phone_Edit_Text);
        submit = view.findViewById(R.id.SignUpFirebase);
        forgotPass = view.findViewById(R.id.ForgotPassword);
        signIn = view.findViewById(R.id.SignIn);

        //Instantiate Firebase Database Variable
        firebaseDatabase = FirebaseDatabase.getInstance();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                //Getting the values passed
                userName = name.getText().toString();
                userPass = pass.getText().toString();
                userEmail = email.getText().toString();
                userNumber = Long.parseLong(number.getText().toString());


                    databaseRef = firebaseDatabase.getReference("Users/Login Details");
                    if(validateForm())
                    {
                        databaseRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                boolean flag = dataSnapshot.hasChild(userName);
                                if(flag)
                                    name.setError("User Name already exists");
                                else {
                                    createAccount(userEmail, userPass);
                                    //Start music player
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.d(TAG, "Error in checking user name: "+ databaseError.getMessage());
                            }
                        });
                    }


            }
        });

        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //start forget password fragment
            }
        });

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //start sign in fragment
                /*FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.add(R.id.signInFragment, new SignIn());
                ft.addToBackStack(null);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.commit();*/
                getFragmentManager().beginTransaction().replace(R.id.signUpFragment, new SignIn()).addToBackStack(null).commit();
            }
        });

        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        firebaseAuth.signOut();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        firebaseAuth.signOut();
    }

    //   [Firebase Authentication Functions]
    //Creating account in firebase
    private void createAccount(String email, String password)
    {
        if(!validateForm())
        {
            return;
        }
        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            newUser = new SignUpDetails(userName, userEmail, userNumber);
                            databaseRef = firebaseDatabase.getReference("Users/Login Details/"+userName);
                            databaseRef.setValue(newUser);
                            sendEmailVerification();
                        }
                        else
                        {
                            Toast.makeText(getContext(), "User already exists", Toast.LENGTH_SHORT).show();
                        }
                    }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Exception in creating new user : "+e);
            }
        });

    }

    //Send email verification via firebase
    private void sendEmailVerification()
    {
        final FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if(currentUser!=null)
        currentUser.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(getContext(), "Verification sent to: "
                                    +currentUser.getEmail() + ",Kindly verify your email", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Exception in sending email verification : "+e);
            }
        });
    }

    //Validate form
    private boolean validateForm()
    {
        final boolean[] flag = {true};
        Pattern validatePass = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).{6,15}$");
        //Username validation
        if(name.getText().length() == 0)
            name.setError("Please enter a username");
        //Password validation
        /*if(!validatePass.matcher(pass.toString().trim()).matches())
        {
            pass.setError("Please enter a password containing atleast one uppercase length, lowercase letter, a special symbol, a number and is of min 6 characters");
            flag[0] = false;
        }*/

        //Email validation
        if(!email.getText().toString().contains("@") || !email.getText().toString().contains(".") || email.getText().length() == 0)
        {
            email.setError("Please enter a valid e-mail");
            flag[0] = false;
        }
        //Number validation
        if(!(number.getText().toString().length() == 10))
        {
            number.setError("Number should be 10 digit long");
            flag[0] = false;
        }
        return flag[0];
    }

    private boolean isValidName(String nameValid){

        String NAME_PATTERN = "^(?=.*[0-9])(?=.*[@#$%^&+=])(?=\\S+$)$";
        Pattern pattern = Pattern.compile(NAME_PATTERN);
        Matcher matcher = pattern.matcher(nameValid);

        return  matcher.matches();
    }

    //  [End][Firebase Authentication Functions]

}
