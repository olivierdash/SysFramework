<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<body>
    <form action="upload" method="post" enctype="multipart/form-data">
        Nom : <input type="text" name="p.nom"><br>
        Age : <input type="number" name="p.age"><br>
        Photo : <input type="file" name="p.photo"><br>
        <input type="submit" value="Envoyer">
    </form>
</body>
</html>
