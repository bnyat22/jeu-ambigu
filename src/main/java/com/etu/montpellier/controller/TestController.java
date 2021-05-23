package com.etu.montpellier.controller;

import com.etu.montpellier.domain.*;
import com.etu.montpellier.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/test")
public class TestController {
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    private PhraseRepository phraseRepository;

    @Autowired
    private MotAmbiguRepository motAmbiguRepository;
    @Autowired
    private PointsRepository pointsRepository;
    @Autowired
    private JoueurRepository joueurRepository;
    @Autowired
    private UtilisateurRepository utilisateurRepository;
    @Autowired
    private ExpertRepository expertRepository;
    @Autowired
    private IntermidaireRepository intermidaireRepository;
    @Autowired
    private RoleRepository roleRepository;

    List<Phrase> questions;
    int currentIndex;

    @GetMapping("/jeu")
    public String jouer() {
        questions = new ArrayList<>();
        //  model.addAttribute("motambigus",questions);
        return "jouerTest";
    }

    @GetMapping("/commence")
    public String playGame(Model model , @ModelAttribute("jwt") String pseudo) {
        questions = new ArrayList<>();
        currentIndex = 0;

        questions = phraseRepository.findAll();
        System.out.println("qdghfdjhdsfgh" + questions.size());
        Collections.shuffle(questions);

        Phrase premiereQuestion = questions.get(currentIndex);

        List<ChoixReponse> choixReponses = getMotsDePhrase(premiereQuestion.getId());
        System.out.println("size reponse wordakan "+ choixReponses.size());
        Options optionsList = new Options();
        for (MotAmbigu m : premiereQuestion.getMot_id())
        {
            optionsList.optionList.add(new Option());
        }
        System.out.println("size listakam " + optionsList.optionList.size());
        System.out.println("current index " + currentIndex);
        model.addAttribute("options" , optionsList);
        model.addAttribute("question", choixReponses);
        currentIndex++;
        return "jouerTest";

    }

    @GetMapping("/suivante")
    public String nextQuestion(Model model) {
        if (currentIndex >= 5) {
            return "fin";
        }
        Phrase questionsSuivante = questions.get(currentIndex);

        List<ChoixReponse> choixReponses = getMotsDePhrase(questionsSuivante.getId());
        System.out.println("size reponse wordakan "+ choixReponses.size());
        Options optionsList = new Options();
        for (MotAmbigu m : questionsSuivante.getMot_id())
        {
            optionsList.optionList.add(new Option());
        }
        System.out.println("size listakam " + optionsList.optionList.size());
        model.addAttribute("options" , optionsList);
        model.addAttribute("question", choixReponses);
        System.out.println("current index " + currentIndex);
        currentIndex++;
        return "jouerTest";
    }



    @PostMapping("/choisir")
    @ResponseStatus(value = HttpStatus.OK)
    public String pickAnswer(@ModelAttribute("options") Options options , Model  model) {
        Phrase question = questions.get(currentIndex - 1);
        List<ChoixReponse> choixReponses = getMotsDePhrase(question.getId());
        MotAmbigu motAmbigu = new MotAmbigu();
        System.out.println("my word size " + question.getMot_id().size());
        for (ChoixReponse m : choixReponses) {
            for (Option option : options.getOptionList()) {
                System.out.println(m + "wshakay era");
                System.out.println(option.getChoix() + " wshakay era optionaka");
                if (m.getMot().equals(option.getWord())) {

                    MotAmbigu mo = motAmbiguRepository.getById(m.getMot_id());
                    System.out.println("wshaka " + mo.getMot());
                    motAmbigu.setId(mo.getId());
                    motAmbigu.setMot(mo.getMot());
                    motAmbigu.setChoix1(mo.getChoix1());
                    motAmbigu.setChoix2(mo.getChoix2());
                    motAmbigu.setChoix3(mo.getChoix3());
                    motAmbigu.setChoix4(mo.getChoix4());

                    Phrase phrase = phraseRepository.getById(question.getId());
                    System.out.println(phrase.getPhrase() + "heyo");
                    Points p = getPoint(phrase.getId(), motAmbigu.getId());
                    int updatedPoint = 0;
                    int updateCredit = 0;
                    System.out.println(option.getWord());
                    System.out.println(motAmbigu.getChoix1());

                    System.out.println(p.getId() + "point id");
                    if (option.getChoix() != null && motAmbigu.getChoix1().equals(option.getChoix())) {
                        System.out.println("point " + p.getPoint_choix1());
                        option.setPoint(p.getPoint_choix1());
                        p.setNJouer_choix1(p.getNJouer_choix1() + 1);//quand un jouer choisi ce choix le numéro de jouer qui ont choisi ce choix augement
                    } else if (option.getChoix() != null && motAmbigu.getChoix2().equals(option.getChoix())) {
                        p.setNJouer_choix2(p.getNJouer_choix2() + 1);
                        option.setPoint(p.getPoint_choix2());
                    } else if (option.getChoix() != null && motAmbigu.getChoix3() != null && motAmbigu.getChoix3().equals(option.getChoix())) {
                        p.setNJouer_choix3(p.getNJouer_choix3() + 1);
                        option.setPoint(p.getPoint_choix3());
                    } else if (option.getChoix() != null && motAmbigu.getChoix4() != null && motAmbigu.getChoix4().equals(option.getChoix())) {
                        p.setNJouer_choix4(p.getNJouer_choix4() + 1);
                        option.setPoint(p.getPoint_choix4());
                    }

                    pointsRepository.save(p);
                }
            }
        }
        options.isValide = true;
        model.addAttribute("options" , options);
        model.addAttribute("question", choixReponses);
        return "jouerTest";
    }





    private List<ChoixReponse> getMotsDePhrase(int phrase_id)
    {
        List<Questions> mots = phraseRepository.findMot(phrase_id);
        System.out.println("size listakam wordakan "+ mots.size());
        List<ChoixReponse> choixReponses = new ArrayList<>();
        for (Questions q : mots)
        {
            ChoixReponse choixReponse = new ChoixReponse();
            choixReponse.setId(q.getPhrase_id());
            choixReponse.setPhrase(q.getPhrase());
            for (MotAmbigu m: q.getMot())
            {

                System.out.println("my quesition id " + m.getMot());
                choixReponse.setMot(m.getMot());
                choixReponse.setMot_id(m.getId());
                List<String> options = new ArrayList<>();
                options.add(m.getChoix1());
                options.add(m.getChoix2());
                options.add(m.getChoix3());
                options.add(m.getChoix4());
                choixReponse.setChoix(options);
            }
            choixReponses.add(choixReponse);

        }
        return choixReponses;
    }
    private Points getPoint(int phrase_id , int mot_id)
    {
        List<Points> points = pointsRepository.getPointsByPhrase_id(phrase_id);
        System.out.println(points.size() + "numero de point");
        for (Points p : points)
        {
            System.out.println("gaishta pointakan" + p.getMotAmbigu_id().getId());
            System.out.println("gaishta pointakan" + mot_id);
            if (p.getMotAmbigu_id().getId() == mot_id)
                return p;
        }
        return null;
    }




    private Options getOptionList(List<MotAmbigu> motAmbiguList)
    {
        Options optionsList = new Options();
        for (MotAmbigu m : motAmbiguList)
        {
            optionsList.optionList.add(new Option());
        }
        return optionsList;
    }

}