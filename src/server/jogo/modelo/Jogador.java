package server.jogo.modelo;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

public class Jogador {
    String nome;
    Sala salaAtual;
    List<Item> inventario;
    private Set<String> ingredientesPegos;
    private boolean temFoice;
    private boolean temColher;
    private boolean temPaoMofado;
    private boolean temChave;

    public Jogador(String nome, Sala salaInicial) {
        this.nome = nome;
        this.salaAtual = salaInicial;
        this.inventario = new ArrayList<>();
        this.ingredientesPegos = new HashSet<>();
        this.temFoice = false;
        this.temColher = false;
        this.temPaoMofado = false;
        this.temChave = false;
    }

    public String getNome() {
        return nome;
    }

    public Sala getSalaAtual() {
        return salaAtual;
    }

    public void setSalaAtual(Sala novaSala) {
        this.salaAtual = novaSala;
    }

    public Sala mover(String direcao) {
        Sala proximaSala = salaAtual.getSaida(direcao);
        if (proximaSala != null) {
            salaAtual = proximaSala;
        }
        return salaAtual;
    }

    public String pegarItem(String nomeItem) {
        for (Item item : salaAtual.getItens()) {
            if (item.getNome().equalsIgnoreCase(nomeItem)) {
                inventario.add(item);
                salaAtual.pegarItem(item);
                return "Você pegou " + item.getNome();
            }
        }
        return "Item não encontrado na sala.";
    }

    public String InventarioString() {
        StringBuilder sb = new StringBuilder("Inventário:\n");
        for (Item item : inventario) {
            sb.append("- ").append(item.getNome()).append("\n");
        }

        if (temFoice)
            sb.append("- Foice\n");
        if (temColher)
            sb.append("- Colher Gigante\n");
        if (temPaoMofado)
            sb.append("- Pão Mofado\n");
        if (temChave)
            sb.append("- Chave\n");

        if (!ingredientesPegos.isEmpty()) {
            sb.append("Ingredientes coletados:\n");
            for (String ingrediente : ingredientesPegos) {
                sb.append("- ").append(ingrediente).append("\n");
            }
        }

        return sb.toString();
    }

    // Métodos para foice e ingredientes
    public String pegarFoice() {
        if (temFoice) {
            return "Você já tem a foice.";
        }
        temFoice = true;
        return "Você pegou a foice. Agora você pode coletar ingredientes na fazenda usando 'usar foice [ingrediente]'.";
    }

    public String pegarColher() {
        if (temColher) {
            return "Você já tem a colher.";
        }
        temColher = true;
        return "Você pegou a colher gigante.";
    }

    public String pegarPaoMofado() {
        if (temPaoMofado) {
            return "Você já tem o pão mofado.";
        }
        temPaoMofado = true;
        return "Você pegou um pão mofado.";
    }

    public String coletarIngrediente(String ingrediente) {
        if (!temFoice) {
            return "Você precisa de uma foice para coletar ingredientes.";
        }

        if (ingredientesPegos.contains(ingrediente)) {
            return "Você já pegou esse ingrediente.";
        }

        ingredientesPegos.add(ingrediente);
        return "Você pegou " + ingrediente + ".";
    }

    public boolean temIngrediente(String ingrediente) {
        return ingredientesPegos.contains(ingrediente);
    }

    public boolean isTemFoice() {
        return temFoice;
    }

    public boolean isTemColher() {
        return temColher;
    }

    public boolean isTemPaoMofado() {
        return temPaoMofado;
    }

    public boolean isTemChave() {
        return temChave;
    }

    public void receberChave() {
        temChave = true;
    }

    public Set<String> getIngredientesPegos() {
        return new HashSet<>(ingredientesPegos);
    }
}
