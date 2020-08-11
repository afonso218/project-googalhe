package pt.iscte.pcd.core;

import java.io.Serializable;

/**
 * Created by Ricardo Afonso on 31/10/17.
 */
public class Tarefa implements Serializable {

    private int tarefa_id;
    private int client_id;
    private String pesquisa;
    private Noticia noticia;
    private boolean enviada;
    private boolean done;

    public Tarefa(int client_id, int tarefa_id, String pesquisa, Noticia noticia) {
        this.client_id = client_id;
        this.tarefa_id = tarefa_id;
        this.pesquisa = pesquisa;
        this.noticia = noticia;
        enviada = false;
        done = false;
    }

    public int getTarefa_id() {
        return tarefa_id;
    }

    public int getClient_id() {
        return client_id;
    }

    public Noticia getNoticia() {
        return noticia;
    }

    public String getPesquisa() {
        return pesquisa;
    }

    public void setNoticia(Noticia noticia) {
        this.noticia = noticia;
    }

    public void setPesquisa(String pesquisa) {
        this.pesquisa = pesquisa;
    }

    public void setEnviada() {
        enviada = true;
    }

    public void setReenviar() {
        enviada = false;
    }

    public void setDone(){
        done = true;
    }

    public boolean isEnviada() {
        return enviada;
    }

    public boolean isDone() {
        return done;
    }

    @Override
    public String toString() {
        return "Tarefa{" +
                "tarefa_id=" + tarefa_id +
                ", client_id=" + client_id +
                ", pesquisa='" + pesquisa + '\'' +
                ", noticia=" + noticia +
                ", enviada=" + enviada +
                ", done=" + done +
                '}';
    }

}
