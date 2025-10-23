package server.jogo.modelo;

public class EstadoGlobal {
    private static EstadoGlobal instancia = null;

    // Estados do jogo
    private boolean portasAbertas = false;
    private boolean jogador1NoHall = false;
    private boolean jogador2NaFazenda = false;
    private boolean caldeiraoAtivo = false;
    private boolean molduraAberta = false;
    private boolean jogadoresNaMesmaSala = false;
    private boolean chaveObtida = false;

    // Localização atual dos jogadores
    private String localizacaoJ1 = "Depósito";
    private String localizacaoJ2 = "Fazenda";
    private boolean encontroJaNotificado = false;

    private EstadoGlobal() {
    }

    public static EstadoGlobal getInstance() {
        if (instancia == null) {
            instancia = new EstadoGlobal();
        }
        return instancia;
    }

    // Getters e Setters
    public boolean isPortasAbertas() {
        return portasAbertas;
    }

    public void setPortasAbertas(boolean portasAbertas) {
        this.portasAbertas = portasAbertas;
    }

    public boolean isJogador1NoHall() {
        return jogador1NoHall;
    }

    public void setJogador1NoHall(boolean jogador1NoHall) {
        this.jogador1NoHall = jogador1NoHall;
    }

    public boolean isJogador2NaFazenda() {
        return jogador2NaFazenda;
    }

    public void setJogador2NaFazenda(boolean jogador2NaFazenda) {
        this.jogador2NaFazenda = jogador2NaFazenda;
    }

    public boolean isCaldeiraoAtivo() {
        return caldeiraoAtivo;
    }

    public void setCaldeiraoAtivo(boolean caldeiraoAtivo) {
        this.caldeiraoAtivo = caldeiraoAtivo;
    }

    public boolean isMolduraAberta() {
        return molduraAberta;
    }

    public void setMolduraAberta(boolean molduraAberta) {
        this.molduraAberta = molduraAberta;
    }

    public boolean isJogadoresNaMesmaSala() {
        return jogadoresNaMesmaSala;
    }

    public void setJogadoresNaMesmaSala(boolean jogadoresNaMesmaSala) {
        this.jogadoresNaMesmaSala = jogadoresNaMesmaSala;
    }

    public boolean isChaveObtida() {
        return chaveObtida;
    }

    public void setChaveObtida(boolean chaveObtida) {
        this.chaveObtida = chaveObtida;
    }

    // Métodos para gerenciar localização dos jogadores
    public String getLocalizacaoJ1() {
        return localizacaoJ1;
    }

    public void setLocalizacaoJ1(String localizacaoJ1) {
        this.localizacaoJ1 = localizacaoJ1;
        verificarEncontro();
    }

    public String getLocalizacaoJ2() {
        return localizacaoJ2;
    }

    public void setLocalizacaoJ2(String localizacaoJ2) {
        this.localizacaoJ2 = localizacaoJ2;
        verificarEncontro();
    }

    public boolean isEncontroJaNotificado() {
        return encontroJaNotificado;
    }

    public void setEncontroJaNotificado(boolean encontroJaNotificado) {
        this.encontroJaNotificado = encontroJaNotificado;
    }

    // Verifica se os jogadores estão na mesma sala
    private void verificarEncontro() {
        boolean estaoNaMesmaSala = localizacaoJ1.equals(localizacaoJ2);

        if (estaoNaMesmaSala && !encontroJaNotificado) {
            jogadoresNaMesmaSala = true;
            // Enviar notificação de encontro
            try {
                SistemaNotificacao.getInstance().enviarEvento("ENCONTRO_JOGADORES", "Sistema");
                encontroJaNotificado = true;
            } catch (Exception e) {
                // Se SistemaNotificacao não estiver disponível, apenas marca o encontro
                System.out.println("⚠️ Erro ao enviar notificação de encontro: " + e.getMessage());
            }
        } else if (!estaoNaMesmaSala) {
            jogadoresNaMesmaSala = false;
            encontroJaNotificado = false; // Reset para permitir nova notificação quando se encontrarem novamente
        }
    }

    // Método para resetar o estado (útil para testes)
    public void reset() {
        portasAbertas = false;
        jogador1NoHall = false;
        jogador2NaFazenda = false;
        caldeiraoAtivo = false;
        molduraAberta = false;
        jogadoresNaMesmaSala = false;
        chaveObtida = false;
        localizacaoJ1 = "Depósito";
        localizacaoJ2 = "Fazenda";
        encontroJaNotificado = false;
    }
}