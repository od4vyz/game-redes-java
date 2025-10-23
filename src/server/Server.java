package server;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import server.jogo.Engine;

public class Server {

    private int port;
    private ServerSocket server;

    private List<ConexaoJogador> conexoesAtivas;

    private Engine engine;

    public Server(){
        this.port = 9020;
        conexoesAtivas = new CopyOnWriteArrayList<>(); //implementacao preparada para concorrencia
        // this.engine = new Engine(    );
    }

    public void iniciarConexao(){
        try{
            server = new ServerSocket(port); //inicia o socket de Server com a porta informada

            System.out.println("Aguardando conexao...");

            while(true){
                Socket client = server.accept(); //estabelece conexao com o cliente

                ConexaoJogador conexao = new ConexaoJogador(client, conexoesAtivas); //instancia a conexao com um jogador
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
        Server Server = new Server();
        Server.iniciarConexao();
    }
}
