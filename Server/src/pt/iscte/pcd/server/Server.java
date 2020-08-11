package pt.iscte.pcd.server;

import pt.iscte.pcd.core.Parameters;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Ricardo Afonso on 30/10/17.
 */
public class Server {

    private JFrame frame;
    private JTextField number_clients;
    private JTextField number_workers;
    private JTextField number_tasks_todo;
    private JTextField number_tasks_done;

    private ServerManager serverListener;

    public Server() {

        initGui();
        LeitorNoticias leitor = new LeitorNoticias();
        serverListener = new ServerManager(leitor.getNoticias(), number_clients, number_workers, number_tasks_todo, number_tasks_done);

    }

    private void initGui() {
        frame = new JFrame("ISCTE-IUL Search Server (" + Parameters.SERVER_PORT + ")");
        frame.setSize(300, 200);
        frame.setLocation(
                (int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2 - frame.getWidth() / 2),
                (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2 - frame.getHeight() / 2)
        );
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setLayout(new GridLayout(4, 2));


        number_clients = new JTextField("0");
        number_clients.setEditable(false);
        frame.add(new JLabel("Número de Clientes:"));
        frame.add(number_clients);


        number_workers = new JTextField("0");
        number_workers.setEditable(false);
        frame.add(new JLabel("Número de Workers:"));
        frame.add(number_workers);

        number_tasks_todo = new JTextField("0");
        number_tasks_todo.setEditable(false);
        frame.add(new JLabel("Tarefas Por Realizar:"));
        frame.add(number_tasks_todo);

        number_tasks_done = new JTextField("0");
        number_tasks_done.setEditable(false);
        frame.add(new JLabel("Tarefas Realizadas:"));
        frame.add(number_tasks_done);
    }

    public void start() {
        frame.setVisible(true);
        serverListener.start();
    }

}
