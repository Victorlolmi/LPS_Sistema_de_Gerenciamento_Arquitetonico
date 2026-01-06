/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import model.dao.ClienteDAO;
import model.entities.Cliente;
import model.entities.Endereco;
import view.screens.dialogs.DlgCadastroCliente;
import org.mindrot.jbcrypt.BCrypt;

/**
 * @author Viktin
 */
public class ClienteController {

    private final DlgCadastroCliente view;
    private final ClienteDAO clienteDAO;

    public ClienteController(DlgCadastroCliente view) {
        this.view = view;
        this.clienteDAO = new ClienteDAO();
    }

    public void cadastrarCliente() {
        String nome = view.getNome();
        String email = view.getEmail();
        String cpf = view.getCpf();
        
        String logradouro = view.getLogradouro();
        String bairro = view.getBairro();
        String numero = view.getNumero();
        
        // Input "raw" para validação de formato antes de limpar
        String rawCep = view.getCep();
        String cidade = view.getCidade();

        if (nome.isEmpty() || email.isEmpty() || cpf.isEmpty()) {
            view.exibeMensagem("Erro: Nome, Email e CPF são obrigatórios.");
            return;
        }

        // Constraint do banco exige CEP not null
        if (rawCep == null || rawCep.trim().isEmpty()) {
            view.exibeMensagem("Erro: O CEP é obrigatório.");
            return;
        }

        // Suporta input puro (12345678) ou com máscara (12345-678)
        if (!rawCep.matches("\\d{8}|\\d{5}-\\d{3}")) {
            view.exibeMensagem("Erro: CEP inválido.\nUse apenas números (Ex: 36000000) ou o formato com traço (Ex: 36000-000).");
            return;
        }

        String cpfApenasNumeros = cpf.replaceAll("[^0-9]", "");

        if (cpfApenasNumeros.length() != 11) {
            view.exibeMensagem("Erro: O CPF deve conter exatamente 11 dígitos numéricos.");
            return;
        }
        
        if (clienteDAO.buscarPorEmail(email) != null) {
            view.exibeMensagem("Erro: Este E-mail já está cadastrado!");
            return;
        }

        if (clienteDAO.buscarPorCpf(cpfApenasNumeros) != null) { 
            view.exibeMensagem("Erro: Este CPF já está cadastrado!");
            return;
        }

        Endereco endereco = new Endereco();
        endereco.setLogradouro(logradouro);
        endereco.setBairro(bairro);
        endereco.setNumero(numero);
        
        // Fix: Remove formatação para evitar Data Truncation (coluna é varchar(8))
        String cepLimpo = rawCep.replaceAll("[^0-9]", "");
        endereco.setCep(cepLimpo);
        
        endereco.setCidade(cidade);

        Cliente novoCliente = new Cliente();
        novoCliente.setNome(nome);
        novoCliente.setEmail(email);
        novoCliente.setCpf(cpfApenasNumeros); 
        novoCliente.setEndereco(endereco);
        
        // Senha provisória para primeiro acesso
        String senhaPadrao = "123456";
        String senhaHash = BCrypt.hashpw(senhaPadrao, BCrypt.gensalt());
        novoCliente.setSenha(senhaHash);

        try {
            clienteDAO.salvar(novoCliente);
            
            String msg = String.format(
                "Cliente salvo com sucesso!\n\nUsuário: %s\nSenha Padrão: %s", 
                email, senhaPadrao
            );
            
            view.exibeMensagem(msg);
            view.dispose();
            
        } catch (Exception e) {
            view.exibeMensagem("Erro ao cadastrar: " + e.getMessage());
            e.printStackTrace();
        }
    }
}