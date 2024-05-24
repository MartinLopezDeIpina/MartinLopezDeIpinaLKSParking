package com.lksnext.parking.domain;

import java.util.List;

public class Usuario {

        private String nombre;
        private String apellido;
        private String email;
        private String password;
        private String telefono;
        private List<Integer> reservasActivas;
        private List<Integer> reservasPasadas;
        public Usuario() {
        }

        public Usuario(String nombre, String apellido, String email, String password, String telefono, List<Integer> reservasActivas, List<Integer> reservasPasadas) {
            this.nombre = nombre;
            this.apellido = apellido;
            this.email = email;
            this.password = password;
            this.telefono = telefono;
            this.reservasActivas = reservasActivas;
            this.reservasPasadas = reservasPasadas;
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
