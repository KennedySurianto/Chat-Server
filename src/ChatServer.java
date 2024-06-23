import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private static final int PORT = 3000;
    private static HashSet<PrintWriter> writers = new HashSet<>();

    public static void main(String[] args) throws IOException {
        System.out.println("Chat Server is running...");
        try (ServerSocket listener = new ServerSocket(PORT)) {
            while (true) {
                new Handler(listener.accept()).start();
            }
        }
    }

    private static class Handler extends Thread {
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                synchronized (writers) {
                    writers.add(out);
                }

                String message;
                while ((message = in.readLine()) != null) {
                    for (PrintWriter writer : writers) {
                        writer.println(message);
                    }
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            } finally {
                if (out != null) {
                    synchronized (writers) {
                        writers.remove(out);
                    }
                }
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }
    }
}