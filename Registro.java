package mysqltest.pruebalogin2;

import android.app.Notification;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class Registro extends AppCompatActivity {
    //Valores registro
    EditText etr_user;
    EditText etr_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        etr_user=(EditText)findViewById(R.id.etr_user);
        etr_password=(EditText)findViewById(R.id.etr_pass);

    }

    public void registrar(View view){

        String user = etr_user.getText().toString();
        String pass =etr_password.getText().toString();


        if(user.equals("")||pass.equals("")){

            Crouton.makeText(Registro.this, "Rellene ambos campos", Style.ALERT).show();

        }else{

            registrarUsuario(user,pass);
        }


    }


    public void registrarUsuario(String usuario,String password){
        AsyncHttpClient client = new AsyncHttpClient();
        String url="http://victordbandroid.esy.es/sw/registro.php?";
        String parametros ="user="+usuario+"&password="+password;
        client.post(url + parametros, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {

                    String resultado = new String(responseBody);
                    Crouton.makeText(Registro.this, "Registro Ok!", Style.ALERT).show();

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });


    }


}
