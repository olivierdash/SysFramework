# sprint 6

## cote framework

- l'url associe a une methode ne passe pas par la view mais directement au controller.
- creer une annotation @WebApi associe a une methode dans le controller, qui retourne du JSON.

## cote test

- On met une condition:
  - si c'est string , on print directement
  - si JSON, on convertit en string et on print
  - sinon, dispatcher