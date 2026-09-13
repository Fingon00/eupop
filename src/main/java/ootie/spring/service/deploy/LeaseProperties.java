package ootie.spring.service.deploy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Component
@Getter
public class LeaseProperties {

    @Value("${ootie.deploy.lease-key:discord-bot}")
    private String leaseKey;

    @Value("${ootie.deploy.lease-duration-seconds:3600}")
    private long leaseDurationSeconds;

    @Value("${ootie.deploy.heartbeat-interval-seconds:5}")
    private long heartbeatIntervalSeconds;

    public long getHeartbeatIntervalMillis() {
        return heartbeatIntervalSeconds * 1000;
    }
}
