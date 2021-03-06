package mysqltest.pruebalogin2;


import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.github.snowdream.android.widget.SmartImageView;
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
public class CameraFragment extends StatedFragment {
    private static final int MI_PERMISO =1 ;
    TextView tv_nombre;
    TextView contenidoTxt;
    TextView formatoTxt;
    //ListView lista;

    Button btIniciar;


    //Intento carga lista con imagenes de producto
    ListView lista;

    //Cargar item manualmente
    EditText etManual;
    Button btManual;

    ArrayList descripcion = new ArrayList();
    ArrayList precio = new ArrayList();
    ArrayList imagen = new ArrayList();



    public CameraFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_camera, container, false);

        tv_nombre = (TextView)view.findViewById(R.id.tv_nombre);
        Aplicacion app = (Aplicacion)getActivity().getApplicationContext();
        tv_nombre.setText("Bienvenido: "+app.getUsuario());
        //Se Instancia el Campo de Texto para el nombre del formato de código de barra
        formatoTxt = (TextView)view.findViewById(R.id.formato);
        //Se Instancia el Campo de Texto para el contenido  del código de barra
        contenidoTxt = (TextView)view.findViewById(R.id.contenido);
        lista = (ListView)view.findViewById(R.id.lista);

        btIniciar = (Button) view.findViewById(R.id.btScan);

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

        }

    }

    public void Scanner ()  {

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) getActivity(), Manifest.permission.CAMERA)) {
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
                ActivityCompat.requestPermissions((Activity) getActivity(), new String[]{Manifest.permission.CAMERA}, MI_PERMISO);
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
        String scanContenido = "";
        String scanFormato="";
        String resultadoCod=null;
        if (resultadoScan != null) {
            System.out.println("**** Tenemos un resultado !");

            scanContenido = resultadoScan.getContents();
            scanFormato = resultadoScan.getFormatName();

            formatoTxt.setText("FORMATO: " + scanFormato);
            contenidoTxt.setText("CONTENIDO: " + scanContenido);

            resultadoCod=resultadoScan.getContents();
            consultar(resultadoCod);
        }else{

            formatoTxt.setText("");
            contenidoTxt.setText("");

        }
    }
    public void consultar(final String codigoG){


        AsyncHttpClient client = new AsyncHttpClient();
        String url ="http://srvpruebas2016.esy.es/android/scan.php";
        RequestParams parametros = new RequestParams();
        parametros.put("cod",codigoG);

        client.post(url, parametros, new AsyncHttpResponseHandler() {

            ArrayList<String>lista;
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                if(statusCode==200){

                    try {
                        JSONArray o = new JSONArray(new String(responseBody));
                        String codi =o.getJSONObject(0).getString("cod_producto");
                        if(!TextUtils.isEmpty(codi)|| !codi.equals(null)|| !codi.equals(contenidoTxt.getText().toString())){

                            Crouton.makeText(getActivity(),"Producto encontrado!", Style.ALERT,(ViewGroup) getView()).show();
                            //cargarLista(obtenerDatosJSON(new String (responseBody)));
                            listaImagenes(codi);

                        }else{

                            Crouton.makeText(getActivity(),"Producto no encontrado", Style.ALERT,(ViewGroup) getView()).show();

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Crouton.makeText(getActivity(),"Producto no encontrado", Style.ALERT,(ViewGroup) getView()).show();
                    }

                }else{

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
        progressDialog.setMessage("Cargando lista...");
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
                        for(int i =0; i<jsonArray.length();i++){
                            descripcion.add(jsonArray.getJSONObject(i).getString("nombre"));
                            precio.add(jsonArray.getJSONObject(i).getString("precio"));
                            imagen.add(jsonArray.getJSONObject(i).getString("imagen"));
                        }
                        lista.setAdapter(new ImagenAdapter(getActivity().getApplicationContext()));
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
    public class ImagenAdapter extends BaseAdapter {
        Context ctx;
        LayoutInflater layoutInflater;
        SmartImageView smartImageView;
        TextView tvdescripcion,tvprecio;
        public ImagenAdapter(Context applicationContext) {
            this.ctx=applicationContext;
            layoutInflater=(LayoutInflater)ctx.getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return imagen.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewGroup viewGroup =(ViewGroup)layoutInflater.inflate(R.layout.activity_scanner_item,null);
            smartImageView=(SmartImageView)viewGroup.findViewById(R.id.imagen1);
            tvdescripcion=(TextView)viewGroup.findViewById(R.id.tvDescripcion);
            tvprecio=(TextView)viewGroup.findViewById(R.id.tvPrecio);
            String url="http://srvpruebas2016.esy.es/imagenes/"+imagen.get(position).toString();
            Rect rect = new Rect(smartImageView.getLeft(),smartImageView.getTop(),smartImageView.getRight(),smartImageView.getBottom());

            smartImageView.setImageUrl(url,rect);
            tvdescripcion.setText("Producto: \n"+descripcion.get(position).toString());
            tvprecio.setText("Precio: \n$"+precio.get(position).toString());


            return viewGroup;
        }
    }

}