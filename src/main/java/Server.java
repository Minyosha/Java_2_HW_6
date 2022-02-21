import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private Scanner scanner;
    private boolean isWorking = true;

    public Server() {
        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            scanner = new Scanner(System.in);
            System.out.println("Сервер запущен");
            Socket socket = serverSocket.accept();
            System.out.println("Клиент подключился");
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

// Эхо клиенту
//            while (true) {
//                String message = in.readUTF();
//                if (message.equals("/end")) {
//                    break;
//                }
//                out.writeUTF("Эхо: " + message);
//            }

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (isWorking) {
                            String messageFromClient = in.readUTF();
                            System.out.println(messageFromClient);
                            if (messageFromClient.equalsIgnoreCase("/end")) {
                                isWorking = false;
                                closeConnection();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("Сервер готов отправлять сообщения.");
                    try {
                        while (isWorking) {
                            String text = scanner.nextLine();
                            if (text.equalsIgnoreCase("/end")) {
                                sendMessage(text);
                                isWorking = false;
                                closeConnection();
                            } else {
                                sendMessage(text);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();


        } catch (IOException exception) {
            exception.printStackTrace();
        }

    }

    public void sendMessage(String text) {
        try {
            out.writeUTF(text);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        try {
            out.writeUTF("/end");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
                out.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static void main(String[] args) {
        new Server();
    }

}
