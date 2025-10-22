import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.*;

public class ConexaoJogador implements Runnable {
    
    private Socket client;
    private BufferedReader entrada;
    private PrintWriter saida;


    public ConexaoJogador(Socket client){
        this.client = client;
    }

    @Override
    public void run(){

        try{  
            this.entrada = new BufferedReader(new InputStreamReader(client.getInputStream()));
            this.saida = new PrintWriter(new OutputStreamWriter(client.getOutputStream()));            

            String mensagem;
            while((mensagem = entrada.readLine()) != null){
                System.out.println(mensagem);

                saida.println("mensagem recebida");
            }

        }catch(Exception e){
            System.out.println("Erro ao processar mensagem. "+e.getMessage());
        }
    }
}
