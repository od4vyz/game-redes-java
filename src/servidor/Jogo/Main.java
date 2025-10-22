package Jogo;

import Jogo.Modelo.Mundo;
import Jogo.Modelo.Logica.MotorDoJogo;
import Jogo.Modelo.Jogador;
import Jogo.Modelo.Sala;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        // --- 1. SETUP INICIAL DO JOGO ---

        // Cria o leitor de entrada do teclado (terminal)
        Scanner scanner = new Scanner(System.in);

        // Cria o motor do jogo, que contém toda a lógica
        MotorDoJogo motorDoJogo = new MotorDoJogo();

        // Cria o mundo usando a classe que você fez para isso
        // Supondo que CriadorDeMundo configure e retorne a localização inicial
        Mundo criador = new Mundo();
        Sala salaInicialJogador1 = criador.criarsala();

        // Adiciona o mundo criado ao motor do jogo
        motorDoJogo.setMundo(salaInicialJogador1);

        // Cria o nosso jogador de teste
        Jogador jogadorDeTeste = new Jogador("Jogador 1", salaInicialJogador1);

        // Adiciona o jogador ao motor do jogo
        motorDoJogo.adicionarJogador(jogadorDeTeste);

        System.out.println("--- Jogo de Teste Local iniciado ---");
        System.out.println("Digite 'sair' a qualquer momento para terminar.");
        System.out.println("----------------------------------------");

        // Exibe a primeira descrição do local para o jogador
        System.out.println(jogadorDeTeste.getSalaAtual().getDescricaoInicial());

        // --- 2. O GAME LOOP ---

        while (true) {
            // Exibe um prompt para o usuário digitar
            System.out.print("> ");

            // Lê a linha inteira que o usuário digitou
            String comando = scanner.nextLine();

            // Condição de saída para terminar o teste
            if (comando.equalsIgnoreCase("sair")) {
                System.out.println("Obrigado por jogar!");
                break;
            }

            // Processa o comando usando o motor do jogo
            String resposta = motorDoJogo.processarComando(jogadorDeTeste, comando);

            // ATENÇÃO: Tratamento especial para o evento multiplayer
            if (resposta.equals("EVENTO_PORTAS_ABERTAS")) {
                System.out.println("--- EVENTO MULTIPLAYER ACIONADO ---");
                System.out.println("No jogo real, isso enviaria mensagens para ambos os jogadores.");
                System.out.println("Para este teste, vamos simular a mensagem do Jogador 1:");
                System.out.println("ATUALIZACAO A porta se abre com um clique alto! Uma escada está adiante de você.");
                // Aqui você teria que manualmente atualizar as saídas no seu teste, se quisesse
                // continuar
                // motorDoJogo.conectarLocais("Depósito", "Galeria");
            } else {
                // Imprime a resposta normal do motor do jogo (que já vem com ATUALIZACAO, ERRO,
                // etc.)
                System.out.println(resposta);
            }
        }

        // Fecha o scanner para evitar vazamento de recursos
        scanner.close();
    }
}