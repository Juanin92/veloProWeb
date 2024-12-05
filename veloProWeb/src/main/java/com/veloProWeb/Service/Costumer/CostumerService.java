package com.veloProWeb.Service.Costumer;

import com.veloProWeb.Model.Entity.Costumer.Costumer;
import com.veloProWeb.Model.Enum.PaymentStatus;
import com.veloProWeb.Repository.Costumer.CostumerRepo;
import com.veloProWeb.Validation.CostumerValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CostumerService implements ICostumerService{

    @Autowired private CostumerRepo costumerRepo;
    @Autowired private CostumerValidator validator;

    @Override
    public void addNewCostumer(Costumer costumer) {
        Costumer costumerDB = getCostumerCreated(costumer.getName(), costumer.getSurname());
        if (costumerDB != null){
            throw new IllegalArgumentException("Cliente Existente: Hay registro de este cliente.");
        }else {
            if (costumer.getEmail() == null || costumer.getEmail().isEmpty()) {
                costumer.setEmail("x@x.xxx");
            }
            validator.validate(costumer);
            costumer.setAccount(true);
            costumer.setStatus(PaymentStatus.NULO);
            costumer.setTotalDebt(0);
            costumer.setDebt(0);
            costumer.setName(capitalize(costumer.getName()));
            costumer.setSurname(capitalize(costumer.getSurname()));
            costumerRepo.save(costumer);
        }
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
            validator.validate(costumer);
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
        costumer.setAccount(false);
        costumerRepo.save(costumer);
    }

    @Override
    public void paymentDebt(Costumer costumer, String amount) {
        int number = Integer.parseInt(amount);
        validator.validateValuePayment(number, costumer);
        costumer.setDebt(costumer.getDebt() - number);
        costumerRepo.save(costumer);
        statusAssign(costumer);
    }

    @Override
    public void statusAssign(Costumer costumer) {
        if (costumer.getTotalDebt() == 0) {
            costumer.setStatus(PaymentStatus.NULO);
        }
        if (costumer.getDebt() >= 0 && costumer.getTotalDebt() > 0) {
            costumer.setStatus(PaymentStatus.PENDIENTE);
        }
        if (costumer.getDebt() < (costumer.getTotalDebt() / 2)) {
            costumer.setStatus(PaymentStatus.PARCIAL);
        }
        if (costumer.getDebt() == 0 && costumer.getTotalDebt() > 0){
            costumer.setStatus(PaymentStatus.PAGADA);
        }
        costumerRepo.save(costumer);
    }

    @Override
    public void addSaleToCostumer(Costumer costumer) {
        costumer.setDebt(costumer.getTotalDebt());
        costumerRepo.save(costumer);
        statusAssign(costumer);
    }

    @Override
    public void updateTotalDebt(Costumer costumer) {
        costumerRepo.save(costumer);
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
