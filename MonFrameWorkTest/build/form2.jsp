<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>

<body>
    <form action="salut2" method="get">
        Nom : <input type="text" name="p.nom"><br>
        Age : <input type="number" name="p.age"><br>
        Sexe : <select name="p.sexe.valeur" id="sexe">
            <option value="Homme">Homme</option>
            <option value="Femme">Femme</option>
        </select>
        <input type="submit" value="Envoyer">
    </form>
</body>

</html>