package com.example.truckage.services;

import com.example.truckage.exceptions.ResourceNotFoundException;
import com.example.truckage.exceptions.UnauthorizedException;
import com.example.truckage.models.Conversa;
import com.example.truckage.models.Frete;
import com.example.truckage.models.Mensagem;
import com.example.truckage.models.Usuario;
import com.example.truckage.repositories.ConversaRepository;
import com.example.truckage.repositories.FreteRepository;
import com.example.truckage.repositories.MensagemRepository;
import com.example.truckage.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ConversaRepository conversaRepository;
    private final MensagemRepository mensagemRepository;
    private final FreteRepository freteRepository;
    private final UsuarioRepository usuarioRepository;
    private final PushNotificationService pushNotificationService;

    @Transactional
    public Conversa criarConversaSeNaoExistir(Integer usuario1Id, Integer usuario2Id, Integer freteId) {
        Usuario usuario1 = usuarioRepository.findById(usuario1Id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário 1 não encontrado"));
        Usuario usuario2 = usuarioRepository.findById(usuario2Id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário 2 não encontrado"));
        Frete frete = freteRepository.findById(freteId)
                .orElseThrow(() -> new ResourceNotFoundException("Frete não encontrado"));

        // Verificar se os usuários são empresa e motorista
        if (!(usuario1.getRole().equals(Usuario.Role.motorista) &&
                usuario2.getRole().equals(Usuario.Role.empresa) ||
                usuario1.getRole().equals(Usuario.Role.empresa) &&
                        usuario2.getRole().equals(Usuario.Role.motorista))) {
            throw new UnauthorizedException("Conversas só permitidas entre motoristas e empresas");
        }

        // Verificar se os usuários estão vinculados ao frete
        if (!frete.getEmpresa().getUsuarioId().equals(usuario1Id) &&
                !frete.getEmpresa().getUsuarioId().equals(usuario2Id) &&
                !(frete.getMotorista() != null &&
                        (frete.getMotorista().getUsuarioId().equals(usuario1Id) ||
                                frete.getMotorista().getUsuarioId().equals(usuario2Id)))) {
            throw new UnauthorizedException("Usuários não vinculados a este frete");
        }

        // Verificar se conversa já existe
        return conversaRepository.findByFreteId(freteId)
                .orElseGet(() -> {
                    Conversa novaConversa = new Conversa();
                    novaConversa.setUsuario1(usuario1);
                    novaConversa.setUsuario2(usuario2);
                    novaConversa.setFrete(frete);
                    novaConversa.setUltimaMensagem(LocalDateTime.now());
                    return conversaRepository.save(novaConversa);
                });
    }

    public List<Conversa> listarConversas(Integer usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        return conversaRepository.findByUsuario1OrUsuario2OrderByUltimaMensagemDesc(usuario, usuario);
    }

    public List<Mensagem> listarMensagens(Integer conversaId, Integer usuarioId) {
        Conversa conversa = conversaRepository.findById(conversaId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversa não encontrada"));

        // Verificar se usuário participa da conversa
        if (!conversa.getUsuario1().getUsuarioId().equals(usuarioId) &&
                !conversa.getUsuario2().getUsuarioId().equals(usuarioId)) {
            throw new UnauthorizedException("Acesso não autorizado a esta conversa");
        }

        return mensagemRepository.findByConversaOrderByDataEnvioAsc(conversa);
    }

    @Transactional
    public Mensagem enviarMensagem(Integer conversaId, Integer remetenteId, String conteudo) {
        Conversa conversa = conversaRepository.findById(conversaId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversa não encontrada"));

        Usuario remetente = usuarioRepository.findById(remetenteId)
                .orElseThrow(() -> new ResourceNotFoundException("Remetente não encontrado"));

        // Verificar se remetente participa da conversa
        if (!conversa.getUsuario1().getUsuarioId().equals(remetenteId) &&
                !conversa.getUsuario2().getUsuarioId().equals(remetenteId)) {
            throw new UnauthorizedException("Remetente não participa desta conversa");
        }

        // Criar mensagem
        Mensagem mensagem = new Mensagem();
        mensagem.setConversa(conversa);
        mensagem.setRemetente(remetente);
        mensagem.setConteudo(conteudo);
        mensagem.setDataEnvio(LocalDateTime.now());

        Mensagem mensagemSalva = mensagemRepository.save(mensagem);

        // Atualizar última mensagem da conversa
        conversa.setUltimaMensagem(LocalDateTime.now());
        conversaRepository.save(conversa);

        // Enviar push notification para o destinatário
        Usuario destinatario = conversa.getUsuario1().getUsuarioId().equals(remetenteId)
                ? conversa.getUsuario2() : conversa.getUsuario1();

        if (destinatario.getPushToken() != null) {
            pushNotificationService.enviarNotificacao(
                    destinatario.getPushToken(),
                    "Nova mensagem de " + remetente.getNomeCompleto(),
                    conteudo.length() > 100 ? conteudo.substring(0, 97) + "..." : conteudo,
                    conversa
            );
        }

        return mensagemSalva;
    }

    @Transactional
    public void marcarMensagemComoLida(Integer mensagemId, Integer usuarioId) {
        mensagemRepository.marcarComoLida(mensagemId, usuarioId);
    }

    public long contarMensagensNaoLidas(Integer conversaId, Integer usuarioId) {
        Conversa conversa = conversaRepository.findById(conversaId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversa não encontrada"));

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        return mensagemRepository.countByConversaAndLidaFalseAndRemetenteNot(conversa, usuario);
    }
}