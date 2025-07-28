package com.farmacia.controller.adm;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.farmacia.entity.adm.Gestion;
import com.farmacia.service.adm.IGestionService;

@RestController
@RequestMapping("/gestion")
// @Secured({ "ROLE_ADMIN", "ROLE_USER", "ROLE_PAGOS" })
public class GestionController {
  private static Logger log = LoggerFactory.getLogger(GestionController.class);

  @Autowired
  private IGestionService gestionService;

  @GetMapping("/listar")
  public ResponseEntity<List<Gestion>> listar() {
    log.debug("Listando las gestiones");
    return ResponseEntity.ok(gestionService.listar());
  }

}
