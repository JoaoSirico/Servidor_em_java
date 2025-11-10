package Clientes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Aqui vai ficar as funções para lidar com os inputs digitados pelo cliente,
 * mensagens em geral É criado uma thread para ficar lendo as mensagens enviadas
 * pelo cliente no servidor
 */
public class ClientesHandler extends Thread {

    private Socket socket;
    private BufferedReader input;
    private ClientesMain cliente;

    /**
     * @param socket
     * @param cliente
     *
     * @todo
     * @param Tela -> Implementar tela que será utilizada pelos clientes
     */
    public ClientesHandler(Socket socket, ClientesMain cliente) throws IOException {
        this.socket = socket;
        this.cliente = cliente;
        //Tela aqui ó

        this.input = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));

        renderizarMensagem("Cliente Criado");
    }

    //Método do thread para rodar
    /**
     * A ideia da função é ficar lendo as mensagens que o usuário mandar através
     * do PrintWriter e Buffreader Por enquanto ela somente é implementada para
     * pegar o username do cliente
     */
    @Override
    public void run() {
        String mensagem;
        while (true) {
            try {
                //Caso o socket estiver ativo e for digitado alguma mensagem
                if (this.socket.isConnected() && this.input != null) {
                    mensagem = this.input.readLine();
                } else {
                    System.out.println("Não foi possível recuperar a mensagem do usuário em: ClientsHandler -> run()");
                    break;
                }

                //Caso a mensagem seja vazia, mas podemos só não imprimir nada e dar o break
                if (mensagem == null || mensagem.isEmpty()) {
                    System.out.println("Mensagem vazia");
                    break;
                }

                renderizarMensagem(mensagem);
            } catch (IOException e) {
                System.out.println("Erro ao pegar mensagem: " + e.getMessage());
                this.cliente.fecharConexao();
                break;
            }
        }
    }

    public void fecharSocket() {
        try {
            if (this.socket != null) {
                socket.close();
            }
            if (this.input != null) {
                input.close();
            }
        } catch (IOException e) {
            System.out.println("Erro ao tentar fechar cliente: " + e.getMessage());
        }
    }

    /**
     * Processa e renderiza a mensagem recebida do servidor
     * @param mensagem
     */
    public void renderizarMensagem(String mensagem) {
        System.out.println("Mensagem recebida: " + mensagem);

        // Verifica o tipo de mensagem
        if (mensagem.startsWith("JOGADORES:")) {
            atualizarListaJogadores(mensagem);
        } else if (mensagem.startsWith("INICIAR_JOGO:")) {
            iniciarJogo(mensagem);
        } else if (mensagem.startsWith("VEZ:")) {
            notificarVez(mensagem);
        } else if (mensagem.startsWith("JOGADA:")) {
            processarJogada(mensagem);
        } else if (mensagem.startsWith("VITORIA:")) {
            processarVitoria(mensagem);
        } else if (mensagem.startsWith("ERRO:")) {
            processarErro(mensagem);
        } else {
            // Outras mensagens
            System.out.println("Mensagem: " + mensagem);
        }
    }

    /**
     * Atualiza a lista de jogadores na tela do cliente
     * @param mensagem - Mensagem no formato "JOGADORES:nome1,nome2,nome3,"
     */
    private void atualizarListaJogadores(String mensagem) {
        try {
            // Remove o prefixo "JOGADORES:"
            String listaStr = mensagem.substring(10);

            // Separa os nomes dos jogadores
            String[] nomes = listaStr.split(",");

            // Cria uma lista de jogadores
            java.util.List<String> jogadores = new java.util.ArrayList<>();
            for (String nome : nomes) {
                if (!nome.trim().isEmpty()) {
                    jogadores.add(nome.trim());
                }
            }

            // Atualiza a tela se o cliente tiver uma tela Hub
            if (cliente.getTelaHub() != null) {
                cliente.getTelaHub().atualizarTela(jogadores);
            }

            System.out.println("Lista de jogadores atualizada: " + jogadores);
        } catch (Exception e) {
            System.out.println("Erro ao atualizar lista de jogadores: " + e.getMessage());
        }
    }

    /**
     * Inicia o jogo quando recebe mensagem do servidor
     * @param mensagem - Formato: "INICIAR_JOGO:linhas,colunas"
     */
    private void iniciarJogo(String mensagem) {
        try {
            String dados = mensagem.substring(13);
            String[] partes = dados.split(",");

            int linhas = Integer.parseInt(partes[0].trim());
            int colunas = Integer.parseInt(partes[1].trim());

            System.out.println("Iniciando jogo: " + linhas + "x" + colunas);

            // Cria a tela do jogo para este cliente
            cliente.iniciarTelaJogo(linhas, colunas);

        } catch (Exception e) {
            System.out.println("Erro ao iniciar jogo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Notifica de quem é a vez de jogar
     * @param mensagem - Formato: "VEZ:nomeJogador"
     */
    private void notificarVez(String mensagem) {
        try {
            String nomeJogador = mensagem.substring(4);
            System.out.println("Vez de: " + nomeJogador);

            // Notifica a tela do jogo
            cliente.notificarVez(nomeJogador);

        } catch (Exception e) {
            System.out.println("Erro ao notificar vez: " + e.getMessage());
        }
    }

    /**
     * Processa uma jogada recebida do servidor
     * @param mensagem - Formato: "JOGADA:nomeJogador,linha,coluna"
     */
    private void processarJogada(String mensagem) {
        try {
            String dados = mensagem.substring(7);
            String[] partes = dados.split(",");

            String nomeJogador = partes[0].trim();
            int linha = Integer.parseInt(partes[1].trim());
            int coluna = Integer.parseInt(partes[2].trim());

            System.out.println("Jogada de " + nomeJogador + ": [" + linha + "][" + coluna + "]");

            // Atualiza a tela do jogo
            cliente.processarJogada(nomeJogador, linha, coluna, false);

        } catch (Exception e) {
            System.out.println("Erro ao processar jogada: " + e.getMessage());
        }
    }

    /**
     * Processa vitória de um jogador
     * @param mensagem - Formato: "VITORIA:nomeJogador,linha,coluna"
     */
    private void processarVitoria(String mensagem) {
        try {
            String dados = mensagem.substring(8);
            String[] partes = dados.split(",");

            String nomeJogador = partes[0].trim();
            int linha = Integer.parseInt(partes[1].trim());
            int coluna = Integer.parseInt(partes[2].trim());

            System.out.println(nomeJogador + " encontrou o tesouro em [" + linha + "][" + coluna + "]!");

            // Atualiza a tela do jogo
            cliente.processarJogada(nomeJogador, linha, coluna, true);

        } catch (Exception e) {
            System.out.println("Erro ao processar vitória: " + e.getMessage());
        }
    }

    /**
     * Processa mensagens de erro
     * @param mensagem - Formato: "ERRO:descrição"
     */
    private void processarErro(String mensagem) {
        String erro = mensagem.substring(5);
        System.out.println("Erro do servidor: " + erro);
        cliente.mostrarErro(erro);
    }

}
