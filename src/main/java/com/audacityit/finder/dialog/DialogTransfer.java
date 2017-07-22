package com.audacityit.finder.dialog;


import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.audacityit.finder.R;
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

public class DialogTransfer extends Dialog implements
        android.view.View.OnClickListener, ProgressGenerator.OnCompleteListener {

    public Activity c;
    public Dialog d;
    public ActionProcessButton btnSend;
    public EditText etFrom, etTo, etAmount;
    public ProgressGenerator progressGenerator;
    public ImageView ivQRCode;
    private final static int DIALOG_TYPE_QUANTITY = 1;
    private final static int DIALOG_TYPE_COIN = 2;
    public LinearLayout llSend, llOr;
    public TextView btnOr;

    public static int DIALOG_TYPE = 0;
    public static String FROM_NAME = "", TO_NAME = "";
    public static int QUANTITY = 0;
    public static float COIN = 0;

    public static DialogTransfer createNewDialogForQuantity(Activity a, String fromName, String toName, int quantity) {
        DialogTransfer dialogTransfer = new DialogTransfer(a);
        /*Bundle args = new Bundle();
        args.putInt("num", DIALOG_TYPE_QUANTITY);
        dialogTransfer.setArguments(args);
        */
        DIALOG_TYPE = DIALOG_TYPE_QUANTITY;
        FROM_NAME = fromName;
        TO_NAME = toName;
        QUANTITY = quantity;

        return dialogTransfer;
    }

    public static DialogTransfer createNewDialogForCoin(Activity a, String fromName, String toName, float coin) {
        DialogTransfer dialogTransfer = new DialogTransfer(a);
        /*Bundle args = new Bundle();
        args.putInt("num", DIALOG_TYPE_QUANTITY);
        dialogTransfer.setArguments(args);
        */
        DIALOG_TYPE = DIALOG_TYPE_COIN;
        FROM_NAME = fromName;
        TO_NAME = toName;
        COIN = coin;

        return dialogTransfer;
    }

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
        etAmount = (EditText) findViewById(R.id.etAmount);
        ivQRCode = (ImageView) findViewById(R.id.qrCode);
        llSend = (LinearLayout) findViewById(R.id.llSend);
        llOr = (LinearLayout) findViewById(R.id.llOr);
        btnOr = (TextView) findViewById(R.id.btnOr);

        btnOr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (llSend.getVisibility() == View.GONE) {
                    llSend.setVisibility(View.VISIBLE);
                } else {
                    llSend.setVisibility(View.GONE);
                }
            }
        });

        etFrom.setText("@" + FROM_NAME);
        etTo.setText("@" + TO_NAME);

        if (DIALOG_TYPE == DIALOG_TYPE_QUANTITY) {

            llSend.setVisibility(View.GONE);


            etAmount.setText(String.valueOf(QUANTITY));

            etFrom.setEnabled(false);
            etTo.setEnabled(false);
            etAmount.setEnabled(false);


            try {
                String str = FROM_NAME + ":" + TO_NAME + ":" + String.valueOf(QUANTITY);
                Bitmap bitmap = encodeAsBitmap(str);
                ivQRCode.setImageBitmap(bitmap);
            } catch (WriterException e) {
                e.printStackTrace();
            }
        } else {
            etAmount.setHint("SCoin");
            llSend.setVisibility(View.VISIBLE);
            ivQRCode.setVisibility(View.GONE);
            llOr.setVisibility(View.GONE);
        }

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
    }

    private void closeDialog() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    DialogTransfer.this.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static int WHITE = 0xFFFFFFFF;
    public static int BLACK = 0xFF000000;
    public final static int WIDTH = 500;

    Bitmap encodeAsBitmap(String str) throws WriterException {
        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(str,
                    BarcodeFormat.QR_CODE, WIDTH, WIDTH, null);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            int offset = y * w;
            for (int x = 0; x < w; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, WIDTH, 0, 0, w, h);
        return bitmap;
    }
}