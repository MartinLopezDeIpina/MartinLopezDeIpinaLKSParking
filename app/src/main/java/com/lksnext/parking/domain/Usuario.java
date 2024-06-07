package com.lksnext.parking.domain;

import java.util.List;

public class Usuario {

        private String ID;
        private String nombre;
        private String email;
        private String telefono;
        private List<Integer> reservasActivas;
        private List<Integer> reservasPasadas;
        public Usuario() {
        }

        public Usuario(String ID, String nombre, String email, String telefono) {
            this.ID = ID;
            this.nombre = nombre;
            this.email = email;
            this.telefono = telefono;
        }

        public String getNombre() {
            return nombre;
        }

        public String getEmail() {
            return email;
        }

        public String getTelefono() {
            return telefono;
        }
        public String getID() {
            return ID;
        }
}
