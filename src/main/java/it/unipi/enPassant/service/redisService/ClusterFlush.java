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

    public void flushClusterDB() {
        // Definizione dei nodi del cluster
        Set<HostAndPort> clusterNodes = new HashSet<>();
        clusterNodes.add(new HostAndPort("10.1.1.62", 7001));
        clusterNodes.add(new HostAndPort("10.1.1.66", 7001));
        clusterNodes.add(new HostAndPort("10.1.1.69", 7001));

        // Esegui flushDB su ogni nodo manualmente
        for (HostAndPort node : clusterNodes) {
            try (Jedis jedis = new Jedis(node)) { // Connessione diretta a ogni nodo
                jedis.flushDB();
                System.out.println("Flushed Redis DB on node: " + node);
            } catch (Exception e) {
                System.err.println("Error flushing node " + node + ": " + e.getMessage());
            }
        }
    }
}
