package mysqltest.pruebalogin2;

import android.app.Application;

/**
 * Created by Victor on 02-abr-16.
 */
public class Aplicacion extends Application {
    private String usuario;

    private String codigo;

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }



    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
}
