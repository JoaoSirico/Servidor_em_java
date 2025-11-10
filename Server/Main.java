package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import logica_jogo.GerenciadorJogo;

/**
 * Cria o servidor na rede Cria as conexões que os clientes podem ter
 *
 */
public class Main extends Thread {

    private ServerSocket server;
    private List<Conexoes> clientes;
    private String hostName;

    private TelaServidor.Main tela;

    private boolean serverRodando = true;

    // Gerenciador do jogo
    private GerenciadorJogo gerenciadorJogo;

    public Main(int porta, String username, TelaServidor.Main tela) throws IOException {
        //Cria a conexão do servidor
        this.server = new ServerSocket(porta);
        System.out.println("Server rodando na porta: " + porta);
        //Cria a lista de clientes que se conectaram no servidor
        this.clientes = new ArrayList<>();

        //Pega o nome do host
        this.hostName = username;
        
        //Seta a tela clobalmente para poder ser reutilizada
        this.tela = tela;
    }

    /**
     * Sempre que um socket for conectado a rede ele vai passar pela classe
     * conexão para que seja possível adicionar o "Escutador" de entrada (input)
     * e saída (output) dele depois ele é enviado apra o handlerConexoes
     */
    @Override
    public void run() {
        Socket socket;
        while (serverRodando && !Thread.currentThread().isInterrupted()) {
            try {
                //Enquanto o servidor estiver aceitando conexões (Socket)
                socket = this.server.accept();

                // Verifica novamente após accept() para não processar novas conexões se servidor está fechando
                if (!serverRodando) {
                    System.out.println("Servidor está fechando, rejeitando nova conexão");
                    socket.close();
                    break;
                }

                //Cria as conexões dentro de um socket chamado de cliente
                Conexoes cliente = new Conexoes(socket);
                addCliente(cliente);

                String username = cliente.getInput().readLine();
                if (!username.isEmpty()) {
                    System.out.println("Novo cliente criado com sucesso");
                    cliente.setUsername(username);
                    this.tela.atualizarTela(this.getClientes());
                    // Envia lista atualizada para todos os clientes
                    broadcastListaJogadores();
                } else {
                    System.out.println("Sem user name no cliente");
                }

                (new HandlerConexoes(cliente, this)).start();
            } catch (IOException e) {
                // Se o servidor foi fechado intencionalmente, não é um erro
                if (serverRodando) {
                    System.out.println("Erro ao rodar a thread do servidor: " + e.getMessage());
                } else {
                    System.out.println("Servidor foi encerrado");
                }
                break;
            } catch (Exception e) {
                System.out.println("Exception ao rodar a thread do servidor: " + e.getMessage());
                fecharServidor();
                break;
            }
        }
        System.out.println("Loop do servidor encerrado");
    }

    //Adiciona ao array o cliente e suas funções
    public void addCliente(Conexoes cliente) {
        this.clientes.add(cliente);
    }

    //Remnove do array o cliente
    public void removerCliente(Conexoes cliente) {
        this.clientes.remove(cliente);
        // Atualiza a lista de jogadores para todos
        broadcastListaJogadores();
    }

    public List getClientes() {
        return clientes;
    }

    /**
     * Envia a lista de jogadores conectados para todos os clientes
     */
    public void broadcastListaJogadores() {
        StringBuilder listaJogadores = new StringBuilder("JOGADORES:");
        for (Conexoes cliente : clientes) {
            if (cliente.getUsername() != null && !cliente.getUsername().isEmpty()) {
                listaJogadores.append(cliente.getUsername()).append(",");
            }
        }

        String mensagem = listaJogadores.toString();
        System.out.println("Broadcasting lista de jogadores: " + mensagem);

        // Envia para todos os clientes conectados
        for (Conexoes cliente : clientes) {
            try {
                cliente.enviarMensagem(mensagem);
            } catch (Exception e) {
                System.out.println("Erro ao enviar lista para cliente: " + e.getMessage());
            }
        }
    }

    /**
     * Envia uma mensagem para todos os clientes
     */
    public void broadcastMensagem(String mensagem) {
        System.out.println("Broadcasting: " + mensagem);
        for (Conexoes cliente : clientes) {
            try {
                cliente.enviarMensagem(mensagem);
            } catch (Exception e) {
                System.out.println("Erro ao enviar mensagem para cliente: " + e.getMessage());
            }
        }
    }

    /**
     * Inicia o jogo com as dimensões especificadas
     */
    public void iniciarJogo(int linhas, int colunas) {
        if (clientes.size() < 2) {
            System.out.println("Não é possível iniciar o jogo com menos de 2 jogadores");
            return;
        }

        gerenciadorJogo = new GerenciadorJogo(clientes, linhas, colunas);
        gerenciadorJogo.iniciarJogo();

        // Notifica todos os clientes para iniciar o jogo
        String mensagemInicio = String.format("INICIAR_JOGO:%d,%d", linhas, colunas);
        broadcastMensagem(mensagemInicio);

        // Notifica quem é o primeiro jogador
        notificarVezDoJogador();
    }

    /**
     * Processa uma jogada recebida de um cliente
     */
    public void processarJogada(Conexoes jogador, int linha, int coluna) {
        if (gerenciadorJogo == null || !gerenciadorJogo.isJogoEmAndamento()) {
            jogador.enviarMensagem("ERRO:Jogo não está em andamento");
            return;
        }

        if (!gerenciadorJogo.isVezDoJogador(jogador)) {
            jogador.enviarMensagem("ERRO:Não é sua vez de jogar");
            return;
        }

        boolean acertou = gerenciadorJogo.processarJogada(linha, coluna);

        if (acertou) {
            // Notifica todos que o jogador encontrou o tesouro
            String mensagemVitoria = String.format("VITORIA:%s,%d,%d",
                jogador.getUsername(), linha, coluna);
            broadcastMensagem(mensagemVitoria);
        } else {
            // Notifica todos sobre a jogada errada
            String mensagemJogada = String.format("JOGADA:%s,%d,%d",
                jogador.getUsername(), linha, coluna);
            broadcastMensagem(mensagemJogada);

            // Notifica o próximo jogador
            notificarVezDoJogador();
        }
    }

    /**
     * Notifica qual jogador deve jogar agora
     */
    private void notificarVezDoJogador() {
        if (gerenciadorJogo == null) {
            return;
        }

        Conexoes jogadorAtual = gerenciadorJogo.getJogadorAtual();
        if (jogadorAtual != null) {
            String mensagem = "VEZ:" + jogadorAtual.getUsername();
            broadcastMensagem(mensagem);
        }
    }

    /**
     * Retorna o gerenciador do jogo
     */
    public GerenciadorJogo getGerenciadorJogo() {
        return gerenciadorJogo;
    }

    @Override
    public void finalize() throws Throwable {
        try {
            fecharServidor();
        } catch (Exception e) {
        } finally {
            super.finalize();

        }
    }

    /**
     * Tenta fechar todas as conexões dos clientes antes de fechar a conexão do
     * servidor
     */
    public void fecharServidor() {
        // Define a flag ANTES de fechar o servidor para parar de aceitar novas conexões
        serverRodando = false;

        for (Conexoes cliente : clientes) {
            try {
                if (!cliente.getSocket()) {
                    cliente.encerrarConexao();
                }
            } catch (Exception e) {
                System.out.println("Erro ao fechar todas as conexões abertas no servidor: " + e.getMessage());
            }
        }

        try {
            if (server != null && !server.isClosed()) {
                server.close();
            }
        } catch (IOException e) {
            System.out.println("Erro ao tentar fechar todo o servidor: " + e.getMessage());
        }


        System.out.println("Servidor foi fechado com sucesso");
    }
}
