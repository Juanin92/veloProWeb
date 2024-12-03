package com.veloProWeb.Service.Costumer;

import com.veloProWeb.Model.Entity.Costumer.Costumer;
import com.veloProWeb.Repository.Costumer.CostumerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CostumerService implements ICostumerService{

    @Autowired private CostumerRepo costumerRepo;

    @Override
    public void addNewCostumer(Costumer costumer) {

    }

    @Override
    public void updateCostumer(Costumer costumer) {
        Costumer costumerDB = getCostumerCreated(costumer.getName(), costumer.getSurname());
        if (costumerDB != null && !costumerDB.getId().equals(costumer.getId())){
            throw new IllegalArgumentException("Cliente Existente: Hay registro de este cliente.");
        }else {
            if (costumer.getEmail() == null || costumer.getEmail().isEmpty()) {
                costumer.setEmail("x@x.xxx");
            }
//            validator.validate(costumer);
            costumer.setName(capitalize(costumer.getName()));
            costumer.setSurname(capitalize(costumer.getSurname()));
            costumerRepo.save(costumer);
        }
    }

    @Override
    public List<Costumer> getAll() {
        return costumerRepo.findAll();
    }

    @Override
    public void delete(Costumer costumer) {

    }

    @Override
    public void paymentDebt(Costumer costumer, String amount) {

    }

    @Override
    public void statusAssign(Costumer costumer) {

    }

    @Override
    public void addSaleToCostumer(Costumer costumer) {

    }

    @Override
    public void updateTotalDebt(Costumer costumer) {

    }

    private Costumer getCostumerCreated(String name, String surname) {
        Optional<Costumer> costumerOptional = costumerRepo.findByNameAndSurname(capitalize(name), capitalize(surname));
        return costumerOptional.orElse(null);
    }

    private String capitalize(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        String[] words = value.split(" ");
        StringBuilder capitalized = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                capitalized.append(word.substring(0, 1).toUpperCase());
                capitalized.append(word.substring(1).toLowerCase());
                capitalized.append(" ");
            }
        }
        return capitalized.toString().trim();
    }
}
