package it.unipi.enPassant.service.redisService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.*;

import java.util.HashSet;
import java.util.Set;

@Service
public class ClusterFlush {

    @Autowired
    private JedisCluster jedisCluster;

    private static final int MAX_WAIT_TIME_MS = 10000; // 10 secondi massimo di attesa
    private static final int CHECK_INTERVAL_MS = 1000; // Controllo ogni 1 secondo

    public void flushClusterDB() {
        // Definizione dei nodi del cluster
        Set<HostAndPort> clusterNodes = new HashSet<>();
        clusterNodes.add(new HostAndPort("10.1.1.62", 7001));
        clusterNodes.add(new HostAndPort("10.1.1.66", 7001));
        clusterNodes.add(new HostAndPort("10.1.1.69", 7001));

        System.out.println("Waiting for cluster synchronization...");

        // Attendi fino a quando tutte le repliche sono sincronizzate
        if (!waitForClusterSync(clusterNodes)) {
            System.err.println("Cluster sync timeout! Proceeding with caution...");
        } else {
            System.out.println("Cluster is synchronized. Proceeding with flushDB...");
        }

        // Esegui flushDB solo sui master
        for (HostAndPort node : clusterNodes) {
            try (Jedis jedis = new Jedis(node)) { // Connessione diretta a ogni nodo
                // Controlla se il nodo è master
                String role = jedis.info("replication").split("role:")[1].split("\n")[0].trim();
                if (!"master".equals(role)) {
                    System.out.println("Skipping node " + node + " as it is a replica.");
                    continue;
                }

                // Flush del DB solo sui master
                jedis.flushDB();
                System.out.println("Flushed Redis DB on master node: " + node);
            } catch (Exception e) {
                System.err.println("Error flushing node " + node + ": " + e.getMessage());
            }
        }
    }

    private boolean waitForClusterSync(Set<HostAndPort> clusterNodes) {
        long startTime = System.currentTimeMillis();

        while (System.currentTimeMillis() - startTime < MAX_WAIT_TIME_MS) {
            boolean allSynced = true;

            for (HostAndPort node : clusterNodes) {
                try (Jedis jedis = new Jedis(node)) {
                    // Controlla lo stato della replica
                    String replicationInfo = jedis.info("replication");
                    if (replicationInfo.contains("role:slave")) {
                        String syncState = replicationInfo.contains("master_sync_in_progress:1") ? "SYNCING" : "SYNCED";
                        System.out.println("Node " + node + " is a replica, state: " + syncState);

                        if (syncState.equals("SYNCING")) {
                            allSynced = false;
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error checking replication status on node " + node + ": " + e.getMessage());
                    return false; // Evitiamo di continuare in caso di errore
                }
            }

            if (allSynced) {
                return true;
            }

            // Aspetta un po' prima di controllare di nuovo
            try {
                Thread.sleep(CHECK_INTERVAL_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }

        return false; // Timeout superato
    }
}

