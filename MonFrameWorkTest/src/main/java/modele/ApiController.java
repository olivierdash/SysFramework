package main.java.modele;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sdm.framework.annotation.ApiRest;
import com.sdm.framework.annotation.Controller;
import com.sdm.framework.annotation.UrlMapping;

@Controller
@ApiRest
public class ApiController {

    @UrlMapping(url = "/api/texte")
    public String texte() {
        return "{\"message\":\"deja du json\"}";
    }

    // @UrlMapping(url = "/api/liste")
    // public List<String> liste() {
    // return Arrays.asList("Nandra", "Olivier", "ITU");
    // }

    // @UrlMapping(url = "/api/objet")
    // public Map<String, Object> objet() {
    // Map<String, Object> m = new HashMap<>();
    // m.put("nom", "Nandra");
    // m.put("age", 20);
    // return m;
    // }

    @UrlMapping(url = "/employe")
    public Employe objet() {
        return new Employe("Jean", 12);
    }
}
