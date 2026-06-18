# Aide-mémoire Git & GitHub - Projet SysFramework

## 1. Configuration Initiale (À faire une fois)

```bash
# Demander à Git de retenir ton Personal Access Token (PAT) sur ton PC
git config --local credential.helper store

# Configurer ton identité (si ce n'est pas déjà fait globalement)
git config --global user.name "olivierdash"
git config --global user.email "ton_email@example.com"

# Créer une nouvelle branche (ex: sprint0) et basculer dessus immédiatement
git checkout -b sprint0

# Lister toutes les branches locales (celle avec une étoile * est la branche active)
git branch

# Revenir sur la branche principale
git checkout main

# Fusionner le code d'une branche validée (ex: sprint0) dans main (une fois le PR accepté)
git checkout main
git pull origin main

# Vérifier l'état des fichiers (modifiés, créés, non suivis)
git status

# Ajouter un fichier spécifique à la zone de préparation (index)
git add src/main/java/controller/FrontController.java

# OU ajouter TOUS les fichiers modifiés et nouveaux d'un coup
git add .

# Enregistrer les modifications avec un message clair et explicite
git commit -m "Sprint 0 : Implémentation du FrontController de base et correction de l'aiguillage"

# Envoyer la branche locale 'sprint0' sur GitHub pour la première fois
git push -u origin sprint0

# Pour les fois suivantes sur la même branche, un simple push suffit
git push

# Récupérer les modifications qui ont été faites en ligne par un camarade ou le prof
git pull origin main

# Voir l'historique de tous les commits effectués
git log --oneline

# Annuler les modifications non enregistrées d'un fichier (retour au dernier commit)
git checkout -- nom_du_fichier.java

# Supprimer le cache des identifiants erronés si GitHub bloque à nouveau
git config --local credential.helper ""