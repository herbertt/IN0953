package com.example.helicoptero.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.helicoptero.myapplication.ngsi.AdapterOcurrence;
import com.example.helicoptero.myapplication.ngsi.Attributes;
import com.example.helicoptero.myapplication.ngsi.Entity;
import com.google.gson.Gson;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import at.abraxas.amarino.Amarino;

public class MainActivity extends AppCompatActivity {

    private static final String DEVICE_ADDRESS = "30:14:11:03:21:35";
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    long lastChange;
    DoTurnOffon randomWork;
    TextView textValor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Amarino.connect(this, DEVICE_ADDRESS);

        textValor = (TextView)findViewById(R.id.textView);

        randomWork = new DoTurnOffon();
        randomWork.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        Amarino.disconnect(this, DEVICE_ADDRESS);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //thread tick fiware
    public class DoTurnOffon extends Thread {

        private static final String TAG = "DoSomethingThread";
        @Override
        public void run() {

            while (true) {
                try {
                    String serverRequest = fiwareRequest();
                    publishProgress(serverRequest);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private void publishProgress(String param) {

            final String resultado = param;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        retornoServidorFiware(resultado);
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public String fiwareRequest() throws Exception {
        int responseCode = 0;
        String json = "";
        String result= "";
        String line = "";


        try {
            String id =  "led1";// pegar id do objeto
            String uri = "http://148.6.80.19:1026/v1/contextEntities/";
            uri += id;

            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(uri)
                    .get()
                    .addHeader("Accept", "application/json")
                    .build();

            Response response;

            int executeCount = 0;
            do
            {
                response = client.newCall(request).execute();
                executeCount++;
            }
            while(response.code() == 408 && executeCount < 5);

            result = response.body().string();
            json = new JSONObject(result).toString();

        } catch (Exception e) {
            responseCode = 408;
            e.printStackTrace();
        }

        return json;
    }



    public void retornoServidorFiware(String retorno) throws Exception {
        Gson gson = new Gson();
        String noValues = "{\"errorCode\":{\"code\":\"404\",\"reasonPhrase\":\"No context element found\"}}";
        if (!retorno.equals(noValues) && !retorno.equals("")) {
           // Integer occurrenceCode = Integer.valueOf(getOccurrenceCode(retorno));
            if (getOccurrenceCode(retorno).equals("0")){
                textValor.setText("OFF");
            }else if(getOccurrenceCode(retorno).equals("1")){
                textValor.setText("ON");
            }

            Amarino.sendDataToArduino(this, DEVICE_ADDRESS, 'A', getOccurrenceCode(retorno));
        }
    }

    public String getOccurrenceCode(String result) throws Exception {
        Entity entity = AdapterOcurrence.parseEntity(result);
        boolean isFirst = true;
        String code = "";

            for (Attributes att : entity.getAttributes()) {
                if (att.getName().equalsIgnoreCase("occurrenceCode")) {
                    String[] tokensVal = att.getValue().split(",");
                    code = String.valueOf(tokensVal[0].trim());
                }
            }

        return code;
    }
}
