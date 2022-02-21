import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private final static String SERVER_ADDRESS = "localhost";
    private final static int SERVER_PORT = 8080;

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private Scanner scanner;
    private boolean isWorking = true;

    public Client() {
        scanner = new Scanner(System.in);
        try {
            openConnection();
            sendMessage("Подключение к серверу выполнено.");

// Выключить сервер
//            sendMessage("/end");
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void openConnection() throws IOException {
        socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (isWorking) {
                        String messageFromServer = in.readUTF();
                        System.out.println(messageFromServer);
                        if (messageFromServer.equalsIgnoreCase("/end")) {
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
                System.out.println("Клиент готов отправлять сообщения.");
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

    public void sendMessage(String text) {
        try {
            out.writeUTF(text);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Client();
    }

}
