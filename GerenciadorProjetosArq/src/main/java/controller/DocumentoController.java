package controller;

import java.io.File;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;
import model.dao.DocumentoDAO;
import model.entities.Documento;
import model.entities.Projeto;
import view.screens.dialogs.DlgVisualizarProjeto;

/**
 * @author Viktin
 */
public class DocumentoController {

    private final DocumentoDAO dao;

    public DocumentoController() {
        this.dao = new DocumentoDAO();
    }

    public File selecionarArquivo(java.awt.Component parent) {
        try {
            // Força o look and feel do SO para o seletor não parecer um app de 1995
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Fallback silencioso: se falhar, usa o padrão Java
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Selecione o documento");
        
        FileNameExtensionFilter filtro = new FileNameExtensionFilter(
                "Arquivos de Projeto (PDF, Imagens, DWG)", "pdf", "jpg", "jpeg", "png", "dwg");
        fileChooser.setFileFilter(filtro);

        int retorno = fileChooser.showOpenDialog(parent);
        if (retorno == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        }
        return null;
    }

    public String sugerirNome(String nomeAtual, File arquivo) {
        // Preenche o nome automaticamente se o usuário ainda não definiu um
        if ((nomeAtual == null || nomeAtual.trim().isEmpty()) && arquivo != null) {
            return arquivo.getName();
        }
        return nomeAtual;
    }

    public boolean salvarDocumento(String nome, String categoria, File arquivo, Projeto projeto) {
        if (projeto == null) {
            JOptionPane.showMessageDialog(null, "Erro crítico: Projeto não vinculado.", "Erro", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (nome == null || nome.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "O nome do documento é obrigatória.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (arquivo == null || !arquivo.exists()) {
            JOptionPane.showMessageDialog(null, "Por favor, selecione um arquivo válido.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        try {
            Documento doc = new Documento();
            doc.setNome(nome);
            doc.setCaminhoArquivo(arquivo.getAbsolutePath());
            doc.setCategoria(categoria);
            doc.setProjeto(projeto);

            // Parsing manual da extensão para o metadado
            String nomeArquivo = arquivo.getName();
            int dotIndex = nomeArquivo.lastIndexOf(".");
            doc.setTipo(dotIndex == -1 ? "Arquivo" : nomeArquivo.substring(dotIndex));

            dao.salvar(doc);
            return true;

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao salvar documento: " + e.getMessage());
            return false;
        }
    }

    public void excluirDocumento(Documento doc, DlgVisualizarProjeto view) {
        int confirm = JOptionPane.showConfirmDialog(view, 
                "Deseja excluir o documento '" + doc.getNome() + "'?", 
                "Excluir", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                dao.remover(doc.getId());
                view.atualizarTabelaDocumentos(); // Callback na view
                JOptionPane.showMessageDialog(view, "Documento removido.");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(view, "Erro ao excluir: " + e.getMessage());
            }
        }
    }
    public List<Documento> listarDocumentosDoProjeto(Long idProjeto) {
        return dao.listarPorProjeto(idProjeto);
    }
}