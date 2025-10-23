package server.jogo;


import java.util.Map;
import server.jogo.modelo.*;

public class Engine {
    private Jogador jogador;
    private Jogador jogador2;
    public Sala SalaJogador1;
    public Sala SalaJogador2;

    public Engine(Jogador jogador) {
        this.jogador = jogador;
    }

    public Map<String, String> getEstadoAtual() {
        return Map.of(
                "salaAtual", jogador.getSalaAtual().getDescricaoInicial(),
                "inventario", jogador.InventarioString());
    }

    public void setMundo(Sala mundo) {
        this.SalaJogador1 = mundo;
    }

    public void setMundo2(Sala mundo) {
        this.SalaJogador2 = mundo;
    }

    public void adicionarJogador(Jogador jogador) {
        this.jogador = jogador;
    }

    public void adicionarJogador2(Jogador jogador2) {
        this.jogador2 = jogador2;
    }

    public Map<String, Jogador> getJogador() {
        return Map.of("jogador", jogador);
    }

    public String processarComando(Jogador jogador, String comando) {
        Sala localAtual = jogador.getSalaAtual();
        String[] palavras = comando.toLowerCase().split(" ");
        String acao = palavras[0];

        // --- LÓGICA DO DEPÓSITO ---
        if (localAtual.getNome().equals("Depósito")) {
            boolean luzAcesa = (boolean) localAtual.getEstado("luzAcesa");

            if (acao.equals("apertar") || acao.equals("ligar") && comando.contains("interruptor")) {
                if (!luzAcesa) {
                    localAtual.setEstado("luzAcesa", true);
                    localAtual.setDescricaoLonga(localAtual.getDescricaoAlternativa("descricaoLuzAcesa"));
                    return "ATUALIZACAO Você anda em direção ao interruptor e o pressiona. \n"
                            + localAtual.getDescricaoAlternativa("descricaoLuzAcesa");
                } else {
                    return "ATUALIZACAO O interruptor já está pressionado.";
                }
            }

            // A partir daqui, só funciona se a luz estiver acesa
            if (!luzAcesa && !acao.equals("observar") && !acao.equals("olhar")) {
                return "ERRO Está escuro demais para fazer isso.";
            }

            if ((acao.equals("verificar") || acao.equals("ler")) && comando.contains("porta")
                    || acao.equals("olhar") && comando.contains("porta")) {
                return "ATUALIZACAO Se esforçando um pouco você vê alguns símbolos estranhos: um triângulo, um quadrado e um círculo.\n"
                        + "SYSTEM TIP: Para olhar mais de perto a fechadura, use: olhar fechadura";
            }

            if ((acao.equals("verificar") || acao.equals("olhar") || acao.equals("ler"))
                    && comando.contains("fechadura")) {
                return "ATUALIZACAO A fechadura possui 3 desenhos de animais, você observa muitos animais em cada uma das voltas, dentre eles tem um Cachorro, Pato, Elefante, Rinoceronte, Borboleta, Lobo, Dinossauro, Avestruz, Leão, Peixe. SYSTEM TIP: Para testar a combinação escreva: testar A B C onde A B C é a inicial do animal e a ordem importa.";
            }

            // O PUZZLE!
            if (acao.equals("testar")) {
                if (palavras.length < 4)
                    return "ERRO Formato inválido. Use: testar A B C";

                // A solução do puzzle baseada nas pistas
                if (palavras[1].equalsIgnoreCase("A") && palavras[2].equalsIgnoreCase("H")
                        && palavras[3].equalsIgnoreCase("P")) {
                    // SUCESSO! Marca o estado global e local
                    EstadoGlobal.getInstance().setPortasAbertas(true);
                    localAtual.setEstado("portaAberta", true);
                    localAtual.setDescricaoLonga(localAtual.getDescricaoAlternativa("descricaoPortaAberta"));
                    return "EVENTO_PORTAS_ABERTAS";
                } else {
                    return "ATUALIZACAO Nada acontece. A combinação parece estar errada.";
                }
            }

            // ABRIR PORTA (só funciona após resolver o puzzle)
            if (acao.equals("abrir") && comando.contains("porta")) {
                boolean portaAberta = (boolean) localAtual.getEstado("portaAberta");
                if (portaAberta) {
                    return "ATUALIZACAO A porta já está aberta! Uma escada está adiante de você. Use: subir escada";
                } else {
                    return "ERRO A porta está trancada. Preciso resolver o puzzle da fechadura primeiro.";
                }
            }

            // SUBIR ESCADA (transição para o Hall - Capítulo 2)
            if ((acao.equals("subir") || acao.equals("ir")) && comando.contains("escada")) {
                boolean portaAberta = (boolean) localAtual.getEstado("portaAberta");
                if (portaAberta) {
                    EstadoGlobal.getInstance().setJogador1NoHall(true);
                    return "TRANSICAO_HALL";
                } else {
                    return "ERRO Não há escada disponível. Preciso abrir a porta primeiro.";
                }
            }

            // NAVEGAÇÃO LIVRE DO DEPÓSITO (quando porta estiver aberta)
            if ((acao.equals("ir") || acao.equals("andar") || acao.equals("entrar")) &&
                    (boolean) localAtual.getEstado("portaAberta")) {
                // Se a porta estiver aberta, permitir navegação automática
                for (String palavra : palavras) {
                    if (localAtual.getSaida(palavra) != null) {
                        if (palavra.equals("hall"))
                            return "TRANSICAO_HALL";
                    }
                }
            }
        }

        // --- LÓGICA DA GALERIA ---
        if (localAtual.getNome().equals("Galeria")) {
            // Verifica se as portas foram abertas pelo Jogador 1
            boolean portasAbertas = EstadoGlobal.getInstance().isPortasAbertas();

            // ENTRAR/IR PARA A FAZENDA (Capítulo 2) - PRIMEIRO VERIFICAR TRANSIÇÕES
            if (((acao.equals("entrar") || acao.equals("ir")) && comando.contains("porta")) ||
                    (acao.equals("entrar") && !comando.contains("quadro"))) {
                if (portasAbertas) {
                    EstadoGlobal.getInstance().setJogador2NaFazenda(true);
                    return "TRANSICAO_FAZENDA";
                } else {
                    return "ERRO A porta ainda não está aberta.";
                }
            }

            // OBSERVAR PORTA (apenas para observação, não movimento)
            if ((acao.equals("observar") || acao.equals("olhar")) && comando.contains("porta")) {
                if (portasAbertas) {
                    // Atualiza descrição da galeria se as portas foram abertas
                    if (!(boolean) localAtual.getEstado("portaAberta")) {
                        localAtual.setEstado("portaAberta", true);
                        localAtual.setDescricaoLonga(localAtual.getDescricaoAlternativa("descricaoPortaAberta"));
                        return "ATUALIZACAO " + localAtual.getDescricaoAlternativa("descricaoPortaAberta");
                    } else {
                        return "ATUALIZACAO A porta atrás de você está aberta. Você pode entrar na fazenda.";
                    }
                } else {
                    return "ATUALIZACAO É uma porta aparentemente normal porém algo a distingue das outras, ela não tem fechadura nem maçaneta se tornando impossível de abrir.";
                }
            }

            if (acao.equals("abrir") && comando.contains("porta")) {
                if (portasAbertas) {
                    return "ATUALIZACAO A porta já está aberta automaticamente! Você pode entrar.";
                } else {
                    return "ATUALIZACAO Impossível abrir a porta, não tem maçaneta.";
                }
            }

            if ((acao.equals("andar") || acao.equals("ir") || acao.equals("observar")) && comando.contains("quadro")) {
                String numeroQuadro = palavras[palavras.length - 1]; // Pega o último elemento, que deve ser o número
                String detalhe = localAtual.getDetalhe("quadro " + numeroQuadro);
                if (detalhe != null) {
                    return "ATUALIZACAO " + detalhe;
                } else {
                    return "ERRO Não consigo encontrar esse quadro.";
                }
            }
        }

        // --- LÓGICA DO HALL (Capítulo 2 e 3) ---
        if (localAtual.getNome().equals("Hall")) {
            // Observar ou ir até a porta
            if (comando.contains("porta") && (comando.contains("saida") || comando.contains("saída")
                    || acao.equals("ir") || acao.equals("observar"))) {
                return "ATUALIZACAO A porta está trancada.";
            }

            // Usar chave na porta
            if ((acao.equals("usar") || acao.equals("abrir")) && comando.contains("chave")
                    && comando.contains("porta")) {
                if (!jogador.isTemChave()) {
                    return "ERRO Você não tem uma chave.";
                }

                // Primeira tentativa - porta errada
                return "ATUALIZACAO Você tentou usar a chave na porta porém ela não encaixa, é estranho pois parece ser exatamente para essa porta, só então você percebe ter colocado na porta errada, eram duas portas, na segunda ela encaixa perfeitamente.\n\nFINAL DO JOGO!\n\nCom a porta aberta você descobre que estava NO TITANIC NA EAC........";
            }

            if (comando.contains("escada") && comando.contains("superior")) {
                return "ATUALIZACAO As escadas levam aos andares superiores, mas isso é para outro capítulo.";
            }
        }

        // --- LÓGICA DA COZINHA (Capítulo 2) ---
        if (localAtual.getNome().equals("Cozinha")) {
            if ((acao.equals("ir") || acao.equals("ler") || acao.equals("olhar")) && comando.contains("lista")) {
                String detalhe = localAtual.getDetalhe("lista de compras");
                return "ATUALIZACAO " + detalhe;
            }

            if ((acao.equals("abrir") || acao.equals("olhar")) && comando.contains("geladeira")) {
                String detalhe = localAtual.getDetalhe("geladeira");
                return "ATUALIZACAO " + detalhe;
            }

            if ((acao.equals("pegar") || acao.equals("tocar")) && comando.contains("pao")) {
                String resultado = jogador.pegarPaoMofado();
                return "ATUALIZACAO " + resultado;
            }

            if ((acao.equals("voltar") || acao.equals("ir")) && comando.contains("sala")) {
                return "TRANSICAO_HALL";
            }

            if ((acao.equals("voltar") || acao.equals("ir"))
                    && (comando.contains("hall") || comando.contains("principal"))) {
                return "TRANSICAO_HALL";
            }
        }

        // --- LÓGICA DA FAZENDA (Capítulo 2) ---
        if (localAtual.getNome().equals("Fazenda")) {
            // Importar o tipo Fazenda para ter acesso aos métodos específicos
            if (localAtual instanceof Fazenda) {
                Fazenda fazenda = (Fazenda) localAtual;

                // Usar foice para pegar ingredientes
                if (acao.equals("usar") && palavras.length >= 3 && palavras[1].equals("foice")) {
                    String ingrediente = palavras[2];

                    if (!jogador.isTemFoice()) {
                        return "ERRO Você não tem uma foice. Vá ao moinho para pegar uma.";
                    }

                    if (!fazenda.ingredienteDisponivel(ingrediente)) {
                        return "ERRO Não há " + ingrediente + " aqui. Ingredientes disponíveis: " +
                                String.join(", ", fazenda.getIngredientesDisponiveis());
                    }

                    return "ATUALIZACAO " + jogador.coletarIngrediente(ingrediente);
                }

                // Observar ingredientes específicos
                for (String ingrediente : fazenda.getIngredientesDisponiveis()) {
                    if (comando.contains(ingrediente)) {
                        return "ATUALIZACAO " + fazenda.observarIngrediente(ingrediente);
                    }
                }
            }

            if ((acao.equals("voltar") || acao.equals("ir")) && comando.contains("galeria")) {
                // Notificar jogador 1 sobre movimento do jogador 2
                return "TRANSICAO_GALERIA";
            }

            if ((acao.equals("ir") || acao.equals("entrar")) && comando.contains("moinho")) {
                return "TRANSICAO_MOINHO";
            }
        }

        // --- LÓGICA DO MOINHO (Capítulo 2) ---
        if (localAtual.getNome().equals("Moinho")) {
            // Importar o tipo Moinho para ter acesso aos métodos específicos
            if (localAtual instanceof Moinho) {
                Moinho moinho = (Moinho) localAtual;

                // Pegar foice
                if ((acao.equals("pegar") || acao.equals("pegar")) && comando.contains("foice")) {
                    String resultado = jogador.pegarFoice();
                    return "ATUALIZACAO " + resultado;
                }

                // Pegar colher
                if ((acao.equals("pegar") || acao.equals("pegar")) && comando.contains("colher")) {
                    String resultado = jogador.pegarColher();
                    return "ATUALIZACAO " + resultado;
                }

                // Colocar ingrediente no caldeirão
                if ((acao.equals("colocar") || acao.equals("adicionar") || acao.equals("por")) &&
                        comando.contains("caldeirao")) {

                    // Procurar ingrediente no comando
                    String ingrediente = null;
                    for (String palavra : palavras) {
                        if (jogador.temIngrediente(palavra)) {
                            ingrediente = palavra;
                            break;
                        }
                    }

                    if (ingrediente == null) {
                        return "ERRO Qual ingrediente você quer colocar no caldeirão? " +
                                "Ingredientes que você tem: " + jogador.getIngredientesPegos();
                    }

                    String resultado = moinho.adicionarIngrediente(ingrediente);

                    // Verificar se o caldeirão ficou ativo e enviar evento
                    if (moinho.isCaldeiraoAtivo() && !EstadoGlobal.getInstance().isCaldeiraoAtivo()) {
                        EstadoGlobal.getInstance().setCaldeiraoAtivo(true);
                        EstadoGlobal.getInstance().setMolduraAberta(true);

                        // Enviar evento para ambos os jogadores sobre barulho
                        SistemaNotificacao.getInstance().enviarEvento("CALDEIRAO_ATIVO", "Sistema");

                        return "EVENTO_CALDEIRAO_ATIVO\n" + resultado;
                    }

                    return "ATUALIZACAO " + resultado;
                }

                // Jogar pão no caldeirão
                if ((acao.equals("jogar") || acao.equals("colocar")) &&
                        comando.contains("pao") && comando.contains("caldeirao")) {

                    if (!jogador.isTemPaoMofado()) {
                        return "ERRO Você não tem pão mofado.";
                    }

                    String resultado = moinho.jogarPaoNoCaldeirao();
                    if (resultado.contains("chave")) {
                        jogador.receberChave();
                        EstadoGlobal.getInstance().setChaveObtida(true);
                        return "EVENTO_CHAVE_OBTIDA\n" + resultado;
                    }

                    return "ATUALIZACAO " + resultado;
                }
            }

            if ((acao.equals("voltar") || acao.equals("ir") || acao.equals("sair")) &&
                    (comando.contains("fazenda") || comando.contains("sair") || acao.equals("voltar"))) {
                return "TRANSICAO_FAZENDA";
            }
        }

        // COMANDOS BÁSICOS (verificados por último)
        if (acao.equals("inventario") || acao.equals("inventário") || acao.equals("inv")) {
            return "ATUALIZACAO " + jogador.InventarioString();
        }

        if (acao.equals("observar") || acao.equals("olhar")
                || acao.equals("olhar") && !comando.contains("fechadura") && !comando.contains("porta")) {

            // Capítulo 3: Mostrar porta secreta no Hall se caldeirão estiver ativo
            if (localAtual.getNome().equals("Hall") && EstadoGlobal.getInstance().isCaldeiraoAtivo()) {
                return "ATUALIZACAO " + localAtual.getDescricaoCompleta() +
                        " Uma passagem secreta se abriu na parede à esquerda, você pode ir para a 'galeria' através dela.";
            }

            // Capítulo 3: Mostrar moldura secreta na Galeria se caldeirão estiver ativo
            if (localAtual.getNome().equals("Galeria") && EstadoGlobal.getInstance().isCaldeiraoAtivo()) {
                return "ATUALIZACAO A galeria é a mesma porém uma das molduras se abriu como uma porta. Você pode ir para a 'casa principal' através da porta secreta à esquerda.";
            }

            return "ATUALIZACAO " + localAtual.getDescricaoCompleta();
        }

        // --- NAVEGAÇÃO AUTOMÁTICA ENTRE SALAS VIZINHAS ---
        // Verifica se é um comando de movimento para uma sala vizinha
        if (acao.equals("ir") || acao.equals("andar") || acao.equals("entrar")) {

            // Verificar comandos com múltiplas palavras primeiro
            if (comando.contains("casa principal")) {
                if (localAtual.getSaida("casa principal") != null) {
                    if (localAtual.getNome().equals("Galeria") && !EstadoGlobal.getInstance().isCaldeiraoAtivo()) {
                        return "ERRO Não há passagem disponível.";
                    }
                    return "TRANSICAO_HALL";
                }
            }

            // Procura por uma saída que corresponda ao comando
            for (String palavra : palavras) {
                if (localAtual.getSaida(palavra) != null) {
                    // Verificar se é uma saída condicional (porta secreta)
                    if ((palavra.equals("galeria") || palavra.equals("hall")) &&
                            (localAtual.getNome().equals("Galeria") || localAtual.getNome().equals("Hall"))) {
                        // Porta secreta só funciona se caldeirão estiver ativo
                        if (!EstadoGlobal.getInstance().isCaldeiraoAtivo()) {
                            return "ERRO Não há passagem disponível.";
                        }
                    }

                    // Navegação normal - retorna a transição
                    if (palavra.equals("galeria"))
                        return "TRANSICAO_GALERIA";
                    if (palavra.equals("hall"))
                        return "TRANSICAO_HALL";
                    if (palavra.equals("cozinha"))
                        return "TRANSICAO_COZINHA";
                    if (palavra.equals("fazenda"))
                        return "TRANSICAO_FAZENDA";
                    if (palavra.equals("moinho"))
                        return "TRANSICAO_MOINHO";
                }
            }
        }

        // Comandos especiais de volta
        if (acao.equals("voltar") || acao.equals("sair")) {
            if (localAtual.getNome().equals("Cozinha")) {
                return "TRANSICAO_HALL";
            }
        }

        return "ERRO Não entendi o que você quis dizer.";
    }

}