package server;

import java.io.*;
import java.net.*;
import java.util.*;

public class ConexaoJogador implements Runnable {
    
    private Socket client;
    private BufferedReader entrada;
    private PrintWriter saida;

    private List<ConexaoJogador> conexoes;

    public ConexaoJogador(Socket client, List<ConexaoJogador> conexoes){
        this.client = client;
        this.conexoes = conexoes;
    }

    @Override
    public void run(){

        try{  
            this.entrada = new BufferedReader(new InputStreamReader(client.getInputStream()));
            this.saida = new PrintWriter(new OutputStreamWriter(client.getOutputStream()), true);            

            String mensagem;
            while((mensagem = entrada.readLine()) != null){
                System.out.println(mensagem);
            }

        }catch(Exception e){
            System.out.println("Erro ao processar mensagem. "+e.getMessage());
        }finally{
            try{
                //fecha os canais de comunicacao
                if (entrada != null) entrada.close();
                if (saida != null) saida.close();
                if (client != null) client.close();
                //se remove da lista de conexoes ativas
                conexoes.remove(this);
            }catch(Exception e){
                System.out.println("Erro ao encerrar conexao. "+e.getMessage());
            }
        }
    }
}
