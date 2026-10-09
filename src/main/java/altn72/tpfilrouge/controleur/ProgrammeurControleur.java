package altn72.tpfilrouge.controleur;

import altn72.tpfilrouge.modele.Programmeur;
import altn72.tpfilrouge.service.ProgrammeurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
//@RequestMapping("/api")
@RequestMapping("/programmeurs")
public class ProgrammeurControleur {

//    @Autowired
//    ProgrammeurService programmeurService;

    // Injection de dépendance via le constructeur -- DÉBUT //
    private final ProgrammeurService programmeurService;
    public ProgrammeurControleur(ProgrammeurService programmeurService) { this.programmeurService = programmeurService;}
    // Injection de dépendance via le constructeur -- FIN //

    // Ancienne route : @GetMapping("/programmeurs"). Le nom de la ressource est désormais dans le @RequestMapping de la classe.
    // GET /programmeurs : on lit la collection.
    // ResponseEntity<List<Programmeur>> : on contrôle explicitement le code HTTP. 200 OK, même si la liste est vide
    // (une liste vide est une réponse valide, pas une erreur : ce n'est PAS un 404).
    @GetMapping
    public ResponseEntity<List<Programmeur>> afficherInfosProgrammeurs(){
        List<Programmeur> liste = programmeurService.getProgrammeurs();
        return ResponseEntity.ok(liste);
    }

    // Ancienne route : /unProgrammeur/{idProgrammeur}. "unProgrammeur" n'apporte rien : le verbe GET dit déjà "lire",
    // et /programmeurs/{id} se lit comme "le programmeur n° id".
    // Optional<Programmeur> en retour : si l'id n'existait pas, le client recevait une erreur 500.
    // Maintenant : 200 + le programmeur s'il existe, 404 sinon.
    @GetMapping("/{idProgrammeur}")
    public ResponseEntity<Programmeur> afficherUnProgrammeur(@PathVariable("idProgrammeur") Integer idProg) {
        return programmeurService.getUnProgrammeur(idProg)
                .map(ResponseEntity::ok)                          // présent : 200 OK + programmeur dans le corps
                .orElse(ResponseEntity.notFound().build());       // absent : 404 Not Found, sans corps
    }

    // Ancienne route : /supprimerProgrammeur/{idProgrammeur}. Le verbe est déjà dans la méthode HTTP : DELETE.
    // 204 No Content : la suppression a réussi et il n'y a plus rien à renvoyer. 404 si l'id n'existe pas.
    @DeleteMapping("/{idProgrammeur}")
    public ResponseEntity<Void> deleteProgrammeur(@PathVariable("idProgrammeur") Integer idProg) {
        if (programmeurService.supprimerProgrammeur(idProg)) return ResponseEntity.noContent().build();
        return ResponseEntity.notFound().build();
    }

    // Ancienne route : /ajouterProgrammeur. Même principe : POST sur la collection = "ajouter un élément à /programmeurs".
    // 201 Created (et non 200) + en-tête Location (l'URL de la nouvelle ressource) + la ressource créée dans le corps.
    // Le client récupère ainsi l'id généré par la base sans avoir à refaire un GET.
    @PostMapping
    public ResponseEntity<Programmeur> creerProgrammeur(@RequestBody Programmeur programmeur){
        Programmeur programmeurCree = programmeurService.ajouterProgrammeur(programmeur);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()     // URL de la requête : .../programmeurs
                .path("/{id}")                                              // on y ajoute /{id}
                .buildAndExpand(programmeurCree.getId())
                .toUri();
        return ResponseEntity.created(location).body(programmeurCree);
    }

    // Ancienne route : "modifier/{idProgrammeur}" (verbe dans l'URL, et "/" initial oublié).
    // PUT /programmeurs/{id} : on REMPLACE l'ensemble de la ressource (cf. 4.6.3). PUT est idempotent.
    // 200 OK + le programmeur à jour. (204 No Content, sans corps, serait aussi acceptable.) 404 si l'id n'existe pas.
    // On ne crée PAS le programmeur s'il n'existe pas : les ids sont générés par la base, pas choisis par le client.
    @PutMapping("/{idProgrammeur}")
    public ResponseEntity<Programmeur> modifierProgrammeur(@PathVariable Integer idProgrammeur,@RequestBody Programmeur programmeurModified ){
        return programmeurService.modifierProgrammeur(idProgrammeur,programmeurModified)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}
