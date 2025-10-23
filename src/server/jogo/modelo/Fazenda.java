package server.jogo.modelo;

import java.util.Arrays;
import java.util.List;

public class Fazenda extends Sala {
    private static final List<String> INGREDIENTES_DISPONIVEIS = Arrays.asList(
            "tomate", "hortela", "cafe", "milho", "arroz", "espinafre",
            "batata", "pepino", "cenoura");

    public Fazenda() {
        super("Fazenda", "Um espaço claro que revela uma fazenda.",
                "O chão inteiro está coberto por trigo, é difícil de perceber mas isso é um quarto, " +
                        "as paredes pintadas lhe dão a impressão de ser uma fazenda de verdade, é possível " +
                        "observar ventiladores escondidos nos cantos. Além disso é possível verificar algumas " +
                        "plantações: tomate, hortelã, café, milho, arroz, espinafre, batata, pepino, cenoura " +
                        "e um moinho no centro.");
    }

    @Override
    public String observar() {
        return "O chão inteiro está coberto por trigo, é difícil de perceber mas isso é um quarto, " +
                "as paredes pintadas lhe dão a impressão de ser uma fazenda de verdade, é possível " +
                "observar ventiladores escondidos nos cantos. Além disso é possível verificar algumas " +
                "plantações: tomate, hortelã, café, milho, arroz, espinafre, batata, pepino, cenoura " +
                "e um moinho no centro.";
    }

    public String observarIngrediente(String ingrediente) {
        if (!INGREDIENTES_DISPONIVEIS.contains(ingrediente.toLowerCase())) {
            return "Não há " + ingrediente + " aqui.";
        }

        switch (ingrediente.toLowerCase()) {
            case "tomate":
                return "É um tomate vermelho e maduro.";
            case "hortela":
                return "É hortelã fresca e aromática.";
            case "cafe":
                return "São grãos de café torrados.";
            case "milho":
                return "É milho amarelo em espigas.";
            case "arroz":
                return "São grãos de arroz brancos.";
            case "espinafre":
                return "São folhas verdes de espinafre.";
            case "batata":
                return "É uma batata grande e saudável.";
            case "pepino":
                return "É um pepino verde e fresco.";
            case "cenoura":
                return "É uma cenoura laranja e crocante.";
            default:
                return "É " + ingrediente + ".";
        }
    }

    public boolean ingredienteDisponivel(String ingrediente) {
        return INGREDIENTES_DISPONIVEIS.contains(ingrediente.toLowerCase());
    }

    public List<String> getIngredientesDisponiveis() {
        return INGREDIENTES_DISPONIVEIS;
    }
}