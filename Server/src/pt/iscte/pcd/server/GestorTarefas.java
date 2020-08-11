package pt.iscte.pcd.server;

import pt.iscte.pcd.core.Noticia;
import pt.iscte.pcd.core.Tarefa;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Ricardo Afonso on 30/10/17.
 */
public class GestorTarefas extends Observable {

    private Lock lock = new ReentrantLock();

    private final List<Noticia> noticias;
    private Deque<Tarefa> tarefas;

    private int tarefaId = 0;

    public GestorTarefas(List<Noticia> noticias) {
        this.noticias = noticias;
        this.tarefas = new LinkedList<>();
    }

    /**
     * Métodos usados pelos Clientes
     **/

    public synchronized void addSearch(Integer clientId, String pesquisa) {

        lock.lock();
        for (Noticia noticia : noticias) {
            tarefas.add(new Tarefa(clientId, tarefaId, pesquisa, new Noticia(noticia.getTitulo(), noticia.getConteudo())));
            tarefaId++;
        }
        lock.unlock();

        setChanged();
        notifyObservers();
        notifyAll();

    }

    public synchronized List<Noticia> getSearch(int id_cliente) throws InterruptedException {

        while (!tarefasRealizadas(id_cliente)) {
            wait();
        }

        lock.lock();
        List<Noticia> noticias = new ArrayList<>();
        Iterator<Tarefa> tarefaIterator = tarefas.iterator();
        while (tarefaIterator.hasNext()) {

            Tarefa t = tarefaIterator.next();
            if (t.getClient_id() == id_cliente) {
                noticias.add(t.getNoticia());
                tarefaIterator.remove();
            }

        }
        lock.unlock();

        setChanged();
        notifyObservers();

        Collections.sort(noticias);
        return noticias;

    }

    /**
     * Métodos usados pelos Workers
     **/

    public synchronized Tarefa getNextTarefa() throws InterruptedException {

        while (getNumTarefasPorEnviar() == 0) {
            wait();
        }

        lock.lock();
        Tarefa nextTarefa = null;
        for (Tarefa tarefa : tarefas) {
            if (!tarefa.isEnviada()) {
                nextTarefa = tarefa;
                nextTarefa.setEnviada();
                break;
            }
        }
        lock.unlock();

        setChanged();
        notifyObservers();

        return nextTarefa;

    }

    public synchronized void doneTarefa(Tarefa tarefa) {

        lock.lock();
        for (Tarefa t : tarefas) {
            if (t.getTarefa_id() == tarefa.getTarefa_id()) {
                t.getNoticia().setOcorrencias(tarefa.getNoticia().getOcorrencias());
                t.setDone();
                break;
            }
        }
        lock.unlock();

        setChanged();
        notifyObservers();
        notifyAll();

    }

    private boolean tarefasRealizadas(int id_cliente) {

        for (Tarefa tarefa : tarefas) {
            if (tarefa.getClient_id() == id_cliente) {
                if (!tarefa.isDone()) {
                    return false;
                }
            }
        }

        return true;

    }

    public synchronized int getNumTarefasPorEnviar() {

        int total = 0;

        for (Tarefa tarefa : tarefas) {
            if (!tarefa.isEnviada()) {
                total++;
            }
        }

        return total;
    }

    public synchronized int getNumTarefasPorRealizar() {

        int total = 0;

        for (Tarefa tarefa : tarefas) {
            if (!tarefa.isDone()) {
                total++;
            }
        }

        return total;
    }

    public synchronized int getNumTarefasRealizadas() {

        int total = 0;
        for (Tarefa tarefa : tarefas) {
            if (tarefa.isDone()) {
                total++;
            }
        }

        return total;
    }

    public synchronized void removeTarefas(int id_cliente) {

        lock.lock();

        Iterator<Tarefa> tarefaIterator = tarefas.iterator();

        while (tarefaIterator.hasNext()) {

            Tarefa t = tarefaIterator.next();
            if (t.getClient_id() == id_cliente) {
                tarefaIterator.remove();
            }

        }

        lock.unlock();

        setChanged();
        notifyObservers();

    }

    public synchronized void reenviar(Tarefa tarefa) {

        lock.lock();
        for (Tarefa t : tarefas) {
            if (t.getTarefa_id() == tarefa.getTarefa_id()) {
                t.setReenviar();
                break;
            }
        }

        notifyAll();

    }
}
