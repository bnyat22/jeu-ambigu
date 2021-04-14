package com.etu.montpellier.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "expert")
@Getter
@Setter
public class Expert extends Intermédiaire{


    private int nbPhrases;
}
