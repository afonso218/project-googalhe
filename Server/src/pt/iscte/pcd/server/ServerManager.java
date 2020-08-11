package pt.iscte.pcd.server;

import pt.iscte.pcd.core.Logger;
import pt.iscte.pcd.core.Noticia;
import pt.iscte.pcd.core.Parameters;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Ricardo Afonso on 30/10/17.
 */
public class ServerManager extends Thread implements Observer {

    private JTextField number_clients;
    private JTextField number_workers;
    private JTextField number_tasks_todo;
    private JTextField number_tasks_done;

    private GestorTarefas gestorTarefas;
    private GestorLigacoes gestorLigacoes;
    private ServerSocket serverSocket;

    public ServerManager(List<Noticia> noticias, JTextField number_clients, JTextField number_workers, JTextField number_tasks_todo, JTextField number_tasks_done) {

        this.number_clients = number_clients;
        this.number_workers = number_workers;
        this.number_tasks_todo = number_tasks_todo;
        this.number_tasks_done = number_tasks_done;

        gestorTarefas = new GestorTarefas(noticias);
        gestorTarefas.addObserver(this);

        gestorLigacoes = new GestorLigacoes();
        gestorLigacoes.addObserver(this);

        try {

            serverSocket = new ServerSocket(Parameters.SERVER_PORT);
            Logger.logInfo("Start server on port " + Parameters.SERVER_PORT);

        } catch (IOException e) {
            Logger.logError("Falhou na inicialização do Server Socket", e);
        }

    }


    @Override
    public void run() {

        int client_id = 1;
        int worker_id = 1;

        while (true) {

            try {
                Socket socket = serverSocket.accept();
                Logger.logInfo("Accepted connection...");

                ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream input = new ObjectInputStream(socket.getInputStream());

                int type = input.readInt();

                if (type == Parameters.CLIENT_TYPE) {

                    Logger.logInfo("Connection established with Client(" + client_id + ")!");
                    ClientHandler novoCliente = new ClientHandler(client_id, gestorLigacoes, gestorTarefas, socket, output, input);
                    gestorLigacoes.add(novoCliente);
                    novoCliente.start();
                    client_id++;

                } else if (type == Parameters.WORKER_TYPE) {

                    Logger.logInfo("Connection established with Worker(" + worker_id + ")!");
                    WorkerHandler novoWorker = new WorkerHandler(worker_id, gestorLigacoes, gestorTarefas, socket, output, input);
                    gestorLigacoes.add(novoWorker);
                    novoWorker.start();
                    worker_id++;

                } else {

                    Logger.logWarning("Connection with invalid type (" + type + ")...");
                    socket.close();

                }

            } catch (IOException e) {
                Logger.logError("Falhou ao estabelecer ligação", e);
            }


        }

    }

    @Override
    public void update(Observable o, Object arg) {

        if (o instanceof GestorTarefas) {
            number_tasks_todo.setText(String.valueOf(((GestorTarefas) o).getNumTarefasPorRealizar()));
            number_tasks_done.setText(String.valueOf(((GestorTarefas) o).getNumTarefasRealizadas()));
        }

        if (o instanceof GestorLigacoes) {
            number_clients.setText(String.valueOf(((GestorLigacoes) o).getClientesSize()));
            number_workers.setText(String.valueOf(((GestorLigacoes) o).getWorkersSize()));
        }


    }

}
