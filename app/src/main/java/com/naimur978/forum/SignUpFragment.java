package com.naimur978.forum;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class SignUpFragment extends Fragment {

    public SignUpFragment() {
        // Required empty public constructor
    }


    private TextView alreadyHaveAnAccount;
    private FrameLayout parentFrameLayout;

    private EditText email,fullname,password,confirmPassword;
    private ImageButton closeBtn;
    private Button signUpBtn;
    private ProgressBar progressBar;


    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;


    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+.[a-z]+";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        alreadyHaveAnAccount = view.findViewById(R.id.tv_already_have_an_account);
        parentFrameLayout = getActivity().findViewById(R.id.register_framelayout);

        email = view.findViewById(R.id.sign_up_email);
        fullname = view.findViewById(R.id.sign_up_full_name);
        password = view.findViewById(R.id.sign_up_password);
        confirmPassword = view.findViewById(R.id.sign_up_confirm_password);
        closeBtn = view.findViewById(R.id.sign_up_close_btn);
        signUpBtn = view.findViewById(R.id.sign_up_btn);
        progressBar = view.findViewById(R.id.signup_progress_bar);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainIntent();
            }
        });

        alreadyHaveAnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new SignInFragment());
            }
        });

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        fullname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        confirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo : send data to firebase
                checkEmailAndPassword();
            }
        });
    }

    private void mainIntent(){
        startActivity(new Intent(getActivity(),MainActivity.class));
        getActivity().finish();
    }

    private void checkEmailAndPassword() {

        Drawable customErrorIcon = getResources().getDrawable(R.mipmap.custom_error_icon);
        customErrorIcon.setBounds(0,0,customErrorIcon.getIntrinsicWidth(),customErrorIcon.getIntrinsicHeight());

        if(email.getText().toString().matches(emailPattern)){
            if(password.getText().toString().equals(confirmPassword.getText().toString()) && password.getText().toString().length()>=8){

                progressBar.setVisibility(View.VISIBLE);
                signUpBtn.setEnabled(false);
                signUpBtn.setTextColor(Color.argb(50,255,255,255));

                firebaseAuth.createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){

                                    /*Map<Object, String> userData = new HashMap<>();//hashmap as a medium to store the data
                                    userData.put("fullname",fullname.getText().toString());*/

                                    //..................................................new
                                    FirebaseUser user = firebaseAuth.getCurrentUser();

                                    String email = user.getEmail();
                                    String uid = user.getUid();

                                    HashMap<Object, String> hashMap = new HashMap<>();//hashmap as a medium to store the data

                                    hashMap.put("email",email);
                                    hashMap.put("uid",uid);
                                    hashMap.put("name",fullname.getText().toString());
                                    hashMap.put("onlineStatus","online");
                                    hashMap.put("typingTo","noOne");
                                    hashMap.put("phone","");
                                    hashMap.put("image","");
                                    hashMap.put("cover","");

                                    FirebaseDatabase database = FirebaseDatabase.getInstance();

                                    DatabaseReference reference = database.getReference("Users");
                                    reference.child(uid).setValue(hashMap);

                                    //..................................................new

                                    firebaseFirestore.collection("USERS") //USERS is a folder
                                            .add(hashMap)//CompleteListener to make the condition of if else
                                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                                    if(task.isSuccessful()){
                                                        startActivity(new Intent(getActivity(), DashboardActivity.class));
                                                        getActivity().finish();
                                                    }else {
                                                        String error = task.getException().getMessage();
                                                        Toast.makeText(getActivity(), "error", Toast.LENGTH_SHORT).show();//here getActivity is the Register activity, which is sworn to contain 2 fragments

                                                        progressBar.setVisibility(View.INVISIBLE);
                                                        signUpBtn.setEnabled(true);
                                                        signUpBtn.setTextColor(Color.rgb(255,255,255));
                                                    }
                                                }
                                            });


                                }else{
                                    String error = task.getException().getMessage();
                                    Toast.makeText(getActivity(), "error", Toast.LENGTH_SHORT).show();//here getActivity is the Register activity, which is sworn to contain 2 fragments

                                    progressBar.setVisibility(View.INVISIBLE);
                                    signUpBtn.setEnabled(true);
                                    signUpBtn.setTextColor(Color.rgb(255,255,255));
                                }
                            }
                        });

            }else{
                if(password.getText().toString().length()<8){
                    Toast.makeText(getActivity(), "Invalid Password", Toast.LENGTH_SHORT).show();
                    password.setError("You have to put at least 8 characters.",customErrorIcon);
                }
                else{
                    confirmPassword.setError("Password doesn't match!",customErrorIcon);
                }
            }
        }else{
            email.setError("Invalid Email!",customErrorIcon);
        }
    }

    //for checking TextUtils
    private void checkInputs() {
        if(!TextUtils.isEmpty(email.getText())){
            if(!TextUtils.isEmpty(fullname.getText())){
                if(!TextUtils.isEmpty(password.getText())){
                    if(!TextUtils.isEmpty(confirmPassword.getText())){
                        signUpBtn.setEnabled(true);
                        signUpBtn.setTextColor(Color.rgb(255,255,255));
                    }else{
                        signUpBtn.setEnabled(false);
                        signUpBtn.setTextColor(Color.argb(50,255,255,255));
                    }
                }else{
                    signUpBtn.setEnabled(false);
                    signUpBtn.setTextColor(Color.argb(50,255,255,255));
                }
            }else{
                signUpBtn.setEnabled(false);
                signUpBtn.setTextColor(Color.argb(50,255,255,255));
            }
        }else{
            signUpBtn.setEnabled(false);
            signUpBtn.setTextColor(Color.argb(50,255,255,255));
        }
    }

    private void setFragment(Fragment fragment) {
        //include getactivity, cz its not from this activity(outside of the group); in contrast with register activity
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_left,R.anim.slideout_from_right);
        fragmentTransaction.replace(parentFrameLayout.getId(),fragment);
        fragmentTransaction.commit();
    }
}
