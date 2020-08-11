package pt.iscte.pcd.client;

/**
 * Created by Ricardo Afonso on 30/10/17.
 */
public class StartClient {

    public static void main(String[] args) {

        if (args.length > 0) {
            new Client(args[0]).start();
        } else {
            new Client(null).start();
        }

    }

}
