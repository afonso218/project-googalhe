package pt.iscte.pcd.core;

import java.io.Serializable;

/**
 * Created by Ricardo Afonso on 30/10/17.
 */
public class Noticia implements Serializable, Comparable<Noticia> {

    private String titulo;
    private String conteudo;
    private int ocorrencias;

    public Noticia(String titulo, String conteudo) {
        this.titulo = titulo;
        this.conteudo = conteudo;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getConteudo() {
        return conteudo;
    }

    public int getOcorrencias() {
        return ocorrencias;
    }

    public void setOcorrencias(int ocorrencias) {
        this.ocorrencias = ocorrencias;
    }

    @Override
    public int compareTo(Noticia n) {
        return n.getOcorrencias() - getOcorrencias();
    }

    @Override
    public String toString() {
        return getOcorrencias() + " - " + getTitulo();
    }
}
