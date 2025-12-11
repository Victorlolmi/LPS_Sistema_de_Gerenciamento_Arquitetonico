package controller;

import controller.tableModel.TMProjeto;
import model.dao.GenericDAO;
import model.entities.Projeto;
import view.screens.FrProjetos; // Supondo que você criou essa tela
import java.util.List;
import javax.persistence.EntityManager;
import factory.JPAUtil;
import javax.persistence.TypedQuery;
import javax.swing.JOptionPane;

/**
 * @author Viktin
 */
public class ProjetoController {

    private final FrProjetos view;
    private final GenericDAO<Projeto> projetoDAO;

    public ProjetoController(FrProjetos view) {
        this.view = view;
        this.projetoDAO = new GenericDAO<>(Projeto.class);
        initController();
    }

    private void initController() {
        // Ao iniciar, já carrega a tabela
        atualizarTabela();
    }

    public void atualizarTabela() {
        // Como o GenericDAO padrão não tem um "findAll", 
        // geralmente fazemos uma busca manual ou adicionamos no DAO.
        // Aqui vou fazer manual para exemplo:
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT p FROM Projeto p";
            TypedQuery<Projeto> query = em.createQuery(jpql, Projeto.class);
            List<Projeto> lista = query.getResultList();
            
            // Cria o Table Model com a lista do banco
            TMProjeto tableModel = new TMProjeto(lista);
            
            // Define o modelo na JTable da sua View
            //view.getTbProjetos().setModel(tableModel);
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
    public void excluirProjetoSelecionado() {
    // 1. Verifica se tem linha selecionada
        int linha = view.getTbProjetos().getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(view, "Selecione um projeto para excluir!");
            return;
        }

        // 2. Pega o objeto
        TMProjeto model = (TMProjeto) view.getTbProjetos().getModel();
        Projeto projeto = model.getObjeto(linha);

        // 3. PERGUNTA DE CONFIRMAÇÃO (Muito Importante)
        int resposta = JOptionPane.showConfirmDialog(view, 
                "Tem certeza que deseja excluir o projeto '" + projeto.getNome() + "'?\nIsso não pode ser desfeito.", 
                "Confirmar Exclusão", 
                JOptionPane.YES_NO_OPTION, 
                JOptionPane.WARNING_MESSAGE);

        if (resposta == JOptionPane.YES_OPTION) {
            try {
                // 4. Chama o DAO para remover do banco
                // Como seu GenericDAO não tem delete explícito no exemplo, 
                // você precisaria adicionar um método delete no GenericDAO ou fazer manual:
                EntityManager em = JPAUtil.getEntityManager();
                em.getTransaction().begin();
                // O merge é necessário se o objeto não estiver "atachado" na sessão atual
                projeto = em.merge(projeto); 
                em.remove(projeto);
                em.getTransaction().commit();
                em.close();

                // 5. Atualiza a tabela na tela
                atualizarTabela();
                JOptionPane.showMessageDialog(view, "Projeto excluído com sucesso!");

            } catch (Exception e) {
                JOptionPane.showMessageDialog(view, "Erro ao excluir: " + e.getMessage());
            }
        }
    }
    
    // Exemplo para o filtro de busca "Buscar projeto..." da imagem
    public void buscarPorNome(String nome) {
        // Lógica similar ao atualizarTabela, mas com WHERE nome LIKE ...
    }
}