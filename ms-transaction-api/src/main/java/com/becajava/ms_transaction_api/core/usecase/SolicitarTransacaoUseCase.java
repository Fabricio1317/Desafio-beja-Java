    package com.becajava.ms_transaction_api.core.usecase;

    import com.becajava.ms_transaction_api.core.domain.Transacao;
    import com.becajava.ms_transaction_api.core.gateway.MensageriaGateway;
    import com.becajava.ms_transaction_api.core.gateway.TransacaoGateway;
    import com.becajava.ms_transaction_api.dto.TransacaoRequestDTO;
    import com.becajava.ms_transaction_api.dto.TransacaoRespondeDTO;

    public class SolicitarTransacaoUseCase {

        private final TransacaoGateway transacaoGateway;
        private final MensageriaGateway mensageriaGateway;

        public SolicitarTransacaoUseCase(TransacaoGateway transacaoGateway, MensageriaGateway mensageriaGateway) {
            this.transacaoGateway = transacaoGateway;
            this.mensageriaGateway = mensageriaGateway;
        }

        public TransacaoRespondeDTO execute(TransacaoRequestDTO dto){
            Transacao novaTransacao = new Transacao(dto.pagadorId(), dto.recebedorId(), dto.valor(), dto.tipo());
            Transacao transacaoSalva = transacaoGateway.salvar(novaTransacao);
            System.out.println("DTO: "+ dto.pagadorId()+ dto.recebedorId());

            mensageriaGateway.enviarParaFila(transacaoSalva);

            return new TransacaoRespondeDTO(transacaoSalva);


        }
    }
