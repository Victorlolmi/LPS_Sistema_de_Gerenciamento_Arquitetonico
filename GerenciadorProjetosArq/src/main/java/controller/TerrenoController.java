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
 *
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
        this.terrenoEmEdicao = t; // Guarda o objeto
        
        if (t != null) {
            view.setTitle("Editar Terreno: " + t.getNome());
            
            // Textos Simples
            view.setTextoNome(t.getNome());
            view.setTextoReferencia(t.getReferencia());
            view.setTextoGabarito(t.getGabaritoAltura());
            view.setTextoDescricao(t.getDescricao());
            
            // Combos
            view.setSelecionadoTopografia(t.getTopografia());
            view.setSelecionadoTipo(t.getTipoSolo());

            // Formatação de Números (Logica de Apresentação)
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

            // Endereço
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
        // -----------------------------------------------------------
        // 1. RECUPERA OS DADOS DA TELA
        // -----------------------------------------------------------
        String nome = view.getNome();
        String areaStr = view.getAreaTotal(); // Vem como String, precisa converter
        String topografia = view.getTopografia();
        String tipoSolo = view.getTipoSolo();
        String caStr = view.getCoeficienteAproveitamento(); // String
        String toStr = view.getTaxaOcupacao();
        String valorStr = view.getValorCompra();
        String gabarito = view.getGabaritoAltura();
        String referencia = view.getReferencia();
        String descricao = view.getDescricao();

        // Dados do Endereço
        String cep = view.getCep();
        String cidade = view.getCidade();
        String logradouro = view.getLogradouro();
        String bairro = view.getBairro();
        String numero = view.getNumero();

        // -----------------------------------------------------------
        // 2. VALIDAÇÕES BÁSICAS
        // -----------------------------------------------------------
        if (nome.isEmpty() || areaStr.isEmpty()) {
            view.exibeMensagem("Erro: Nome e Área Total são obrigatórios.");
            return;
        }

        if (cep.isEmpty() || cidade.isEmpty() || logradouro.isEmpty()) {
            view.exibeMensagem("Erro: Preencha os dados básicos de endereço (CEP, Cidade, Rua).");
            return;
        }
        
        // -----------------------------------------------------------
        // 3. IDENTIFICAR SE É NOVO OU EDIÇÃO
        // -----------------------------------------------------------
        Terreno terreno;
        if (this.terrenoEmEdicao != null) {
            terreno = this.terrenoEmEdicao; 
        } else {
            terreno = new Terreno(); 
        }
        
        if (terrenoEmEdicao != null) {
            terreno = terrenoEmEdicao; // Usa o existente (Mantém o ID)
        } else {
            terreno = new Terreno(); // Cria um novo (Sem ID)
        }
        
        if (!toStr.isEmpty()) {
            terreno.setTaxaOcupacao(Double.parseDouble(toStr.replace(",", ".")));
        } else {
            terreno.setTaxaOcupacao(null);
        }

        try {
            // -----------------------------------------------------------
            // 4. CONVERSÃO DE NÚMEROS (Double)
            // -----------------------------------------------------------
            // Troca vírgula por ponto para evitar erro (ex: "12,5" -> "12.5")
            double areaTotal = Double.parseDouble(areaStr.replace(",", "."));
            terreno.setAreaTotal(areaTotal);

            // Coeficiente de Aproveitamento (C.A.)
            if (!caStr.isEmpty()) {
                double ca = Double.parseDouble(caStr.replace(",", "."));
                terreno.setCoeficienteAproveitamento(ca);
            } else {
                terreno.setCoeficienteAproveitamento(null);
            }
            
            // Taxa de Ocupação (T.O.) - CORRIGIDO (Movido para dentro do Try)
            if (!toStr.isEmpty()) {
                terreno.setTaxaOcupacao(Double.parseDouble(toStr.replace(",", ".")));
            } else {
                terreno.setTaxaOcupacao(null);
            }

            // Valor de Compra
            if (!valorStr.isEmpty()) {
                String valorLimpo = valorStr.replace("R$", "")
                                            .replace(".", "")   // Remove ponto de milhar
                                            .replace(",", ".")  // Troca vírgula decimal por ponto
                                            .trim();
                terreno.setValorCompra(Double.parseDouble(valorLimpo));
            } else {
                terreno.setValorCompra(null);
            }

            // -----------------------------------------------------------
            // 5. POPULAR OBJETO TERRENO
            // -----------------------------------------------------------
            terreno.setNome(nome);
            terreno.setTopografia(topografia);
            terreno.setTipoSolo(tipoSolo);
            terreno.setGabaritoAltura(gabarito);
            terreno.setReferencia(referencia);
            terreno.setDescricao(descricao);

            // -----------------------------------------------------------
            // 6. TRATAMENTO DO ENDEREÇO (Vinculado)
            // -----------------------------------------------------------
            // Se o terreno já tem endereço, atualizamos ele. Se não, criamos um novo.
            Endereco endereco = (terreno.getEndereco() != null) ? terreno.getEndereco() : new Endereco();

            endereco.setCep(cep.replaceAll("[^0-9]", "")); // Limpa o CEP (só números)
            endereco.setCidade(cidade);
            endereco.setLogradouro(logradouro);
            endereco.setBairro(bairro);
            endereco.setNumero(numero);

            // Vincula o endereço ao terreno
            terreno.setEndereco(endereco);

            // -----------------------------------------------------------
            // 7. SALVAR NO BANCO
            // -----------------------------------------------------------
            // O CascadeType.ALL na entidade Terreno fará o endereço ser salvo/atualizado automaticamente!
            dao.salvar(terreno);

           // SE tivermos um projeto pai, fazemos o vínculo agora
            if (this.projetoPai != null) {
                this.projetoPai.setTerreno(terreno); // Vincula na memória
                
                // Salva o Projeto para persistir o vínculo (chave estrangeira)
                ProjetoDAO projetoDao = new ProjetoDAO();
                projetoDao.salvar(this.projetoPai);
            }

            view.exibeMensagem("Terreno salvo com sucesso!");
            view.dispose(); // Fecha a janela

        } catch (NumberFormatException e) {
            view.exibeMensagem("Erro: Verifique os campos numéricos (Área ou Coeficiente).\nUse apenas números e vírgula/ponto.");
        } catch (Exception e) {
            view.exibeMensagem("Erro ao salvar terreno: " + e.getMessage());
            e.printStackTrace();
        }
    }
}