package pt.iscte.pcd.core;

/**
 * Created by Ricardo Afonso on 30/10/17.
 */
public class Logger {

    public static void logInfo(String message) {
        System.out.println("[INFO] " + message);
    }

    public static void logWarning(String message) {
        System.out.println("[WARN] " + message);
    }

    public static void logError(String message, Exception e) {
        System.out.println("[ERROR] " + message);
        if (e != null) {
            e.printStackTrace();
        }
    }

}
