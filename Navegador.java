package mysqltest.pruebalogin2;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.inthecheesefactory.thecheeselibrary.fragment.support.v4.app.bus.ActivityResultBus;
import com.inthecheesefactory.thecheeselibrary.fragment.support.v4.app.bus.ActivityResultEvent;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class Navegador extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    TextView nombre,correo;
    private MenuItem mCurrentFragmentPosition;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ActivityResultBus.getInstance().postQueue(
                new ActivityResultEvent(requestCode, resultCode, data));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navegador);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //Abrir activity Navegador, con fragment de inicio
        android.support.v4.app.FragmentTransaction fragmentTransaction =
                getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.container, new InicioFragment());
        fragmentTransaction.commit();
        getSupportActionBar().setTitle("Inicio");


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        //Cargar nombre de usuario y correo en header de navigationDrawer
        Aplicacion app = (Aplicacion)getApplicationContext();
        View header=navigationView.getHeaderView(0);
        nombre = (TextView)header.findViewById(R.id.nombre_header);
        nombre.setText(app.getUsuario());
        correo = (TextView) header.findViewById(R.id.correo_header);
        correo.setText(app.getCorreo());


    }

    //@Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.container);
        //corregir


        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }else{
            if (f instanceof InicioFragment){
                dialogoCerrarSesion();
            }else{
                super.onBackPressed();
            }
        }
            }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navegador, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        Aplicacion app = (Aplicacion)getApplicationContext();
        Bundle data = new Bundle();
        data.putString("usuario",app.getUsuario().toString());

        int id = item.getItemId();
        android.support.v4.app.FragmentTransaction fragmentTransaction =
                getSupportFragmentManager().beginTransaction();
        mCurrentFragmentPosition = item;

        if (id == R.id.nav_inicio) {
            // Handle the camera action
            fragmentTransaction.replace(R.id.container,new InicioFragment());
            fragmentTransaction.commit();
            getSupportActionBar().setTitle("Inicio");

        }else if (id == R.id.nav_cuenta) {
            fragmentTransaction.replace(R.id.container, new CuentaFragment());
            fragmentTransaction.addToBackStack(null).commit();
            getSupportActionBar().setTitle("Mi cuenta");
        }
        else if (id == R.id.nav_scan) {
            fragmentTransaction.replace(R.id.container,new LectorFragment());
            fragmentTransaction.addToBackStack(null).commit();
            getSupportActionBar().setTitle("Escanear");

        } else if (id == R.id.nav_lista) {
            fragmentTransaction.replace(R.id.container,new ListaFragment());
            fragmentTransaction.addToBackStack(null).commit();
            getSupportActionBar().setTitle("Lista productos");

        } else if(id == R.id.nav_salir){
            dialogoCerrarSesion();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void dialogoCerrarSesion(){
    AlertDialog.Builder ventana = new AlertDialog.Builder(Navegador.this);
    ventana.setTitle("Atención!");
    ventana.setMessage("¿Desea cerrar sesión?");
    ventana.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {

        }
    });
    ventana.setPositiveButton("Cerrar sesión", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            startActivity(new Intent(Navegador.this,MainActivity.class));
            finish();
        }
    });
    ventana.show();
    }

}
