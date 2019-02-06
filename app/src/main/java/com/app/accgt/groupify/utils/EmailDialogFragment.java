package com.app.accgt.groupify.utils;

import android.content.Context;
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
import com.app.accgt.groupify.activities.FeedActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EmailDialogFragment extends DialogFragment {

    private static final String TAG = ReAuthenticateDialogFragment.class.getSimpleName();
    private EditText newEmailEditText;
    private EditText repeatNewEmailEditText;
    private FeedActivity feedActivity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_email, container);

        newEmailEditText = view.findViewById(R.id.new_email_edit_text);
        repeatNewEmailEditText = view.findViewById(R.id.repeat_new_email_edit_text);
        Button cancelButton = view.findViewById(R.id.cancel_button);
        Button setEmailButton = view.findViewById(R.id.set_email_button);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        setEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (newEmailEditText.getText().toString().isEmpty()) {
                    Toast.makeText(feedActivity, "Type new email.", Toast.LENGTH_LONG).show();
                } else if (repeatNewEmailEditText.getText().toString().isEmpty()) {
                    Toast.makeText(feedActivity, "Confirm new email.", Toast.LENGTH_LONG).show();
                } else if (newEmailEditText.getText().toString().equals(repeatNewEmailEditText.getText().toString())) {
                    updateEmail(newEmailEditText.getText().toString());
                } else {
                    Toast.makeText(feedActivity, "Emails do not match.", Toast.LENGTH_LONG).show();
                }
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        feedActivity = (FeedActivity) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        feedActivity = null;
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
        newEmailEditText.getText().clear();
        repeatNewEmailEditText.getText().clear();
    }

    private void updateEmail(String newEmail) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            firebaseUser.updateEmail(newEmail)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "User email updated.");

                                newEmailEditText.getText().clear();
                                repeatNewEmailEditText.getText().clear();

                                feedActivity.updateEmailTextView();

                                Toast.makeText(feedActivity, "New email set.", Toast.LENGTH_LONG).show();
                                dismiss();
                            } else {
                                Toast.makeText(feedActivity, "Wrong email format, try again", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }
}
