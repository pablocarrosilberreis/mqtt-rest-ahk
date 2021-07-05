package ar.edu.ahk;

import io.javalin.Javalin;

public class Server {
    public static void main(String[] args) {
        Javalin app = Javalin.create().start(getHerokuAssignedPort());
        app.get("/", ctx -> ctx.result("Prueba6"));
    }

    private static int getHerokuAssignedPort() {
        String herokuPort = System.getenv("PORT");
        if (herokuPort != null) {
            return Integer.parseInt(herokuPort);
        }
        return 7000;
    }

}
