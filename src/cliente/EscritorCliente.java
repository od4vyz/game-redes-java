package cliente;

import java.io.PrintWriter;
import java.util.Scanner;
import Parser.*;

public class EscritorCliente implements Runnable {

    private PrintWriter saida;
    private Scanner scanner;
    private ParserCliente parser;

    EscritorCliente(PrintWriter saida, Scanner scanner, ParserCliente parser){
        this.saida = saida;
        this.scanner = scanner;
        this.parser = parser;
    }

    @Override
    public void run(){
        while (true) {
            try{
                String mensagem = scanner.nextLine(); //le a entrada do usuario
                String  mensagemProtocolada = ParserCliente.Analisar(mensagem); //mensagem limpa
                saida.println(mensagemProtocolada);
            }catch(Exception e){
                System.out.println("Erro ao enviar mensagem. "+ e.getMessage());
                break;
            }
        }
    }
}
