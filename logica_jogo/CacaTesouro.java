package logica_jogo;

import javax.swing.JOptionPane;

/**
 * Classe responsável por gerenciar o jogo de Caça ao Tesouro
 *
 * @author J
 */
public class CacaTesouro {

    private static int linhas;
    private static int colunas;
    private static boolean jogoInstanciado = false;

    /**
     * Instancia um novo jogo com as dimensões especificadas
     *
     * @param linhas Número de linhas do tabuleiro
     * @param colunas Número de colunas do tabuleiro
     */
    public static void InstanciarJogo(int linhas, int colunas) {
        try {
            System.out.println("Criando jogo com " + linhas + " linhas e " + colunas + " colunas");

            CacaTesouro.linhas = linhas;
            CacaTesouro.colunas = colunas;
            CacaTesouro.jogoInstanciado = true;

            // Mensagem de sucesso
            String mensagem = String.format(
                "Jogo criado com sucesso!\n\n" +
                "Dimensões: %dx%d\n" +
                "Total de células: %d\n\n" +
                "O jogo está pronto para começar!",
                linhas, colunas, linhas * colunas
            );

            JOptionPane.showMessageDialog(null, mensagem, "Jogo Criado", JOptionPane.INFORMATION_MESSAGE);

            System.out.println("Jogo instanciado com sucesso!");

        } catch (Exception e) {
            System.err.println("Erro ao instanciar o jogo: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                null,
                "Erro ao criar o jogo: " + e.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Retorna o número de linhas do jogo
     * @return número de linhas
     */
    public static int getLinhas() {
        return linhas;
    }

    /**
     * Retorna o número de colunas do jogo
     * @return número de colunas
     */
    public static int getColunas() {
        return colunas;
    }

    /**
     * Verifica se o jogo foi instanciado
     * @return true se o jogo foi instanciado, false caso contrário
     */
    public static boolean isJogoInstanciado() {
        return jogoInstanciado;
    }

    /**
     * Reseta o jogo
     */
    public static void resetarJogo() {
        linhas = 0;
        colunas = 0;
        jogoInstanciado = false;
        System.out.println("Jogo resetado");
    }
}
