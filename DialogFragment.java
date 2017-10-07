package mysqltest.pruebalogin2;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
/**
 * A simple {@link Fragment} subclass.
 */
public class DialogFragment extends Fragment {

    public DialogFragment() {
        // Required empty public constructor
    }
    ImageView img;
    Button bt;
    TextView tv_descripcion,tv_precio;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_dialog, container, false);
        img = (ImageView) view.findViewById(R.id.imageView22);
        tv_descripcion = (TextView) view.findViewById(R.id.tv_descripcion);
        tv_precio = (TextView) view.findViewById(R.id.tv_precio);
        Bundle extras = getArguments();
        String url1 = extras.getString("urlImagen");
        int precio = extras.getInt("precio");
        String textoDescripcion = extras.getString("nombre");
        tv_descripcion.setText(textoDescripcion);
        tv_precio.setText("$"+String.valueOf(precio));
        Picasso.with(getActivity()).load(url1).into(img);
        bt = (Button) view.findViewById(R.id.btCerrar);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cerrarDialogo();
            }
        });
        return view;
    }
    public void cerrarDialogo(){
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }
}