import Jogo.Modelo.*;
import Jogo.Modelo.Logica.*;

import java.util.Scanner;

public class Jogador1TerminalCap2 {

    public static void main(String[] args) {
        // --- 1. SETUP INICIAL DO JOGADOR 1 ---

        Scanner scanner = new Scanner(System.in);
        MotorDoJogo motorDoJogo = new MotorDoJogo();

        // Reset estado global e limpa eventos anteriores
        EstadoGlobal.getInstance().reset();
        SistemaNotificacao.getInstance().limparEventos();

        Mundo criador = new Mundo();
        criador.configurarNavegacao(); // CRÍTICO: Configura todas as conexões
        Sala salaAtual = criador.getDeposito();

        motorDoJogo.setMundo(salaAtual);
        Jogador jogador1 = new Jogador("Jogador 1", salaAtual);
        motorDoJogo.adicionarJogador(jogador1);

        System.out.println("=== JOGADOR 1 - CAPÍTULOS 1 e 2 ===");
        System.out.println("Digite 'sair' a qualquer momento para terminar.");
        System.out.println("Digite 'dica' para ver comandos disponíveis.");
        System.out.println("--------------------------------------");

        // Exibe a primeira descrição do local para o jogador
        System.out.println(jogador1.getSalaAtual().getDescricaoInicial());

        // --- 2. O GAME LOOP ---
        boolean caldeiraoJaNotificado = false;
        while (true) {
            System.out.print("J1 (" + jogador1.getSalaAtual().getNome() + ")> ");
            String comando = scanner.nextLine();

            if (comando.equalsIgnoreCase("sair")) {
                System.out.println("Jogador 1 saiu do jogo!");
                break;
            }

            if (comando.equalsIgnoreCase("dica")) {
                String sala = jogador1.getSalaAtual().getNome();
                System.out.println("DICAS JOGADOR 1 (" + sala + "):");

                if (sala.equals("Depósito")) {
                    System.out.println("- observar (ver ao redor)");
                    System.out.println("- apertar interruptor");
                    System.out.println("- olhar porta");
                    System.out.println("- olhar fechadura");
                    System.out.println("- testar A B C (testar combinação)");
                    System.out.println("- abrir porta (após resolver puzzle)");
                    System.out.println("- subir escada (após abrir porta)");
                } else if (sala.equals("Hall")) {
                    System.out.println("- observar (ver ao redor)");
                    System.out.println("- ir cozinha");
                    System.out.println("- olhar porta saida");
                    System.out.println("- olhar escada superior");
                } else if (sala.equals("Cozinha")) {
                    System.out.println("- observar (ver ao redor)");
                    System.out.println("- ler lista");
                    System.out.println("- abrir geladeira");
                    System.out.println("- pegar pao");
                    System.out.println("- voltar sala");
                }
                continue;
            }

            if (comando.equalsIgnoreCase("eventos")) {
                String eventos = SistemaNotificacao.getInstance().lerEventosRecentes("Jogador 1");
                if (eventos != null) {
                    System.out.println("=== EVENTOS RECENTES ===");
                    System.out.println(eventos);
                    System.out.println("========================");
                } else {
                    System.out.println("Nenhum evento recente.");
                }
                continue;
            }

            // Verificar evento do caldeirão ativo
            if (!caldeiraoJaNotificado && SistemaNotificacao.getInstance().temEvento("CALDEIRAO_ATIVO")) {
                System.out.println();
                System.out.println("🔔 *** NOTIFICAÇÃO MULTIPLAYER ***");
                if (jogador1.getSalaAtual().getNome().equals("Hall")
                        || jogador1.getSalaAtual().getNome().equals("Cozinha")) {
                    System.out.println("Você escuta um barulho na sala!");
                } else {
                    System.out.println("Você escuta um barulho!");
                }
                System.out.println("*** Algo importante aconteceu! ***");
                System.out.println();

                EstadoGlobal.getInstance().setCaldeiraoAtivo(true);
                caldeiraoJaNotificado = true;
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
            String resposta = motorDoJogo.processarComando(jogador1, comando);

            // Tratamento especial para eventos e transições
            if (resposta.equals("EVENTO_PORTAS_ABERTAS")) {
                System.out.println("🎉 SUCESSO! A porta se abre com um clique alto!");
                System.out.println("ATUALIZACAO Uma escada está adiante de você.");
                System.out.println("*** EVENTO MULTIPLAYER: Jogador 2 foi notificado! ***");

                // Envia notificação para outros jogadores
                SistemaNotificacao.getInstance().enviarEvento("PORTAS_ABERTAS", "Jogador 1");
                SistemaNotificacao.getInstance().enviarEvento(
                        "A porta que antes estava trancada magicamente se abre fazendo um barulho BIZARRO!", "Sistema");

            } else if (resposta.startsWith("EVENTO_CALDEIRAO_ATIVO")) {
                // Evento para quando o caldeirão fica ativo
                String[] partes = resposta.split("\n");
                if (partes.length > 1) {
                    System.out.println("🔥 " + partes[1]); // Mostra o resultado do caldeirão
                }
                System.out.println("*** EVENTO MULTIPLAYER: Ambos os jogadores foram notificados sobre o barulho! ***");

                // Marca o estado global
                EstadoGlobal.getInstance().setCaldeiraoAtivo(true);

                // Envia notificações para ambos os jogadores conforme o script
                SistemaNotificacao.getInstance().enviarEvento("CALDEIRAO_ATIVO", "Jogador 2");
                SistemaNotificacao.getInstance().enviarEvento("Você escuta um barulho na sala", "Sistema-J1");
                SistemaNotificacao.getInstance().enviarEvento("Você escuta um barulho na galeria", "Sistema-J2");

            } else if (resposta.equals("TRANSICAO_HALL")) {
                Sala hall = criador.getHall();
                jogador1.setSalaAtual(hall);
                EstadoGlobal.getInstance().setLocalizacaoJ1("Hall");
                System.out.println("🚶 Você subiu a escada!");
                System.out.println("ATUALIZACAO " + hall.getDescricaoInicial());
            } else if (resposta.equals("TRANSICAO_COZINHA")) {
                Sala cozinha = criador.getCozinha();
                jogador1.setSalaAtual(cozinha);
                EstadoGlobal.getInstance().setLocalizacaoJ1("Cozinha");
                System.out.println("🚶 Você foi até a cozinha!");
                System.out.println("ATUALIZACAO " + cozinha.getDescricaoInicial());
            } else if (resposta.equals("TRANSICAO_FAZENDA")) {
                Sala fazenda = criador.getFazenda();
                jogador1.setSalaAtual(fazenda);
                EstadoGlobal.getInstance().setLocalizacaoJ1("Fazenda");
                System.out.println("🚶 Você entrou na fazenda!");
                System.out.println("ATUALIZACAO " + fazenda.getDescricaoInicial());
            } else if (resposta.equals("TRANSICAO_MOINHO")) {
                Sala moinho = criador.getMoinho();
                jogador1.setSalaAtual(moinho);
                EstadoGlobal.getInstance().setLocalizacaoJ1("Moinho");
                System.out.println("🚶 Você entrou no moinho!");
                System.out.println("ATUALIZACAO " + moinho.getDescricaoInicial());
            } else if (resposta.equals("TRANSICAO_GALERIA")) {
                Sala galeria = criador.getGaleria();
                // Manter estado da porta se já foi aberta
                if (EstadoGlobal.getInstance().isPortasAbertas()) {
                    galeria.setEstado("portaAberta", true);
                }
                jogador1.setSalaAtual(galeria);
                EstadoGlobal.getInstance().setLocalizacaoJ1("Galeria");
                System.out.println("🚶 Você foi para a galeria!");
                if (EstadoGlobal.getInstance().getLocalizacaoJ2().equals("Galeria")) {
                    System.out.println("*** VOCÊ ENCONTROU O JOGADOR 2! ***");
                }
                System.out.println("ATUALIZACAO " + galeria.getDescricaoInicial());
            } else {
                System.out.println(resposta);
            }
        }

        scanner.close();
    }
}