package it.unipi.enPassant.service.redisService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.*;

import java.util.Map;

@Service
public class ClusterFlush {

    @Autowired
    private JedisCluster jedisCluster;

    private static final int MAX_WAIT_TIME_MS = 10000;
    private static final int CHECK_INTERVAL_MS = 1000;

    public void flushClusterDB() {
        System.out.println("Waiting for cluster synchronization...");

        if (!waitForClusterSync()) {
            System.err.println("Cluster sync timeout! Proceeding with caution...");
        } else {
            System.out.println("Cluster is synchronized. Proceeding with flushDB...");
        }

        // Ottenere i nodi dal cluster
        for (Map.Entry<String, ConnectionPool> entry : jedisCluster.getClusterNodes().entrySet()) {
            try (Jedis jedis = new Jedis(entry.getKey().split(":")[0], Integer.parseInt(entry.getKey().split(":")[1]))) {
                String role = jedis.info("replication").split("role:")[1].split("\n")[0].trim();
                if ("master".equals(role)) {
                    jedis.flushDB();
                    System.out.println("Flushed Redis DB on master node: " + entry.getKey());
                } else {
                    System.out.println("Skipping replica node: " + entry.getKey());
                }
            } catch (Exception e) {
                System.err.println("Error flushing node: " + e.getMessage());
            }
        }
    }

    private boolean waitForClusterSync() {
        long startTime = System.currentTimeMillis();

        while (System.currentTimeMillis() - startTime < MAX_WAIT_TIME_MS) {
            boolean allSynced = true;

            for (Map.Entry<String, ConnectionPool> entry : jedisCluster.getClusterNodes().entrySet()) {
                try (Jedis jedis = new Jedis(entry.getKey().split(":")[0], Integer.parseInt(entry.getKey().split(":")[1]))) {
                    if (!"master".equals(jedis.info("replication").split("role:")[1].split("\n")[0].trim())) {
                        continue;
                    }

                    String replicationInfo = jedis.info("replication");
                    if (replicationInfo.contains("master_sync_in_progress:1")) {
                        allSynced = false;
                    }
                } catch (Exception e) {
                    System.err.println("Error checking replication status: " + e.getMessage());
                    return false;
                }
            }

            if (allSynced) {
                return true;
            }

            try {
                Thread.sleep(CHECK_INTERVAL_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return false;
    }
}
