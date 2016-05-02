package mysqltest.pruebalogin2;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.github.snowdream.android.widget.SmartImageView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.ParseException;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class Scanner extends AppCompatActivity {
    private static final int MI_PERMISO =1 ;
    TextView tv_nombre;
    TextView contenidoTxt;
    TextView formatoTxt;
    //ListView lista;
    int valorTemp=0;
    TextView sumaT;

    //Intento carga lista con imagenes de producto
    ListView lista;

    ArrayList descripcion = new ArrayList();
    ArrayList precio = new ArrayList();
    ArrayList imagen = new ArrayList();


    ArrayList<String>full = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        tv_nombre = (TextView)findViewById(R.id.tv_nombre);
        Aplicacion app = (Aplicacion)getApplicationContext();
        tv_nombre.setText("Bienvenido: "+app.getUsuario());
        //Se Instancia el Campo de Texto para el nombre del formato de código de barra
        formatoTxt = (TextView)findViewById(R.id.formato);
        //Se Instancia el Campo de Texto para el contenido  del código de barra
        contenidoTxt = (TextView)findViewById(R.id.contenido);
        lista = (ListView)findViewById(R.id.lista);
        sumaT =(TextView)findViewById(R.id.tvSuma);

    }
    public void Scanner (View view) {

        if (ActivityCompat.checkSelfPermission(Scanner.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) Scanner.this, Manifest.permission.CAMERA)) {
                new SweetAlertDialog(Scanner.this, SweetAlertDialog.WARNING_TYPE)
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
                                ActivityCompat.requestPermissions(Scanner.this, new String[]{Manifest.permission.CAMERA}, MI_PERMISO);
                            }
                        }).show();
            } else {
                ActivityCompat.requestPermissions((Activity) Scanner.this, new String[]{Manifest.permission.CAMERA}, MI_PERMISO);
            }

        } else {

            IntentIntegrator integrator = new IntentIntegrator(this);
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
        String url ="http://victordbandroid.esy.es/sw/consulta.php";
        RequestParams parametros = new RequestParams();
        parametros.put("cod",codigoG);

        client.post(url, parametros, new AsyncHttpResponseHandler() {

            ArrayList<String>lista;
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                if(statusCode==200){

                    try {
                        JSONArray o = new JSONArray(new String(responseBody));
                        String codi =o.getJSONObject(0).getString("cod");
                        if(!TextUtils.isEmpty(codi)|| !codi.equals(null)|| !codi.equals(contenidoTxt.getText().toString())){

                            Crouton.makeText(Scanner.this,"Producto encontrado!", Style.ALERT).show();
                            //cargarLista(obtenerDatosJSON(new String (responseBody)));
                            listaImagenes(codigoG);

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Crouton.makeText(Scanner.this,"Producto no encontrado", Style.ALERT).show();
                    }

                }else{

                    Crouton.makeText(Scanner.this,"Producto no encontrado", Style.ALERT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Crouton.makeText(Scanner.this, "Producto no encontrado", Style.ALERT).show();
            }
        });

    }

/*    public ArrayList<String>obtenerDatosJSON(String arrayJson){
        ArrayList<String> listado = new ArrayList<String>();
        try{
            JSONArray jsonArray = new JSONArray(arrayJson);
            String texto;
            for(int i =0;i<jsonArray.length();i++){

                texto = "Codigo: "+jsonArray.getJSONObject(i).getString("cod")+"\nDescripcion: "+
                        jsonArray.getJSONObject(i).getString("descripcion")+"\nPrecio: "+
                        jsonArray.getJSONObject(i).getString(("precio"));
                    listado.add(texto);
                full.add(texto);
                valorTemp=valorTemp+Integer.valueOf(jsonArray.getJSONObject(i).getString("precio"));
                sumaT.setText(String.valueOf(valorTemp));
            }

        }catch(Exception e){
            e.printStackTrace();
        }

        return full;
    } */

   /* public void cargarLista (ArrayList<String> datos){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,datos);
        lista.setAdapter(adapter);
    } */

    public void listaImagenes(String codigoG){
        //codigo.clear();
        //descripcion.clear();
        //precio.clear();
        //imagen.clear();

    final ProgressDialog progressDialog = new ProgressDialog(Scanner.this);
        progressDialog.setMessage("Cargando lista...");
        progressDialog.show();
        AsyncHttpClient client = new AsyncHttpClient();
        String url="http://victordbandroid.esy.es/sw/consulta2.php";
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
                        descripcion.add(jsonArray.getJSONObject(i).getString("descripcion"));
                        precio.add(jsonArray.getJSONObject(i).getString("precio"));
                        imagen.add(jsonArray.getJSONObject(i).getString("imagen"));
                    }
                    lista.setAdapter(new ImagenAdapter(getApplicationContext()));
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
    public class ImagenAdapter extends BaseAdapter{
        Context ctx;
        LayoutInflater layoutInflater;
        SmartImageView smartImageView;
        TextView tvdescripcion,tvprecio,tvSuma;
        public ImagenAdapter(Context applicationContext) {
            this.ctx=applicationContext;
            layoutInflater=(LayoutInflater)ctx.getSystemService(LAYOUT_INFLATER_SERVICE);
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
            tvSuma=(TextView)findViewById(R.id.tvSuma);
            String url="http://victordbandroid.esy.es/sw/imagenes/"+imagen.get(position).toString();
            Rect rect = new Rect(smartImageView.getLeft(),smartImageView.getTop(),smartImageView.getRight(),smartImageView.getBottom());

            smartImageView.setImageUrl(url,rect);
            tvdescripcion.setText("Producto: \n"+descripcion.get(position).toString());
            tvprecio.setText("Precio: \n$"+precio.get(position).toString());
            String precioS=sumar(precio);
            tvSuma.setText("$"+precioS);

            return viewGroup;
        }
    }
     public String sumar(ArrayList precio){
         String sumat;
         int p=0;
         for(int i =0;i<precio.size();i++){
             p=p+Integer.valueOf(precio.get(i).toString());
         }
         sumat=String.valueOf(p);
         return sumat;
     }

}