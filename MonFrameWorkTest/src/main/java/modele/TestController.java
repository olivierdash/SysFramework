package main.java.modele;

import com.sdm.framework.annotation.Auth;
import com.sdm.framework.annotation.Controller;
import com.sdm.framework.annotation.UrlMapping;
import com.sdm.framework.constant.HttpMethod;
import com.sdm.framework.outils.ModelAndView;
import com.sdm.framework.outils.MySession;

@Controller
public class TestController {

    @UrlMapping(url = "/hello")
    public ModelAndView hello() {
        ModelAndView mv = new ModelAndView("/hello.jsp");
        mv.addAttribut("nom", "Nandra");
        return mv;
    }

    @UrlMapping(url = "/salutForm")
    public ModelAndView salutationForm() {
        return new ModelAndView("/form.jsp");
    }

    @UrlMapping(url = "/salut")
    public ModelAndView salut(String nom, int age) {
        ModelAndView mv = new ModelAndView("/salut.jsp");
        mv.addAttribut("nom", nom);
        mv.addAttribut("age", age);
        return mv;
    }

    @UrlMapping(url = "/salut2")
    public ModelAndView salut2(Personne p) {
        ModelAndView mv = new ModelAndView("/salut2.jsp");
        mv.addAttribut("personne", p);
        return mv;
    }

    @UrlMapping(url = "/form2Salut")
    public ModelAndView form2(Personne p) {
        ModelAndView mv = new ModelAndView("/form2.jsp");
        mv.addAttribut("personne", p);
        return mv;
    }

    @UrlMapping(url = "/salut3")
    public ModelAndView salut3(Personne p1, Personne p2) {
        ModelAndView mv = new ModelAndView("/salut3.jsp");
        mv.addAttribut("p1", p1);
        mv.addAttribut("p2", p2);
        return mv;
    }

    @UrlMapping(url = "/form3Salut")
    public ModelAndView form3(Personne p) {
        ModelAndView mv = new ModelAndView("/form3.jsp");
        mv.addAttribut("personne", p);
        return mv;
    }

    @UrlMapping(url = "/uploadForm")
    public ModelAndView uploadForm() {
        return new ModelAndView("/upload.jsp");
    }

    @UrlMapping(url = "/upload", method = HttpMethod.POST)
    public ModelAndView upload(Personne p) {
        ModelAndView mv = new ModelAndView("/uploadResult.jsp");
        mv.addAttribut("personne", p);
        return mv;
    }

    @UrlMapping(url = "/loginForm")
    public ModelAndView loginForm() {
        return new ModelAndView("/login.jsp");
    }

    @UrlMapping(url = "/login", method = HttpMethod.POST)
    public ModelAndView login(String nom, String role, MySession session) {
        session.put("user", nom);
        session.put("roleUtilisateur", role);
        return new ModelAndView("/profil.jsp");
    }

    // @UrlMapping(url = "/login")
    // public ModelAndView login(String nom, MySession session) {
    // session.put("user", nom);
    // return new ModelAndView("/profil.jsp");
    // }

    // @UrlMapping(url = "/profil")
    // public ModelAndView profil(MySession session) {
    // ModelAndView mv = new ModelAndView("/profil.jsp");
    // mv.addAttribut("user", session.get("user"));
    // return mv;
    // }

    @Auth
    @UrlMapping(url = "/profilDetail")
    public ModelAndView profilDetail(MySession session) {
        ModelAndView mv = new ModelAndView("/profilDetail.jsp");
        mv.addAttribut("user", session.get("user"));
        return mv;
    }

    @Auth(roles = { "Admin", "assistant" })
    @UrlMapping(url = "/roleProfil")
    public ModelAndView adminPage(MySession session) {
        ModelAndView mv = new ModelAndView("/profil.jsp");
        mv.addAttribut("user", session.get("user"));
        return mv;
    }

    @UrlMapping(url = "/logout")
    public ModelAndView logout(MySession session) {
        session.supprimer("user");
        return new ModelAndView("/profil.jsp");
    }

}