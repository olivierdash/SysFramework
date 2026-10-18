package main.java.modele;

import com.sdm.framework.annotation.Controller;
import com.sdm.framework.annotation.UrlMapping;
import com.sdm.framework.constant.HttpMethod;
import com.sdm.framework.outils.ModelAndView;

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
}