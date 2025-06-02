package src;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.Timer;
import org.jfree.chart.*;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import java.util.Arrays;

class Enquete {
    private String titulo;
    private List<String> opcoes;
    private Map<String, Set<String>> votos;
    private boolean votacaoEncerrada;
    private Set<String> participantesQueVotaram;

    public Enquete(String titulo, List<String> opcoes) {
        this.titulo = titulo;
        this.opcoes = new ArrayList<>(opcoes);
        this.votos = new HashMap<>();
        this.votacaoEncerrada = false;
        this.participantesQueVotaram = new HashSet<>();
        for (String opcao : opcoes) {
            votos.put(opcao, new HashSet<>());
        }
    }

    public void editarEnquete(String novoTitulo, List<String> novasOpcoes) {
        this.titulo = novoTitulo;
        this.opcoes = new ArrayList<>(novasOpcoes);
        this.votos.clear();
        this.participantesQueVotaram.clear();
        this.votacaoEncerrada = false;
        for (String opcao : novasOpcoes) {
            votos.put(opcao, new HashSet<>());
        }
    }

    public String getTitulo() {
        return titulo;
    }

    public List<String> getOpcoes() {
        return new ArrayList<>(opcoes);
    }

    public void adicionarVoto(String participante, String opcao) {
        if (!votacaoEncerrada && opcoes.contains(opcao)) {
            if (participantesQueVotaram.contains(participante)) {
                throw new IllegalStateException("Você não pode mais votar, já registramos seu voto.");
            }
            
            for (Set<String> votantes : votos.values()) {
                votantes.remove(participante);
            }
            votos.get(opcao).add(participante);
            participantesQueVotaram.add(participante);
        }
    }

    public void encerrarVotacao() {
        votacaoEncerrada = true;
    }

    public boolean isVotacaoEncerrada() {
        return votacaoEncerrada;
    }

    public Map<String, Integer> getResultados() {
        Map<String, Integer> resultados = new LinkedHashMap<>();
        for (String opcao : opcoes) {
            resultados.put(opcao, votos.get(opcao).size());
        }
        return resultados;
    }

    public int getTotalVotos() {
        int total = 0;
        for (Set<String> votantes : votos.values()) {
            total += votantes.size();
        }
        return total;
    }

    public Map<String, Set<String>> getVotos() {
        return Collections.unmodifiableMap(votos);
    }
}

class PainelCriarEnquete extends JPanel {
    private static final JPanel painelEnquetes = null;
    private JTextField txtTitulo;
    private DefaultListModel<String> opcoesModel;
    private JList<String> listaOpcoes;
    private JButton btnAdicionarOpcao;
    private JButton btnRemoverOpcao;
    private JButton btnCriarEnquete;
    private JButton btnEditarEnquete;
    private List<Enquete> enquetes;
    private JPanel painelPrincipal;
    private Enquete enqueteSendoEditada;

    public PainelCriarEnquete(List<Enquete> enquetes, JPanel painelPrincipal, Enquete enqueteParaEditar) {
        this.enquetes = enquetes;
        this.painelPrincipal = painelPrincipal;
        this.enqueteSendoEditada = enqueteParaEditar;

        setLayout(new BorderLayout());
        setBackground(new Color(84, 106, 232));

        JPanel painelFormulario = new JPanel();
        painelFormulario.setLayout(new BoxLayout(painelFormulario, BoxLayout.Y_AXIS));
        painelFormulario.setBackground(new Color(154, 168, 245));
        painelFormulario.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblTitulo = new JLabel("Título da Enquete:");
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtTitulo = new JTextField();
        txtTitulo.setMaximumSize(new Dimension(Integer.MAX_VALUE, txtTitulo.getPreferredSize().height));

        painelFormulario.add(lblTitulo);
        painelFormulario.add(txtTitulo);
        painelFormulario.add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel lblOpcoes = new JLabel("Opções de Votação:");
        lblOpcoes.setAlignmentX(Component.LEFT_ALIGNMENT);

        opcoesModel = new DefaultListModel<>();
        listaOpcoes = new JList<>(opcoesModel);
        listaOpcoes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollOpcoes = new JScrollPane(listaOpcoes);
        scrollOpcoes.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel painelBotoesOpcoes = new JPanel();
        painelBotoesOpcoes.setLayout(new BoxLayout(painelBotoesOpcoes, BoxLayout.X_AXIS));
        painelBotoesOpcoes.setAlignmentX(Component.LEFT_ALIGNMENT);
        painelBotoesOpcoes.setOpaque(false);

        btnAdicionarOpcao = new JButton("Adicionar Opção");
        btnAdicionarOpcao.setBackground(null);
        btnAdicionarOpcao.addActionListener(e -> adicionarOpcao());

        btnRemoverOpcao = new JButton("Remover Opção");
        btnRemoverOpcao.setBackground(null);
        btnRemoverOpcao.addActionListener(e -> removerOpcao());

        painelBotoesOpcoes.add(btnAdicionarOpcao);
        painelBotoesOpcoes.add(Box.createRigidArea(new Dimension(10, 0)));
        painelBotoesOpcoes.add(btnRemoverOpcao);

        painelFormulario.add(lblOpcoes);
        painelFormulario.add(Box.createRigidArea(new Dimension(0, 5)));
        painelFormulario.add(scrollOpcoes);
        painelFormulario.add(Box.createRigidArea(new Dimension(0, 10)));
        painelFormulario.add(painelBotoesOpcoes);
        painelFormulario.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.setOpaque(false);

        btnCriarEnquete = new JButton(enqueteSendoEditada == null ? "Criar Enquete" : "Salvar Alterações");
        btnCriarEnquete.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnCriarEnquete.setBackground(null);
        btnCriarEnquete.addActionListener(e -> {
            if (enqueteSendoEditada == null) {
                criarEnquete();
            } else {
                editarEnquete();
            }
        });

        if (enqueteSendoEditada != null) {
            btnEditarEnquete = new JButton("Cancelar");
            btnEditarEnquete.setBackground(null);
            btnEditarEnquete.addActionListener(e -> cancelarEdicao());
            buttonPanel.add(btnEditarEnquete);
            buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
            
            txtTitulo.setText(enqueteSendoEditada.getTitulo());
            opcoesModel.clear();
            for (String opcao : enqueteSendoEditada.getOpcoes()) {
                opcoesModel.addElement(opcao);
            }
        }

        buttonPanel.add(btnCriarEnquete);
        painelFormulario.add(buttonPanel);

        add(painelFormulario, BorderLayout.CENTER);
    }

    private void adicionarOpcao() {
        String opcao = JOptionPane.showInputDialog(this, "Digite a opção:");
        if (opcao != null && !opcao.trim().isEmpty()) {
            opcoesModel.addElement(opcao.trim());
        }
    }

    private void removerOpcao() {
        int selectedIndex = listaOpcoes.getSelectedIndex();
        if (selectedIndex != -1) {
            opcoesModel.remove(selectedIndex);
        }
    }

    private void criarEnquete() {
        String titulo = txtTitulo.getText().trim();
        if (titulo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Digite um título para a enquete!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (opcoesModel.size() < 2) {
            JOptionPane.showMessageDialog(this, "Adicione pelo menos 2 opções para a enquete!", "Erro",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<String> opcoes = new ArrayList<>();
        for (int i = 0; i < opcoesModel.size(); i++) {
            opcoes.add(opcoesModel.getElementAt(i));
        }

        Enquete novaEnquete = new Enquete(titulo, opcoes);
        enquetes.add(novaEnquete);
        atualizarListaEnquetes();

        Window window = SwingUtilities.getWindowAncestor(this);
        if (window != null) {
            window.dispose();
        }
    }

    private void editarEnquete() {
        String titulo = txtTitulo.getText().trim();
        if (titulo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Digite um título para a enquete!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (opcoesModel.size() < 2) {
            JOptionPane.showMessageDialog(this, "Adicione pelo menos 2 opções para a enquete!", "Erro",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<String> opcoes = new ArrayList<>();
        for (int i = 0; i < opcoesModel.size(); i++) {
            opcoes.add(opcoesModel.getElementAt(i));
        }

        enqueteSendoEditada.editarEnquete(titulo, opcoes);
        atualizarListaEnquetes();

        Window window = SwingUtilities.getWindowAncestor(this);
        if (window != null) {
            window.dispose();
        }
    }

    private void cancelarEdicao() {
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window != null) {
            window.dispose();
        }
    }

    private void atualizarListaEnquetes() {
        painelPrincipal.removeAll();

        for (Enquete enquete : enquetes) {
            JPanel panelEnquete = new JPanel();
            panelEnquete.setLayout(new BorderLayout());
            panelEnquete.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            panelEnquete.setBackground(new Color(200, 200, 255));
            panelEnquete.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

            JLabel lblTitulo = new JLabel(enquete.getTitulo(), SwingConstants.CENTER);
            lblTitulo.setFont(new Font("Arial", Font.BOLD, 14));

            JPanel panelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
            panelBotoes.setOpaque(false);

            JButton btnVisualizar = new JButton(enquete.isVotacaoEncerrada() ? "Visualizar Resultados" : "Visualizar");
            btnVisualizar.setBackground(null);
            btnVisualizar.addActionListener(e -> visualizarEnquete(enquete));

            panelBotoes.add(btnVisualizar);

            if (!enquete.isVotacaoEncerrada()) {
                JButton btnIniciar = new JButton("Iniciar Votação");
                btnIniciar.setBackground(null);
                btnIniciar.addActionListener(e -> iniciarVotacao(enquete));
                panelBotoes.add(btnIniciar);
            }

            panelEnquete.add(lblTitulo, BorderLayout.CENTER);
            panelEnquete.add(panelBotoes, BorderLayout.SOUTH);

            painelPrincipal.add(panelEnquete);
            painelPrincipal.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        painelPrincipal.revalidate();
        painelPrincipal.repaint();
    }

    private void visualizarEnquete(Enquete enquete) {
        JDialog dialog = new JDialog();
        dialog.setTitle("Resultados: " + enquete.getTitulo());
        dialog.setSize(800, 600);
        dialog.setModal(false);
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(this);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(new Color(154, 168, 245));

        JPanel panelResultados = new JPanel(new BorderLayout());
        panelResultados.setBackground(new Color(154, 168, 245));
        panelResultados.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        JLabel lblTitulo = new JLabel("Enquete: " + enquete.getTitulo());
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblStatus = new JLabel("Status: " + (enquete.isVotacaoEncerrada() ? "Encerrada" : "Em andamento"));
        lblStatus.setFont(new Font("Arial", Font.BOLD, 14));
        lblStatus.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTotal = new JLabel("Total de votos: " + enquete.getTotalVotos());
        lblTotal.setFont(new Font("Arial", Font.PLAIN, 14));
        lblTotal.setAlignmentX(Component.CENTER_ALIGNMENT);

        infoPanel.add(lblTitulo);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        infoPanel.add(lblStatus);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        infoPanel.add(lblTotal);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        Map<String, Integer> resultados = enquete.getResultados();
        int totalVotos = enquete.getTotalVotos();

        String[] colunas = { "Opção", "Votos", "Porcentagem" };
        Object[][] dados = new Object[resultados.size()][3];

        int i = 0;
        for (Map.Entry<String, Integer> entry : resultados.entrySet()) {
            String opcao = entry.getKey();
            int votos = entry.getValue();
            double porcentagem = totalVotos > 0 ? (votos * 100.0 / totalVotos) : 0;

            dados[i][0] = opcao;
            dados[i][1] = votos;
            dados[i][2] = String.format("%.1f%%", porcentagem);
            i++;
        }

        JTable tabela = new JTable(dados, colunas);
        tabela.setFont(new Font("Arial", Font.PLAIN, 12));
        tabela.setEnabled(false);
        JScrollPane scrollTabela = new JScrollPane(tabela);

        DefaultPieDataset dataset = new DefaultPieDataset();
        for (Map.Entry<String, Integer> entry : resultados.entrySet()) {
            dataset.setValue(entry.getKey(), entry.getValue());
        }

        JFreeChart chart = ChartFactory.createPieChart(
                "Distribuição de Votos",
                dataset,
                true,
                true,
                false);

        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setSectionPaint(0, new Color(100, 150, 255));
        plot.setSectionPaint(1, new Color(70, 130, 255));
        plot.setSectionPaint(2, new Color(40, 110, 255));
        plot.setBackgroundPaint(new Color(154, 168, 245));

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(400, 300));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);

        if (!enquete.isVotacaoEncerrada()) {
            JButton btnEncerrar = new JButton("Encerrar Votação");
            btnEncerrar.addActionListener(e -> {
                enquete.encerrarVotacao();
                dialog.dispose();
                atualizarListaEnquetes();
                visualizarEnquete(enquete);
            });
            buttonPanel.add(btnEncerrar);

            JButton btnEditar = new JButton("Editar Enquete");
            btnEditar.addActionListener(e -> {
                dialog.dispose();
                abrirJanelaEditarEnquete(enquete);
            });
            buttonPanel.add(btnEditar);
        }

        panelResultados.add(infoPanel, BorderLayout.NORTH);
        panelResultados.add(scrollTabela, BorderLayout.CENTER);
        panelResultados.add(chartPanel, BorderLayout.EAST);
        panelResultados.add(buttonPanel, BorderLayout.SOUTH);

        tabbedPane.addTab("Resultados", panelResultados);

        JPanel panelDetalhesVotos = new JPanel(new BorderLayout());
        panelDetalhesVotos.setBackground(new Color(154, 168, 245));
        panelDetalhesVotos.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblTituloDetalhes = new JLabel("Detalhes dos Votos - " + enquete.getTitulo(), SwingConstants.CENTER);
        lblTituloDetalhes.setFont(new Font("Arial", Font.BOLD, 16));
        panelDetalhesVotos.add(lblTituloDetalhes, BorderLayout.NORTH);

        Map<String, String> votosPorParticipante = new HashMap<>();
        for (Map.Entry<String, Set<String>> entry : enquete.getVotos().entrySet()) {
            String opcao = entry.getKey();
            for (String participante : entry.getValue()) {
                votosPorParticipante.put(participante, opcao);
            }
        }

        String[] colunasDetalhes = {"Participante", "Opção Votada"};
        Object[][] dadosDetalhes = new Object[votosPorParticipante.size()][2];
        int j = 0;
        for (Map.Entry<String, String> entry : votosPorParticipante.entrySet()) {
            dadosDetalhes[j][0] = entry.getKey();
            dadosDetalhes[j][1] = entry.getValue();
            j++;
        }

        JTable tabelaDetalhes = new JTable(dadosDetalhes, colunasDetalhes);
        tabelaDetalhes.setFont(new Font("Arial", Font.PLAIN, 12));
        JScrollPane scrollTabelaDetalhes = new JScrollPane(tabelaDetalhes);
        panelDetalhesVotos.add(scrollTabelaDetalhes, BorderLayout.CENTER);

        tabbedPane.addTab("Detalhes dos Votos", panelDetalhesVotos);

        dialog.add(tabbedPane);
        dialog.setVisible(true);
    }

    private void iniciarVotacao(Enquete enquete) {
        if (enquete.isVotacaoEncerrada()) {
            JOptionPane.showMessageDialog(this, "Esta votação já foi encerrada!", "Votação Encerrada",
                    JOptionPane.INFORMATION_MESSAGE);
            visualizarEnquete(enquete);
            return;
        }

        JDialog dialogVotacao = new JDialog();
        dialogVotacao.setTitle("Votação: " + enquete.getTitulo());
        dialogVotacao.setSize(800, 600);
        dialogVotacao.setResizable(false);
        dialogVotacao.setModal(true);
        dialogVotacao.setLocationRelativeTo(this);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(400);
        splitPane.setResizeWeight(0.5);

        JPanel panelVotacao = new JPanel(new BorderLayout());
        panelVotacao.setBackground(new Color(154, 168, 245));
        panelVotacao.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblInstrucao = new JLabel("Selecione uma opção para votar:", SwingConstants.CENTER);
        lblInstrucao.setFont(new Font("Arial", Font.BOLD, 14));

        JPanel panelOpcoes = new JPanel();
        panelOpcoes.setLayout(new BoxLayout(panelOpcoes, BoxLayout.Y_AXIS));
        panelOpcoes.setOpaque(false);

        ButtonGroup grupoOpcoes = new ButtonGroup();

        for (String opcao : enquete.getOpcoes()) {
            JRadioButton radio = new JRadioButton(opcao);
            radio.setActionCommand(opcao);
            grupoOpcoes.add(radio);
            panelOpcoes.add(radio);
            panelOpcoes.add(Box.createRigidArea(new Dimension(0, 5)));
        }

        JScrollPane scrollOpcoes = new JScrollPane(panelOpcoes);

        final JPanel panelResultados = criarPainelResultados(enquete);

        JButton btnVotar = new JButton("Votar");
        btnVotar.addActionListener(e -> {
            String opcaoSelecionada = grupoOpcoes.getSelection() != null ? grupoOpcoes.getSelection().getActionCommand()
                    : null;

            if (opcaoSelecionada != null) {
                String participante = JOptionPane.showInputDialog(dialogVotacao, "Digite seu nome:");
                if (participante != null && !participante.trim().isEmpty()) {
                    try {
                        enquete.adicionarVoto(participante.trim(), opcaoSelecionada);
                        JOptionPane.showMessageDialog(dialogVotacao, "Voto registrado com sucesso!", "Sucesso",
                                JOptionPane.INFORMATION_MESSAGE);
                        atualizarGrafico(enquete, panelResultados);
                    } catch (IllegalStateException ex) {
                        JOptionPane.showMessageDialog(dialogVotacao, ex.getMessage(), "Erro",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(dialogVotacao, "Selecione uma opção para votar!", "Erro",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        panelVotacao.add(lblInstrucao, BorderLayout.NORTH);
        panelVotacao.add(scrollOpcoes, BorderLayout.CENTER);
        panelVotacao.add(btnVotar, BorderLayout.SOUTH);

        splitPane.setLeftComponent(panelVotacao);
        splitPane.setRightComponent(panelResultados);

        dialogVotacao.add(splitPane);
        dialogVotacao.setVisible(true);
    }

    private JPanel criarPainelResultados(Enquete enquete) {
        JPanel panelResultados = new JPanel(new BorderLayout());
        panelResultados.setBackground(new Color(154, 168, 245));
        panelResultados.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        DefaultPieDataset dataset = new DefaultPieDataset();
        for (Map.Entry<String, Integer> entry : enquete.getResultados().entrySet()) {
            dataset.setValue(entry.getKey(), entry.getValue());
        }

        JFreeChart chart = ChartFactory.createPieChart(
                "Resultados Parciais",
                dataset,
                true,
                true,
                false);

        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setSectionPaint(0, new Color(100, 150, 255));
        plot.setSectionPaint(1, new Color(70, 130, 255));
        plot.setSectionPaint(2, new Color(40, 110, 255));
        plot.setBackgroundPaint(new Color(154, 168, 245));

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(350, 350));

        panelResultados.add(chartPanel, BorderLayout.CENTER);

        return panelResultados;
    }

    private void atualizarGrafico(Enquete enquete, JPanel panelResultados) {
        panelResultados.removeAll();

        DefaultPieDataset dataset = new DefaultPieDataset();
        for (Map.Entry<String, Integer> entry : enquete.getResultados().entrySet()) {
            dataset.setValue(entry.getKey(), entry.getValue());
        }

        JFreeChart chart = ChartFactory.createPieChart(
                "Resultados Parciais",
                dataset,
                true,
                true,
                false);

        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setSectionPaint(0, new Color(100, 150, 255));
        plot.setSectionPaint(1, new Color(70, 130, 255));
        plot.setSectionPaint(2, new Color(40, 110, 255));
        plot.setBackgroundPaint(new Color(154, 168, 245));

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(350, 350));

        panelResultados.add(chartPanel, BorderLayout.CENTER);
        panelResultados.revalidate();
        panelResultados.repaint();
    }

    private void abrirJanelaEditarEnquete(Enquete enquete) {
        JFrame janelaEditarEnquete = new JFrame("Editar Enquete");
        janelaEditarEnquete.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        janelaEditarEnquete.setSize(500, 400);
        janelaEditarEnquete.setResizable(false);
        janelaEditarEnquete.getContentPane().setBackground(new Color(84, 106, 232));
        janelaEditarEnquete.setIconImage(new ImageIcon("Resource_Image/icon.png").getImage());
        janelaEditarEnquete.setLocationRelativeTo(this);

        PainelCriarEnquete painel = new PainelCriarEnquete(enquetes, painelEnquetes, enquete);
        janelaEditarEnquete.add(painel);

        janelaEditarEnquete.setVisible(true);
    }
}

public class SystemVotoSimula extends JFrame {
    private JFrame janelaCriarEnquete = null;
    private JLabel statusLabel;
    private List<Enquete> enquetes;
    private JPanel painelEnquetes;

    private static final Color COR_FUNDO_JANELA = new Color(84, 106, 232);
    private static final Color COR_FUNDO_COMPONENTES = new Color(154, 168, 245);

    public SystemVotoSimula() {
        super("Sistema de Votação Simulada");
        this.enquetes = new ArrayList<>();

        setSize(800, 600);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setIconImage(new ImageIcon("Resource_Image/icon.png").getImage());
        getContentPane().setBackground(COR_FUNDO_JANELA);
        setLayout(null);

        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 12));
        statusLabel.setOpaque(true);
        statusLabel.setBackground(COR_FUNDO_COMPONENTES);
        statusLabel.setForeground(Color.BLACK);
        statusLabel.setBounds(0, 0, 800, 25);
        add(statusLabel);

        JPanel painelBotoes = new JPanel(null);
        painelBotoes.setBounds(20, 40, 120, 110);
        painelBotoes.setBackground(COR_FUNDO_JANELA);
        add(painelBotoes);

        JButton btnTeste = new JButton("Teste");
        btnTeste.setBounds(0, 0, 110, 50);
        btnTeste.setFont(new Font("Arial", Font.PLAIN, 12));
        btnTeste.setBackground(null);
        btnTeste.addActionListener(e -> gerarEnqueteTeste());
        painelBotoes.add(btnTeste);

        JButton btnEnquete = new JButton("Enquete");
        btnEnquete.setBounds(0, 60, 110, 50);
        btnEnquete.setFont(new Font("Arial", Font.PLAIN, 12));
        btnEnquete.setBackground(null);
        btnEnquete.addActionListener(e -> abrirJanelaCriarEnquete());
        painelBotoes.add(btnEnquete);

        painelEnquetes = new JPanel();
        painelEnquetes.setLayout(new BoxLayout(painelEnquetes, BoxLayout.Y_AXIS));
        painelEnquetes.setBackground(COR_FUNDO_COMPONENTES);

        JScrollPane scrollPane = new JScrollPane(painelEnquetes);
        scrollPane.setBounds(150, 40, 620, 510);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        scrollPane.getViewport().setBackground(COR_FUNDO_COMPONENTES);

        add(scrollPane);
    }

    private void gerarEnqueteTeste() {
        enquetes.clear();
        
        List<String> opcoes = Arrays.asList("Pizza", "Esfirra", "Hamburguer", "Pastel");
        Enquete enqueteTeste = new Enquete("Qual é a melhor comida?", opcoes);
        enquetes.add(enqueteTeste);
        
        atualizarListaEnquetes();
        
        statusLabel.setText("Enquete teste criada com sucesso!");
        statusLabel.setForeground(new Color(16, 145, 1));
    }

    private void atualizarListaEnquetes() {
        painelEnquetes.removeAll();

        for (Enquete enquete : enquetes) {
            JPanel panelEnquete = new JPanel();
            panelEnquete.setLayout(new BorderLayout());
            panelEnquete.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            panelEnquete.setBackground(new Color(200, 200, 255));
            panelEnquete.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

            JLabel lblTitulo = new JLabel(enquete.getTitulo(), SwingConstants.CENTER);
            lblTitulo.setFont(new Font("Arial", Font.BOLD, 14));

            JPanel panelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
            panelBotoes.setOpaque(false);

            JButton btnVisualizar = new JButton(enquete.isVotacaoEncerrada() ? "Visualizar Resultados" : "Visualizar");
            btnVisualizar.setBackground(null);
            btnVisualizar.addActionListener(e -> visualizarEnquete(enquete));

            panelBotoes.add(btnVisualizar);

            if (!enquete.isVotacaoEncerrada()) {
                JButton btnIniciar = new JButton("Iniciar Votação");
                btnIniciar.setBackground(null);
                btnIniciar.addActionListener(e -> iniciarVotacao(enquete));
                panelBotoes.add(btnIniciar);
            }

            panelEnquete.add(lblTitulo, BorderLayout.CENTER);
            panelEnquete.add(panelBotoes, BorderLayout.SOUTH);

            painelEnquetes.add(panelEnquete);
            painelEnquetes.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        painelEnquetes.revalidate();
        painelEnquetes.repaint();
    }

    private void visualizarEnquete(Enquete enquete) {
        JDialog dialog = new JDialog();
        dialog.setTitle("Resultados: " + enquete.getTitulo());
        dialog.setSize(800, 600);
        dialog.setModal(false);
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(this);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(new Color(154, 168, 245));

        JPanel panelResultados = new JPanel(new BorderLayout());
        panelResultados.setBackground(new Color(154, 168, 245));
        panelResultados.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        JLabel lblTitulo = new JLabel("Enquete: " + enquete.getTitulo());
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblStatus = new JLabel("Status: " + (enquete.isVotacaoEncerrada() ? "Encerrada" : "Em andamento"));
        lblStatus.setFont(new Font("Arial", Font.BOLD, 14));
        lblStatus.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTotal = new JLabel("Total de votos: " + enquete.getTotalVotos());
        lblTotal.setFont(new Font("Arial", Font.PLAIN, 14));
        lblTotal.setAlignmentX(Component.CENTER_ALIGNMENT);

        infoPanel.add(lblTitulo);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        infoPanel.add(lblStatus);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        infoPanel.add(lblTotal);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        Map<String, Integer> resultados = enquete.getResultados();
        int totalVotos = enquete.getTotalVotos();

        String[] colunas = { "Opção", "Votos", "Porcentagem" };
        Object[][] dados = new Object[resultados.size()][3];

        int i = 0;
        for (Map.Entry<String, Integer> entry : resultados.entrySet()) {
            String opcao = entry.getKey();
            int votos = entry.getValue();
            double porcentagem = totalVotos > 0 ? (votos * 100.0 / totalVotos) : 0;

            dados[i][0] = opcao;
            dados[i][1] = votos;
            dados[i][2] = String.format("%.1f%%", porcentagem);
            i++;
        }

        JTable tabela = new JTable(dados, colunas);
        tabela.setFont(new Font("Arial", Font.PLAIN, 12));
        tabela.setEnabled(false);
        JScrollPane scrollTabela = new JScrollPane(tabela);

        DefaultPieDataset dataset = new DefaultPieDataset();
        for (Map.Entry<String, Integer> entry : resultados.entrySet()) {
            dataset.setValue(entry.getKey(), entry.getValue());
        }

        JFreeChart chart = ChartFactory.createPieChart(
                "Distribuição de Votos",
                dataset,
                true,
                true,
                false);

        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setSectionPaint(0, new Color(100, 150, 255));
        plot.setSectionPaint(1, new Color(70, 130, 255));
        plot.setSectionPaint(2, new Color(40, 110, 255));
        plot.setBackgroundPaint(new Color(154, 168, 245));

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(400, 300));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);

        if (!enquete.isVotacaoEncerrada()) {
            JButton btnEncerrar = new JButton("Encerrar Votação");
            btnEncerrar.addActionListener(e -> {
                enquete.encerrarVotacao();
                dialog.dispose();
                atualizarListaEnquetes();
                visualizarEnquete(enquete);
            });
            buttonPanel.add(btnEncerrar);

            JButton btnEditar = new JButton("Editar Enquete");
            btnEditar.addActionListener(e -> {
                dialog.dispose();
                abrirJanelaEditarEnquete(enquete);
            });
            buttonPanel.add(btnEditar);
        }

        panelResultados.add(infoPanel, BorderLayout.NORTH);
        panelResultados.add(scrollTabela, BorderLayout.CENTER);
        panelResultados.add(chartPanel, BorderLayout.EAST);
        panelResultados.add(buttonPanel, BorderLayout.SOUTH);

        tabbedPane.addTab("Resultados", panelResultados);

        JPanel panelDetalhesVotos = new JPanel(new BorderLayout());
        panelDetalhesVotos.setBackground(new Color(154, 168, 245));
        panelDetalhesVotos.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblTituloDetalhes = new JLabel("Detalhes dos Votos - " + enquete.getTitulo(), SwingConstants.CENTER);
        lblTituloDetalhes.setFont(new Font("Arial", Font.BOLD, 16));
        panelDetalhesVotos.add(lblTituloDetalhes, BorderLayout.NORTH);

        Map<String, String> votosPorParticipante = new HashMap<>();
        for (Map.Entry<String, Set<String>> entry : enquete.getVotos().entrySet()) {
            String opcao = entry.getKey();
            for (String participante : entry.getValue()) {
                votosPorParticipante.put(participante, opcao);
            }
        }

        String[] colunasDetalhes = {"Participante", "Opção Votada"};
        Object[][] dadosDetalhes = new Object[votosPorParticipante.size()][2];
        int j = 0;
        for (Map.Entry<String, String> entry : votosPorParticipante.entrySet()) {
            dadosDetalhes[j][0] = entry.getKey();
            dadosDetalhes[j][1] = entry.getValue();
            j++;
        }

        JTable tabelaDetalhes = new JTable(dadosDetalhes, colunasDetalhes);
        tabelaDetalhes.setFont(new Font("Arial", Font.PLAIN, 12));
        JScrollPane scrollTabelaDetalhes = new JScrollPane(tabelaDetalhes);
        panelDetalhesVotos.add(scrollTabelaDetalhes, BorderLayout.CENTER);

        tabbedPane.addTab("Detalhes dos Votos", panelDetalhesVotos);

        dialog.add(tabbedPane);
        dialog.setVisible(true);
    }

    private void abrirJanelaEditarEnquete(Enquete enquete) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'abrirJanelaEditarEnquete'");
    }

    private void iniciarVotacao(Enquete enquete) {
        if (enquete.isVotacaoEncerrada()) {
            JOptionPane.showMessageDialog(this, "Esta votação já foi encerrada!", "Votação Encerrada",
                    JOptionPane.INFORMATION_MESSAGE);
            visualizarEnquete(enquete);
            return;
        }

        JDialog dialogVotacao = new JDialog();
        dialogVotacao.setTitle("Votação: " + enquete.getTitulo());
        dialogVotacao.setSize(800, 600);
        dialogVotacao.setResizable(false);
        dialogVotacao.setModal(true);
        dialogVotacao.setLocationRelativeTo(this);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(400);
        splitPane.setResizeWeight(0.5);

        JPanel panelVotacao = new JPanel(new BorderLayout());
        panelVotacao.setBackground(new Color(154, 168, 245));
        panelVotacao.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblInstrucao = new JLabel("Selecione uma opção para votar:", SwingConstants.CENTER);
        lblInstrucao.setFont(new Font("Arial", Font.BOLD, 14));

        JPanel panelOpcoes = new JPanel();
        panelOpcoes.setLayout(new BoxLayout(panelOpcoes, BoxLayout.Y_AXIS));
        panelOpcoes.setOpaque(false);

        ButtonGroup grupoOpcoes = new ButtonGroup();

        for (String opcao : enquete.getOpcoes()) {
            JRadioButton radio = new JRadioButton(opcao);
            radio.setActionCommand(opcao);
            grupoOpcoes.add(radio);
            panelOpcoes.add(radio);
            panelOpcoes.add(Box.createRigidArea(new Dimension(0, 5)));
        }

        JScrollPane scrollOpcoes = new JScrollPane(panelOpcoes);

        final JPanel panelResultados = criarPainelResultados(enquete);

        JButton btnVotar = new JButton("Votar");
        btnVotar.addActionListener(e -> {
            String opcaoSelecionada = grupoOpcoes.getSelection() != null ? grupoOpcoes.getSelection().getActionCommand()
                    : null;

            if (opcaoSelecionada != null) {
                String participante = JOptionPane.showInputDialog(dialogVotacao, "Digite seu nome:");
                if (participante != null && !participante.trim().isEmpty()) {
                    try {
                        enquete.adicionarVoto(participante.trim(), opcaoSelecionada);
                        JOptionPane.showMessageDialog(dialogVotacao, "Voto registrado com sucesso!", "Sucesso",
                                JOptionPane.INFORMATION_MESSAGE);
                        atualizarGrafico(enquete, panelResultados);
                    } catch (IllegalStateException ex) {
                        JOptionPane.showMessageDialog(dialogVotacao, ex.getMessage(), "Erro",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(dialogVotacao, "Selecione uma opção para votar!", "Erro",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        panelVotacao.add(lblInstrucao, BorderLayout.NORTH);
        panelVotacao.add(scrollOpcoes, BorderLayout.CENTER);
        panelVotacao.add(btnVotar, BorderLayout.SOUTH);

        splitPane.setLeftComponent(panelVotacao);
        splitPane.setRightComponent(panelResultados);

        dialogVotacao.add(splitPane);
        dialogVotacao.setVisible(true);
    }

    private void atualizarGrafico(Enquete enquete, JPanel panelResultados) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'atualizarGrafico'");
    }

    private JPanel criarPainelResultados(Enquete enquete) {
        JPanel panelResultados = new JPanel(new BorderLayout());
        panelResultados.setBackground(new Color(154, 168, 245));
        panelResultados.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        DefaultPieDataset dataset = new DefaultPieDataset();
        for (Map.Entry<String, Integer> entry : enquete.getResultados().entrySet()) {
            dataset.setValue(entry.getKey(), entry.getValue());
        }

        JFreeChart chart = ChartFactory.createPieChart(
                "Resultados Parciais",
                dataset,
                true,
                true,
                false);

        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setSectionPaint(0, new Color(100, 150, 255));
        plot.setSectionPaint(1, new Color(70, 130, 255));
        plot.setSectionPaint(2, new Color(40, 110, 255));
        plot.setBackgroundPaint(new Color(154, 168, 245));

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(350, 350));

        panelResultados.add(chartPanel, BorderLayout.CENTER);

        return panelResultados;
    }

    private void setWindowIcon(Window window) {
        try {
            window.setIconImage(new ImageIcon("Resource_Image/icon.png").getImage());
        } catch (Exception e) {
            System.err.println("Erro ao carregar ícone: " + e.getMessage());
        }
    }

    private void abrirJanelaCriarEnquete() {
        if (janelaCriarEnquete != null && janelaCriarEnquete.isVisible()) {
            janelaCriarEnquete.toFront();
            return;
        }

        janelaCriarEnquete = new JFrame("Criar Nova Enquete");
        janelaCriarEnquete.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        janelaCriarEnquete.setSize(500, 400);
        janelaCriarEnquete.setResizable(false);
        janelaCriarEnquete.getContentPane().setBackground(COR_FUNDO_JANELA);
        setWindowIcon(janelaCriarEnquete);
        janelaCriarEnquete.setLocationRelativeTo(this);

        PainelCriarEnquete painel = new PainelCriarEnquete(enquetes, painelEnquetes, null);
        janelaCriarEnquete.add(painel);

        janelaCriarEnquete.setVisible(true);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Erro ao configurar look and feel: " + e.getMessage());
        }

        SwingUtilities.invokeLater(() -> {
            SystemVotoSimula janela = new SystemVotoSimula();
            janela.setVisible(true);
        });
    }
}