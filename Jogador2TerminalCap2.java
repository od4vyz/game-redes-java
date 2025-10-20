import Jogo.Modelo.*;
import Jogo.Modelo.Logica.*;

import java.util.Scanner;

public class Jogador2TerminalCap2 {

    public static void main(String[] args) {
        // --- 1. SETUP INICIAL DO JOGADOR 2 ---

        Scanner scanner = new Scanner(System.in);
        MotorDoJogo motorDoJogo = new MotorDoJogo();

        Mundo criador = new Mundo();
        criador.configurarNavegacao(); // CRÍTICO: Configura todas as conexões
        Sala salaAtual = criador.getGaleria();

        motorDoJogo.setMundo2(salaAtual);
        Jogador jogador2 = new Jogador("Jogador 2", salaAtual);
        motorDoJogo.adicionarJogador2(jogador2);

        System.out.println("=== JOGADOR 2 - CAPÍTULOS 1 e 2 ===");
        System.out.println("Digite 'sair' a qualquer momento para terminar.");
        System.out.println("Digite 'dica' para ver comandos disponíveis.");
        System.out.println("--------------------------------------");

        // Exibe a primeira descrição do local para o jogador
        System.out.println(jogador2.getSalaAtual().getDescricaoInicial());

        // --- 2. O GAME LOOP ---
        boolean portaJaNotificada = false;
        boolean caldeiraoJaNotificado = false;
        while (true) {
            System.out.print("J2 (" + jogador2.getSalaAtual().getNome() + ")> ");
            String comando = scanner.nextLine();

            if (comando.equalsIgnoreCase("sair")) {
                System.out.println("Jogador 2 saiu do jogo!");
                break;
            }

            if (comando.equalsIgnoreCase("dica")) {
                String sala = jogador2.getSalaAtual().getNome();
                System.out.println("DICAS JOGADOR 2 (" + sala + "):");

                if (sala.equals("Galeria")) {
                    System.out.println("- observar (ver ao redor)");
                    System.out.println("- ir até a porta");
                    System.out.println("- abrir a porta");
                    System.out.println("- andar para o quadro 1-6");
                    System.out.println("- entrar (após J1 resolver puzzle)");
                    System.out.println("- eventos (verificar notificações)");
                } else if (sala.equals("Fazenda")) {
                    System.out.println("- observar (ver ao redor)");
                    System.out.println("- ir moinho");
                    System.out.println("- olhar plantas");
                    System.out.println("- voltar galeria");
                }
                continue;
            }

            if (comando.equalsIgnoreCase("eventos")) {
                String eventos = SistemaNotificacao.getInstance().lerEventosRecentes("Jogador 2");
                if (eventos != null) {
                    System.out.println("=== EVENTOS RECENTES ===");
                    System.out.println(eventos);
                    System.out.println("========================");
                } else {
                    System.out.println("Nenhum evento recente.");
                }
                continue;
            }

            // Verificar se o estado global mudou (simular notificação via arquivo)
            if (!portaJaNotificada && SistemaNotificacao.getInstance().temEvento("PORTAS_ABERTAS") &&
                    jogador2.getSalaAtual().getNome().equals("Galeria") &&
                    !(boolean) jogador2.getSalaAtual().getEstado("portaAberta")) {

                System.out.println();
                System.out.println("🔔 *** NOTIFICAÇÃO MULTIPLAYER ***");
                System.out.println("Você escuta um barulho BIZARRO!");
                System.out.println("A porta que antes estava trancada magicamente se abre!");
                System.out.println("Um som de aves e uma leve brisa sai dessa porta.");
                System.out.println("*** Agora você pode usar: entrar ***");
                System.out.println();

                jogador2.getSalaAtual().setEstado("portaAberta", true);
                jogador2.getSalaAtual().setDescricaoLonga(
                        jogador2.getSalaAtual().getDescricaoAlternativa("descricaoPortaAberta"));
                EstadoGlobal.getInstance().setPortasAbertas(true);
                portaJaNotificada = true;
            }

            // Verificar evento do caldeirão ativo
            if (!caldeiraoJaNotificado && SistemaNotificacao.getInstance().temEvento("CALDEIRAO_ATIVO")) {
                System.out.println();
                System.out.println("🔔 *** NOTIFICAÇÃO MULTIPLAYER ***");
                if (jogador2.getSalaAtual().getNome().equals("Galeria")) {
                    System.out.println("Você escuta um barulho na galeria!");
                } else {
                    System.out.println("Você escuta um barulho!");
                }
                System.out.println("*** Algo importante aconteceu! ***");
                System.out.println();

                EstadoGlobal.getInstance().setCaldeiraoAtivo(true);
                caldeiraoJaNotificado = true;
            }

            // Verificar se o estado global mudou (versão antiga como backup)
            if (!portaJaNotificada && EstadoGlobal.getInstance().isPortasAbertas() &&
                    jogador2.getSalaAtual().getNome().equals("Galeria") &&
                    !(boolean) jogador2.getSalaAtual().getEstado("portaAberta")) {

                System.out.println("🔔 NOTIFICAÇÃO: Você escuta um barulho BIZARRO!");
                System.out.println("A porta que antes estava trancada magicamente se abre!");
                jogador2.getSalaAtual().setEstado("portaAberta", true);
                jogador2.getSalaAtual().setDescricaoLonga(
                        jogador2.getSalaAtual().getDescricaoAlternativa("descricaoPortaAberta"));
                portaJaNotificada = true;
            }

            // Verificar encontro de jogadores
            if (SistemaNotificacao.getInstance().temEvento("ENCONTRO_JOGADORES")) {
                System.out.println();
                System.out.println("🎉 *** ENCONTRO MULTIPLAYER ***");
                System.out.println("*** VOCÊ ENCONTROU O OUTRO JOGADOR! ***");
                System.out.println();
                SistemaNotificacao.getInstance().limparEvento("ENCONTRO_JOGADORES");
            }

            // Processa o comando usando o motor do jogo
            String resposta = motorDoJogo.processarComando(jogador2, comando);

            // Tratamento especial para transições
            if (resposta.equals("TRANSICAO_FAZENDA")) {
                Sala fazenda = criador.getFazenda();
                jogador2.setSalaAtual(fazenda);
                EstadoGlobal.getInstance().setLocalizacaoJ2("Fazenda");
                System.out.println("🚶 Você avança em direção à porta!");
                System.out.println("ATUALIZACAO " + fazenda.getDescricaoInicial());
            } else if (resposta.equals("TRANSICAO_GALERIA")) {
                Sala galeria = criador.getGaleria();
                // Manter estado da porta se já foi aberta
                if (EstadoGlobal.getInstance().isPortasAbertas()) {
                    galeria.setEstado("portaAberta", true);
                }
                jogador2.setSalaAtual(galeria);
                EstadoGlobal.getInstance().setLocalizacaoJ2("Galeria");
                System.out.println("🚶 Você voltou para a galeria!");
                System.out.println("ATUALIZACAO " + galeria.getDescricaoInicial());
            } else if (resposta.equals("TRANSICAO_MOINHO")) {
                Sala moinho = criador.getMoinho();
                jogador2.setSalaAtual(moinho);
                EstadoGlobal.getInstance().setLocalizacaoJ2("Moinho");
                System.out.println("🚶 Você entrou no moinho!");
                System.out.println("ATUALIZACAO " + moinho.getDescricaoInicial());
            } else if (resposta.equals("TRANSICAO_COZINHA")) {
                Sala cozinha = criador.getCozinha();
                jogador2.setSalaAtual(cozinha);
                EstadoGlobal.getInstance().setLocalizacaoJ2("Cozinha");
                System.out.println("🚶 Você entrou na cozinha!");
                System.out.println("ATUALIZACAO " + cozinha.getDescricaoInicial());
            } else if (resposta.equals("TRANSICAO_HALL")) {
                Sala hall = criador.getHall();
                jogador2.setSalaAtual(hall);
                EstadoGlobal.getInstance().setLocalizacaoJ2("Hall");
                System.out.println("🚶 Você foi para o hall!");
                if (EstadoGlobal.getInstance().getLocalizacaoJ1().equals("Hall")) {
                    System.out.println("*** VOCÊ ENCONTROU O JOGADOR 1! ***");
                }
                System.out.println("ATUALIZACAO " + hall.getDescricaoInicial());
            } else {
                System.out.println(resposta);
            }
        }

        scanner.close();
    }
}