package com.inter.graphtech_solutions.repositories;

import java.time.LocalDateTime;
import java.math.BigDecimal;

public interface LogPrecoProjection {
    String getProduto();
    LocalDateTime getDataAlteracao();
    BigDecimal getPrecoAntigo();
    BigDecimal getPrecoNovo();
    String getUsuario();
}