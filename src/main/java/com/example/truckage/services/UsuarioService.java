package com.example.truckage.services;

import com.example.truckage.dtos.UsuarioDTO;
import com.example.truckage.exceptions.ResourceNotFoundException;
import com.example.truckage.exceptions.UnauthorizedException;
import com.example.truckage.models.Usuario;
import com.example.truckage.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final S3Service s3Service;

    @Transactional
    public Usuario criarUsuario(UsuarioDTO dto) {
        log.info("=== INICIANDO CADASTRO DE USUÁRIO ===");
        log.info("Email: {}", dto.email());
        log.info("Role: {}", dto.role());
        log.info("Nome: {}", dto.nomeCompleto());

        // Verificar se email já existe
        if (usuarioRepository.existsByEmail(dto.email())) {
            log.error("Email já cadastrado: {}", dto.email());
            throw new RuntimeException("Email já cadastrado");
        }

        // Criar usuário
        Usuario usuario = new Usuario();
        usuario.setEmail(dto.email());
        usuario.setSenha(passwordEncoder.encode(dto.senha()));
        usuario.setNomeCompleto(dto.nomeCompleto());
        usuario.setCelular(dto.celular());
        usuario.setRole(dto.role());
        usuario.setHabilitado(false);
        usuario.setDataCadastro(LocalDateTime.now());

        // ===== CAMPOS BÁSICOS =====
        if (dto.cpf() != null) usuario.setCpf(dto.cpf());
        if (dto.cnpj() != null) usuario.setCnpj(dto.cnpj());
        if (dto.endereco() != null) usuario.setEndereco(dto.endereco());
        if (dto.dataNascimento() != null) usuario.setDataNascimento(dto.dataNascimento());
        if (dto.numeroPlacas() != null) usuario.setNumeroPlacas(dto.numeroPlacas());

        // ===== REFERÊNCIAS PESSOAIS =====
        if (dto.nomeReferenciaPessoal1() != null) usuario.setNomeReferenciaPessoal1(dto.nomeReferenciaPessoal1());
        if (dto.numeroReferenciaPessoal1() != null) usuario.setNumeroReferenciaPessoal1(dto.numeroReferenciaPessoal1());
        if (dto.nomeReferenciaPessoal2() != null) usuario.setNomeReferenciaPessoal2(dto.nomeReferenciaPessoal2());
        if (dto.numeroReferenciaPessoal2() != null) usuario.setNumeroReferenciaPessoal2(dto.numeroReferenciaPessoal2());
        if (dto.nomeReferenciaPessoal3() != null) usuario.setNomeReferenciaPessoal3(dto.nomeReferenciaPessoal3());
        if (dto.numeroReferenciaPessoal3() != null) usuario.setNumeroReferenciaPessoal3(dto.numeroReferenciaPessoal3());

        // ===== REFERÊNCIAS COMERCIAIS =====
        if (dto.nomeReferenciaComercial1() != null) usuario.setNomeReferenciaComercial1(dto.nomeReferenciaComercial1());
        if (dto.numeroReferenciaComercial1() != null) usuario.setNumeroReferenciaComercial1(dto.numeroReferenciaComercial1());
        if (dto.nomeReferenciaComercial2() != null) usuario.setNomeReferenciaComercial2(dto.nomeReferenciaComercial2());
        if (dto.numeroReferenciaComercial2() != null) usuario.setNumeroReferenciaComercial2(dto.numeroReferenciaComercial2());
        if (dto.nomeReferenciaComercial3() != null) usuario.setNomeReferenciaComercial3(dto.nomeReferenciaComercial3());
        if (dto.numeroReferenciaComercial3() != null) usuario.setNumeroReferenciaComercial3(dto.numeroReferenciaComercial3());

        // ===== REFERÊNCIAS DE MOTORISTA (para empresas) =====
        if (dto.nomeReferenciaMotorista1() != null) usuario.setNomeReferenciaMotorista1(dto.nomeReferenciaMotorista1());
        if (dto.numeroReferenciaMotorista1() != null) usuario.setNumeroReferenciaMotorista1(dto.numeroReferenciaMotorista1());
        if (dto.nomeReferenciaMotorista2() != null) usuario.setNomeReferenciaMotorista2(dto.nomeReferenciaMotorista2());
        if (dto.numeroReferenciaMotorista2() != null) usuario.setNumeroReferenciaMotorista2(dto.numeroReferenciaMotorista2());
        if (dto.nomeReferenciaMotorista3() != null) usuario.setNomeReferenciaMotorista3(dto.nomeReferenciaMotorista3());
        if (dto.numeroReferenciaMotorista3() != null) usuario.setNumeroReferenciaMotorista3(dto.numeroReferenciaMotorista3());

        // ===== RESPONSÁVEL ADMINISTRATIVO =====
        if (dto.nomeResponsavelAdministrativo() != null) usuario.setNomeResponsavelAdministrativo(dto.nomeResponsavelAdministrativo());
        if (dto.telefoneResponsavelAdministrativo() != null) usuario.setTelefoneResponsavelAdministrativo(dto.telefoneResponsavelAdministrativo());

        // ===== DADOS BANCÁRIOS =====
        if (dto.banco() != null) usuario.setBanco(dto.banco());
        if (dto.agencia() != null) usuario.setAgencia(dto.agencia());
        if (dto.numeroConta() != null) usuario.setNumeroConta(dto.numeroConta());
        if (dto.tipoConta() != null) usuario.setTipoConta(dto.tipoConta());
        if (dto.titularConta() != null) usuario.setTitularConta(dto.titularConta());
        if (dto.cpfTitularConta() != null) usuario.setCpfTitularConta(dto.cpfTitularConta());

        // ===== UPLOAD DE IMAGEM DE PERFIL =====
        if (dto.imagemBase64() != null && !dto.imagemBase64().isEmpty()) {
            log.info("Processando imagem de perfil...");
            try {
                String imagemUrl = s3Service.uploadImagem(dto.imagemBase64(), "usuarios/perfil");
                usuario.setImagemPerfil(imagemUrl);
                log.info("Imagem de perfil upload concluído: {}", imagemUrl);
            } catch (Exception e) {
                log.error("Erro no upload da imagem de perfil: {}", e.getMessage());
            }
        }

        // ===== UPLOAD DE DOCUMENTOS (ANTT, CNH, etc) =====
        if (dto.documentos() != null && !dto.documentos().isEmpty()) {
            log.info("Processando documentos: {}", dto.documentos().keySet());
            for (var entry : dto.documentos().entrySet()) {
                String docKey = entry.getKey();
                String docValue = entry.getValue();

                if (docValue != null && !docValue.isEmpty() && docValue.startsWith("data:image")) {
                    log.info("Fazendo upload do documento: {}", docKey);
                    try {
                        String docUrl = s3Service.uploadImagem(docValue, "usuarios/documentos");
                        setDocumentoField(usuario, docKey, docUrl);
                        log.info("Documento {} upload concluído: {}", docKey, docUrl);
                    } catch (Exception e) {
                        log.error("Erro no upload do documento {}: {}", docKey, e.getMessage());
                    }
                }
            }
        }

        // ===== PROCESSAR PLACAS COMO CAMPOS SEPARADOS =====
        processarPlaca(dto.placa1(), usuario, "placa_1");
        processarPlaca(dto.placa2(), usuario, "placa_2");
        processarPlaca(dto.placa3(), usuario, "placa_3");

        Usuario saved = usuarioRepository.save(usuario);
        log.info("Usuário salvo com sucesso! ID: {}", saved.getUsuarioId());

        return saved;
    }

    private void processarPlaca(String valorPlaca, Usuario usuario, String nomeCampo) {
        if (valorPlaca != null && !valorPlaca.isEmpty() && valorPlaca.startsWith("data:image")) {
            log.info("Processando {} como campo separado...", nomeCampo);
            try {
                String docUrl = s3Service.uploadImagem(valorPlaca, "usuarios/documentos");
                setDocumentoField(usuario, nomeCampo, docUrl);
                log.info("{} upload concluído: {}", nomeCampo, docUrl);
            } catch (Exception e) {
                log.error("Erro no upload de {}: {}", nomeCampo, e.getMessage());
            }
        }
    }

    private void setDocumentoField(Usuario usuario, String fieldName, String value) {
        switch (fieldName) {
            // Documentos de motorista
            case "antt": usuario.setAntt(value); break;
            case "cnh": usuario.setCnh(value); break;
            case "placa_1": usuario.setPlaca1(value); break;
            case "placa_2": usuario.setPlaca2(value); break;
            case "placa_3": usuario.setPlaca3(value); break;
            case "comprovante_residencia_motorista":
                usuario.setComprovanteResidenciaMotorista(value); break;
            case "documento_dono_caminhao":
                usuario.setDocumentoDonoCaminhao(value); break;
            case "comprovante_residencia_dono_caminhao":
                usuario.setComprovanteResidenciaDonoCaminhao(value); break;

            // Documentos de empresa
            case "alvara": usuario.setAlvara(value); break;
            case "comprovante_empresa": usuario.setComprovanteEmpresa(value); break;
            case "documento_empresa": usuario.setDocumentoEmpresa(value); break;

            // Referências de motorista (são textos, não documentos)
            case "nome_referencia_motorista_1":
                usuario.setNomeReferenciaMotorista1(value); break;
            case "numero_referencia_motorista_1":
                usuario.setNumeroReferenciaMotorista1(value); break;
            case "nome_referencia_motorista_2":
                usuario.setNomeReferenciaMotorista2(value); break;
            case "numero_referencia_motorista_2":
                usuario.setNumeroReferenciaMotorista2(value); break;
            case "nome_referencia_motorista_3":
                usuario.setNomeReferenciaMotorista3(value); break;
            case "numero_referencia_motorista_3":
                usuario.setNumeroReferenciaMotorista3(value); break;

            default:
                log.warn("Tipo de documento não mapeado: {}", fieldName);
        }
    }

    private String getDocumentoField(Usuario usuario, String fieldName) {
        switch (fieldName) {
            case "imagem_perfil": return usuario.getImagemPerfil();
            case "antt": return usuario.getAntt();
            case "cnh": return usuario.getCnh();
            case "placa_1": return usuario.getPlaca1();
            case "placa_2": return usuario.getPlaca2();
            case "placa_3": return usuario.getPlaca3();
            case "comprovante_residencia_motorista":
                return usuario.getComprovanteResidenciaMotorista();
            case "documento_dono_caminhao":
                return usuario.getDocumentoDonoCaminhao();
            case "comprovante_residencia_dono_caminhao":
                return usuario.getComprovanteResidenciaDonoCaminhao();
            case "alvara": return usuario.getAlvara();
            case "comprovante_empresa": return usuario.getComprovanteEmpresa();
            case "documento_empresa": return usuario.getDocumentoEmpresa();
            default: return null;
        }
    }

    // ===== MÉTODOS EXISTENTES (mantidos iguais) =====

    public boolean validarSenha(Usuario usuario, String senha) {
        return passwordEncoder.matches(senha, usuario.getSenha());
    }

    public List<Usuario> listarUsuarios(Usuario usuarioLogado) {
        if (usuarioLogado.getRole() == Usuario.Role.admin) {
            return usuarioRepository.findAll();
        } else {
            Usuario.Role targetRole = usuarioLogado.getRole() == Usuario.Role.motorista
                    ? Usuario.Role.empresa
                    : Usuario.Role.motorista;
            return usuarioRepository.findByRole(targetRole);
        }
    }

    public Usuario buscarPorId(Integer id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
    }

    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
    }

    @Transactional
    public Usuario atualizarUsuario(Integer id, UsuarioDTO dto, Integer usuarioLogadoId, String role) {
        if (!role.equals("admin") && !id.equals(usuarioLogadoId)) {
            throw new UnauthorizedException("Não autorizado a editar este usuário");
        }

        Usuario usuario = buscarPorId(id);

        if (dto.nomeCompleto() != null) usuario.setNomeCompleto(dto.nomeCompleto());
        if (dto.email() != null) usuario.setEmail(dto.email());
        if (dto.celular() != null) usuario.setCelular(dto.celular());
        if (dto.endereco() != null) usuario.setEndereco(dto.endereco());

        if (role.equals("admin") && dto.role() != null) {
            usuario.setRole(dto.role());
        }

        usuario.setDataAtualizacao(LocalDateTime.now());
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public void alterarSenha(Integer id, String senhaAtual, String novaSenha, Integer usuarioLogadoId) {
        if (!id.equals(usuarioLogadoId)) {
            throw new UnauthorizedException("Não autorizado a alterar senha de outro usuário");
        }

        Usuario usuario = buscarPorIdComSenha(id);

        if (!passwordEncoder.matches(senhaAtual, usuario.getSenha())) {
            throw new UnauthorizedException("Senha atual incorreta");
        }

        usuario.setSenha(passwordEncoder.encode(novaSenha));
        usuario.setDataAtualizacao(LocalDateTime.now());
        usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario atualizarDocumento(Integer id, String docType, String imageBase64, Integer usuarioLogadoId) {
        if (!id.equals(usuarioLogadoId)) {
            throw new UnauthorizedException("Não autorizado a atualizar documento de outro usuário");
        }

        Usuario usuario = buscarPorId(id);

        String oldFileUrl = getDocumentoField(usuario, docType);
        if (oldFileUrl != null) {
            s3Service.deletarArquivo(oldFileUrl);
        }

        String folder = docType.equals("imagem_perfil") ? "usuarios/perfil" : "usuarios/documentos";
        String newFileUrl = s3Service.uploadImagem(imageBase64, folder);

        setDocumentoField(usuario, docType, newFileUrl);
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public void atualizarPushToken(Integer id, String pushToken, Integer usuarioLogadoId) {
        if (!id.equals(usuarioLogadoId)) {
            throw new UnauthorizedException("Não autorizado a atualizar push token de outro usuário");
        }
        usuarioRepository.updatePushToken(id, pushToken);
    }

    @Transactional
    public Usuario buscarPorIdComSenha(Integer id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
    }

    @Transactional
    public String gerarTokenResetSenha(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);
        if (usuario == null) return null;

        String token = UUID.randomUUID().toString();
        LocalDateTime expires = LocalDateTime.now().plusHours(1);

        usuarioRepository.updateResetToken(email, token, expires);
        return token;
    }

    @Transactional
    public Usuario validarTokenEResetarSenha(String token, String novaSenha) {
        Usuario usuario = usuarioRepository
                .findByPasswordResetTokenAndPasswordResetExpiresGreaterThan(token, LocalDateTime.now())
                .orElse(null);

        if (usuario == null) return null;

        usuarioRepository.updateSenhaAndClearResetToken(
                usuario.getUsuarioId(), passwordEncoder.encode(novaSenha));
        return usuario;
    }
}