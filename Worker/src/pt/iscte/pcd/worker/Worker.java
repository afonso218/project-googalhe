package pt.iscte.pcd.worker;

import pt.iscte.pcd.core.Logger;
import pt.iscte.pcd.core.Noticia;
import pt.iscte.pcd.core.Parameters;
import pt.iscte.pcd.core.Tarefa;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by Ricardo Afonso on 30/10/17.
 */
public class Worker extends Thread {

    private JFrame frame;
    private JLabel label;

    private String serverIP;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public Worker() {
        this.serverIP = null;
        buildFrame();
    }

    private void buildFrame() {

        frame = new JFrame("Worker");
        frame.setSize(200, 100);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        label = new JLabel();
        label.setOpaque(true);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);
        frame.add(label, BorderLayout.CENTER);

    }

    private void displayConnecting() {
        label.setText("Connecting...");
        label.setForeground(Color.WHITE);
        label.setBackground(Color.GRAY);
    }

    private void displayWaiting() {
        label.setText("Waiting...");
        label.setForeground(Color.BLACK);
        label.setBackground(Color.WHITE);
    }

    private void displayWorking() {
        label.setText("Working...");
        label.setForeground(Color.WHITE);
        label.setBackground(Color.GREEN);
    }

    @Override
    public void run() {

        frame.setVisible(true);

        while (true) {

            displayConnecting();
            connectToServer();

            try {
                while (socket != null && !socket.isClosed()) {

                    try {

                        displayWaiting();

                        Tarefa tarefa = (Tarefa) in.readObject();

                        displayWorking();

                        Logger.logInfo("Recebida a tarefa " + tarefa.toString());

                        countOcorrencias(tarefa.getPesquisa(), tarefa.getNoticia());
                        tarefa.setDone();

                        Logger.logInfo("Processada a tarefa " + tarefa.toString());

                        out.writeObject(tarefa);
                        out.flush();


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
                    Logger.logError("[Worker] Ligação já tinha sido encerrada!", e);
                }
            }

        }
    }

    private void countOcorrencias(String pesquisa, Noticia noticia) {

        int count = 0;

        Scanner scannerTitulo = new Scanner(noticia.getTitulo());
        while (scannerTitulo.hasNext()) {
            if (scannerTitulo.next().toLowerCase().equals(pesquisa.toLowerCase())) {
                count++;
            }
        }

        Scanner scannerConteudo = new Scanner(noticia.getConteudo());
        while (scannerConteudo.hasNext()) {
            if (scannerConteudo.next().toLowerCase().equals(pesquisa.toLowerCase())) {
                count++;
            }
        }

        noticia.setOcorrencias(count);

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

                out.writeInt(Parameters.WORKER_TYPE);
                out.flush();

                Logger.logInfo("Ligação estabelecida!");

            } catch (IOException e) {
                Logger.logError("Erro a estabelecer ligação... a tentar dentro de 5s", null);
                try {
                    sleep(5000);
                } catch (InterruptedException e1) {
                    Logger.logError("[Worker] Thread interrompida.", e);
                }
            }

        }

    }

}
