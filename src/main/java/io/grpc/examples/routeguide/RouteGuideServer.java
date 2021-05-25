package io.grpc.examples.routeguide;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.util.List;

/**
 * 描述：
 * Created by zjw on 2021/5/25 16:43
 */
public class RouteGuideServer {
    private final int port;//服务端端口
    private final Server server;// 服务器

    public RouteGuideServer(int port) throws IOException {
        this.port = port;
        List<Feature> featureList = RouteGuideUtil.parseFeatures(RouteGuideUtil.getDefaultFeaturesFile());

        server = ServerBuilder.forPort(port)
                .addService(new RouteGuideService(featureList))
                .build();
    }

    public void start() throws IOException {
        server.start();

        System.out.println("Server started, listening on " + port);
        //程序退出时关闭资源

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.err.println("*** shutting down gRPC server since JVM is shutting down");
            try {
                RouteGuideServer.this.stop();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.err.println("*** server shut down");
        }));

    }


    public void stop() throws IOException {
        if (server != null) {
            server.shutdown();
        }
    }

    /**
     * 使得server一直处于运行状态
     */
    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        RouteGuideServer server = new RouteGuideServer(8980);
        server.start();
        server.blockUntilShutdown();
    }
}
