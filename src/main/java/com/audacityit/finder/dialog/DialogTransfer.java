package com.audacityit.finder.dialog;


import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.audacityit.finder.R;
import com.audacityit.finder.util.ProgressGenerator;
import com.dd.processbutton.iml.ActionProcessButton;

public class DialogTransfer extends Dialog implements
        android.view.View.OnClickListener, ProgressGenerator.OnCompleteListener {

    public Activity c;
    public Dialog d;
    public ActionProcessButton btnSend;
    public EditText etFrom, etTo;
    public ProgressGenerator progressGenerator;

    public DialogTransfer(Activity a) {
        super(a);
        this.c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_transfer);
        btnSend = (ActionProcessButton) findViewById(R.id.btnSend);
        etFrom = (EditText) findViewById(R.id.etFrom);
        etTo = (EditText) findViewById(R.id.etTo);
        btnSend.setOnClickListener(this);

        progressGenerator = new ProgressGenerator(this);

        /*Bundle extras = getIntent().getExtras();
        if(extras != null && extras.getBoolean(EXTRAS_ENDLESS_MODE)) {
            btnSignIn.setMode(ActionProcessButton.Mode.ENDLESS);
        } else {
            btnSignIn.setMode(ActionProcessButton.Mode.PROGRESS);
        }
        */

    }

    @Override
    public void onComplete() {
        Toast.makeText(getContext(), R.string.TransferSuccess, Toast.LENGTH_LONG).show();
        closeDialog();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSend:
                progressGenerator.start(btnSend);
                btnSend.setEnabled(false);
                etFrom.setEnabled(false);
                etTo.setEnabled(false);
                //closeDialog();
                break;
            default:
                break;
        }
        dismiss();
    }

    private void closeDialog() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    DialogTransfer.this.hide();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


}