package com.javanauta.usuario.business;


import com.javanauta.usuario.business.converter.UsuarioConverter;
import com.javanauta.usuario.business.dto.UsuarioDTO;
import com.javanauta.usuario.insfrastructure.entity.Usuario;
import com.javanauta.usuario.insfrastructure.exceptions.ConflictException;
import com.javanauta.usuario.insfrastructure.exceptions.ResourceNotFoundException;
import com.javanauta.usuario.insfrastructure.repository.UsuarioRepository;
import com.javanauta.usuario.insfrastructure.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioConverter usuarioConverter;
    private final PasswordEncoder passwordEncoder ;
    private final JwtUtil jwtUtil;

    public UsuarioDTO salvaUsuario(UsuarioDTO usuarioDTO){
        emailExiste(usuarioDTO.getEmail());
        usuarioDTO.setSenha(passwordEncoder.encode(usuarioDTO.getSenha()));
        Usuario usuario = usuarioConverter.paraUsuario(usuarioDTO);
        usuario = usuarioRepository.save(usuario);
        return usuarioConverter.paraUsuarioDTO(usuario);
    }

    public void emailExiste(String email){
        try{
            boolean existe = verificaEmailExistente(email);
            if(existe){
                throw new ConflictException("Email já cadastrado" + email);
            }
        } catch (ConflictException e){
            throw new ConflictException("Email já cadastrado" + e.getCause());
        }
    }

    public boolean verificaEmailExistente(String email){
        return usuarioRepository.existisByEmail(email);
    }

    public Usuario buscarUsuarioPorEmail(String email){
        return usuarioRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException("Email não encontrado" + email));
    }

    public void deletaUsuarioPorEmail(String email){
        usuarioRepository.deleteByEmail(email);
    }


    public UsuarioDTO atualizaDadosUsuario(String token, UsuarioDTO dto){

        // Aqui buscamos o email do usuario através do token (tirar a obrigatoriedade do email)
         String email = jwtUtil.extrairEmailToken(token.substring(7));

         // Criptografia de Senha
         dto.setSenha(dto.getSenha() != null ? passwordEncoder.encode(dto.getSenha()) : null);

         // Busca os dados do usuário no banco de dados
         Usuario usuarioEntity = usuarioRepository.findByEmail(email).orElseThrow(() ->
                 new ResourceNotFoundException("Email não localizado"));

         // mescla os dados que recebe na requisição DTO com os dados do banco de dados
         Usuario usuario = usuarioConverter.uptadeUsuario(dto, usuarioEntity);


         // salva os dados do usuario convertido e depois pega o retorno e converte para UsuarioDTO
         return usuarioConverter.paraUsuarioDTO(usuarioRepository.save(usuario));

    }







}
