package it.unipi.enPassant.service.redisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.HostAndPort;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RedisReplicationChecker {

    @Autowired
    private JedisCluster jedisCluster;

    private static final int MAX_RETRIES = 10;
    private static final int SLEEP_INTERVAL_MS = 2000;

    public boolean waitForReplicationSync() {
        try {
            for (int i = 0; i < MAX_RETRIES; i++) {
                boolean allSynced = true;

                Set<HostAndPort> nodes = jedisCluster.getClusterNodes().keySet().stream()
                        .map(this::parseHostAndPort)
                        .collect(Collectors.toSet());

                for (HostAndPort node : nodes) {
                    try (Jedis jedis = new Jedis(node)) {
                        String info = jedis.info("replication");
                        if (info.contains("role:slave")) {
                            String masterOffset = extractValue(info, "master_repl_offset");
                            String slaveOffset = extractValue(info, "slave_repl_offset");
                            if (slaveOffset == null || !masterOffset.equals(slaveOffset)) {
                                allSynced = false;
                                break;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        allSynced = false;
                    }
                }

                if (allSynced) {
                    System.out.println("All Redis cluster nodes are synchronized.");
                    return true;
                }

                System.out.println("Waiting for Redis cluster nodes to sync...");
                Thread.sleep(SLEEP_INTERVAL_MS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.err.println("Timeout: Redis cluster nodes did not synchronize in time.");
        return false;
    }

    private HostAndPort parseHostAndPort(String nodeInfo) {
        String[] parts = nodeInfo.split(":");
        return new HostAndPort(parts[0], Integer.parseInt(parts[1]));
    }

    private String extractValue(String info, String key) {
        for (String line : info.split("\n")) {
            if (line.startsWith(key + ":")) {
                return line.split(":")[1];
            }
        }
        return null;
    }
}
