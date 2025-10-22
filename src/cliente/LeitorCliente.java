package cliente;

import java.io.BufferedReader;

public class LeitorCliente implements Runnable{

    private BufferedReader entrada;

    public LeitorCliente(BufferedReader entrada){
        this.entrada = entrada;
    }

    @Override
    public void run(){
        try{
            String mensagem;

            while((mensagem = entrada.readLine()) != null){
                System.out.println(mensagem);
            }
        }catch(Exception e){
            System.out.println("Erro de conexao com o servidor. " + e.getMessage());
        }
    }
}
