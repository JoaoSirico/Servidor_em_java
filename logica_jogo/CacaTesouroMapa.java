package logica_jogo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * Cria automaticamente a tela do caça ao tesouro,
 * basicamente é criado uma tela onde vão ter vários botões e um deles vai ser o tesouro
 * se o jogador clicar e errar o botão é desativado e fica um X
 * se o jogador acertar o botão troca de cor e aparece uma janela perguntando se quer jogar de novo
 * se sim o jogo é reiniciado e o tesouro é randomizado novamente
 * se não o jogo acaba e fecha
 * 
 * @author Joao Pedro
 */
public class CacaTesouroMapa extends JFrame {
    private JButton[][] botoes;
    private int linhas;
    private int colunas;
    private int hgap = 2;
    private int vgap = 2;
    private Point tesouro;
    
    public CacaTesouroMapa(int linhas, int colunas) {
        this.linhas = linhas;
        this.colunas = colunas;
        
        initComponents();
        criarMapa();
    }
    
    private void initComponents() {
        setTitle("Caça ao Tesouro");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
    }
    
    public void criarMapa() {
        JPanel painelMapa = new JPanel();
        GridLayout gridLayout = new GridLayout(linhas, colunas, hgap, vgap);
        painelMapa.setLayout(gridLayout);
        
        botoes = new JButton[linhas][colunas];
        
        // Coloca o tesouro em um ponto aleatório
        tesouro = new Point(
                (int) (Math.random() * linhas),
                (int) (Math.random() * colunas)
        );
        
        // Debug: mostrar posição do tesouro no console
        System.out.println("Tesouro está em: [" + tesouro.x + "][" + tesouro.y + "]");
        
        for (int l = 0; l < linhas; l++) {
            for (int c = 0; c < colunas; c++) {
                final int linha = l;
                final int coluna = c;
                
                botoes[l][c] = new JButton("?");
                botoes[l][c].setPreferredSize(new Dimension(50, 50));
                
                botoes[l][c].addActionListener(action -> {
                    verificarPosicao(linha, coluna);
                });
                
                painelMapa.add(botoes[l][c]);
            }
        }
        
        add(painelMapa, BorderLayout.CENTER);
        
        pack();
        setLocationRelativeTo(null);
    }
    
    public void verificarPosicao(int linha, int coluna) {
        if (linha == tesouro.x && coluna == tesouro.y) {
            botoes[linha][coluna].setText("Tesouro!");
            botoes[linha][coluna].setBackground(Color.YELLOW);
            JOptionPane.showMessageDialog(this, "Tesouro encontrado!");
            
            int opcao = JOptionPane.showConfirmDialog(this, 
                    "Deseja jogar novamente?", 
                    "Parabens!", 
                    JOptionPane.YES_NO_OPTION);
            
            if (opcao == JOptionPane.YES_OPTION) {
                reiniciarJogo();
            } else {
                System.exit(0);
            }
        } else {
            botoes[linha][coluna].setText("X");
            botoes[linha][coluna].setEnabled(false);
        }
    }
    
    private void reiniciarJogo() {
        getContentPane().removeAll();
        
        criarMapa();
        
        revalidate();
        repaint();
    }
    
    public static void InstanciarJogo(int linhas, int colunas) {
        try {
            java.awt.EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    CacaTesouroMapa jogo = new CacaTesouroMapa(linhas, colunas);
                    jogo.setVisible(true);
                }
            });
        } catch (Exception e) {
        }
    }
    
    
//    public static void main(String[] args) {
//        java.awt.EventQueue.invokeLater(() -> {
//            CacaTesouro jogo = new CacaTesouro();
//            jogo.setVisible(true);
//        });
//    }
}