package cliente;

import java.io.PrintWriter;
import java.util.Scanner;

public class EscritorCliente implements Runnable {

    private PrintWriter saida;
    private Scanner scanner;

    EscritorCliente(PrintWriter saida, Scanner scanner){
        this.saida = saida;
        this.scanner = scanner;
    }

    @Override
    public void run(){
        while (true) {
            try{
                String mensagem = scanner.nextLine(); //le a entrada do usuario
                saida.println(mensagem); //envia mensagem para o servidor
            }catch(Exception e){
                System.out.println("Erro ao enviar mensagem. "+ e.getMessage());
                break;
            }
        }
    }
}
