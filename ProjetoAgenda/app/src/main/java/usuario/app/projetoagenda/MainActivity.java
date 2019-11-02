    package usuario.app.projetoagenda;

    import androidx.appcompat.app.AppCompatActivity;
    import usuario.app.projetoagenda.ContextoDados.ContatosCursor;

    import android.os.Bundle;
    import android.app.Activity;
    import android.content.Context;
    import android.view.View;
    import android.view.View.OnClickListener;
    import android.widget.Button;
    import android.widget.EditText;
    import android.widget.TextView;

    public class MainActivity extends AppCompatActivity {

        Button btnSalvar, btnCancelar, btnNovo;
        EditText txtNome, txtEndereco, txtTelefone;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            CarregarInterfaceListagem();
        }

        public void CarregarInterfaceListagem(){
            setContentView(R.layout.activity_main);

            //configurando o botão de criar novo cadastro
            btnNovo = (Button)findViewById(R.id.btnNovo);
            btnNovo.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View view) {
                        CarregarInterfaceListagem();
                }
            });
            CarregarLista(this);
        }

        public void CarregarIntercaceCadastro(){
            setContentView(R.layout.cadastro);

            //configurando o botão de cancelar cadastro
            btnCancelar = (Button)findViewById(R.id.btnCancelar);
            btnCancelar.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    CarregarInterfaceListagem();
                }
            });

            //configurando o formulário de cadastro
            txtNome = (EditText)findViewById(R.id.txtNome);
            txtEndereco = (EditText)findViewById(R.id.txtEndereco);
            txtTelefone = (EditText)findViewById(R.id.txtTelefone);

            //configurando o botão salvar
            btnSalvar = (Button)findViewById(R.id.btnSalvar);
            btnSalvar.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    SalvarCadastro();
                }
            });

        }

        public void SalvarCadastro(){
            ContextoDados db = new ContextoDados(this);
            db.InserirContato(txtNome.getText().toString(), txtTelefone.getText().toString(), txtEndereco.getText().toString());
            setContentView(R.layout.activity_main);
            CarregarLista(this);
        }

        public void CarregarLista(Context c){
            ContextoDados db = new ContextoDados(c);
            ContatosCursor cursor = db.RetornarContatos(ContatosCursor.OrdernarPor.NomeCrescente);

            for(int i = 0; i < cursor.getCount(); i++){
                cursor.moveToPosition(i);
                ImprimirLinha(cursor.getNome(), cursor.getTelefone(), cursor.getEndereco());
            }
        }

        public void ImprimirLinha(String nome, String telefone, String endereco){
            TextView tv = (TextView)findViewById(R.id.listaContatos);

            if(tv.getText().toString().equalsIgnoreCase("Nenhum contato cadastrado.")){
                tv.setText("");

                tv.setText(tv.getText() + "\r\n" + "Nome: "+ nome + "\n" + "Telefone: "+ telefone + "\n" + "Endereço: "+endereco);
            }

        }

    }
