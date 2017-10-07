package mysqltest.pruebalogin2;


import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.TextView;

import com.github.snowdream.android.widget.SmartImageView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListaFragment extends Fragment {
    private ListView listView,listView2;

    ArrayList descripcion = new ArrayList();
    ArrayList precio = new ArrayList();
    ArrayList imagen = new ArrayList();

    List<String> list = new ArrayList<String>();
    List<String> listFiltered = new ArrayList<String>();



    List<Integer> listFilteredPrecio = new ArrayList<Integer>();
    List<Integer> listFilteredImagen = new ArrayList<Integer>();


    public ListaFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lista, container, false);
        listView = (ListView) view.findViewById(R.id.listView);
        listView.setTextFilterEnabled(true);
        listView2 = (ListView) view.findViewById(R.id.imagenLista);

        descargarLista();
        final adaptadorLista ad = new adaptadorLista(ListaFragment.this.getContext());


        return view;
    }
    private void descargarLista(){

        //Inicializar icono de progreso
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Cargando lista de productos");
        progressDialog.show();

        AsyncHttpClient client = new AsyncHttpClient();
        String url="http://srvpruebas2016.esy.es/android/cargarLista.php";
        client.post(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            if(statusCode==200){
                progressDialog.dismiss();

                try {
                    JSONArray jsonArray = new JSONArray(new String(responseBody));

                    for(int i =0; i<jsonArray.length();i++){
                        descripcion.add(jsonArray.getJSONObject(i).getString("nombre"));
                        precio.add(jsonArray.getJSONObject(i).getString("precio"));
                        imagen.add(jsonArray.getJSONObject(i).getString("ruta"));

                    }
                    listView.setAdapter(new adaptadorLista(getActivity().getApplicationContext()));

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
        private class adaptadorLista extends BaseAdapter implements Filterable {
            Context ctx;
            LayoutInflater layoutInflater;
            SmartImageView smartImageView;
            TextView tv_descripcion, tv_precio;


            public adaptadorLista(Context applicationContext) {

                this.ctx = applicationContext;
                layoutInflater = (LayoutInflater) ctx.getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
                list=descripcion;
                listFiltered=list;

            }

            @Override
            public int getCount() {
                return listFiltered.size();
            }

            @Override
            public Object getItem(int position) {
                return listFiltered.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }


            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                ViewGroup viewGroup = (ViewGroup) layoutInflater.inflate(R.layout.fragment_lista_item, null);
                        smartImageView = (SmartImageView) viewGroup.findViewById(R.id.imagenLista);
                        tv_descripcion = (TextView) viewGroup.findViewById(R.id.descripcionLista);
                        tv_precio = (TextView) viewGroup.findViewById(R.id.precioLista);
                        String urlImagen = "http://srvpruebas2016.esy.es/" + imagen.get(position).toString();
                        Rect rect = new Rect(smartImageView.getLeft(), smartImageView.getTop(), smartImageView.getRight(), smartImageView.getBottom());
                        smartImageView.setImageUrl(urlImagen, rect);
                        tv_descripcion.setText("Producto: \n" + listFiltered.get(position).toString());
                        tv_precio.setText("Precio: \n$" + precio.get(position).toString());
                        return viewGroup;

            }


            @Override
            public Filter getFilter() {
                return new Filter() {
                    @Override
                    protected FilterResults performFiltering(CharSequence constraint) {
                        FilterResults results = new FilterResults();

                        if (constraint == null || constraint.length() == 0) {
                            //no constraint given, just return all the data. (no search)
                            results.count = descripcion.size();
                            results.values = descripcion;
                            listFilteredPrecio=precio;
                            listFilteredImagen=imagen;



                        } else {//do the search
                            List<String> resultsData = new ArrayList<>();
                            String searchStr = constraint.toString().toUpperCase();
                            for (String s : list)
                                if (s.toUpperCase().contains(searchStr)) resultsData.add(s);
                            results.count = resultsData.size();
                            results.values = resultsData;


                        }
                        return results;


                    }

                    @Override
                    protected void publishResults(CharSequence constraint, FilterResults results) {
                        listFiltered = (ArrayList<String>) results.values;
                        notifyDataSetChanged();

                        for(int i =0;i<listFiltered.size();i++){
                            System.out.println("Filtrado: "+listFiltered.get(i));
                            System.out.println("Tamaño lista: "+listFiltered.size());


                        }
                    }
                };

            }

        }


}
