package controller;

import model.dao.ProjetoDAO;
import model.dao.TerrenoDAO;
import model.entities.Endereco;
import model.entities.Projeto;
import model.entities.Terreno;
import view.screens.dialogs.Gestor.DlgCadastroTerreno;

/**
 * @author Viktin
 */
public class TerrenoController {

    private final DlgCadastroTerreno view;
    private final TerrenoDAO dao;
    private Projeto projetoPai = null;
    private Terreno terrenoEmEdicao = null;

    public TerrenoController(DlgCadastroTerreno view) {
        this.view = view;
        this.dao = new TerrenoDAO();
    }

    public void setProjetoPai(Projeto p) {
        this.projetoPai = p;
    }

    public void preencherTelaParaEdicao(Terreno t) {
        this.terrenoEmEdicao = t;
        if (t == null) return;

        view.setTitle("Editar Terreno: " + t.getNome());
        view.setTextoNome(t.getNome());
        view.setTextoReferencia(t.getReferencia());
        view.setTextoGabarito(t.getGabaritoAltura());
        view.setTextoDescricao(t.getDescricao());
        view.setSelecionadoTopografia(t.getTopografia());
        view.setSelecionadoTipo(t.getTipoSolo());

        // Conversão UI via método auxiliar
        view.setTextoArea(formatarParaMoedaOuDecimal(t.getAreaTotal()));
        view.setTextoValor(formatarParaMoedaOuDecimal(t.getValorCompra()));
        view.setTextoCoeficiente(formatarParaMoedaOuDecimal(t.getCoeficienteAproveitamento()));
        view.setTextoTaxa(formatarParaMoedaOuDecimal(t.getTaxaOcupacao()));

        if (t.getEndereco() != null) {
            preencherEndereco(t.getEndereco());
        }
    }

    public void salvarTerreno() {
        if (!validarCamposObrigatorios()) return;

        try {
            Terreno terreno = (this.terrenoEmEdicao != null) ? this.terrenoEmEdicao : new Terreno();
            
            // Montagem do Objeto (Delegada a métodos privados para limpeza do código principal)
            capturarDadosBasicos(terreno);
            capturarEndereco(terreno);

            // Persistência
            dao.salvar(terreno);

            // Vínculo com Projeto
            vincularAoProjetoPai(terreno);

            view.exibeMensagem("Terreno salvo com sucesso!");
            view.dispose();

        } catch (NumberFormatException e) {
            view.exibeMensagem("Erro: Verifique os campos numéricos. Use vírgula para decimais.");
        } catch (Exception e) {
            view.exibeMensagem("Erro ao salvar terreno: " + e.getMessage());
        }
    }

    // --- Métodos de Apoio (Helper Methods) ---

    private void capturarDadosBasicos(Terreno t) {
        t.setNome(view.getNome());
        t.setAreaTotal(converterParaDouble(view.getAreaTotal()));
        t.setTopografia(view.getTopografia());
        t.setTipoSolo(view.getTipoSolo());
        t.setGabaritoAltura(view.getGabaritoAltura());
        t.setReferencia(view.getReferencia());
        t.setDescricao(view.getDescricao());
        t.setCoeficienteAproveitamento(converterParaDouble(view.getCoeficienteAproveitamento()));
        t.setTaxaOcupacao(converterParaDouble(view.getTaxaOcupacao()));
        t.setValorCompra(converterParaDouble(view.getValorCompra()));
    }

    private void capturarEndereco(Terreno t) {
        Endereco end = (t.getEndereco() != null) ? t.getEndereco() : new Endereco();
        end.setCep(view.getCep().replaceAll("[^0-9]", ""));
        end.setCidade(view.getCidade());
        end.setLogradouro(view.getLogradouro());
        end.setBairro(view.getBairro());
        end.setNumero(view.getNumero());
        t.setEndereco(end);
    }

    private void vincularAoProjetoPai(Terreno t) {
        if (this.projetoPai != null) {
            this.projetoPai.setTerreno(t);
            new ProjetoDAO().salvar(this.projetoPai);
        }
    }

    private boolean validarCamposObrigatorios() {
        if (view.getNome().isEmpty() || view.getAreaTotal().isEmpty()) {
            view.exibeMensagem("Nome e Área Total são obrigatórios.");
            return false;
        }
        if (view.getCep().isEmpty() || view.getCidade().isEmpty()) {
            view.exibeMensagem("Dados básicos de endereço são obrigatórios.");
            return false;
        }
        return true;
    }

    private Double converterParaDouble(String valor) {
        if (valor == null || valor.trim().isEmpty()) return null;
        // Limpa R$, milhar e converte decimal
        String limpo = valor.replace("R$", "").replace(".", "").replace(",", ".").trim();
        return Double.valueOf(limpo);
    }

    private String formatarParaMoedaOuDecimal(Double valor) {
        return (valor == null) ? "" : String.format("%.2f", valor).replace(".", ",");
    }

    private void preencherEndereco(Endereco e) {
        view.setTextoCep(e.getCep());
        view.setTextoCidade(e.getCidade());
        view.setTextoLogradouro(e.getLogradouro());
        view.setTextoBairro(e.getBairro());
        view.setTextoNumero(e.getNumero());
    }
}