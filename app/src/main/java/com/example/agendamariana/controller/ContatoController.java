package com.example.agendamariana.controller;

import android.content.Context;

import com.example.agendamariana.model.Contato;
import com.example.agendamariana.model.ContatoDAO;

import java.util.List;

public class ContatoController {
    private final ContatoDAO contatoDAO;

    public ContatoController(Context context){
        contatoDAO = new ContatoDAO(context);
        contatoDAO.open();
    }

    //acoes do CRUD - Lembrar que SQL está no model
    //create
    public long adicionarContato(String nome, String email, String telefone, String foto){
        Contato contato = new Contato(nome, email ,telefone, foto);
        return contatoDAO.adicionarContato(contato);
    }
    //read
    public List<Contato> listarContatos(){
        return contatoDAO.listarContatos();
    }
    //update
    public int atualizarContato(int id, String nome, String email, String telefone, String foto){
        Contato contato = new Contato(id, nome, email, telefone, foto);
        return contatoDAO.atualizarContato(contato);
    }
    //delete
    public void apagarContato(int id){
        contatoDAO.apagarContato(id);
    }

    public void close(){
        contatoDAO.close();
    }
}
