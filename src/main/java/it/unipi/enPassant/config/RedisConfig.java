package it.unipi.enPassant.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.*;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
public class RedisConfig {

    @Bean
    public JedisCluster jedisCluster() {

        // Cluster Nodes definition
        Set<HostAndPort> clusterNodes = new HashSet<HostAndPort>();
        clusterNodes.add(new HostAndPort("10.1.1.62", 7001));
        clusterNodes.add(new HostAndPort("10.1.1.66", 7001));
        clusterNodes.add(new HostAndPort("10.1.1.69", 7001));

        JedisClientConfig clientConfig = DefaultJedisClientConfig.builder()
                .timeoutMillis(2000) // Timeout connessione (2 secondi)
                .socketTimeoutMillis(2000) // Timeout socket (2 secondi)
                .build();

        // Connection pool configuration
        GenericObjectPoolConfig<Connection> poolConfig = new GenericObjectPoolConfig<>();
        poolConfig.setMaxTotal(10);
        poolConfig.setMaxIdle(5);
        poolConfig.setMinIdle(1);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        poolConfig.setJmxEnabled(false);

        // Cluster creation
        return new JedisCluster(clusterNodes, clientConfig, poolConfig, Duration.ofSeconds(2), 5, Duration.ofSeconds(2));
    }
}
