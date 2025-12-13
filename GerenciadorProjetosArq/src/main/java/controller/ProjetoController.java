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
/**
 *
 * @author Viktin
 */
public class ProjetoController {
    private final DlgCadastroProjetos view;
    private final ProjetoDAO projetoDAO;

    public ProjetoController(DlgCadastroProjetos view) {
        this.view = view;
        this.projetoDAO = new ProjetoDAO();
        ClienteDAO clienteDAO = new ClienteDAO();
        java.util.List<Cliente> lista = clienteDAO.listarTodos(); // Supondo que exista o findAll
        view.atualizarComboClientes(lista);
    }
    
    public void salvarProjeto() {
        // 1. Obter dados da View (Assumindo que sua View tenha esses getters)
        String nome = view.getNome();
        String desc = view.getDescricao();
        String strOrcamento = view.getOrcamento(); // Vem como String, ex: "1500,00"
        String strDataInicio = view.getDataInicio();
        String strDataPrev = view.getDataPrevisao();
        Cliente clienteSelecionado = view.getClienteSelecionado(); // Pega do ComboBox
        // Supondo que o status venha de um ComboBox ou seja fixo no início
        String status = view.getStatus(); 

        // 2. Validar dados de entrada (Campos Obrigatórios)
        if (nome.isEmpty()) {
            view.exibeMensagem("Erro: O nome do projeto é obrigatório.");
            return;
        }
        
        if (clienteSelecionado == null) {
            view.exibeMensagem("Erro: Selecione um cliente para o projeto.");
            return;
        }

        // 3. Instanciar e preencher o Objeto Projeto
        Projeto projeto = new Projeto();
        projeto.setNome(nome);
        projeto.setDescricao(desc);
        projeto.setStatus(status);
        projeto.setCliente(clienteSelecionado);

        // 4. Conversão e Tratamento de Tipos (Números e Datas)
        try {
            // -- Conversão do Orçamento (Double) --
            if (!strOrcamento.isEmpty()) {
                projeto.setOrcamento(Double.parseDouble(strOrcamento));
            }

            // -- Lógica Completa de Datas --
            java.time.LocalDate dataIni = null;
            java.time.LocalDate dataPrev = null;
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
            
            // 1. Tenta converter Data de Início
            if (!strDataInicio.isEmpty()) {
                dataIni = java.time.LocalDate.parse(strDataInicio, formatter);
            }
            
            // 2. Tenta converter Data de Previsão
            if (!strDataPrev.isEmpty()) {
                dataPrev = java.time.LocalDate.parse(strDataPrev, formatter);
            }

            // 3. VERIFICAÇÃO LÓGICA: Previsão vs Início
            // Só faz sentido comparar se as duas datas foram preenchidas
            if (dataIni != null && dataPrev != null) {
                if (dataPrev.isBefore(dataIni)) {
                    view.exibeMensagem("Erro de Lógica: A data de previsão não pode ser anterior à data de início.");
                    return; // Para o salvamento aqui mesmo!
                }
            }

            // Se passou na verificação, joga para o objeto
            projeto.setDataInicio(dataIni);
            projeto.setDataPrevisao(dataPrev);


            // 5. Salvar no Banco
            projetoDAO.salvar(projeto);
            
            view.exibeMensagem("Projeto salvo com sucesso!");
            view.dispose();

        } catch (NumberFormatException e) {
            view.exibeMensagem("Erro: O orçamento deve ser um número válido (Ex: 1500.00)");
        } catch (java.time.format.DateTimeParseException e) {
            // Esse erro dispara se o usuário digitar "01/13/2025" (mês 13 não existe) ou texto errado
            view.exibeMensagem("Erro: Data inválida. Verifique se o dia/mês existem e use o formato dd/MM/yyyy.");
        } catch (Exception e) {
            view.exibeMensagem("Erro ao salvar: " + e.getMessage());
            e.printStackTrace();
        }

    }
    
    /**
     * Método auxiliar para fechar a tela sem salvar
     */
    public void cancelar() {
        view.dispose();
    }
    
}
