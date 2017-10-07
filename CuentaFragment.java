package mysqltest.pruebalogin2;


import android.app.DownloadManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;


/**
 * A simple {@link Fragment} subclass.
 */
public class CuentaFragment extends Fragment {

    Button btEliminar;
    Button btCambiar;
    public CuentaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Aplicacion app = (Aplicacion)getActivity().getApplicationContext();
        System.out.print("Usuario activo: "+app.getUsuario().toString());
        View view= inflater.inflate(R.layout.fragment_cuenta, container, false);
        btEliminar = (Button) view.findViewById(R.id.btEliminar);
        btEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogoEliminar();
            }
        });

        btCambiar = (Button) view.findViewById(R.id.btCambiar);
        btCambiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent i = new Intent(getActivity(),CambiaPass.class);
                startActivity(i);
            }
        });
        return view;
    }

    public void dialogoEliminar(){
        AlertDialog.Builder ventana = new AlertDialog.Builder(getContext());
        ventana.setTitle("¿Desea eliminar su cuenta?");
        ventana.setMessage("Debe tener en cuenta que una vez eliminada su cuenta, ya no tendra acceso sistema, a no ser que realize el proceso de registro nuevamente");
        ventana.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                eliminar();
            }
        });
        ventana.setNegativeButton("Cancelar",null);
        ventana.show();
    }


    public void eliminar(){
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        String url ="http://srvpruebas2016.esy.es/android/eliminarCuenta.php";
        Aplicacion app = (Aplicacion)getActivity().getApplicationContext();
        String usuario = app.getUsuario();
        System.out.print("Usuario activo: "+usuario);
        params.put("usuario",usuario);
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if(statusCode==200){
                    try{
                        JSONObject o = new JSONObject(new String(responseBody));
                        int elimina = o.getInt("resultado");
                        if(elimina==1){
                            AlertDialog.Builder ventana = new AlertDialog.Builder(getContext());
                            ventana.setMessage("Su cuenta ha sido eliminada correctamente!");
                            ventana.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //getActivity().getSupportFragmentManager().beginTransaction().remove(CuentaFragment.this).addToBackStack(null).commit();
                                    startActivity(new Intent(getContext(),MainActivity.class));
                                    getActivity().finish();
                                }
                            });
                            ventana.show();
                        }if(elimina==0){
                            Crouton.makeText(getActivity(),"Fallo la eliminacion de su cuenta",Style.ALERT).show();
                        }
                    }catch(JSONException e){

                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    private void clearBackStack() {
        FragmentManager manager = getChildFragmentManager();
        if (manager.getBackStackEntryCount() > 0) {
            FragmentManager.BackStackEntry first = manager.getBackStackEntryAt(0);
            manager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

}
