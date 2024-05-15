package com.lksnext.parkingplantilla.domain;

public class Usuario {

        private String nombre;
        private String apellido;
        private String email;
        private String password;
        private String telefono;
        private String direccion;
        public Usuario() {
        }

        public Usuario(String nombre, String apellido, String email, String password, String telefono) {
            this.nombre = nombre;
            this.apellido = apellido;
            this.email = email;
            this.password = password;
            this.telefono = telefono;
        }

        public String getNombre() {
            return nombre;
        }

        public String getApellido() {
            return apellido;
        }

        public String getEmail() {
            return email;
        }

        public String getPassword() {
            return password;
        }

        public String getTelefono() {
            return telefono;
        }
}
