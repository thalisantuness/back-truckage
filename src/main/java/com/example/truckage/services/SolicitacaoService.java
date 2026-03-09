package com.example.truckage.services;

import com.example.truckage.exceptions.ResourceNotFoundException;
import com.example.truckage.exceptions.UnauthorizedException;
import com.example.truckage.models.Frete;
import com.example.truckage.models.SolicitacaoFrete;
import com.example.truckage.models.Usuario;
import com.example.truckage.repositories.FreteRepository;
import com.example.truckage.repositories.SolicitacaoRepository;
import com.example.truckage.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SolicitacaoService {

    private final SolicitacaoRepository solicitacaoRepository;
    private final FreteRepository freteRepository;
    private final UsuarioRepository usuarioRepository;
    private final ChatService chatService;
    private final PushNotificationService pushNotificationService;

    @Transactional
    public SolicitacaoFrete solicitar(Integer freteId, Integer motoristaId) {
        Frete frete = freteRepository.findById(freteId)
                .orElseThrow(() -> new ResourceNotFoundException("Frete não encontrado"));

        Usuario motorista = usuarioRepository.findById(motoristaId)
                .orElseThrow(() -> new ResourceNotFoundException("Motorista não encontrado"));

        // Verificar se frete está disponível
        if (!frete.getStatus().equals(Frete.StatusFrete.anunciado)) {
            throw new RuntimeException("Frete não disponível para solicitação");
        }

        // Verificar se já existe solicitação
        if (solicitacaoRepository.existsByFreteAndMotorista(frete, motorista)) {
            throw new RuntimeException("Você já solicitou este frete");
        }

        // Criar solicitação
        SolicitacaoFrete solicitacao = new SolicitacaoFrete();
        solicitacao.setFrete(frete);
        solicitacao.setMotorista(motorista);
        solicitacao.setEmpresa(frete.getEmpresa());

        SolicitacaoFrete solicitacaoSalva = solicitacaoRepository.save(solicitacao);

        // Enviar push para a empresa
        if (frete.getEmpresa().getPushToken() != null) {
            pushNotificationService.enviarNotificacao(
                    frete.getEmpresa().getPushToken(),
                    "Nova solicitação de frete",
                    motorista.getNomeCompleto() + " quer realizar o frete #" + freteId,
                    solicitacaoSalva
            );
        }

        return solicitacaoSalva;
    }

    public List<SolicitacaoFrete> listarPorEmpresa(Integer empresaId) {
        Usuario empresa = usuarioRepository.findById(empresaId)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada"));

        return solicitacaoRepository.findByEmpresaOrderByDataSolicitacaoDesc(empresa);
    }

    public List<SolicitacaoFrete> listarPorMotorista(Integer motoristaId) {
        Usuario motorista = usuarioRepository.findById(motoristaId)
                .orElseThrow(() -> new ResourceNotFoundException("Motorista não encontrado"));

        return solicitacaoRepository.findByMotoristaOrderByDataSolicitacaoDesc(motorista);
    }

    @Transactional
    public SolicitacaoFrete responder(Integer solicitacaoId, SolicitacaoFrete.StatusSolicitacao status, Integer empresaId) {
        SolicitacaoFrete solicitacao = solicitacaoRepository.findById(solicitacaoId)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitação não encontrada"));

        if (!solicitacao.getEmpresa().getUsuarioId().equals(empresaId)) {
            throw new UnauthorizedException("Não autorizado a responder esta solicitação");
        }

        if (!solicitacao.getStatus().equals(SolicitacaoFrete.StatusSolicitacao.pendente)) {
            throw new RuntimeException("Solicitação já respondida");
        }

        solicitacao.setStatus(status);
        solicitacao.setDataResposta(LocalDateTime.now());

        SolicitacaoFrete solicitacaoAtualizada = solicitacaoRepository.save(solicitacao);

        if (status.equals(SolicitacaoFrete.StatusSolicitacao.aceita)) {
            // Atualizar frete
            Frete frete = solicitacao.getFrete();
            frete.setMotorista(solicitacao.getMotorista());
            frete.setStatus(Frete.StatusFrete.em_andamento);
            freteRepository.save(frete);

            // Rejeitar outras solicitações
            solicitacaoRepository.rejeitarOutrasSolicitacoes(
                    frete.getFreteId(), solicitacaoId, LocalDateTime.now());

            // Criar conversa
            chatService.criarConversaSeNaoExistir(
                    empresaId,
                    solicitacao.getMotorista().getUsuarioId(),
                    frete.getFreteId()
            );

            // Enviar push para o motorista
            if (solicitacao.getMotorista().getPushToken() != null) {
                pushNotificationService.enviarNotificacao(
                        solicitacao.getMotorista().getPushToken(),
                        "Solicitação aceita!",
                        "Sua solicitação para o frete #" + frete.getFreteId() + " foi aceita",
                        solicitacaoAtualizada
                );
            }
        } else {
            // Enviar push para o motorista sobre rejeição
            if (solicitacao.getMotorista().getPushToken() != null) {
                pushNotificationService.enviarNotificacao(
                        solicitacao.getMotorista().getPushToken(),
                        "Solicitação não aceita",
                        "Infelizmente sua solicitação para o frete #" + solicitacao.getFrete().getFreteId() + " não foi aceita",
                        solicitacaoAtualizada
                );
            }
        }

        return solicitacaoAtualizada;
    }

    public SolicitacaoFrete buscarPorId(Integer id) {
        return solicitacaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitação não encontrada"));
    }
}