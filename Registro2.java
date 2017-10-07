package mysqltest.pruebalogin2;

import android.app.ProgressDialog;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
public class Registro2 extends AppCompatActivity {
    EditText usuarioR,correoR,passR,pass2R;
    private static final String PATTERN_EMAIL = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro2);
        usuarioR = (EditText) findViewById(R.id.et_usuarioR);
        correoR = (EditText) findViewById(R.id.et_correoR);
        passR = (EditText)findViewById(R.id.et_passR);
        pass2R = (EditText) findViewById(R.id.et_pass2R);
    }
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            finish();
            Crouton.clearCroutonsForActivity(Registro2.this);
        }
        return super.onKeyDown(keyCode, event);
    }
    public boolean compararPassword(String pass, String pass2){
        boolean coincide;
        if(pass.equals(pass2)){
            coincide=true;
        }
        else {
            Crouton.makeText(Registro2.this,"Contraseñas no coinciden!",Style.ALERT).show();
            coincide = false;
        }
        return coincide;
    }

    public boolean validarCorreo(String correo){
        //Fuente: https://amatellanes.wordpress.com/2013/05/29/java-validar-direccion-de-correo-electronico-en-java/
        AlertDialog.Builder builder = new AlertDialog.Builder(Registro2.this);
        builder.setTitle("Error: ");
        // Compiles the given regular expression into a pattern.
        Pattern pattern = Pattern.compile(PATTERN_EMAIL);
        // Match the given input against this pattern
        Matcher matcher = pattern.matcher(correo);
        if(matcher.matches()==false) {
            builder.setMessage("El correo ingresado no es valido.");
            builder.setPositiveButton("Aceptar",null);
            builder.show();
        }
        return matcher.matches();
    }

    public boolean validarVacios(String usuario,String correo,String pass,String pass2){
        boolean todoIngresado=true;
        String errorVacio="Por favor, complete los siguientes campos:\n ";
        AlertDialog.Builder ventana = new AlertDialog.Builder(Registro2.this);
        ventana.setTitle("Datos requeridos!");
        ventana.setPositiveButton("Aceptar",null);
        if(usuario.isEmpty()){
            //Crouton.makeText(Registro2.this,"Debe ingresar nombre de usuario!", Style.ALERT).show();
            errorVacio=errorVacio+"\nNombre de usuario.";
            todoIngresado=false;
        }
        if(correo.isEmpty()){
            //Crouton.makeText(Registro2.this,"Debe ingresar un correo!",Style.ALERT).show();
            errorVacio=errorVacio+"\nCorreo.";
            todoIngresado=false;
        }
        if(pass.isEmpty()){
            //Crouton.makeText(Registro2.this,"Debe ingresar una contraseña",Style.ALERT).show();
            errorVacio=errorVacio+"\nContraseña.";
            todoIngresado=false;
        }
        if(pass2.isEmpty()){
            //Crouton.makeText(Registro2.this,"Debe confirmar contraseña!",Style.ALERT).show();
            errorVacio=errorVacio+"\nConfirmar contraseña.";
            todoIngresado=false;
        }
        if(todoIngresado==false){

            ventana.setMessage(errorVacio);
            ventana.show();
        }

        return  todoIngresado;
    }
    public void validarUsuario(final String usuario,final String correo,final String pass){
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        String url ="http://srvpruebas2016.esy.es/android/validarUC.php";
        requestParams.put("usuario",usuario);
        requestParams.put("correo",correo);
        final AlertDialog.Builder builder = new AlertDialog.Builder(Registro2.this);
        builder.setTitle("Error: ");
        client.post(url, requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                boolean error=false;
                String mensaje="Se han producido los siguientes errores durante el registro: \n";
                if(statusCode==200){
                    String usuarioJ;
                    String correoJ;
                    try {
                        JSONArray jsonArray = new JSONArray(new String(responseBody));
                        for (int i=0;i<jsonArray.length();i++) {
                            usuarioJ = jsonArray.getJSONObject(i).getString("usuario");
                            correoJ = jsonArray.getJSONObject(i).getString("correo");
                            usuarioJ.toLowerCase();
                            correoJ.toLowerCase();
                            if (usuario.toLowerCase().equals(usuarioJ.toLowerCase())) {
                                //Crouton.makeText(Registro2.this, "Usuario ya existe!", Style.ALERT).show();
                                mensaje=mensaje+"\nEl usuario ya existe.";
                                error=true;
                            }
                            if (correo.toLowerCase().equals(correoJ.toLowerCase())) {
                                //Crouton.makeText(Registro2.this, "Correo ya existe!", Style.ALERT).show();
                                mensaje=mensaje+"\nEl correo ya existe.";
                                error=true;
                            }

                        }
                        if(error==false){

                            registra(usuario,correo,pass);
                        }
                        if(error==true){

                            builder.setMessage(mensaje);
                            builder.setPositiveButton("Aceptar",null);
                            builder.show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Crouton.makeText(Registro2.this, "Entro conexion", Style.INFO).show();
            }
        });
    }

    public void registrarUsuario(View view){
        //Ejecutar validaciones
        boolean registrar=true;
        String usuario = usuarioR.getText().toString();
        String correo = correoR.getText().toString();
        String pass = passR.getText().toString();
        String pass2 = pass2R.getText().toString();

        boolean vacios = validarVacios(usuario,correo,pass,pass2);
        boolean contra = compararPassword(pass,pass2);
        boolean correoV=true;

        if(vacios==false)
            registrar=false;

        if(contra==false)
            registrar=false;

        if(vacios==true && contra==true){
            correoV=validarCorreo(correo);
        }

        if(correoV==false)
            registrar=false;

        if(registrar==true)
            validarUsuario(usuario,correo,pass);
    }
    public void registra (String usuario,String correo,String pass){
        final ProgressDialog progressDialog = new ProgressDialog(Registro2.this);
        progressDialog.setMessage("Registrando usuario");
        progressDialog.show();
        AsyncHttpClient client = new AsyncHttpClient();
        String url="http://srvpruebas2016.esy.es/android/registro.php?";
        String parametros ="usuario="+usuario+"&correo="+correo+"&pass="+pass;
            client.post(url + parametros, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    if (statusCode == 200) {
                        try {
                            JSONObject o = new JSONObject(new String(responseBody));
                            boolean ingreso = o.getBoolean("resultado");
                            if (ingreso == true) {
                                Crouton.makeText(Registro2.this, "Registro Ok!", Style.INFO).show();
                            } else {

                                Crouton.makeText(Registro2.this, "Error registro!", Style.ALERT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    progressDialog.dismiss();
                }
                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Crouton.makeText(Registro2.this, "Fallo el registro :(", Style.ALERT).show();
                }
            });
    }
}