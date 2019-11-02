package usuario.app.projetoagenda;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQuery;
import android.util.Log;

public class ContextoDados extends SQLiteOpenHelper {

    //nome do arquivo de base de dados no sistema de arquivos
    private static final String nome_bd = "Agenda";
    //versão da base de dados
    private static final int versao_bd = 2;
    private static final String log_tag = "Agenda";
    //Mantém rastreamento do contexto
    private final Context contexto;

    public ContextoDados(Context context){
        super(context, nome_bd, null, versao_bd);
        this.contexto = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        String[] sql = contexto.getString(R.string.ContextoDados_onCreate).split("\n");
        db.beginTransaction();

        try{
            //Cria a tabela e testa os dados
            ExecutarComandosSQL(db, sql);
            db.setTransactionSuccessful();
        }catch(SQLException e){
            Log.e("Erro ao criar as tabelas e testar os dados", e.toString());
        }
        finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        Log.w(log_tag, "Atualizando a base de dados da versão "+ oldVersion + " para "+ newVersion + ", que destruirá todos os dados antigos");
        String[] sql = contexto.getString(R.string.ContextoDados_onUpgrade).split("\n");
        db.beginTransaction();

        try{
            ExecutarComandosSQL(db, sql);
            db.setTransactionSuccessful();
        }
        catch(SQLException e){
            Log.e("Erro ao atualizar as tabelas e testar os dados", e.toString());
            throw e;
        }
        finally {
            db.endTransaction();
        }
        onCreate(db);
    }

    private void ExecutarComandosSQL(SQLiteDatabase db, String[] sql){
        for(String s : sql)
            if(s.trim().length()>0)
                db.execSQL(s);
    }

    public ContatosCursor RetornarContatos(ContatosCursor.OrdenarPor ordenarPor){
        String sql = ContatosCursor.CONSULTA + (ordenarPor == ContatosCursor.OrdenarPor.NomeCrescente ? "ASC" : "DESC");
        SQLiteDatabase bd = getReadableDatabase();
        ContatosCursor cc = (ContatosCursor) bd.rawQueryWithFactory(new ContatosCursor.Factory(), sql, null ,null);
        cc.moveToFirst();
        return cc;
    }

    public long InserirContato(String nome, String telefone, String endereco){
        SQLiteDatabase db = getReadableDatabase();

        try{
            ContentValues initiaValues = new ContentValues();
            initiaValues.put("Nome", nome);
            initiaValues.put("Telefone", telefone);
            initiaValues.put("Endereco", endereco);
            return db.insert("Contatos", null, initiaValues);
        }
        finally{
            db.close();
        }
    }

    public static class ContatosCursor extends SQLiteCursor{
        public static enum OrdenarPor{
            NomeCrescente,
            NomeDescrescente
        }

        private static final String CONSULTA = "Select * from contatos order by nome";

        private ContatosCursor(SQLiteDatabase db, SQLiteCursorDriver driver, String editTable, SQLiteQuery query){
            super(db, driver, editTable, query);
        }

        private static class Factory implements SQLiteDatabase.CursorFactory{
            @Override
            public Cursor newCursor(SQLiteDatabase db, SQLiteCursorDriver driver, String editTable, SQLiteQuery query){
                return new ContatosCursor(db, driver, editTable, query);
            }
        }

        public long getID(){
            return getLong(getColumnIndexOrThrow("ID"));
        }

        public String getNome(){
            return getString(getColumnIndexOrThrow("Nome"));
        }
        public String getEndereco(){
            return getString(getColumnIndexOrThrow("Endereco"));
        }

        public String getTelefone(){
            return getString(getColumnIndexOrThrow("Telefone"));
        }
    }
}
