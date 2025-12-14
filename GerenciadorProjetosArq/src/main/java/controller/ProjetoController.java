/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import model.dao.ProjetoDAO;
import model.dao.ClienteDAO;
import model.entities.Cliente;
import model.entities.Projeto;
import view.screens.dialogs.DlgCadastroProjetos;
import java.util.List;
/**
 *
 * @author Viktin
 */
public class ProjetoController {

    private final DlgCadastroProjetos view;
    private final ProjetoDAO dao;
    private final ClienteDAO clienteDAO;

    public ProjetoController(DlgCadastroProjetos view) {
        this.view = view;
        this.dao = new ProjetoDAO();
        this.clienteDAO = new ClienteDAO();
        inicializar();
    }

    private void inicializar() {
        // Carrega os clientes no ComboBox ao abrir a tela
        List<Cliente> clientes = clienteDAO.listarTodos();
        view.atualizarComboClientes(clientes);
    }
    
    public ProjetoController() {
        this.view = null; // Não tem tela vinculada
        this.dao = new ProjetoDAO();
        this.clienteDAO = new ClienteDAO();
    }
    
    public Projeto buscarPorId(Long id) {
        return dao.buscarPorId(id);
    }

    public void salvarProjeto() {
        // 1. Validar campos obrigatórios
        if (view.getNome().isEmpty() || view.getDataInicio().isEmpty()) {
            view.exibeMensagem("Preencha o Nome e a Data de Início!");
            return;
        }

        Projeto projeto;

        // --- AQUI ESTÁ A CORREÇÃO MÁGICA ---
        // Perguntamos para a View: "Existe um projeto em edição?"
        Projeto projetoExistente = view.getProjetoEmEdicao();

        if (projetoExistente != null) {
            // CENÁRIO EDIÇÃO: Usamos o objeto que já tem ID
            projeto = projetoExistente; 
        } else {
            // CENÁRIO NOVO: Criamos um objeto zerado
            projeto = new Projeto();
        }
        // -----------------------------------

        try {
            // 2. Preencher os dados (seja novo ou velho, atualizamos os campos)
            projeto.setNome(view.getNome());
            projeto.setDescricao(view.getDescricao());
            projeto.setStatus(view.getStatus());
            projeto.setCliente(view.getClienteSelecionado());

            // 3. Conversão de Datas (String -> LocalDate)
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            
            // Data Início
            if (!view.getDataInicio().isEmpty()) {
                projeto.setDataInicio(LocalDate.parse(view.getDataInicio(), dtf));
            }

            // Data Previsão
            if (!view.getDataPrevisao().trim().isEmpty() && !view.getDataPrevisao().equals("__/__/____")) {
                 projeto.setDataPrevisao(LocalDate.parse(view.getDataPrevisao(), dtf));
            }

            // 4. Conversão de Orçamento (String -> Double)
            String valorTexto = view.getOrcamento();
            if (!valorTexto.isEmpty()) {
                // Troca vírgula por ponto para o Java entender (ex: 10,50 -> 10.50)
                valorTexto = valorTexto.replace(",", ".");
                projeto.setOrcamento(Double.parseDouble(valorTexto));
            }

            // 5. Salvar no Banco
            // Se o projeto tiver ID, o DAO fará 'merge' (Update). Se não, 'persist' (Insert).
            dao.salvar(projeto);

            view.exibeMensagem("Projeto salvo com sucesso!");
            view.dispose(); // Fecha a janela

        } catch (Exception e) {
            view.exibeMensagem("Erro ao salvar: " + e.getMessage());
            e.printStackTrace();
        }
    }
}