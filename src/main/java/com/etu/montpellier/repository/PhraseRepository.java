package com.etu.montpellier.repository;


import com.etu.montpellier.domain.Expert;
import com.etu.montpellier.domain.Phrase;
import com.etu.montpellier.domain.Questions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhraseRepository extends JpaRepository<Phrase, Long> {
 String FIND_PROJECTS = "select f.id as phrase_id ,f.phrase ,ma.id as mot_id,  ma.mot,ma.choix1 ,ma.choix2 , ma.choix3 , ma.choix4 from phras f inner join phras_mot_id m on  m.phrase_id= f.id inner join mot_ambigu ma on ma.id = m.mot_id";
 String Find = "select f.id as phrase_id,f.phrase ,ma.id as mot_id, ma.mot ,ma.choix1 ,ma.choix2 , ma.choix3" +
         " , ma.choix4  from phras f inner join phras_mot_id m on " +
         " m.phrase_id= f.id inner join mot_ambigu ma on ma.id = m.mot_id where  f.id =?1";
    Phrase getById(int id);

    @Query(value = FIND_PROJECTS , nativeQuery = true)
     List<Questions> findPhras();

@Query(value = Find , nativeQuery = true)
    List<Questions> findMot(int id);

Phrase getByPhrase(String phrase);

    String FindByMot = "select f.id as phrase_id,f.phrase ,ma.id as mot_id, ma.mot ,ma.choix1 ,ma.choix2 , ma.choix3" +
            " , ma.choix4  from phras f inner join phras_mot_id m on " +
            " m.phrase_id= f.id inner join mot_ambigu ma on ma.id = m.mot_id where f.id = ?1 and ma.mot LIKE ?2";
@Query(value = FindByMot , nativeQuery = true)
    Questions getByWord(int id ,String word);

    void deleteAllByExpert(Expert expert);
    void deleteById(int id);
}
