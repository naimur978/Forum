package com.naimur978.forum;


import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordFragment extends Fragment {


    public ResetPasswordFragment() {
        // Required empty public constructor
    }

    private EditText registeredEmail;
    private TextView goBack;
    private Button resetPasswordButton;
    private ProgressBar progressBar;

    private FrameLayout parentFrameLayout;

    private FirebaseAuth firebaseAuth;

    private ViewGroup emailIconContainer; //linear layout=viewgroup
    private ImageView emailIcon;
    private TextView emailIconText;

    @Override//createview is used for casting
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reset_password, container, false);

        registeredEmail = view.findViewById(R.id.forgot_password_email);
        resetPasswordButton = view.findViewById(R.id.reset_password_btn);
        goBack = view.findViewById(R.id.tv_forgot_password_go_back);
        progressBar =view.findViewById(R.id.forgot_password_progress_bar);

        parentFrameLayout = getActivity().findViewById(R.id.register_framelayout);
        firebaseAuth = FirebaseAuth.getInstance();

        emailIconContainer = view.findViewById(R.id.forgot_password_email_icon_container);
        emailIcon = view.findViewById(R.id.forgot_password_email_icon);
        emailIconText = view.findViewById(R.id.forgot_password_email_icon_text);

        return view;
    }

    @Override//viewcreated is used for functionality
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        registeredEmail.addTextChangedListener(new TextWatcher() {
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
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new SignInFragment());
            }
        });
        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TransitionManager.beginDelayedTransition(emailIconContainer);//transition will happen inside the parenthesis(viewgroup-emailcontainer)

                emailIconText.setVisibility(View.GONE);
                emailIcon.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);

                resetPasswordButton.setEnabled(false);
                resetPasswordButton.setTextColor(Color.argb(50,255,255,255));

                firebaseAuth.sendPasswordResetEmail(registeredEmail.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(getActivity(), "Email sent successfully!", Toast.LENGTH_LONG).show();

                                    ////ANIMATION CODE STARTS HERE

                                    ScaleAnimation scaleAnimation = new ScaleAnimation(1,0,1,0,emailIcon.getWidth()/2,emailIcon.getHeight()/2);
                                    scaleAnimation.setDuration(100);
                                    scaleAnimation.setInterpolator(new AccelerateInterpolator());
                                    scaleAnimation.setRepeatMode(Animation.REVERSE);
                                    scaleAnimation.setRepeatCount(1);

                                    scaleAnimation.setAnimationListener(new Animation.AnimationListener(){
                                        @Override
                                        public void onAnimationStart(Animation animation){

                                        }
                                        @Override
                                        public void onAnimationEnd(Animation animation){
                                            emailIconText.setText("Recovery email sent successfully ! check your inbox");
                                            emailIconText.setTextColor(getResources().getColor(R.color.successGreen));

                                            TransitionManager.beginDelayedTransition(emailIconContainer);
                                            emailIconText.setVisibility(View.VISIBLE);
                                        }
                                        @Override
                                        public void onAnimationRepeat(Animation animation){
                                            emailIcon.setImageResource(R.mipmap.green_email);
                                        }
                                    });

                                    emailIcon.startAnimation(scaleAnimation);


////ANIMATION CODE ENDS HERE

                                }else{
                                    String error = task.getException().getMessage();

                                    resetPasswordButton.setEnabled(true);
                                    resetPasswordButton.setTextColor(Color.rgb(255,255,255));

                                    emailIconText.setText(error);
                                    emailIconText.setTextColor(getResources().getColor(R.color.colorPrimary));
                                    TransitionManager.beginDelayedTransition(emailIconContainer);
                                    emailIconText.setVisibility(View.VISIBLE);
                                }

                                progressBar.setVisibility(View.GONE);
                            }
                        });
            }
        });
    }

    private void checkInputs() {
        if(!TextUtils.isEmpty(registeredEmail.getText())){
            resetPasswordButton.setEnabled(true);
            resetPasswordButton.setTextColor(Color.rgb(255,255,255));
        }else{
            resetPasswordButton.setEnabled(false);
            resetPasswordButton.setTextColor(Color.argb(50,255,255,255));
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


