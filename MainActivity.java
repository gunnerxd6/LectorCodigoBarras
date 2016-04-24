package mysqltest.pruebalogin2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class MainActivity extends AppCompatActivity {
    //Valores Login
    EditText et_login;
    EditText et_password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        et_login=(EditText)findViewById(R.id.et_login);
        et_password=(EditText)findViewById(R.id.et_password);


    }


    public void formRegistro(View view){
        startActivity(new Intent(MainActivity.this,Registro.class));
    }









    public void validarOnClick(View view){

        AsyncHttpClient client = new AsyncHttpClient();
        String url ="http://victordbandroid.esy.es/sw/login.php";
        RequestParams requestParams = new RequestParams();
        requestParams.add("login",et_login.getText().toString());
        requestParams.add("password",et_password.getText().toString());

        RequestHandle post = client.post(url, requestParams, new AsyncHttpResponseHandler() {
            String usuario = null;
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            if(statusCode ==200){
                try{
                    JSONObject o = new JSONObject(new String(responseBody));
                    usuario = o.getString("login");
                    if(!TextUtils.isEmpty(usuario)){

                        Aplicacion app = (Aplicacion)getApplicationContext();
                        app.setUsuario(usuario);
                        startActivity(new Intent(MainActivity.this,Scanner.class));

                    }else{

                        Crouton.makeText(MainActivity.this, "Error de ingreso", Style.ALERT).show();

                    }
                }catch(JSONException e){
                    Crouton.makeText(MainActivity.this,"Fallo general", Style.ALERT).show();

                }

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Crouton.makeText(MainActivity.this, "Fallo general", Style.ALERT).show();
            }
        });

    }



}
