package com.app.accgt.groupify.utils;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.app.accgt.groupify.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ReAuthenticateDialogFragment extends DialogFragment {

    private static final String TAG = ReAuthenticateDialogFragment.class.getSimpleName();
    private OnReAuthenticateSuccessListener onReauthenticateSuccessListener;
    private EditText passwordEditText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_reauthenticate, container);

        passwordEditText = view.findViewById(R.id.password_edit_text);
        Button cancelButton = view.findViewById(R.id.cancel_button);
        Button reLoginButton = view.findViewById(R.id.re_login_button);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        reLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (passwordEditText.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "Type password.", Toast.LENGTH_LONG).show();
                } else {
                    reAuthenticateUser(passwordEditText.getText().toString());
                }
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Window window = getDialog().getWindow();
        if (window != null) {
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        passwordEditText.getText().clear();
    }

    private void reAuthenticateUser(String password) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null && firebaseUser.getEmail() != null) {
            AuthCredential authCredential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), password);
            firebaseUser.reauthenticate(authCredential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "User re-authenticated.");

                                onReauthenticateSuccessListener.onReAuthenticateSuccess();
                                passwordEditText.getText().clear();

                                dismiss();
                            } else {
                                Toast.makeText(getActivity(), "Incorrect password, try again.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }

    public void addOnReAuthenticateSuccessListener(OnReAuthenticateSuccessListener onReauthenticateSuccessListener) {
        this.onReauthenticateSuccessListener = onReauthenticateSuccessListener;
    }

    public interface OnReAuthenticateSuccessListener {
        void onReAuthenticateSuccess();
    }
}
