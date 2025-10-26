package cliente;

import java.io.PrintWriter;
import java.util.Scanner;

public class EscritorCliente implements Runnable {

    private PrintWriter saida;
    private Scanner scanner;

    private ParserCliente parser;

    EscritorCliente(PrintWriter saida, Scanner scanner){
        this.saida = saida;
        this.scanner = scanner;
        this.parser = new ParserCliente();
    }

    @Override
    public void run(){

        System.out.println("Digite seu nome: ");
        String nome = scanner.nextLine();
        saida.println("CONECTAR|" + nome);

        while (true) {
            try{
                String mensagem = scanner.nextLine(); //le a entrada do usuario
                String mensagemProtocolo = parser.Analisar(mensagem);
                saida.println(mensagemProtocolo); //envia mensagem para o servidor
                saida.println(mensagem);
            }catch(Exception e){
                System.out.println("Erro ao enviar mensagem. "+ e.getMessage());
                break;
            }
        }
    }
}
