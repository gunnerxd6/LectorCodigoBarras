package mysqltest.pruebalogin2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.util.TextUtils;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class CambiaPass extends AppCompatActivity {
    EditText etPass1,etPass2;
    Button btCambia;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambia_pass);
        etPass1 =(EditText) findViewById(R.id.etPass1);
        etPass2 = (EditText) findViewById(R.id.etPass2);
        btCambia = (Button) findViewById(R.id.btCambiaPass);
        btCambia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    cambiarPass(etPass1.getText().toString(),etPass2.getText().toString());
                }
        });

    }

    private void validarPass(final String pass, final String pass1, final String pass2) {
        Aplicacion app = (Aplicacion)getApplicationContext();
        final String usuario = app.getUsuario();
        AsyncHttpClient client = new AsyncHttpClient();
        final String passN=pass1;
        String url="http://srvpruebas2016.esy.es/android/validarPass.php";
        RequestParams params = new RequestParams();
        params.put("usuario",usuario);
        params.put("pass",pass);
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if(statusCode==200){
                    try{
                        JSONArray jsonArray = new JSONArray(new String(responseBody));
                        String usuarioJ = jsonArray.getJSONObject(0).getString("usuario");
                        String passJ = jsonArray.getJSONObject(0).getString("contrasena");

                        if(passN.equals(passJ)){
                            Crouton.makeText(CambiaPass.this,"Contraseña actual incorrecta",Style.ALERT).show();

                        }else {
                            Crouton.makeText(CambiaPass.this,"Contraseña actual incorrecta",Style.ALERT).show();
                        }


                       /* if(!jsonArray.getJSONObject(0).getString("usuario").equals("") && !jsonArray.getJSONObject(0).getString("contrasena").equals("")){

                        }
                        else {

                        } */
                    }catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });

    }

    private void cambiarPass(String pass1, String pass2){
        Aplicacion app = (Aplicacion)getApplicationContext();
        String usuario = app.getUsuario();
        AsyncHttpClient client = new AsyncHttpClient();
        String url="http://srvpruebas2016.esy.es/android/cambiaPass.php";
        RequestParams params = new RequestParams();
        params.put("pass",pass1);
        params.put("usuario",usuario);
        if (pass1.equals("")||pass2.equals("")){
            Crouton.makeText(CambiaPass.this,"Debe completar todos los campos!",Style.ALERT).show();
        }else{
            if(pass1.equals(pass2)){
                client.post(url, params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        if(statusCode==200) {
                            try {
                                JSONObject o  = new JSONObject(new String(responseBody));
                                int cambia = o.getInt("resultado");
                                if(cambia==1){
                                    Crouton.makeText(CambiaPass.this,"Su contraseña se actualizo correctamente!",Style.CONFIRM).show();
                                }
                                if(cambia==0){
                                    Crouton.makeText(CambiaPass.this,"Error al actualizar contraseña",Style.ALERT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                    }
                });

            }else{
                Crouton.makeText(CambiaPass.this,"Contraseñas no coinciden!",Style.ALERT).show();
            }

        }

    }

}
