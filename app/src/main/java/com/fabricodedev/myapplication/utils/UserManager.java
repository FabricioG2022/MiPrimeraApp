package com.fabricodedev.myapplication.utils;

import com.fabricodedev.myapplication.models.Miembro;
import com.fabricodedev.myapplication.models.User;
import com.fabricodedev.myapplication.models.Visita;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID; // Para generar IDs únicos

public class UserManager {
    private static UserManager instance;

    // Mapa para usuarios (Login): <username, User>
    private final Map<String, User> appUsers;

    // Mapa para MIEMBROS A VISITAR: <id, Miembro>
    private final Map<String, Miembro> congregacion;

    private UserManager() {
        appUsers = new HashMap<>();
        congregacion = new HashMap<>();

        // HARDCODEAR el usuario de la aplicación
        User hardcodedUser = new User("admin@example.com", "admin", "password123");
        appUsers.put(hardcodedUser.getUsername(), hardcodedUser);

        // HARDCODEAR algunos miembros de la congregación
        hardcodeMiembros();
    }

    private void hardcodeMiembros() {
        // Miembro 1
        Miembro m1 = new Miembro(UUID.randomUUID().toString(),
                "Juan Pérez",
                "Calle Falsa 123",
                "555-1234");
        m1.setUltimaVisita("10-09-2025");
        m1.setEstadoEspiritual("Amarillo");
        congregacion.put(m1.getId(), m1);

        // Miembro 2
        Miembro m2 = new Miembro(UUID.randomUUID().toString(),
                "María Gómez",
                "Avenida Siempre Viva 742",
                "555-5678");
        m2.setUltimaVisita("01-08-2025");
        m2.setEstadoEspiritual("Rojo");
        congregacion.put(m2.getId(), m2);
        // Miembro 3
        Miembro m3 = new Miembro(UUID.randomUUID().toString(),
                "Luis Segovia",
                "Sarmiento 932",
                "589-2587");
        m2.setUltimaVisita("30-10-2025");
        m2.setEstadoEspiritual("Amarillo");
        congregacion.put(m3.getId(), m3);
        // Miembro 4
        Miembro m4 = new Miembro(UUID.randomUUID().toString(),
                "Emanuel Bilbao",
                "3 de Febrero 555",
                "986-2587");
        m2.setUltimaVisita("17-09-2025");
        m2.setEstadoEspiritual("Verde");
        congregacion.put(m4.getId(), m4);
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
        if (appUsers.containsKey(username)) {
            return false; // El usuario ya existe
        }

        // Crear y guardar el nuevo usuario
        User newUser = new User(email, username, password);
        appUsers.put(username, newUser);
        return true;
    }
    // --- Lógica de LOGIN (se mantiene igual)
    public boolean login(String username, String password) {
        User storedUser = appUsers.get(username);
        return storedUser != null && storedUser.getPassword().equals(password);
    }

    // --- Lógica de MIEMBROS

    // 1. Obtener todos los miembros
    public Map<String, Miembro> getAllMiembros() {
        return congregacion;
    }

    // 2. Obtener un miembro por ID
    public Miembro getMiembroById(String id) {
        return congregacion.get(id);
    }

    // 3. Crear un nuevo miembro
    public void addMiembro(String nombre, String direccion, String telefono) {
        // Generar un ID único
        String newId = UUID.randomUUID().toString();

        // Crear el objeto Miembro usando el constructor
        Miembro nuevoMiembro = new Miembro(newId, nombre, direccion, telefono);

        // Almacenar el miembro en el mapa
        congregacion.put(newId, nuevoMiembro);
    }
    public void addMiembro(Miembro nuevoMiembro) {
        // Generar un ID único antes de guardarlo
        congregacion.put(nuevoMiembro.getId(), nuevoMiembro);
    }
    /**
     * 4. Lógica para EDITAR/ACTUALIZAR un miembro existente.
     * @param id El ID del miembro a actualizar.
     * @param nombre Nuevo nombre.
     * @param direccion Nueva dirección.
     * @param telefono Nuevo teléfono.
     * @return true si se actualizó, false si el miembro no existe.
     */
    public boolean updateMiembroData(String id, String nombre, String direccion, String telefono) {
        Miembro miembroAEditar = congregacion.get(id);

        if (miembroAEditar != null) {
            miembroAEditar.setNombre(nombre);
            miembroAEditar.setDireccion(direccion);
            miembroAEditar.setTelefono(telefono);
            // NOTA: Los campos de visita y estado se actualizarán en otra función (visita)
            return true;
        }
        return false;
    }
    /**
    * 5. Eliminar un miembro de la congregación por su ID.
    * @param id El ID del miembro a eliminar.
    * @return true si se eliminó, false si el miembro no existía.
    */
    public boolean deleteMiembro(String id) {
        if (congregacion.containsKey(id)) {
            congregacion.remove(id);
            return true;
        }
        return false;
    }
    public boolean registrarNuevaVisita(String miembroId, String fecha, String nota, String estado, String visitador) {
        Miembro miembro = congregacion.get(miembroId);

        if (miembro != null) {
            // 1. Creamos el objeto Visita
            Visita nuevaVisita = new Visita(fecha, nota, estado, visitador);

            // 2. Usamos el método de Miembro para actualizar sus campos principales
            miembro.agregarVisita(nuevaVisita);

            return true;
        }
        return false;
    }
}