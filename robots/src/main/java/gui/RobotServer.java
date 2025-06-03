package gui;

import log.Logger;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
public class RobotServer {
    private final  int port;
    private final GameVisualizer gameVisualizer;    private ServerSocket serverSocket;
    private boolean isRunning = false;
    private final ExecutorService executorService;


    public RobotServer(int port, GameVisualizer gameVisualizer) {
        this.port = port;
        this.gameVisualizer = gameVisualizer;
        this.executorService = Executors.newCachedThreadPool();
    }

    public void start() {
        if (isRunning) {
            return;
        }
        executorService.submit(() -> {
            try {
                serverSocket = new ServerSocket(port);
                isRunning = true;

                while (isRunning) {
                    try {
                        Socket clientSocket = serverSocket.accept();
                        handleClient(clientSocket);
                    } catch (IOException e) {
                        if (isRunning) {
                            Logger.debug("Ошибка клиентского подключения: " + e.getMessage());
                        }
                    }
                }
            } catch (IOException e) {
                Logger.debug("Ошибка при запуске сервера: " + e.getMessage());
            }
        });
    }

    private void handleClient(Socket clientSocket) {
        executorService.submit(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true)) {

                writer.println("Соединение установлено. Доступные команды: MOVE x y, STOP, STATUS");

                String line;
                while ((line = reader.readLine()) != null) {
                    final String command = line;
                    CompletableFuture<String> responseFuture = new CompletableFuture<>();

                     EventQueue.invokeLater(() -> {
                        try {
                            String response = "Неизвестная команда. Доступные команды: MOVE x y, STOP, STATUS";
                            if (command.startsWith("MOVE")) {
                                String[] parts = command.split(" ");
                                if (parts.length == 3) {
                                    try {
                                        int x = Integer.parseInt(parts[1]);
                                        int y = Integer.parseInt(parts[2]);
                                        gameVisualizer.setTargetPosition(new Point(x, y));

                                        Point robotPos = gameVisualizer.getRobotPosition();
                                        double direction = gameVisualizer.getRobotDirection();

                                        response = String.format(
                                                "OK: Робот движется к точке (%d, %d). Текущая позиция: (%d, %d), направление: %.2f",
                                                x, y, robotPos.x, robotPos.y, direction
                                        );

                                        Logger.debug("Установлена новая цель>: x=" + x + ", y=" + y);
                                    } catch (NumberFormatException e) {
                                        response = "Ожидаются целые числа.";
                                    }
                                } else {
                                    response = "ОШИБКА: Неверный формат команды MOVE. Ожидается: MOVE x y";
                                    Logger.debug("Неверный формат команды MOVE. Ожидается: MOVE x y");
                                }
                            } else if (command.equals("STOP")) {
                                gameVisualizer.stopRobot();

                                Point robotPos = gameVisualizer.getRobotPosition();
                                double direction = gameVisualizer.getRobotDirection();

                                response = String.format(
                                        "OK: Робот остановлен в позиции (%d, %d), направление: %.2f",
                                        robotPos.x, robotPos.y, direction
                                );

                            } else if (command.equals("STATUS")) {
                                Point robotPos = gameVisualizer.getRobotPosition();
                                Point targetPos = gameVisualizer.getTargetPosition();
                                double direction = gameVisualizer.getRobotDirection();
                                boolean isMoving = !robotPos.equals(targetPos);

                                response = String.format(
                                        "OK: Статус робота - Позиция: (%d, %d), Цель: (%d, %d), Направление: %.2f, Движение: %s",
                                        robotPos.x, robotPos.y, targetPos.x, targetPos.y, direction,
                                        isMoving ? "в движении" : "остановлен"
                                );

                            }
                            responseFuture.complete(response);
                        } catch (Exception e) {
                            String errorMsg = "ОШИБКА: " + e.getMessage();
                            Logger.debug("Ошибка при обработке команды: " + e.getMessage());
                            responseFuture.complete(errorMsg);
                        }
                    });

                    try {
                        String response = responseFuture.get();
                        writer.println(response);
                    } catch (Exception e) {
                        writer.println("Ошибка сервера: " + e.getMessage());
                    }
                }
            } catch (IOException e) {
                Logger.debug("Ошибка при обработке команд: " + e.getMessage());
            } finally {
                try {
                    clientSocket.close();
                    Logger.debug("Клиент отключен");
                } catch (IOException e) {
                    Logger.debug("Ошибка при закрытии соединения: " + e.getMessage());
                }
            }
        });
    }

    public void stop() {
        isRunning = false;
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
                Logger.debug("Сервер остановлен");
            } catch (IOException e) {
                Logger.debug("Ошибка при остановке сервера: " + e.getMessage());
            }
        }
        executorService.shutdown();
    }



}
