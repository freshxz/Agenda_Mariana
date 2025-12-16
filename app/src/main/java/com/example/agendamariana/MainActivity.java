package com.example.agendamariana;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.agendamariana.R;
import com.example.agendamariana.controller.ContatoController;
import com.example.agendamariana.model.Contato;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    //propriedades referentes aos componentes View no layout
    private Toolbar toolbar;
    private ListView listView;
    private FloatingActionButton fabAdicionar;
    private List<Contato> contatos;
    private ArrayAdapter<Contato> adapter;
    private ContatoController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        inicializarViews();
        carregarContatos();
        configurarListeners();
    }

    private void inicializarViews(){
        //inicalizar os componentes
        toolbar = findViewById(R.id.toolbar);
        listView = findViewById(R.id.listView);
        fabAdicionar = findViewById(R.id.fabAdicionar);

        fabAdicionar.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AdicionarContatoActivity.class);
            startActivity(intent);
        });
    }

    private void carregarContatos(){
        controller = new ContatoController(this);
        contatos = controller.listarContatos();
        //precisamos levar os dados para a Activity - Adapter
        adapter = new ArrayAdapter<>(this, R.layout.list_item, contatos){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if (convertView == null){
                    convertView = getLayoutInflater().inflate(R.layout.list_item, parent, false);
                }
                Contato contato = getItem(position);
                //escrever nos TextViews do layout
                TextView textNome = convertView.findViewById(R.id.textNomeLista);
                TextView textTelefone = convertView.findViewById(R.id.textTelLista);

                assert contato != null;
                textNome.setText(contato.getNome());
                textTelefone.setText(contato.getTelefone());
                return convertView;

            }
        };
        listView.setAdapter(adapter);
    }

    private void configurarListeners(){
        //clique curto em item do listView - alterar contato
        listView.setOnItemClickListener((parent, view, position, id) -> {
            //position dá a posição do item selecionado
            Contato contato = contatos.get(position);
            //abrir a activity
            Intent intent = new Intent(MainActivity.this, AdicionarContatoActivity.class);
            intent.putExtra("contato", contato);
            startActivity(intent);
        });

        //clique longo em item do liostView - Excluir contato
        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            Contato contato = contatos.get(position);
            //alerta de confirmação de exclusão
            new AlertDialog.Builder(this).
                    setTitle("Confirmação de Exclusão").
                    setMessage("Deseja realmente excluir o contato " + contato.getNome() + "?").
                    setPositiveButton("Sim", (dialog, wich) -> {
                        controller.apagarContato(contato.getId());
                        carregarContatos(); //atualizar a listView
                        Toast.makeText(this, "Contato excluído", Toast.LENGTH_SHORT).show();
                    }).
                    setNegativeButton("Não", null).
                    show();
            return true;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarContatos(); //ao voltar para esta activity, recarrega
    }
}