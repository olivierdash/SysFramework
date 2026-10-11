<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<body>
    <form action="salut3" method="get">
        <h3>Personne 1</h3>
        Nom : <input type="text" name="p1.nom"><br>
        Age : <input type="number" name="p1.age"><br>
        Ville : <input type="text" name="p1.adresse.ville"><br>

        <h3>Personne 2</h3>
        Nom : <input type="text" name="p2.nom"><br>
        Age : <input type="number" name="p2.age"><br>
        Ville : <input type="text" name="p2.adresse.ville"><br>

        <input type="submit" value="Envoyer">
    </form>
</body>
</html>