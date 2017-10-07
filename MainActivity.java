package mysqltest.pruebalogin2;

import android.content.Context;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

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
       // Toast.makeText(this,"onDestroy "+getApplicationContext().getCacheDir().toString() , Toast.LENGTH_LONG).show();

    }

    protected void onDestroy() { super.onDestroy();

        try {
            trimCache(this);
            // Toast.makeText(this,"onDestroy " ,Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static void trimCache(Context context) {
        try {
            //Limpiar cache de imagenes
            //File dir = context.getCacheDir();
            File dir = new File(context.getCacheDir()+"/web_image_cache");
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        return dir.delete();
    }

    public void formRegistro(View view){
        startActivity(new Intent(MainActivity.this,Registro2.class));
        Crouton.clearCroutonsForActivity(MainActivity.this);
    }

    public void ingresar(View view){
        final String usuario=et_login.getText().toString();
        String pass =et_password.getText().toString();
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams requestParams  = new RequestParams();
        String url ="http://srvpruebas2016.esy.es/android/login.php";
        requestParams.put("usuario",usuario);
        requestParams.put("pass",pass);
        client.post(url, requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if(statusCode==200){
                        String usuarioJ,correoJ;
                    try {
                        JSONArray jsonArray = new JSONArray(new String(responseBody));
                            usuarioJ=jsonArray.getJSONObject(0).getString("usuario");
                            correoJ=jsonArray.getJSONObject(0).getString("correo");
                                if(!TextUtils.isEmpty(usuarioJ)){
                                    Aplicacion app = (Aplicacion)getApplicationContext();
                                    app.setUsuario(usuarioJ);
                                    app.setCorreo(correoJ);
                                    startActivity(new Intent(MainActivity.this,Navegador.class));
                                    finish();
                                }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Crouton.makeText(MainActivity.this,"Datos de ingreso no validos",Style.ALERT).show();
                    }
                }
            }

            @Override

            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Crouton.makeText(MainActivity.this,"Error de conexión",Style.INFO).show();

            }
        });

    }





}
