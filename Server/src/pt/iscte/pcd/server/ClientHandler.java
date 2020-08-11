package pt.iscte.pcd.server;

import pt.iscte.pcd.core.Logger;
import pt.iscte.pcd.core.Noticia;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

/**
 * Created by Ricardo Afonso on 30/10/17.
 */
public class ClientHandler extends Thread {

    private final int id;
    private GestorLigacoes gestorLigacoes;
    private GestorTarefas gestorTarefas;

    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;

    public ClientHandler(int id, GestorLigacoes gestorLigacoes, GestorTarefas gestorTarefas, Socket socket, ObjectOutputStream output, ObjectInputStream input) {
        this.gestorLigacoes = gestorLigacoes;
        this.gestorTarefas = gestorTarefas;
        this.socket = socket;
        this.output = output;
        this.input = input;
        this.id = id;
    }

    public int getClientId() {
        return id;
    }

    @Override
    public void run() {

        try {

            while (true) {

                try {
                    String pesquisa = (String) input.readObject();
                    Logger.logInfo("[ClientHandler " + id + "] Recebido pedido para pesquisar \"" + pesquisa + "\"");

                    gestorTarefas.addSearch(id, pesquisa);

                    List<Noticia> noticias = gestorTarefas.getSearch(id);
                    output.writeObject(noticias);
                    output.flush();
                    Logger.logInfo("[ClientHandler " + id + "] Enviado pedido para pesquisar \"" + pesquisa + "\"");

                } catch (InterruptedException | ClassNotFoundException e) {
                    Logger.logError("[ClientHandler " + id + "] Falha na leitura de Objecto!", e);
                }


            }

        } catch (IOException e) {

            Logger.logError("[ClientHandler " + id + "] Ligação desligada pelo Cliente!", e);

            try {
                socket.close();
                input.close();
                output.close();
            } catch (IOException e2) {
                Logger.logError("[ClientHandler " + id + "] Ligação já tinha sido fechada!", e);
            }
        } finally {

            gestorTarefas.removeTarefas(id);
            gestorLigacoes.remove(this);

        }


    }

}
