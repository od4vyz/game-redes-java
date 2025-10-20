package Jogo.Modelo;

import java.util.ArrayList;
import java.util.List;

public class Moinho extends Sala {
    private boolean colherPegue;
    private boolean foicePegue;
    private List<String> ingredientesNoCaldeirao;
    private boolean caldeiraoAtivo;

    public Moinho() {
        super("Moinho", "O moinho é espaçoso, um caldeirão fervente está no meio dele.",
                "Ao lado do caldeirão é visto uma colher gigante, usada provavelmente para mexer esse caldeirão, " +
                        "e uma foice está em um suporte na parede, uma placa ao lado do caldeirão diz 'comer'.");
        this.colherPegue = false;
        this.foicePegue = false;
        this.ingredientesNoCaldeirao = new ArrayList<>();
        this.caldeiraoAtivo = false;
    }

    @Override
    public String observar() {
        StringBuilder sb = new StringBuilder();
        sb.append("Ao lado do caldeirão é visto ");

        if (!colherPegue) {
            sb.append("uma colher gigante, usada provavelmente para mexer esse caldeirão, ");
        }

        if (!foicePegue) {
            sb.append("e uma foice está em um suporte na parede, ");
        }

        sb.append("uma placa ao lado do caldeirão diz 'comer'.");

        if (caldeiraoAtivo) {
            sb.append(
                    "\nO caldeirão está fervendo intensamente - pode ser útil para derreter coisas duras mas não parece forte o suficiente para derreter metal.");
        }

        return sb.toString();
    }

    public String pegarColher() {
        if (colherPegue) {
            return "Você já pegou a colher.";
        }
        colherPegue = true;
        return "Você pegou a colher gigante.";
    }

    public String pegarFoice() {
        if (foicePegue) {
            return "Você já pegou a foice.";
        }
        foicePegue = true;
        return "Você pegou a foice. Agora você pode coletar ingredientes na fazenda usando 'usar foice [ingrediente]'.";
    }

    public String adicionarIngrediente(String ingrediente) {
        // Sequência correta: café, cenoura, batata, hortelã, arroz
        String[] sequenciaCorreta = { "cafe", "cenoura", "batata", "hortela", "arroz" };

        String ingredienteEsperado = sequenciaCorreta[ingredientesNoCaldeirao.size()];

        if (ingrediente.toLowerCase().equals(ingredienteEsperado)) {
            ingredientesNoCaldeirao.add(ingrediente);

            if (ingredientesNoCaldeirao.size() == sequenciaCorreta.length) {
                caldeiraoAtivo = true; // esse evento o servidor precisa enviar para os dois jogadores
                return "Você colocou " + ingrediente
                        + " no caldeirão. O caldeirão ficou mais intenso - pode ser útil para derreter coisas duras mas não parece forte o suficiente para derreter metal seria mais seguro JOGAR algo nele.";
            } else {
                return "Você colocou " + ingrediente + " no caldeirão. Continua fervendo...";
            }
        } else {
            // Ingrediente errado - resetar
            ingredientesNoCaldeirao.clear();
            return "Você colocou " + ingrediente
                    + " no caldeirão, uma pequena fumaça subiu e o caldo voltou a ficar transparente, preciso colocá-los novamente.";
        }
    }

    public String jogarPaoNoCaldeirao() {
        boolean caldeiraoEstaAtivo = caldeiraoAtivo || EstadoGlobal.getInstance().isCaldeiraoAtivo();

        if (!caldeiraoEstaAtivo) {
            return "O caldeirão não está quente o suficiente. Preciso colocar os ingredientes corretos primeiro.";
        }

        return "Você jogou o pão no caldeirão, o pão começa a derreter e o líquido começa a vaporizar de forma tão rápida a ponto de o pão sumir, o líquido secar e sobrar apenas uma chave.";
    }

    public boolean isFoicePegue() {
        return foicePegue;
    }

    public boolean isColherPegue() {
        return colherPegue;
    }

    public boolean isCaldeiraoAtivo() {
        return caldeiraoAtivo;
    }

    public List<String> getIngredientesNoCaldeirao() {
        return new ArrayList<>(ingredientesNoCaldeirao);
    }
}