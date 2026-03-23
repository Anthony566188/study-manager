package br.com.fiap.study_manager.controllers;

import br.com.fiap.study_manager.models.StudyBalance;
import br.com.fiap.study_manager.services.StudyBalanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/balances")
public class StudyBalanceController {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private StudyBalanceService service;

    @GetMapping("/{idUser}")
    public List<StudyBalance> listAll(@PathVariable Long idUser) {
        return service.getAllUserBalances(idUser);
    }

}
