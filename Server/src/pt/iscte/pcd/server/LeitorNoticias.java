package pt.iscte.pcd.server;

import pt.iscte.pcd.core.Noticia;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Ricardo Afonso on 01/11/17.
 */
public class LeitorNoticias {

    private static final String DIR_NAME = "noticias";

    private List<Noticia> noticias;

    public LeitorNoticias() {

        noticias = new ArrayList<>();

        try {

            lerFicheiros();

        } catch (IllegalAccessException e) {
            System.out.println("Não foi possível carregar noticias!");
        }

        System.out.println("Carregadas " + noticias.size() + " Noticias");

    }

    private void lerFicheiros() throws IllegalAccessException {

        URL resource = this.getClass().getClassLoader().getResource(DIR_NAME);

        if (resource == null) {
            throw new IllegalAccessException("Directoria de noticias não foi encontrada!");
        }

        File folder = new File(resource.getPath());

        System.out.println("A carregar ficheiros da directoria: " + folder.toPath());

        File[] files = folder.listFiles();

        if (files != null) {
            for (File file : files) {

                if (!file.isDirectory()) {

                    Noticia noticia = lerFicheiro(file);
                    if (noticia != null) {
                        noticias.add(noticia);
                    }
                }

            }
        }

    }


    private Noticia lerFicheiro(File file) {

        try {

            System.out.println("A ler ficheiro " + file.getName());
            Scanner scanner = new Scanner(file);

            String titulo = "";
            String conteudo = "";

            boolean extrairTitulo = true;
            while (scanner.hasNextLine()) {

                String texto = scanner.nextLine();

                if (extrairTitulo) {
                    titulo = texto;
                    extrairTitulo = false;
                } else {
                    conteudo += texto + "\n";
                }

            }

            if (titulo.isEmpty() || conteudo.isEmpty()) {
                System.out.println("Ficheiro não é valido = [" + file + "]");
            } else {
                return new Noticia(titulo, conteudo);
            }

        } catch (FileNotFoundException e) {
            System.out.println("Ficheiro não encontrado = [" + file + "]");
        }

        return null;

    }

    public List<Noticia> getNoticias() {
        return noticias;
    }

}
