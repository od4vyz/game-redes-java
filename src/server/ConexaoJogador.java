package server;

import java.io.*;
import java.net.*;
import java.util.*;

import server.jogo.*;
import server.jogo.modelo.*;;

public class ConexaoJogador implements Runnable {

    private Socket client;
    private BufferedReader entrada;
    private PrintWriter saida;

    private Jogador player;
    private List<ConexaoJogador> conexoes;
    private Engine engine;

    public ConexaoJogador(Socket client, List<ConexaoJogador> conexoes, Engine engine){
        this.client = client;
        this.conexoes = conexoes;
        this.engine = engine;
    }

    @Override
    public void run(){

        try{  
            this.entrada = new BufferedReader(new InputStreamReader(client.getInputStream()));
            this.saida = new PrintWriter(new OutputStreamWriter(client.getOutputStream()), true);          
            
            //aguardando conexao do player
            String msgConexao = entrada.readLine(); // Espera mensagem de conexao

            if (msgConexao == null || !msgConexao.startsWith("CONECTAR|")) {
                saida.println("ERRO|Mensagem inválido. Envie 'CONECTAR|SeuNome'.");
                return;
            }

            String nomeJogador = msgConexao.split("\\|")[1];
            String respostaLogin = engine.conectarJogador(nomeJogador); // Chama a engine

            // Se o login falhar (nome em uso, servidor cheio)
            if (respostaLogin.startsWith("ERRO|")) {
                saida.println(respostaLogin);
                return;
            }

            // se consegue conectar o jogador
            this.player = engine.getJogador(nomeJogador);

            // Envia a primeira descrição da sala para o cliente
            saida.println(respostaLogin); 
            


            String mensagemBruta;
            while((mensagemBruta = entrada.readLine()) != null){
                System.out.println("Recebido: " + mensagemBruta);
                String mensagem = processarMensagem(mensagemBruta);
                if(mensagem != null){
                    saida.println(mensagem);
                }
            }

        }catch(Exception e){
            System.out.println("Erro ao processar mensagem. " + e.getMessage());
            e.printStackTrace();
        }finally{
            try{
                //fecha os canais de comunicacao
                if (entrada != null) entrada.close();
                if (saida != null) saida.close();
                if (client != null) client.close();
                //se remove da lista de conexoes ativas
                conexoes.remove(this);
                if (this.player != null) {
                    engine.desconectarJogador(this.player); 
                }
            }catch(Exception e){
                System.out.println("Erro ao encerrar conexao. "+e.getMessage());
            }
        }
    }

    public PrintWriter getSaida() {
        return this.saida;
    }

    private void broadcast(String mensagem) {
        for (ConexaoJogador conexao : conexoes) {
            // envia a mensagem para todos os clientes conectados
            if (conexao.getSaida() != null) {
                conexao.getSaida().println(mensagem);
            }
        }
    }   

    private String processarMensagem(String mensagemBruta){
        String [] partes = mensagemBruta.split("\\|");

        String comando = partes[0]; 
        String [] parametros = Arrays.copyOfRange(partes, 1, 4);  

        String mensagem;

        if(comando.equals("FALAR")){
            mensagem = "CHAT|" + player.getNome() + "|" + parametros[0];
            broadcast(mensagem);
            return null;
        }else{
            mensagem = engine.processarComando(player, comando, parametros);
            if(mensagem.startsWith("NARRACAO")){
                broadcast(mensagem);
                return null;
            }else{
                return mensagem;
            }
        }
    }
}
