package mysqltest.pruebalogin2;


import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.inthecheesefactory.thecheeselibrary.fragment.support.v4.app.StatedFragment;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cz.msebera.android.httpclient.Header;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;


/**
 * A simple {@link Fragment} subclass.
 */
public class LectorFragment extends StatedFragment {
    private static final int MI_PERMISO =1 ;
    TextView tv_nombre;
    TextView contenidoTxt;
    TextView formatoTxt;
    //ListView lista;
    TextView sumaT;
    Button btIniciar;
    TextView tvSuma;
    RelativeLayout relativeLayoutl;

    //Intento carga lista con imagenes de producto
    ListView lista;

    //Cargar item manualmente
    EditText etManual;
    Button btManual;

    ArrayList descripcion = new ArrayList();
    ArrayList precio = new ArrayList();
    ArrayList imagen = new ArrayList();
    ArrayList des = new ArrayList();





    public LectorFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_lector, container, false);

        relativeLayoutl = (RelativeLayout) view.findViewById(R.id.lPrincipal);

        tv_nombre = (TextView)view.findViewById(R.id.tv_nombre);
        Aplicacion app = (Aplicacion)getActivity().getApplicationContext();
        tv_nombre.setText("Bienvenido: "+app.getUsuario());
        //Se Instancia el Campo de Texto para el nombre del formato de código de barra
        formatoTxt = (TextView)view.findViewById(R.id.formato);
        //Se Instancia el Campo de Texto para el contenido  del código de barra
        contenidoTxt = (TextView)view.findViewById(R.id.contenido);
        lista = (ListView)view.findViewById(R.id.lista);
        sumaT =(TextView)view.findViewById(R.id.tvSuma);
        btIniciar = (Button) view.findViewById(R.id.btScan);
        tvSuma=(TextView)view.findViewById(R.id.tvSuma);
        etManual=(EditText)view.findViewById(R.id.etManual);
        btManual= (Button) view.findViewById(R.id.btManual);
        btIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Scanner();
            }
        });

        btManual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manual();
            }
        });

        return view;
    }
    public void manual(){
        String codigo;
        Context ctx = getContext();
        final AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        codigo = etManual.getText().toString();
        if(codigo.equals("")){
            builder.setTitle("Error!");
            builder.setMessage("Debe ingresar un codigo para escanear");
            builder.setPositiveButton("Aceptar",null);
            builder.show();
        }else{
            listaImagenes(etManual.getText().toString());
            crearDialogo(etManual.getText().toString());

        }

    }

    public void Scanner ()  {

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CAMERA)) {
                new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Atención")
                        .setContentText("Debe otorgar permisos para acceder a su a la camara de su dispositivo")
                        .setConfirmText("Solicitar permiso")
                        .setCancelText("Cancelar")
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.cancel();
                            }
                        })
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.cancel();
                                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, MI_PERMISO);
                            }
                        }).show();
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, MI_PERMISO);
            }

        } else {

            IntentIntegrator integrator = new IntentIntegrator(getActivity());
            integrator.addExtra("SCAN_WIDTH", 640);
            integrator.addExtra("SCAN_HEIGHT", 480);
            integrator.addExtra("PROMPT_MESSAGE", "Busque un código para escanear");
            integrator.addExtra("RESULT_DISPLAY_DURATION_MS",0l);

            integrator.initiateScan();



        }
    }
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        IntentResult resultadoScan = IntentIntegrator.parseActivityResult( requestCode, resultCode, intent);

        String resultadoCod;
        if (resultadoScan != null) {
            System.out.println("**** Tenemos un resultado !");

            resultadoCod=resultadoScan.getContents();
            consultar(resultadoCod);

        }else{

            formatoTxt.setText("");
            contenidoTxt.setText("");

        }
    }
    public void consultar(String codigoG){
        AsyncHttpClient client = new AsyncHttpClient();
        String url ="http://srvpruebas2016.esy.es/android/scan.php";
        RequestParams parametros = new RequestParams();
        parametros.put("cod",codigoG);
        client.post(url, parametros, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String codi;
                if(statusCode==200){
                    try {
                        JSONArray o = new JSONArray(new String(responseBody));
                        codi =o.getJSONObject(0).getString("codigo");
                        if(!TextUtils.isEmpty(codi)|| !codi.equals(null)|| !codi.equals(contenidoTxt.getText().toString())){
                            System.out.println("Codigo en entrada"+codi.toString());
                            Crouton.makeText(getActivity(),"Producto encontrado!", Style.CONFIRM,(ViewGroup) getView()).show();
                            String codigoTemp= codi;
                            crearDialogo(codigoTemp);
                        }else{
                            Crouton.makeText(getActivity(),"Producto no encontrado", Style.ALERT,(ViewGroup) getView()).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Crouton.makeText(getActivity(),"Producto no encontrado", Style.ALERT,(ViewGroup) getView()).show();
                    }
                }
                else{
                    Crouton.makeText(getActivity(),"Producto no encontrado", Style.ALERT,(ViewGroup) getView()).show();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Crouton.makeText(getActivity(), "Producto no encontrado", Style.ALERT).show();
            }
        });

    }
    public void listaImagenes(String codigoG){
        //codigo.clear();
        //descripcion.clear();
        //precio.clear();
        //imagen.clear();

        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Cargando resultado...");
        progressDialog.show();
        AsyncHttpClient client = new AsyncHttpClient();
        String url="http://srvpruebas2016.esy.es/android/scan.php";
        RequestParams requestParams = new RequestParams();
        requestParams.put("cod",codigoG);
        client.post(url, requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if(statusCode==200){
                    progressDialog.dismiss();
                    try {
                        JSONArray jsonArray = new JSONArray(new String (responseBody));
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    descripcion.add(jsonArray.getJSONObject(i).getString("nombre"));
                                    precio.add(jsonArray.getJSONObject(i).getString("precio"));
                                    imagen.add(jsonArray.getJSONObject(i).getString("imagen"));
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


    }



    public void crearDialogo(String codigoG){
        final Intent i = new Intent(getActivity(),Dialogo.class);



       AsyncHttpClient client = new AsyncHttpClient();
        String url="http://srvpruebas2016.esy.es/android/scan.php";
        RequestParams requestParams = new RequestParams();
        requestParams.put("cod",codigoG);
        client.post(url,requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONArray jsonArray = new JSONArray(new String (responseBody));
                    for(int i =0; i<jsonArray.length();i++){
                        descripcion.add(jsonArray.getJSONObject(i).getString("nombre"));
                        precio.add(jsonArray.getJSONObject(i).getString("precio"));
                        imagen.add(jsonArray.getJSONObject(i).getString("ruta"));
                        des.add(jsonArray.getJSONObject(i).getString("des"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                catch (IndexOutOfBoundsException x){
                    Crouton.makeText(getActivity(),"Producto no encontrado",Style.ALERT).show();
                }

                try {
                    Bundle bundle = new Bundle();
                    String urlImagen = "http://srvpruebas2016.esy.es/" + imagen.get(0);
                    bundle.putString("urlImagen", urlImagen);
                    bundle.putString("nombre", descripcion.get(0).toString());
                    bundle.putInt("precio", Integer.valueOf(precio.get(0).toString()));
                    bundle.putString("des", des.get(0).toString());


                    i.putExtras(bundle);
                    startActivity(i);
                }catch (IndexOutOfBoundsException e){
                    Crouton.makeText(getActivity(),"Producto no encontrado", Style.ALERT,(ViewGroup) getView()).show();
                }

                imagen.clear();
                descripcion.clear();
                precio.clear();
                des.clear();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }


}