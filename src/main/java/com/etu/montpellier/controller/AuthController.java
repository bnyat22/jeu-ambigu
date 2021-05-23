package com.etu.montpellier.controller;

import com.etu.montpellier.domain.*;
import com.etu.montpellier.repository.*;
import com.etu.montpellier.request.LoginRequest;
import com.etu.montpellier.request.SignupRequest;
import com.etu.montpellier.security.jwt.JwtUtils;
import com.etu.montpellier.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;


@Controller
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    UtilisateurRepository utilisateurRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    JoueurRepository joueurRepository;
    @Autowired
    PhraseRepository phraseRepository;
   // @Autowired
  //  RankRepository rankRepository;
    @GetMapping("/")
  public String getLogin(Model model)
  {
      LoginRequest loginRequest = new LoginRequest();
      model.addAttribute("personForm", loginRequest);
      return "login";
  }
  @GetMapping("/index")
  public void getIndex(Model model , HttpServletResponse response) throws IOException {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      for (GrantedAuthority admin:authentication.getAuthorities())
      {
          if (admin.getAuthority().equals("ADMIN"))
          {
              model.addAttribute("admin" , admin.getAuthority());
          }
      }

       response.sendRedirect("/");
  }
    @GetMapping("/logout")
    public String Logout(Model model)
    {
        SecurityContextHolder.clearContext();
        return getLogin(model);
    }

    @PostMapping("/signin")
    public String authenticateUtilisateur(@ModelAttribute("personForm") LoginRequest loginRequest , Model model , HttpServletResponse response)
    {

try {
    Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.getPseudo(), loginRequest.getPassword()));
    SecurityContextHolder.getContext().setAuthentication(authentication);


    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
    model.addAttribute("jwt", userDetails.getUsername());

    getIndex(model , response);
    return "index";
} catch (BadCredentialsException | IOException e)
{

    model.addAttribute("bad" , "Votre pseudo ou mot de passe n'est pas correcte");
    return "login";
}

    }
    @GetMapping("/reg")
    public String getReg(Model model)
    {
        SignupRequest signupRequest = new SignupRequest();
        model.addAttribute("regForm", signupRequest);
        return "register";
    }
    @PostMapping("/signup")
    public String engistrerUtilisateur(@ModelAttribute("regForm") SignupRequest signupRequest , Model model)  {
        if (utilisateurRepository.existsByPseudo(signupRequest.getPseudo()))
        {
            model.addAttribute("errorPseudo" , "Cet pseudo existe déjà");
            return "register" ;
        }
        if (utilisateurRepository.existsByEmail(signupRequest.getEmail()))
        {
            model.addAttribute("errorEmail" , "Cet email existe déjà");
            return "register";

        }


        if (utilisateurRepository.existsByEmail(signupRequest.getEmail()))
        {
            model.addAttribute("errorEmail" , "Cet email existe déjà");
            return "register";

        }
        if (!PasswordValidator.isValid(signupRequest.getPassword()))
        {
            model.addAttribute("errorChiffre" , "Le mot de passe ne doit pas être moins de 8 lettres et chiffres");
            model.addAttribute("errorMinMax" , "il doit y avoir au moins une lettre majuscule ,minuscule et un chiffre ");
            return "register" ;
        }
        System.out.println(signupRequest.getPasswordConfirm() + "dlnyabunawa");
        if (!signupRequest.getPassword().equals(signupRequest.getPasswordConfirm()))
        {
            model.addAttribute("errorPass" , "les mots de passes ne sont pas égaux");
            return "register" ;
        }

        Utilisateur utilisateur = new Utilisateur(signupRequest.getPseudo() ,
                signupRequest.getEmail()
        ,passwordEncoder.encode(signupRequest.getPassword()));

        Role roleDeUtilisateur = roleRepository.findByName(ERoles.ROLE_DEBUTANT);

        Set<Role> roles = new HashSet<>();
        roles.add(roleDeUtilisateur);
        utilisateur.setRoles(roles);

        utilisateurRepository.save(utilisateur);
        Joueur joueur = new Joueur(utilisateur);
        joueurRepository.save(joueur);

        return getLogin(model);
    }
}
