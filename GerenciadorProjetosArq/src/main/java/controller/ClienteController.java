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
 *
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
        // 1. Pegar dados da View
        String nome = view.getNome();
        String email = view.getEmail();
        String cpf = view.getCpf(); 
        
        // Dados do Endereço
        String logradouro = view.getLogradouro();
        String bairro = view.getBairro();
        String numero = view.getNumero();
        
        // --- NOVOS CAMPOS ---
        String rawCep = view.getCep(); // Pegamos o CEP "bruto" da tela
        String cidade = view.getCidade();
        // --------------------

        // 2. Validações Básicas (Nome, Email, CPF)
        if (nome.isEmpty() || email.isEmpty() || cpf.isEmpty()) {
            view.exibeMensagem("Erro: Nome, Email e CPF são obrigatórios.");
            return;
        }

        // --- VALIDAÇÃO DO CEP (Obrigatório + Formato) ---
        // 1. Verifica se está vazio (O Banco exige NOT NULL)
        if (rawCep == null || rawCep.trim().isEmpty()) {
            view.exibeMensagem("Erro: O CEP é obrigatório.");
            return;
        }

        // 2. Verifica se o formato é válido (Regex)
        // Aceita apenas números (12345678) OU formato com traço (12345-678)
        if (!rawCep.matches("\\d{8}|\\d{5}-\\d{3}")) {
            view.exibeMensagem("Erro: CEP inválido.\nUse apenas números (Ex: 36000000) ou o formato com traço (Ex: 36000-000).");
            return;
        }
        // ------------------------------------------------

        // --- VALIDAÇÃO DE CPF (11 DÍGITOS) ---
        String cpfApenasNumeros = cpf.replaceAll("[^0-9]", "");

        if (cpfApenasNumeros.length() != 11) {
            view.exibeMensagem("Erro: O CPF deve conter exatamente 11 dígitos numéricos.");
            return;
        }
        
        // Verifica duplicidade
        if (clienteDAO.buscarPorEmail(email) != null) {
            view.exibeMensagem("Erro: Este E-mail já está cadastrado!");
            return;
        }

        if (clienteDAO.buscarPorCpf(cpfApenasNumeros) != null) { 
            view.exibeMensagem("Erro: Este CPF já está cadastrado!");
            return;
        }

        // 3. Montar Objetos
        Endereco endereco = new Endereco();
        endereco.setLogradouro(logradouro);
        endereco.setBairro(bairro);
        endereco.setNumero(numero);
        
        // --- PREENCHENDO NOVOS CAMPOS DO ENDEREÇO ---
        // AQUI ESTÁ A CORREÇÃO: Limpamos o traço e salvamos no objeto
        // O banco receberá apenas os 8 números (evita erro "Data too long")
        String cepLimpo = rawCep.replaceAll("[^0-9]", "");
        endereco.setCep(cepLimpo);
        
        endereco.setCidade(cidade);
        // --------------------------------------------

        Cliente novoCliente = new Cliente();
        novoCliente.setNome(nome);
        novoCliente.setEmail(email);
        novoCliente.setCpf(cpfApenasNumeros); 
        novoCliente.setEndereco(endereco);
        
        // 4. LÓGICA DA SENHA PADRÃO
        String senhaPadrao = "123456";
        String senhaHash = BCrypt.hashpw(senhaPadrao, BCrypt.gensalt());
        novoCliente.setSenha(senhaHash);

        // 5. Salvar
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