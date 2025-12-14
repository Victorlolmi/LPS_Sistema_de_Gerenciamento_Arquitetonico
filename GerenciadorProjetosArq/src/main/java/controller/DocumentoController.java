/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;
import java.io.File;
import java.util.List;
import javax.swing.JOptionPane;
import model.dao.DocumentoDAO;
import model.entities.Documento;
import model.entities.Projeto;

/**
 *
 * @author Viktin
 */
public class DocumentoController {

    private final DocumentoDAO dao;

    public DocumentoController() {
        this.dao = new DocumentoDAO();
    }

    // Método para salvar recebendo os dados brutos da tela (futura DlgCadastroDocumento)
    public boolean salvarDocumento(String nome, String categoria, File arquivo, Projeto projeto) {
        
        // Validações
        if (nome == null || nome.isEmpty()) {
            JOptionPane.showMessageDialog(null, "O nome do documento é obrigatório.");
            return false;
        }
        if (arquivo == null || !arquivo.exists()) {
            JOptionPane.showMessageDialog(null, "Selecione um arquivo válido.");
            return false;
        }
        if (projeto == null) {
            JOptionPane.showMessageDialog(null, "Erro: Projeto não vinculado.");
            return false;
        }

        try {
            Documento doc = new Documento();
            doc.setNome(nome);
            doc.setCaminhoArquivo(arquivo.getAbsolutePath()); // Salva o caminho completo
            doc.setCategoria(categoria);
            doc.setProjeto(projeto);

            // Extrai a extensão do arquivo (Ex: "contrato.pdf" -> ".pdf")
            String nomeArquivo = arquivo.getName();
            if (nomeArquivo.lastIndexOf(".") != -1) {
                doc.setTipo(nomeArquivo.substring(nomeArquivo.lastIndexOf(".")));
            } else {
                doc.setTipo("Arquivo");
            }

            dao.salvar(doc);
            return true;
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao salvar documento: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean excluirDocumento(Long id) {
        try {
            dao.remover(id);
            return true;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao excluir: " + e.getMessage());
            return false;
        }
    }

    public List<Documento> listarDocumentosDoProjeto(Long idProjeto) {
        return dao.listarPorProjeto(idProjeto);
    }
}