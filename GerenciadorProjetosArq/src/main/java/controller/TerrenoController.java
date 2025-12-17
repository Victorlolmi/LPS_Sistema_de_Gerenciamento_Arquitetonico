/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import model.dao.ProjetoDAO;
import model.dao.TerrenoDAO;
import model.entities.Endereco;
import model.entities.Projeto;
import model.entities.Terreno;
import view.screens.dialogs.DlgCadastroTerreno;

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
        
        if (t != null) {
            view.setTitle("Editar Terreno: " + t.getNome());
            
            view.setTextoNome(t.getNome());
            view.setTextoReferencia(t.getReferencia());
            view.setTextoGabarito(t.getGabaritoAltura());
            view.setTextoDescricao(t.getDescricao());
            
            view.setSelecionadoTopografia(t.getTopografia());
            view.setSelecionadoTipo(t.getTipoSolo());

            // Formatação UI (Double -> String com vírgula)
            if (t.getAreaTotal() != null) {
                view.setTextoArea(String.format("%.2f", t.getAreaTotal()).replace(".", ","));
            }
            if (t.getValorCompra() != null) {
                view.setTextoValor(String.format("%.2f", t.getValorCompra()).replace(".", ","));
            }
            if (t.getCoeficienteAproveitamento() != null) {
                view.setTextoCoeficiente(String.valueOf(t.getCoeficienteAproveitamento()).replace(".", ","));
            }
            if (t.getTaxaOcupacao() != null) {
                view.setTextoTaxa(String.valueOf(t.getTaxaOcupacao()).replace(".", ","));
            }

            if (t.getEndereco() != null) {
                view.setTextoCep(t.getEndereco().getCep());
                view.setTextoCidade(t.getEndereco().getCidade());
                view.setTextoLogradouro(t.getEndereco().getLogradouro());
                view.setTextoBairro(t.getEndereco().getBairro());
                view.setTextoNumero(t.getEndereco().getNumero());
            }
        }
    }

    public void salvarTerreno() {
        String nome = view.getNome();
        String areaStr = view.getAreaTotal(); 
        String topografia = view.getTopografia();
        String tipoSolo = view.getTipoSolo();
        String caStr = view.getCoeficienteAproveitamento();
        String toStr = view.getTaxaOcupacao();
        String valorStr = view.getValorCompra();
        String gabarito = view.getGabaritoAltura();
        String referencia = view.getReferencia();
        String descricao = view.getDescricao();

        String cep = view.getCep();
        String cidade = view.getCidade();
        String logradouro = view.getLogradouro();
        String bairro = view.getBairro();
        String numero = view.getNumero();

        if (nome.isEmpty() || areaStr.isEmpty()) {
            view.exibeMensagem("Erro: Nome e Área Total são obrigatórios.");
            return;
        }

        if (cep.isEmpty() || cidade.isEmpty() || logradouro.isEmpty()) {
            view.exibeMensagem("Erro: Preencha os dados básicos de endereço (CEP, Cidade, Rua).");
            return;
        }
        
        // Estratégia de Update vs Insert: Se já existe objeto em memória, usa ele (preserva ID).
        Terreno terreno = (this.terrenoEmEdicao != null) ? this.terrenoEmEdicao : new Terreno();

        try {
            // Sanitização numérica (PT-BR -> Double)
            // Replace manual necessário pois o Java swing não lida nativamente com locale em campos de texto simples
            double areaTotal = Double.parseDouble(areaStr.replace(",", "."));
            terreno.setAreaTotal(areaTotal);

            if (!caStr.isEmpty()) {
                double ca = Double.parseDouble(caStr.replace(",", "."));
                terreno.setCoeficienteAproveitamento(ca);
            } else {
                terreno.setCoeficienteAproveitamento(null);
            }
            
            if (!toStr.isEmpty()) {
                terreno.setTaxaOcupacao(Double.parseDouble(toStr.replace(",", ".")));
            } else {
                terreno.setTaxaOcupacao(null);
            }

            if (!valorStr.isEmpty()) {
                String valorLimpo = valorStr.replace("R$", "")
                                            .replace(".", "")   // Remove milhar
                                            .replace(",", ".")  // Decimal
                                            .trim();
                terreno.setValorCompra(Double.parseDouble(valorLimpo));
            } else {
                terreno.setValorCompra(null);
            }

            terreno.setNome(nome);
            terreno.setTopografia(topografia);
            terreno.setTipoSolo(tipoSolo);
            terreno.setGabaritoAltura(gabarito);
            terreno.setReferencia(referencia);
            terreno.setDescricao(descricao);

            // Se o terreno já tem endereço (edição), atualizamos a referência existente
            // Isso previne problemas com Hibernate/JPA criando linhas órfãs
            Endereco endereco = (terreno.getEndereco() != null) ? terreno.getEndereco() : new Endereco();

            endereco.setCep(cep.replaceAll("[^0-9]", "")); 
            endereco.setCidade(cidade);
            endereco.setLogradouro(logradouro);
            endereco.setBairro(bairro);
            endereco.setNumero(numero);

            terreno.setEndereco(endereco);

            // Persistence: CascadeType.ALL na entidade cuida do Endereço
            dao.salvar(terreno);

            // Vinculação reversa manual (Foreign Key no Projeto)
            if (this.projetoPai != null) {
                this.projetoPai.setTerreno(terreno); 
                
                ProjetoDAO projetoDao = new ProjetoDAO();
                projetoDao.salvar(this.projetoPai);
            }

            view.exibeMensagem("Terreno salvo com sucesso!");
            view.dispose();

        } catch (NumberFormatException e) {
            view.exibeMensagem("Erro: Verifique os campos numéricos (Área ou Coeficiente).\nUse apenas números e vírgula/ponto.");
        } catch (Exception e) {
            view.exibeMensagem("Erro ao salvar terreno: " + e.getMessage());
            e.printStackTrace();
        }
    }
}