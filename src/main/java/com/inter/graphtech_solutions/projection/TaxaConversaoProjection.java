package com.inter.graphtech_solutions.projection;

import java.math.BigDecimal;

public interface TaxaConversaoProjection {
    Integer getIdPessoa();
    String getNome();
    Integer getTotalOrcamentos();
    Integer getOrcamentosConvertidos();
    BigDecimal getTaxaConversao();
    String getClassificacao();
    Integer getVendas();
}