package com.account.demo.model;



    public class AccountBuilder {

        private Long id;
        private String name;
        private String email;
        private String password;

        public AccountBuilder setId(Long id) {
            this.id = id;
            return this;
        }

        public AccountBuilder setName(String name) {
            this.name = name;
            return this;
        }

        public AccountBuilder setEmail(String email) {
            this.email = email;
            return this;
        }

        public AccountBuilder setPassword(String password) {
            this.password = password;
            return this;
        }

        public Account build() {
            return new Account(id, name, email, password);
        }
    }

