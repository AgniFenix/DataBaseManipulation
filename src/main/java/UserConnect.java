import org.jetbrains.annotations.NotNull;

public class UserConnect {

    private static String user;
    private static final String USER_DEFAULT = "root";

    public UserConnect() {
        this(USER_DEFAULT);
    }

    public UserConnect(String user) {
        setUserConnect(user);
    }

    public String getUserConnect() {
        return user;
    }

    public void setUserConnect(@NotNull String user) {
        if (user.length() > 15) {
            throw new IllegalArgumentException("El nombre de usuario debe tener un máximo de 15 caracteres.");
        } else {
            System.out.println("EL usuario es: " + user);
            UserConnect.user = user;
        }
    }
}