package pt.iscte.pcd.server;

import pt.iscte.pcd.core.Logger;
import pt.iscte.pcd.core.Noticia;
import pt.iscte.pcd.core.Tarefa;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by Ricardo Afonso on 30/10/17.
 */
public class WorkerHandler extends Thread {

    private final int id;
    private GestorLigacoes gestorLigacoes;
    private GestorTarefas gestorTarefas;

    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;

    public WorkerHandler(int id, GestorLigacoes gestorLigacoes, GestorTarefas gestorTarefas, Socket socket, ObjectOutputStream output, ObjectInputStream input) {
        this.gestorLigacoes = gestorLigacoes;
        this.gestorTarefas = gestorTarefas;
        this.socket = socket;
        this.output = output;
        this.input = input;
        this.id = id;
    }

    public int getWorkerId() {
        return id;
    }

    @Override
    public void run() {

        try {


            while (socket != null && !socket.isClosed()) {

                Tarefa tarefa = null;
                try {

                    tarefa = gestorTarefas.getNextTarefa();
                    output.writeObject(tarefa);
                    output.flush();

                    Logger.logInfo("[WorkerHandler " + id + "] Enviada tarefa para o worker \"" + tarefa.toString() + "\"");

                    tarefa = (Tarefa) input.readObject();
                    Logger.logInfo("[WorkerHandler " + id + "] Recebida tarefa processada \"" + tarefa.toString() + "\"");

                    gestorTarefas.doneTarefa(tarefa);

                } catch (IOException | ClassNotFoundException e) {

                    if (tarefa != null) {
                        gestorTarefas.reenviar(tarefa);
                    }

                    Logger.logError("[WorkerHandler " + id + "] Falha na leitura de Objecto!", e);

                    try {
                        socket.close();
                        input.close();
                        output.close();
                    } catch (IOException e2) {
                        Logger.logError("[WorkerHandler " + id + "] Ligação já tinha sido fechada!", e);
                    }

                }

            }

            gestorLigacoes.remove(this);
            Logger.logInfo("[WorkerHandler " + id + "] Foi perdida ligação!");

        } catch (InterruptedException e) {
            Logger.logInfo("[WorkerHandler " + id + "] Foi interrompido!");
        }


    }

    public synchronized void send(Noticia noticia) {

        if (socket != null && !socket.isClosed()) {
            try {
                output.writeObject(noticia);
                output.flush();
            } catch (IOException e) {
                Logger.logError("[WorkerHandler " + id + "] Falha no envio de noticia para o Worker", e);
            }
        } else {
            Logger.logError("[WorkerHandler " + id + "] Não foi possível enviar noticia para o Worker por nao existir ligação estabelecida", null);
        }
    }

}
