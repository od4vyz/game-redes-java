package cliente;

import java.io.*;
import java.util.Scanner;
import java.net.Socket;

public class Cliente {

    private String host;
    private int port;

    private Socket socket;
    private PrintWriter saida;
    private BufferedReader entrada;

    private Scanner scanner;

    public Cliente(){        
        this.port = 9020;
        this.scanner = new Scanner(System.in);
    }

    public void iniciarConexao(){
        try{
            System.out.println("Informe o IP do servidor (digite 0 para localhost):");
            host = scanner.nextLine();

            if (host.equals("0")) {
                host = "localhost";
            }

            socket = new Socket(host, port);

            // instanciando objetos de comunicacao
            this.entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.saida = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

            // inicializa as instancias responsaveis pela comunicacao do lado cliente
            LeitorCliente leitor = new LeitorCliente(entrada);
            EscritorCliente escritor = new EscritorCliente(saida);

            // cria as threads para gerenciar essas instancias
            Thread threadLeitura = new Thread(leitor);
            Thread threadEscrita = new Thread(escritor);

            //inicia as threads
            threadLeitura.start();
            threadEscrita.start();

            System.out.println("Conexão estabelecida");

        }catch(Exception e){
            System.out.println("Erro ao tentar conectar.\n"+e.getMessage());
        }
    }

    public static void main(String[] args) {
        Cliente cliente = new Cliente();
        cliente.iniciarConexao();
    }
}
