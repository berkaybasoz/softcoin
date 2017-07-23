package com.audacityit.finder.dialog;


import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.renderscript.Float4;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.audacityit.finder.R;
import com.audacityit.finder.model.User;
import com.audacityit.finder.util.ProgressGenerator;
import com.dd.processbutton.iml.ActionProcessButton;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;


//https://android-arsenal.com/details/1/3439
//https://android-arsenal.com/tag/11?sort=created
//https://github.com/barmstrong/bitcoin-android/
//https://stackoverflow.com/questions/28232116/android-using-zxing-generate-qr-code
//https://android-arsenal.com/details/1/4457

public class DialogCoinYukle extends Dialog implements
        View.OnClickListener, ProgressGenerator.OnCompleteListener {

    public Activity c;
    public Dialog d;
    public ActionProcessButton btnSend;
    public EditText etAd, etKart, etTarih, etCVV, etAmount;
    public ProgressGenerator progressGenerator;

    private final static int DIALOG_TYPE_QUANTITY = 1;
    private final static int DIALOG_TYPE_COIN = 2;
    public LinearLayout llSend;


    public static DialogCoinYukle createNewDialog(Activity a) {
        DialogCoinYukle dialogTransfer = new DialogCoinYukle(a);

        return dialogTransfer;
    }

    public DialogCoinYukle(Activity a) {
        super(a);
        this.c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        setContentView(R.layout.dialog_coin_yukle);
        btnSend = (ActionProcessButton) findViewById(R.id.btnSend);
        etAd = (EditText) findViewById(R.id.etAd);
        etAmount = (EditText) findViewById(R.id.etAmount);
        llSend = (LinearLayout) findViewById(R.id.llSend);
        etKart = (EditText) findViewById(R.id.etKart);
        etTarih = (EditText) findViewById(R.id.etTarih);
        etCVV = (EditText) findViewById(R.id.etCVV);
        btnSend.setOnClickListener(this);

        etAd.setText("Aytekin Ulaş");
        etKart.setText("4299 2735 9486 2624");
        etTarih.setText("12/20");
        etCVV.setText("294");
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
        Toast.makeText(getContext(), "Yüklendi", Toast.LENGTH_LONG).show();
        closeDialog();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSend:
                if (etAmount.getText().toString().length() < 1) {
                    Toast.makeText(getContext(), "Tutar alanı boş geçilemez", Toast.LENGTH_LONG).show();
                } else {

                    progressGenerator.start(btnSend);
                    btnSend.setEnabled(false);
                    //closeDialog();
                }
                break;
            default:
                break;
        }
    }

    private void closeDialog() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    DialogCoinYukle.this.dismiss();
                    float currentCoin = User.getCurrentUser().getCoin() + Float.parseFloat(etAmount.getText().toString());
                    User.getCurrentUser().setCoin(currentCoin);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}