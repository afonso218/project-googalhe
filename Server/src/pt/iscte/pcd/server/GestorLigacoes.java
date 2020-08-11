package pt.iscte.pcd.server;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

/**
 * Created by Ricardo Afonso on 30/10/17.
 */
public class GestorLigacoes extends Observable{

    private Map<Integer, ClientHandler> clientsHandler;
    private Map<Integer, WorkerHandler> workersHandler;

    public GestorLigacoes(){
        clientsHandler = new HashMap<>();
        workersHandler = new HashMap<>();
    }

    public synchronized void add(ClientHandler clientHandler){

        clientsHandler.put(clientHandler.getClientId(), clientHandler);

        setChanged();
        notifyObservers();

    }

    public synchronized void add(WorkerHandler workerHandler){

        workersHandler.put(workerHandler.getWorkerId(), workerHandler);

        setChanged();
        notifyObservers();

    }

    public synchronized void remove(ClientHandler clientHandler){

        clientsHandler.remove(clientHandler.getClientId());

        setChanged();
        notifyObservers();

    }

    public synchronized void remove(WorkerHandler workerHandler){

        workersHandler.remove(workerHandler.getWorkerId());

        setChanged();
        notifyObservers();

    }

    public int getClientesSize() {
        return clientsHandler.size();
    }

    public int getWorkersSize(){
        return workersHandler.size();
    }

}
