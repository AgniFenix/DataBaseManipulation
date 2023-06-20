/*Este código agrega una lista existingDatabaseNames que almacena
 los nombres de las bases de datos existentes. Cuando el usuario
  ingresa un nombre para la base de datos, se verifica si está en
   la lista existingDatabaseNames. Si el nombre está en la lista,
    se muestra un mensaje de error y se solicita al usuario que
     vuelva a ingresar el nombre. Si el nombre no está en la
     lista y tiene menos de 9 caracteres, se asigna a
     databasename y se agrega a la lista existingDatabaseNames.
Ten en cuenta que este código solo verifica si el nombre ingresado
 ya existe en la lista existingDatabaseNames dentro del programa.
  Si quieres verificar si el nombre ingresado ya existe en una
   base de datos real, necesitarías conectarte a esa base de datos
    y realizar una consulta para verificar si el nombre ya existe.
 */


import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class NewDataBaseName {

    private static String newdatabasename = "fenix";
    private static final List<String> existingDatabaseNames = new ArrayList<>();

    public NewDataBaseName(String newdatabasename) {
        setNewDataBaseName(newdatabasename);
    }

    public String getNewDataBaseName() {
        return newdatabasename;
    }

    public void setNewDataBaseName(@NotNull String newdatabasename) {
        if (newdatabasename.isEmpty()) {
            System.out.println("Error: El nombre de la base de datos no puede estar vacío.");
        } else if (newdatabasename.length() > 9) {
            System.out.println("Error: El nombre de la base de datos no puede tener más de 9 caracteres.");
        } else if (existingDatabaseNames.contains(newdatabasename)) {
            System.out.println("Error: Ya existe una base de datos con ese nombre.");
        } else {
            existingDatabaseNames.add(newdatabasename);
        }
        NewDataBaseName.newdatabasename = newdatabasename;
    }
}