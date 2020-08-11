package pt.iscte.pcd.client;

import pt.iscte.pcd.core.Noticia;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by Ricardo Afonso on 30/10/17.
 */
public class Client {

    private static final String SEARCH_PLACEHOLDER = "Introduzir texto a pesquisar...";

    private ClientListener listener;

    private JFrame frame;
    private JTextField inputText;
    private JButton buttonSearch;
    private JList<Noticia> list;
    private JTextArea content;

    public Client(String serverIP) {

        frame = new JFrame("ISCTE-IUL Search Client");
        frame.setSize(
                (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2,
                (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2)
        );
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        createTop();
        createCenter();

        listener = new ClientListener(serverIP, list, buttonSearch);

    }

    private void createTop() {

        JPanel painel = new JPanel();

        inputText = new JTextField(SEARCH_PLACEHOLDER);
        inputText.setPreferredSize(new Dimension(300, 30));
        inputText.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (inputText.getText().equals(SEARCH_PLACEHOLDER)) {
                    inputText.setText("");
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        buttonSearch = new JButton("Search");
        buttonSearch.setPreferredSize(new Dimension(75, 30));
        buttonSearch.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                if (inputText.getText().equals(SEARCH_PLACEHOLDER)) {

                    JOptionPane.showMessageDialog(frame, "Por favor introduza texto de pesquisa...");

                } else {

                    listener.send(inputText.getText());

                }

            }

        });

        painel.add(inputText);
        painel.add(buttonSearch);

        frame.add(painel, BorderLayout.NORTH);

    }

    private void createCenter() {

        // LISTA
        list = new JList<>();
        list.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {

                JList list = (JList) evt.getSource();
                int index = list.locationToIndex(evt.getPoint());
                Noticia noticia = (Noticia) list.getModel().getElementAt(index);
                content.setText(noticia.getConteudo());

            }
        });

        // NOTICIA
        content = new JTextArea();
        content.setEditable(false);

        JScrollPane listScrollPane = new JScrollPane(list);
        JScrollPane textScrollPane = new JScrollPane(content);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listScrollPane, textScrollPane);
        splitPane.setResizeWeight(0.5);
        frame.add(splitPane, BorderLayout.CENTER);

    }

    public void start() {
        frame.setVisible(true);
        listener.start();
    }

}
