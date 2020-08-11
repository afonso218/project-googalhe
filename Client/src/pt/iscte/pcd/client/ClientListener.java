package pt.iscte.pcd.client;

import pt.iscte.pcd.core.Logger;
import pt.iscte.pcd.core.Noticia;
import pt.iscte.pcd.core.Parameters;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;

/**
 * Created by Ricardo Afonso on 30/10/17.
 */
public class ClientListener extends Thread {

    private String serverIP;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    private JList list;
    private JButton buttonSearch;

    public ClientListener(String serverIP, JList list, JButton buttonSearch) {
        this.serverIP = serverIP;
        this.list = list;
        this.buttonSearch = buttonSearch;
    }

    @Override
    public void run() {

        while (true) {

            connectToServer();

            try {
                while (socket != null && !socket.isClosed()) {

                    try {

                        List<Noticia> noticias = (List<Noticia>) in.readObject();

                        Logger.logInfo("Recebido pedido de pesquisa ao Servidor!");
                        DefaultListModel<Noticia> model = new DefaultListModel<>();
                        for (Noticia n : noticias) {
                            model.addElement(n);
                        }
                        list.setModel(model);
                        list.repaint();
                        buttonSearch.setEnabled(true);

                    } catch (ClassNotFoundException e) {
                        Logger.logError("Erro ao receber informacao do servidor.", e);
                    }
                }

            } catch (IOException e) {
                Logger.logError("Ligação encerrada pelo servidor", e);
                try {
                    socket.close();
                    in.close();
                    out.close();
                } catch (IOException e2) {
                    Logger.logError("[ClienteHandler] Ligação já tinha sido fechada!", e);
                }
            }

        }
    }

    private void connectToServer() {

        while (socket == null || socket.isClosed()) {

            try {

                if (serverIP == null) {
                    Logger.logInfo("A tentar estabelecer ligação... IP:" + InetAddress.getByName(null) + ":" + Parameters.SERVER_PORT);
                    socket = new Socket(InetAddress.getByName(null), Parameters.SERVER_PORT);
                } else {
                    Logger.logInfo("A tentar estabelecer ligação... IP:" + serverIP + ":" + Parameters.SERVER_PORT);
                    socket = new Socket(serverIP, Parameters.SERVER_PORT);
                }

                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());

                out.writeInt(Parameters.CLIENT_TYPE);
                out.flush();

                Logger.logInfo("Ligação estabelecida!");
                buttonSearch.setEnabled(true);

            } catch (IOException e) {
                Logger.logError("Erro a estabelecer ligação... a tentar dentro de 5s", null);
                try {
                    sleep(5000);
                } catch (InterruptedException e1) {
                    Logger.logError("[ClientListener] Thread interrompida.", e);
                }
            }

        }

    }

    public void send(String input) {

        try {

            if (socket != null && socket.isConnected()) {
                buttonSearch.setEnabled(false);
                out.writeObject(input);
                out.flush();
                Logger.logInfo("Enviado pedido de pesquisa ao Servidor para \"" + input + "\"!");
            } else {
                JOptionPane.showMessageDialog(null, "Não foi possível comunicar com o servidor!");
            }

        } catch (IOException e) {
            Logger.logError("Falhou a enviar \"" + input + "\"", e);
        }

    }

}
