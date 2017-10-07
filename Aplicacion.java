package mysqltest.pruebalogin2;

import android.app.Application;

/**
 * Created by Victor on 02-abr-16.
 */
public class Aplicacion extends Application {
    private String usuario;
    private int res;
    String codigo;


    private boolean filtro;


    public String getCorreo() {
        return correo;
    }
    public void setCorreo(String correo) {
        this.correo = correo;
    }

    private String correo;
    public String getUsuario() {
        return usuario;
    }
    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public int getRes() {
        return res;
    }

    public void setRes(int res) {
        this.res = res;
    }

}
