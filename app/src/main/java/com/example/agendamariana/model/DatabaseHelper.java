package com.example.agendamariana.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "contato.db";
    private static final int DB_VERSION = 1;

    public DatabaseHelper(@Nullable Context context) {
        super(context, DB_NAME,null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //create db
        db.execSQL("CREATE TABLE contato(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nome TEXT NOT NULL," +
                "email TEXT, " +
                "telefone TEXT NOT NULL, " +
                "foto TEXT);"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    // migration - alteracao de versao do bd
        if (oldVersion<2){
            db.execSQL("ALTER TABLE contato ADD COLUMN foto TEXT");
        }
        db.execSQL("DROP TABLE IF EXISTS contato");
        onCreate(db);
    }
}
