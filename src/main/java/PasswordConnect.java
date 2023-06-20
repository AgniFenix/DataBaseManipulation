import org.jetbrains.annotations.NotNull;

/**
 * Este código usa el método readPassword de la clase Console para leer una contraseña del usuario sin mostrarla en la consola.
 * Si el usuario no ingresa una contraseña, se genera una contraseña aleatoria de 9 caracteres utilizando el método generateRandomPassword
 * y se muestra al usuario utilizando el método printf de la clase Console.
 * Ten en cuenta que la clase Console solo está disponible si el programa se ejecuta desde una consola o terminal.
 * Si el programa se ejecuta desde un entorno que no proporciona una consola, como un IDE, el método System.console() devolverá null
 * y no podrás usar la clase Console.
 */
public class PasswordConnect {

    private static String password;
    private static final String DEFAULT_PASSWORD = "tupapichulo";

    public PasswordConnect() {
        this(DEFAULT_PASSWORD);
    }

    public PasswordConnect(String password) {
        setPasswordConnect(password);
    }

    /**
     * Prompts the user to enter a password.
     *
     * @return the password entered by the user
     */
    private String getPassword() {
        return password;
    }

    public String getPasswordConnect() {
        return password;
    }

    public void setPasswordConnect(@NotNull String password) {
        if (password.length() > 0 && password.length() <= 15) {
            System.out.println("LA contraseña se a introducido exitosamente");
            PasswordConnect.password = password;

        } else {
            System.out.println("Error: Contraseña mayor a 15 caracteres");
        }
    }
}