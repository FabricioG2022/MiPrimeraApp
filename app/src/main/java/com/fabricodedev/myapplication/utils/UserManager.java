package com.fabricodedev.myapplication.utils;
import com.fabricodedev.myapplication.models.User;
import java.util.HashMap;
import java.util.Map;

public class UserManager {
    private static UserManager instance;

    // Almacena objetos User: <Nombre de Usuario, Objeto User>
    private final Map<String, User> users;

    private UserManager() {
        users = new HashMap<>();
        // HARDCODEAR el usuario inicial (usando el campo de usuario como clave)
        // Datos: Email, Username, Password
        User hardcodedUser = new User("admin@example.com", "admin", "password123");
        users.put(hardcodedUser.getUsername(), hardcodedUser);
    }

    public static synchronized UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    /**
     * Lógica de REGISTRO: Guarda el usuario si el nombre de usuario no existe.
     */
    public boolean registerUser(String email, String username, String password) {
        // Usamos el username como clave única
        if (users.containsKey(username)) {
            return false; // El usuario ya existe
        }

        // Crear y guardar el nuevo usuario
        User newUser = new User(email, username, password);
        users.put(username, newUser);
        return true;
    }

    /**
     * Lógica de LOGIN: Verifica si el nombre de usuario y la contraseña coinciden.
     */
    public boolean login(String username, String password) {
        User storedUser = users.get(username);

        // 1. Verificar si el usuario existe
        if (storedUser == null) {
            return false;
        }

        // 2. Verificar si la contraseña coincide
        return storedUser.getPassword().equals(password);
    }
}
