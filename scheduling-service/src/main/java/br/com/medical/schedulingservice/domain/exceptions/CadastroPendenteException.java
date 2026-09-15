package br.com.medical.schedulingservice.domain.exceptions;
public class CadastroPendenteException extends RuntimeException {
    public CadastroPendenteException() { super("CADASTRO_PENDENTE: complete seu cadastro em /api/v1/cadastro/me"); }
}
