package com.example.testpaypal;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.paypal.android.sdk.payments.PayPalAuthorization;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalFuturePaymentActivity;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;

import java.math.BigDecimal;

public class MainActivity extends AppCompatActivity {

    private static final String  TAG ="paymentExample";

    public static final String PAYPAY_KEY="AT-MwVJMVJJ98rwxViHEeBLmn30CZ0o3siXNfGRc3sJCVLSGbhfOjb6kdETS4D0KpOM3sE1W5Daa4lxF";

    private static final int REQUEST_CODE_PAYMENT=1;
    private static final int REQUEST_CODE_FUTURE_PAYMENT=2;
    private static final String CONFIG_ENVIRONMENT= PayPalConfiguration.ENVIRONMENT_SANDBOX;
    private static PayPalConfiguration config;
    PayPalPayment thingToBuy;
    Button order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        order =(Button)findViewById(R.id.order);
        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MakePayment();
            }
        });
        configPayPal();
    }
    private void configPayPal(){
        config=new PayPalConfiguration().environment(CONFIG_ENVIRONMENT).clientId(PAYPAY_KEY).merchantName("Paypal Login").merchantPrivacyPolicyUri(Uri.parse("https://www.example.com/privacy")).merchantUserAgreementUri(Uri.parse("https://www.example.com/legal"));
    }
    private void MakePayment(){
        Intent intent=new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config);
        startService(intent);

        thingToBuy =new PayPalPayment(new BigDecimal(String.valueOf("10.45")),"MYR","Payment",PayPalPayment.PAYMENT_INTENT_SALE);
        Intent payment=new Intent(this, PaymentActivity.class);
        payment.putExtra(PaymentActivity.EXTRA_PAYMENT,thingToBuy);
        payment.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config);
        startActivityForResult(payment,REQUEST_CODE_PAYMENT);
    }
    @Override
    protected  void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==REQUEST_CODE_PAYMENT){
            if(resultCode== Activity.RESULT_OK){

                PaymentConfirmation confirm=data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if(confirm!=null){
                    try{
                        System.out.println(confirm.toJSONObject().toString(4));
                        System.out.println(confirm.getPayment().toJSONObject().toString(4));
                        Toast.makeText(this,"payment successfully",Toast.LENGTH_LONG).show();
                    }catch (JSONException e){
                        Toast.makeText(this,e.toString(),Toast.LENGTH_LONG).show();

                    }

                }
            }else if(resultCode==Activity.RESULT_CANCELED){
                Toast.makeText(this,"Payment has been cancelled",Toast.LENGTH_LONG).show();
            }else if(resultCode==PaymentActivity.RESULT_EXTRAS_INVALID) {
                Toast.makeText(this, "error occured", Toast.LENGTH_LONG).show();
            }

        }else if(requestCode==REQUEST_CODE_FUTURE_PAYMENT){
            if(resultCode==Activity.RESULT_OK){

                PayPalAuthorization auth=data.getParcelableExtra(PayPalFuturePaymentActivity.EXTRA_RESULT_AUTHORIZATION);
                if(auth!=null){
                    try{
                        Log.i("FuturePaymentExample",auth.toJSONObject().toString(4));
                        String authroization_code=auth.getAuthorizationCode();
                        Log.d("FuturePaymentExample",authroization_code);
                        Log.e("paypal","future Payment code received from PayPal :"+authroization_code);
                    }catch (JSONException e){
                        Toast.makeText(this,"failure Occured",Toast.LENGTH_LONG).show();
                        Log.e("FuturePaymentExample","an extremely unlikely failure occured: ",e);

                    }

                }
            }

        }
    }
}