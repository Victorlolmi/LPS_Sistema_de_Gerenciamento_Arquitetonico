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
        List<Cliente> clientes = clienteDAO.listarTodos();
        view.atualizarComboClientes(clientes);
    }
    
    public ProjetoController() {
        this.view = null; // Modo headless (sem GUI vinculada)
        this.dao = new ProjetoDAO();
        this.clienteDAO = new ClienteDAO();
    }
    
    public Projeto buscarPorId(Long id) {
        return dao.buscarPorId(id);
    }

    public void salvarProjeto() {
        if (view.getNome().isEmpty() || view.getDataInicio().isEmpty()) {
            view.exibeMensagem("Preencha o Nome e a Data de Início!");
            return;
        }

        Projeto projeto;

        // Recupera instância da view para decidir entre Update (com ID) ou Insert (sem ID)
        Projeto projetoExistente = view.getProjetoEmEdicao();

        if (projetoExistente != null) {
            projeto = projetoExistente; 
        } else {
            projeto = new Projeto();
        }

        try {
            projeto.setNome(view.getNome());
            projeto.setDescricao(view.getDescricao());
            projeto.setStatus(view.getStatus());
            projeto.setCliente(view.getClienteSelecionado());

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            
            if (!view.getDataInicio().isEmpty()) {
                projeto.setDataInicio(LocalDate.parse(view.getDataInicio(), dtf));
            }

            // Ignora máscara vazia do Swing (__/__/____) para não quebrar o parse
            if (!view.getDataPrevisao().trim().isEmpty() && !view.getDataPrevisao().equals("__/__/____")) {
                 projeto.setDataPrevisao(LocalDate.parse(view.getDataPrevisao(), dtf));
            }

            String valorTexto = view.getOrcamento();
            if (!valorTexto.isEmpty()) {
                // Sanitização manual (PT-BR -> Double)
                valorTexto = valorTexto.replace(",", ".");
                projeto.setOrcamento(Double.parseDouble(valorTexto));
            }

            // DAO decide estratégia (persist ou merge) baseada na presença do ID
            dao.salvar(projeto);

            view.exibeMensagem("Projeto salvo com sucesso!");
            view.dispose();

        } catch (Exception e) {
            view.exibeMensagem("Erro ao salvar: " + e.getMessage());
            e.printStackTrace();
        }
    }
}