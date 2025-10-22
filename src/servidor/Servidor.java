import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Servidor {

    private int port;
    private ServerSocket server;
    private Socket client;

    private List<ConexaoJogador> conexoesAtivas;

    public Servidor(){
        this.port = 9020;
        conexoesAtivas = new ArrayList<>();
    }

    public void iniciarConexao(){
        try{
            server = new ServerSocket(port);

            System.out.println("Aguardando conexao...");

            while(true){
                client = server.accept();

                ConexaoJogador conexao = new ConexaoJogador(client); //instancia a conexao com um jogador
                Thread threadConexao = new Thread(conexao); // cria uma thread para gerenciar essa conexao
                threadConexao.start(); // inicia a thread

                conexoesAtivas.add(conexao); //adiciona esse cliente a uma lista de controle de conexoes ativas

                System.out.println("Cliente conectado: " + client.getInetAddress().getHostAddress());
            }
        }catch(IOException e){
            System.out.println("Erro ao estabelecer conexao.\n"+e.getMessage());
        }
    }

    public static void main(String[] args) {
        Servidor servidor = new Servidor();
        servidor.iniciarConexao();
    }
}
