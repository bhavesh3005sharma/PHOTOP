package com.example.bestphotocollections;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bestphotocollections.Configurations.PaypalConfiguration;
import com.example.bestphotocollections.Model.ItemData;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.File;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class showSelectedImg extends AppCompatActivity {
    @BindView(R.id.selectedImg)
    ImageView img;
    @BindView(R.id.likes)
    TextView number_Likes;
    @BindView(R.id.dislike)
    TextView number_Dislikes;
    @BindView(R.id.views)
    TextView number_Views;
    @BindView(R.id.imgLike)
    ImageView like;
    @BindView(R.id.ImgDislike)
    ImageView dislike;
    @BindView(R.id.download)
    Button download;
    @BindView(R.id.textPrice)
    TextView price;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.metadata)
    TextView metadata;
    Unbinder unbinder;
    String uri=null;
    String str_title;
    String str_metadata;
    ArrayList<ItemData> list;
    public static final int PAYPAL_REQUEST_CODE = 123;
    String paymentAmount;


    //Paypal Configuration Object
    private static PayPalConfiguration config = new PayPalConfiguration()
            // Start with mock environment.  When ready, switch to sandbox (ENVIRONMENT_SANDBOX)
            // or live (ENVIRONMENT_PRODUCTION)
            .environment(PayPalConfiguration.ENVIRONMENT_NO_NETWORK)
            .clientId(PaypalConfiguration.PAYPAL_CLIENT_ID)
            .merchantName("Photop(Best Photos Collection)");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_selected_img);
        setTitle("Photop");
        unbinder = ButterKnife.bind(this);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
        );

        Intent intent =getIntent();
        uri = intent.getStringExtra("uri");
        Picasso.get().load(Uri.parse(uri)).placeholder(R.mipmap.ic_launcher).into(img);
        str_title=intent.getStringExtra("title");
        str_metadata = intent.getStringExtra("metadata");
        title.setText(str_title);
        metadata.setText(str_metadata);
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        unbinder.unbind();
        super.onDestroy();
    }

    @OnClick(R.id.download)
    public void download(View view){
        getPayment();
    }

    private void downloadFile() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl(uri);
        //StorageReference islandRef = storageRef.child("file.txt");

        File rootPath = new File(Environment.getExternalStorageDirectory(), "Photop Downloads");
        if (!rootPath.exists()) {
            rootPath.mkdirs();
        }

        final File localFile = new File(rootPath, Calendar.getInstance().getTimeInMillis()+".jpg");

        storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Log.e("firebase ", ";local tem file created  created " + localFile.toString());
                loadFromDownloadsSharedPref();
                list.add(new ItemData(str_metadata,uri,str_title));
                saveToDownloadsSharedPref();
                Toast.makeText(showSelectedImg.this, "Download Successful\n Check in Storage/Photop Downloads", Toast.LENGTH_LONG).show();
                //  updateDb(timestamp,localFile.toString(),position);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("firebase ", ";local tem file not created  created " + exception.toString());
                Toast.makeText(showSelectedImg.this, "Download UnSuccessful\n"+exception.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void saveToDownloadsSharedPref() {
        SharedPreferences sharedPreferences = getSharedPreferences(String.valueOf(R.string.sharedPrefDownloads),MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString("list",json);
        editor.apply();
        Log.d("selectedImgSave",list.toString());
    }

    private  void loadFromDownloadsSharedPref(){
        SharedPreferences sharedPreferences = getSharedPreferences(String.valueOf(R.string.sharedPrefDownloads),MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("list", null);
        Type type = new TypeToken<ArrayList<ItemData>>() {}.getType();
        list = gson.fromJson(json, type);

        if(list==null)
            list = new ArrayList<>();

        Log.d("selectedImgLoad",list.toString());
    }

    private void getPayment() {
        //Getting the amount from editText
        paymentAmount =   "1";//price.getText().toString();

        Intent intent_service = new Intent(this, PayPalService.class);
        intent_service.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent_service);

        //Creating a paypalpayment
        PayPalPayment payment = new PayPalPayment(new BigDecimal(String.valueOf(paymentAmount)), "USD", "Download Charge ",
                PayPalPayment.PAYMENT_INTENT_SALE);

        //Creating Paypal Payment activity intent
        Intent intent = new Intent(this, PaymentActivity.class);

        //putting the paypal configuration to the intent
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        //Puting paypal payment to the intent
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);

        //Starting the intent activity for result
        //the request code will be used on the method onActivityResult
        startActivityForResult(intent, PAYPAL_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //If the result is from paypal
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAYPAL_REQUEST_CODE) {

            //If the result is OK i.e. user has not canceled the payment
            if (resultCode == Activity.RESULT_OK) {
                //Getting the payment confirmation
                PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);

                //if confirmation is not null
                if (confirm != null) {
                    try {
                        //Getting the payment details
                        String paymentDetails = confirm.toJSONObject().toString(4);
                        Log.i("paymentExample", paymentDetails);

                        //Starting a new activity for the payment details and also putting the payment details with intent
                        startActivity(new Intent(this, ConformationActivity.class)
                                .putExtra("PaymentDetails", paymentDetails)
                                .putExtra("PaymentAmount", paymentAmount));
                        downloadFile();

                    } catch (JSONException e) {
                        Log.e("paymentExample", "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("paymentExample", "The user canceled.");
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i("paymentExample", "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
            }
        }
    }
}
