package com.koznem.countrycode.docker_configuration;


import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

@Component
public class DockerProxy {
    private static String dockerContainer;
    private static boolean testing = false;

    private final static String CHECK_DOCKER_VERSION_CMD = "docker --version";
    private final static String CHECK_DOCKER_VERSION_EXCEPTION = "Docker is not installed";
    private final static String CHECK_DOCKER_PORT_AVAILABILITY_CMD = "docker ps --filter expose=5432 --format {{.Ports}}";
    private final static String CHECK_DOCKER_PORT_AVAILABILITY_EXCEPTION = "Port 5432 is already in use by another Docker container";
    private final static String CHECK_PORT_AVAILABILITY_CMD = "lsof -i:5432";
    private final static String CHECK_PORT_AVAILABILITY_EXCEPTION = "Port 5432 is already in use";
    private final static String DOCKER_ERROR = "Unresolved Docker error";
    private final static String RUN_DOCKER_IMAGE_CMD = "docker run -d -p 5432:5432 ghusta/postgres-world-db:2.4";
    private final static String RUN_DOCKER_IMAGE_EXCEPTION = "Exception during running database Docker image";
    private final static String IS_CONTAINER_RUNNING_CMD = "docker inspect -f \"{{.State.Running}}\" ";
    private final static String IS_CONTAINER_RUNNING_EXCEPTION = "Failed to run the Docker container";
    private final static String STOP_DOCKER_CONTAINER_CMD = "docker stop ";
    private final static String STOP_DOCKER_CONTAINER_EXCEPTION = "Exception during stopping database Docker container";
    private final static String REMOVE_DOCKER_CONTAINER_CMD = "docker rm ";
    private final static String REMOVE_DOCKER_CONTAINER_EXCEPTION = "Exception during removing database Docker container";

    public static void setTesting(boolean isTesting) {testing=isTesting;}

    static void runDockerImage() throws DockerProxyException {
        try {
            if (!testing) dockerContainer = executeBashCommand(RUN_DOCKER_IMAGE_CMD);
        } catch (Exception e) {
            throw new DockerProxyException(RUN_DOCKER_IMAGE_EXCEPTION);
        }
    }

    static void handleDockerPreConstructExceptions()
            throws IOException, InterruptedException, DockerProxyException {
        if (executeBashCommand(CHECK_DOCKER_VERSION_CMD)==null)
            throw new DockerProxyException(CHECK_DOCKER_VERSION_EXCEPTION);
        if (!testing && executeBashCommand(CHECK_DOCKER_PORT_AVAILABILITY_CMD)!=null)
            throw new DockerProxyException(CHECK_DOCKER_PORT_AVAILABILITY_EXCEPTION);
        if (!testing && executeBashCommand(CHECK_PORT_AVAILABILITY_CMD)!=null)
            throw new DockerProxyException(CHECK_PORT_AVAILABILITY_EXCEPTION);
    }

    static void handleDockerPostConstructExceptions()
            throws IOException, InterruptedException, DockerProxyException {
        if (!testing && executeBashCommand(getIsContainerRunningCmd(dockerContainer))==null ||
                executeBashCommand(getIsContainerRunningCmd(dockerContainer)).equals("false"))
            throw new DockerProxyException(IS_CONTAINER_RUNNING_EXCEPTION);

        if (!testing && dockerContainer==null)
            throw new DockerProxyException(DOCKER_ERROR);
    }

    static void handleDockerPreDestroyExceptions()
            throws DockerProxyException {
        if (dockerContainer!=null) {
            try {
                executeBashCommand(getStopDockerContainerCmd(dockerContainer));
            } catch (Exception e) {
                throw new DockerProxyException(STOP_DOCKER_CONTAINER_EXCEPTION);
            }

            try {
                executeBashCommand(getRemoveDockerContainerCmd(dockerContainer));
            } catch (Exception e) {
                throw new DockerProxyException(REMOVE_DOCKER_CONTAINER_EXCEPTION);
            }
        }
    }

    private static String getIsContainerRunningCmd(String dockerContainer) {
        return String.format(IS_CONTAINER_RUNNING_CMD+"%s", dockerContainer);
    }

    private static String getStopDockerContainerCmd(String dockerContainer) {
        return String.format(STOP_DOCKER_CONTAINER_CMD+"%s", dockerContainer);
    }

    private static String getRemoveDockerContainerCmd(String dockerContainer) {
        return String.format(REMOVE_DOCKER_CONTAINER_CMD+"%s", dockerContainer);
    }

    private static String executeBashCommand(String command)
            throws IOException, InterruptedException {
        Process process;
        String[] cmdOutput = new String[1];

        process = Runtime.getRuntime().exec(command);

        StreamGobbler streamGobbler = new StreamGobbler(process.getInputStream(), (output) -> {
            cmdOutput[0] = output;
        });
        Executors.newSingleThreadExecutor().submit(streamGobbler);
        process.waitFor();

        if (command.equals(DockerProxy.RUN_DOCKER_IMAGE_CMD) && cmdOutput[0]==null)
            return DockerProxy.DOCKER_ERROR;

        return cmdOutput[0];
    }

    private static class StreamGobbler implements Runnable {
        private final InputStream inputStream;
        private final Consumer<String> consumer;

        public StreamGobbler(InputStream inputStream, Consumer<String> consumer) {
            this.inputStream = inputStream;
            this.consumer = consumer;
        }

        @Override
        public void run() {
            new BufferedReader(new InputStreamReader(inputStream)).lines().forEach(consumer);
        }
    }
}