package com.example.agendamariana;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.agendamariana.controller.ContatoController;
import com.example.agendamariana.model.Contato;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AdicionarContatoActivity extends AppCompatActivity {
    private EditText editNome, editEmail, editTelefone;
    private Button btnSalvar, btnSelecionarFoto;
    private ImageView imageViewFoto;
    private ContatoController contatoController;

    private Uri caminhoFoto;
    private static final int REQUEST_CODE_CAMERA = 100;
    private static final int REQUEST_CODE_GALLERY = 101;
    private static final int PERMISSION_CAMERA = 102;
    private static final int PERMISSION_GALLERY = 103;

    private Contato contatoEditando;
    private boolean editando = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_adicionar_contato);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        contatoController = new ContatoController(this);
        inicializarViews();

        // Verificar se é edição ou adição
        if (getIntent().hasExtra("contato")) {
            contatoEditando = (Contato) getIntent().getSerializableExtra("contato");
            editando = true;
            preencherDados();
        }
    }

    public void inicializarViews(){
        editNome = findViewById(R.id.editTextNome);
        editEmail = findViewById(R.id.editTextEmail);
        editTelefone = findViewById(R.id.editTextTelefone);
        btnSalvar = findViewById(R.id.btnSalvar);
        btnSelecionarFoto = findViewById(R.id.btnSelecionarFoto);
        imageViewFoto = findViewById(R.id.imageView);

        btnSalvar.setOnClickListener(v -> salvarContato());
        btnSelecionarFoto.setOnClickListener(v -> abrirDialogoFoto());
    }

    private void preencherDados() {
        editNome.setText(contatoEditando.getNome());
        editEmail.setText(contatoEditando.getEmail());
        editTelefone.setText(contatoEditando.getTelefone());

        // Carregar foto se existir
        if (contatoEditando.getFoto() != null && !contatoEditando.getFoto().isEmpty()) {
            try {
                Uri photoUri = Uri.parse(contatoEditando.getFoto());
                imageViewFoto.setImageURI(photoUri);
                caminhoFoto = photoUri;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void abrirDialogoFoto() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecionar Foto");
        builder.setMessage("Escolha a fonte da foto:");
        builder.setPositiveButton("Câmera", (dialog, which) -> abrirCamera());
        builder.setNegativeButton("Galeria", (dialog, which) -> abrirGaleria());
        builder.setNeutralButton("Cancelar", null);
        builder.show();
    }

    private void abrirCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, PERMISSION_CAMERA);
        } else {
            iniciarCamera();
        }
    }

    private void abrirGaleria() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES}, PERMISSION_GALLERY);
            } else {
                iniciarGaleria();
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_GALLERY);
            } else {
                iniciarGaleria();
            }
        }
    }

    private void iniciarCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File fotoFile = null;
        try {
            fotoFile = criarArquivoFoto();
        } catch (IOException e) {
            Toast.makeText(this, "Erro ao criar arquivo de foto", Toast.LENGTH_SHORT).show();
        }

        if (fotoFile != null) {
            caminhoFoto = FileProvider.getUriForFile(this,
                    "com.example.agendamariana.fileprovider", fotoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, caminhoFoto);
            startActivityForResult(intent, REQUEST_CODE_CAMERA);
        }
    }

    private void iniciarGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_GALLERY);
    }

    private File criarArquivoFoto() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_CAMERA) {
                // Foto da câmera já foi salva em caminhoFoto
                imageViewFoto.setImageURI(caminhoFoto);
            } else if (requestCode == REQUEST_CODE_GALLERY && data != null) {
                // Foto da galeria
                caminhoFoto = data.getData();
                imageViewFoto.setImageURI(caminhoFoto);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == PERMISSION_CAMERA) {
                iniciarCamera();
            } else if (requestCode == PERMISSION_GALLERY) {
                iniciarGaleria();
            }
        } else {
            Toast.makeText(this, "Permissão negada", Toast.LENGTH_SHORT).show();
        }
    }

    public void salvarContato(){
        // Recuperar os valores dos edits
        String nome = editNome.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String telefone = editTelefone.getText().toString().trim();
        String foto = caminhoFoto != null ? caminhoFoto.toString() : "";

        // Validação
        if (nome.isEmpty() || telefone.isEmpty()){
            Toast.makeText(this, "Nome e telefone são obrigatórios", Toast.LENGTH_SHORT).show();
            return;
        }

        if (editando) {
            // Atualizar contato
            contatoEditando.setNome(nome);
            contatoEditando.setEmail(email);
            contatoEditando.setTelefone(telefone);
            contatoEditando.setFoto(foto);
            contatoController.atualizarContato(contatoEditando.getId(), nome, email, telefone, foto);
            Toast.makeText(this, "Contato atualizado com sucesso", Toast.LENGTH_SHORT).show();
        } else {
            // Adicionar novo contato
            contatoController.adicionarContato(nome, email, telefone, foto);
            Toast.makeText(this, "Contato adicionado com sucesso", Toast.LENGTH_SHORT).show();
        }

        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        contatoController.close();
    }
}
