package mysqltest.pruebalogin2;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
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
    EditText etNombre;
    EditText etApp;
    EditText etApm;
    EditText etRut;
    boolean esta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        etr_user=(EditText)findViewById(R.id.etr_user);
        etr_password=(EditText)findViewById(R.id.etr_pass);
        etNombre=(EditText)findViewById(R.id.etNombre);
        etApp=(EditText)findViewById(R.id.etApp);
        etApm=(EditText)findViewById(R.id.etApm);
        etRut=(EditText)findViewById(R.id.etRut);

    }

    public void registrar(View view){

        String user = etr_user.getText().toString();
        String pass =etr_password.getText().toString();
        String nombre =etNombre.getText().toString();
        String app =etApp.getText().toString();
        String apm =etApm.getText().toString();
        String rut =etRut.getText().toString();

        if(user.equals("")||pass.equals("")||nombre.equals("")||app.equals("")||apm.equals("")||rut.equals("")){

            Crouton.makeText(Registro.this, "Debe completar todos los campos!", Style.ALERT).show();

        }else{

            registrarUsuario(user,pass,app,apm,rut,nombre);
        }


    }


    public void registrarUsuario(String usuario,String password,String app,String apm,String rut,String nombre){
        AsyncHttpClient client = new AsyncHttpClient();
        String url="http://victordbandroid.esy.es/sw/registro.php?";
        String parametros ="user="+usuario+"&password="+password+"&nombre="+nombre+"&app="+app+"&apm="+apm+"&rut="+rut;
            if(validarRut(rut)){
            client.post(url + parametros, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                    if (statusCode == 200) {
                        try {
                            JSONObject o = new JSONObject(new String(responseBody));
                            boolean ingreso = o.getBoolean("resultado");
                            if (ingreso == true) {
                                String resultado = new String(responseBody);
                                Crouton.makeText(Registro.this, "Registro Ok!", Style.ALERT).show();
                            } else {

                                Crouton.makeText(Registro.this, "Error registro!", Style.ALERT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Crouton.makeText(Registro.this, "Fallo el registro :(", Style.ALERT).show();
                }
            });}else{

                Crouton.makeText(Registro.this, "Rut invalido!", Style.ALERT).show();
            }

        }

    public static boolean validarRut(String rut) {

        boolean validacion = false;
        try {
            rut =  rut.toUpperCase();
            rut = rut.replace(".", "");
            rut = rut.replace("-", "");
            int rutAux = Integer.parseInt(rut.substring(0, rut.length() - 1));

            char dv = rut.charAt(rut.length() - 1);

            int m = 0, s = 1;
            for (; rutAux != 0; rutAux /= 10) {
                s = (s + rutAux % 10 * (9 - m++ % 6)) % 11;
            }
            if (dv == (char) (s != 0 ? s + 47 : 75)) {
                validacion = true;
            }

        } catch (java.lang.NumberFormatException e) {
        } catch (Exception e) {
        }
        return validacion;
    }

}
