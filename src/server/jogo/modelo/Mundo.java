package server.jogo.modelo;

public class Mundo {

    Sala deposito;
    Sala galeria;
    Sala hall;
    Sala cozinha;
    Sala fazenda;
    Sala moinho;

    public Sala criarDeposito() {
        // Depósito
        deposito = new Sala("DEPOSITO", "Você está em um lugar escuro.", // Descrição Curta (inicial)
                "A sala está escura, você não consegue enxergar nada a não ser um pequeno interruptor a sua frente.");
        deposito.adicionarEstado("luzAcesa", false);
        deposito.adicionarEstado("portaAberta", false);

        deposito.adicionarDescricaoAlternativa("descricaoLuzAcesa",
                "O quarto parece uma espécie de depósito, diversas caixas espalhadas, uma escada largada no chão e uma porta com uma espécie de fechadora com 3 desenhos de animais, a porta aparenta ser um pouco estranha, parece ter algo escrito nela");

        deposito.adicionarDescricaoAlternativa("descricaoPortaAberta",
                "O depósito está iluminado. A porta se abriu revelando uma escada que leva para cima. Você pode subir a escada para sair deste lugar.");

        return deposito;
    }

    public Sala criarGaleria() {

        // --- GALERIA ---
        galeria = new Sala(
                "GALERIA",
                "Você está em uma sala grande que parece ser uma galeria, bem iluminada com diversos quadros estranhos.", // Descrição
                                                                                                                          // Curta
                "Olhando em volta você percebe que tem uma porta atrás de você, além dos quadros terem animais caricatos com formas estranhas. SYSTEM TIP: Andar para o quadro 1, Andar para o quadro 2... Andar para o quadro 6" // Descrição
                                                                                                                                                                                                                                  // Longa
        );

        galeria.adicionarEstado("portaAberta", false);
        galeria.adicionarDescricaoAlternativa("descricaoPortaAberta",
                "A galeria é a mesma, porém a porta atrás de você se abre automaticamente. Um som de aves e uma leve brisa sai dessa porta.");

        galeria.adicionarDetalhe("QUADRO 1",
                "Você anda até o quadro 1, ele aparenta ser um Pato roxo com um longo bico e concha em sua barriga.");
        galeria.adicionarDetalhe("QUADRO 2", "É um Rinoceronte magro, verde com grandes orelhas e um chifre quadrado.");
        galeria.adicionarDetalhe("QUADRO 3",
                "É um peixe azul, ela aparenta ter asas enormes e uma baita barba com um formato cilíndrico.");
        galeria.adicionarDetalhe("QUADRO 4",
                "É um avestruz laranja de chapéu e meia, seu chapéu tem formato de pirâmide.");
        galeria.adicionarDetalhe("QUADRO 5",
                "É um dinossauro cinza e feroz aparenta ser um dinossauro comum como qualquer outro que você já tenha visto em um filme porém ele está comendo um snickers, quem desenharia isso?");
        galeria.adicionarDetalhe("QUADRO 6",
                "Aparenta ser metade leão e metade girafa, é difícil reconhecer pois esse quadro tem uma rachadura no meio.");

        return galeria;
    }

    public Sala criarHall() {
        // --- HALL---
        hall = new Sala(
                "HALL",
                "Ao subir você percebe que estava em um porão, que dá em uma grande sala, parece um hall enorme.",
                "A sala é bem espaçosa e iluminada, à sua direita você consegue ver a borda de um balcão, uma possível cozinha, atrás duas portas que aparentam ser a saída.");

        return hall;
    }

    public Sala criarCozinha() {
        // --- COZINHA ---
        cozinha = new Sala(
                "COZINHA",
                "Uma cozinha americana simples, um balcão com vários utensílios de cozinha como facas alguns pratos na louça e uma geladeira.",
                "Em uma das paredes tem uma lista de compras, uma janela com grades de aço é vista em cima da pia.");

        cozinha.adicionarDetalhe("lista de compras",
                "Uma lista de compras, escrita como uma rotina: Todo dia às 9h eu tomo meu café 11h eu preparo meu arroz Enquanto preparo, preciso sempre ter minhas batatas e cenouras em mão Meio dia eu já comi as cenouras e batata nessa ordem Só então meu arroz está pronto Lembre-se de sempre tomar seu chá de hortelã antes de comer o arroz");

        cozinha.adicionarDetalhe("geladeira",
                "A geladeira está uma bagunça mas tudo parece estar bem conservado e parece ser bem recente. Algo te chama atenção, um pão mofado está no meio das comidas, ele aparenta estar há muito tempo por aqui onde ele está é possível ver um leve decaimento aparenta ser pesado eu deveria pegar isso?");

        return cozinha;
    }

    public Sala criarFazenda() {
        // --- FAZENDA ---
        fazenda = new Fazenda();

        return fazenda;
    }

    public Sala criarMoinho() {
        // --- MOINHO ---
        moinho = new Moinho();

        return moinho;
    }

    public void configurarNavegacao() {
        criarDeposito();
        criarGaleria();
        criarHall();
        criarCozinha();
        criarFazenda();
        criarMoinho();

        // Aqui mostra as configurações e mudanças que as salas recebem após alguns
        // eventos do jogo
        // tambem refere as sala vizinhas
        deposito.adicionarSaida("HALL", hall);
        hall.adicionarSaida("DEPOSITO", deposito);

        hall.adicionarSaida("COZINHA", cozinha);
        cozinha.adicionarSaida("HALL", hall);

        galeria.adicionarSaida("FAZENDA", fazenda);
        fazenda.adicionarSaida("GALERIA", galeria);

        fazenda.adicionarSaida("MOINHO", moinho);
        moinho.adicionarSaida("FAZENDA", fazenda);

    }

    public void HALLGALERIA() {
        hall.adicionarSaida("GALERIA", galeria);
        galeria.adicionarSaida("HALL", hall);
    }

    public Sala criarsala() {
        return criarDeposito();
    }

    public Sala getMundo() {
        return deposito;
    }

    public Sala getDeposito() {
        return deposito;
    }

    public Sala getGaleria() {
        return galeria;
    }

    public Sala getHall() {
        return hall;
    }

    public Sala getCozinha() {
        return cozinha;
    }

    public Sala getFazenda() {
        return fazenda;
    }

    public Sala getMoinho() {
        return moinho;
    }

}
