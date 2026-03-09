package com.example.truckage.services;

import com.example.truckage.dtos.FreteDTO;
import com.example.truckage.exceptions.ResourceNotFoundException;
import com.example.truckage.exceptions.UnauthorizedException;
import com.example.truckage.models.Frete;
import com.example.truckage.models.Usuario;
import com.example.truckage.repositories.FreteRepository;
import com.example.truckage.repositories.UsuarioRepository;
import com.example.truckage.specifications.FreteSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FreteService {

    private final FreteRepository freteRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public Frete criarFrete(FreteDTO dto, Integer usuarioLogadoId) {
        Usuario empresa = usuarioRepository.findById(dto.empresaId())
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada"));

        if (!empresa.getRole().equals(Usuario.Role.empresa) ||
                !empresa.getUsuarioId().equals(usuarioLogadoId)) {
            throw new UnauthorizedException("Não autorizado");
        }

        Frete frete = new Frete();
        frete.setEmpresa(empresa);
        frete.setOrigemEstado(dto.origemEstado());
        frete.setOrigemCidade(dto.origemCidade());
        frete.setDestinoEstado(dto.destinoEstado());
        frete.setDestinoCidade(dto.destinoCidade());
        frete.setValorFrete(dto.valorFrete());
        frete.setPrecisaLona(dto.precisaLona());
        frete.setProdutoQuimico(dto.produtoQuimico());
        frete.setObservacoesMotorista(dto.observacoesMotorista());
        frete.setVeiculoTracao(dto.veiculoTracao());
        frete.setTiposCarreta(dto.tiposCarreta());
        frete.setComprimentoCarreta(dto.comprimentoCarreta());
        frete.setNumeroEixos(dto.numeroEixos());
        frete.setConfiguracaoModelo(dto.configuracaoModelo());
        frete.setTipoCarga(dto.tipoCarga());
        frete.setObservacoesCarga(dto.observacoesCarga());
        frete.setDataPrevistaEntrega(dto.dataPrevistaEntrega());

        return freteRepository.save(frete);
    }

    public List<Frete> listarFretes(Usuario usuario, FreteDTO filtros) {
        Specification<Frete> spec = Specification.where(null);

        if (usuario.getRole().equals(Usuario.Role.motorista)) {
            // Motorista vê apenas fretes anunciados de outras empresas
            spec = spec.and(FreteSpecifications.statusAnunciado())
                    .and(FreteSpecifications.empresaDiferente(usuario));
        } else {
            // Empresa vê seus próprios fretes
            spec = spec.and(FreteSpecifications.pertenceAoUsuario(usuario));
        }

        // Aplicar filtros adicionais
        if (filtros != null) {
            if (filtros.origemCidade() != null) {
                spec = spec.and(FreteSpecifications.origemCidadeLike(filtros.origemCidade()));
            }
            if (filtros.destinoCidade() != null) {
                spec = spec.and(FreteSpecifications.destinoCidadeLike(filtros.destinoCidade()));
            }
            if (filtros.tipoCarga() != null) {
                spec = spec.and(FreteSpecifications.tipoCargaLike(filtros.tipoCarga()));
            }
            if (filtros.valorFrete() != null) {
                spec = spec.and(FreteSpecifications.valorFreteMaiorQue(filtros.valorFrete()));
            }
        }

        return freteRepository.findAll(spec);
    }

    public Frete buscarPorId(Integer id) {
        return freteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Frete não encontrado"));
    }

    @Transactional
    public Frete atualizarFrete(Integer id, FreteDTO dto, Integer usuarioLogadoId) {
        Frete frete = buscarPorId(id);

        if (!frete.getEmpresa().getUsuarioId().equals(usuarioLogadoId)) {
            throw new UnauthorizedException("Não autorizado a editar este frete");
        }

        // Atualizar campos permitidos
        if (dto.origemEstado() != null) frete.setOrigemEstado(dto.origemEstado());
        if (dto.origemCidade() != null) frete.setOrigemCidade(dto.origemCidade());
        if (dto.destinoEstado() != null) frete.setDestinoEstado(dto.destinoEstado());
        if (dto.destinoCidade() != null) frete.setDestinoCidade(dto.destinoCidade());
        if (dto.valorFrete() != null) frete.setValorFrete(dto.valorFrete());
        if (dto.status() != null) frete.setStatus(dto.status());

        return freteRepository.save(frete);
    }

    @Transactional
    public void deletarFrete(Integer id, Integer usuarioLogadoId) {
        Frete frete = buscarPorId(id);

        if (!frete.getEmpresa().getUsuarioId().equals(usuarioLogadoId)) {
            throw new UnauthorizedException("Não autorizado a deletar este frete");
        }

        freteRepository.delete(frete);
    }

    @Transactional
    public void atribuirMotorista(Integer freteId, Integer motoristaId) {
        Frete frete = buscarPorId(freteId);
        Usuario motorista = usuarioRepository.findById(motoristaId)
                .orElseThrow(() -> new ResourceNotFoundException("Motorista não encontrado"));

        frete.setMotorista(motorista);
        frete.setStatus(Frete.StatusFrete.em_andamento);
        freteRepository.save(frete);
    }
}