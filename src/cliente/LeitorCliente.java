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

                System.out.println(processarMensagem(mensagem+"\n"));

            }   
        }catch(Exception e){
            System.out.println("Erro de conexao com o servidor. " + e.getMessage());
        }
    }

    private String processarMensagem(String mensagemBruta){

        //recebe a mensagem na formatacao de protocolo e quebra para interpretacao
        String [] parametros = mensagemBruta.split("\\|");
        
        String comando = parametros[0]; 

        String mensagem;

        switch (comando) {
            case "DESCRICAO":
            case "NARRACAO":    
                String mensagemRecebida = parametros[1];
                mensagem = mensagemRecebida.replace("\\n", "\n"); //tratando as quebras de linha
                break;

            case "CHAT":
                mensagem = "[" + parametros[1] + "]" + " " + parametros[2];
                break;

            case "ERRO":
                mensagem = "[ERRO] " + parametros[1];
                break;
        
            default:
                mensagem = "[SERVIDOR] Não foi possível processar a mensagem.";
                break;
        }
        return mensagem;
    }
}
