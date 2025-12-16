package com.example.agendamariana.model;

import java.io.Serializable;

public class Contato implements Serializable {

    //propriedade - correspondem aos atribudos da tabela no DB

    private int id;

    private String nome;

    private String telefone;

    private String email;

    private String foto;

    //construtor

    public Contato() {

    }

    //construtor (outra assinatura)

    public Contato(int id, String nome, String email, String telefone, String foto) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.foto = foto;
    }


    public Contato(String nome, String email, String telefone, String foto) {
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.foto = foto;
    }

    //métodos getter e setter - para cada propriedade

    public int getId() {

        return id;

    }

    public void setId(int id) {

        this.id = id;

    }

    public String getNome() {

        return nome;

    }

    public void setNome(String nome) {

        this.nome = nome;

    }

    public String getTelefone() {

        return telefone;

    }

    public void setTelefone(String telefone) {

        this.telefone = telefone;

    }

    public String getEmail() {

        return email;

    }

    public void setEmail(String email) {

        this.email = email;

    }

    public String getFoto(){

        return foto;

    }

    public void setFoto(String foto){

        this.foto = foto;

    }

}