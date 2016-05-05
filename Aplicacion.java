package mysqltest.pruebalogin2;

import android.app.Application;

/**
 * Created by Victor on 02-abr-16.
 */
public class Aplicacion extends Application {
    private String usuario;
    private int res;
    String codigo;
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

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
}
