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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PasswordDialogFragment extends DialogFragment {

    private static final String TAG = ReAuthenticateDialogFragment.class.getSimpleName();
    private EditText newPasswordEditText;
    private EditText repeatNewPasswordEditText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_password, container);

        newPasswordEditText = view.findViewById(R.id.new_password_edit_text);
        repeatNewPasswordEditText = view.findViewById(R.id.repeat_new_password_edit_text);
        Button cancelButton = view.findViewById(R.id.cancel_button);
        Button setPasswordButton = view.findViewById(R.id.set_password_button);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        setPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (newPasswordEditText.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), "Type new password.", Toast.LENGTH_LONG).show();
                } else if (repeatNewPasswordEditText.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), "Confirm new password.", Toast.LENGTH_LONG).show();
                } else if (newPasswordEditText.getText().toString().equals(repeatNewPasswordEditText.getText().toString())) {
                    updatePassword(newPasswordEditText.getText().toString());
                } else {
                    Toast.makeText(getActivity(), "Passwords do not match.", Toast.LENGTH_LONG).show();
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
        newPasswordEditText.getText().clear();
        repeatNewPasswordEditText.getText().clear();
    }

    private void updatePassword(String newPassword) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            firebaseUser.updatePassword(newPassword)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "User password updated.");

                                newPasswordEditText.getText().clear();
                                repeatNewPasswordEditText.getText().clear();

                                Toast.makeText(getActivity(), "New password set.", Toast.LENGTH_LONG).show();
                                dismiss();
                            } else {
                                Toast.makeText(getActivity(), "Wrong password format, try again", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }
}
