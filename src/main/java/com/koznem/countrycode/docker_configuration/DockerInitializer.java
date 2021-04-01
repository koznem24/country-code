package com.koznem.countrycode.docker_configuration;


import com.koznem.countrycode.dao.Dao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;

@Component
public class DockerInitializer {
    @Lazy
    @Autowired
    private Dao dao;
    @Autowired
    private DockerProxy dockerProxy;
    private static boolean ready = false;

    public static boolean isReady() {return ready;}

    private static void setReady(boolean isReady) {ready=isReady;}

    @PostConstruct
    private void init() throws IOException, InterruptedException, DockerProxyException {
        DockerProxy.handleDockerPreConstructExceptions();

        DockerProxy.runDockerImage();

        DockerProxy.handleDockerPostConstructExceptions();
    }

    @PreDestroy
    private void destroy() throws DockerProxyException {
        DockerProxy.handleDockerPreDestroyExceptions();
    }

    @EventListener
    private void onApplicationEvent(ContextRefreshedEvent event) {
        setReady(true);
    }
}