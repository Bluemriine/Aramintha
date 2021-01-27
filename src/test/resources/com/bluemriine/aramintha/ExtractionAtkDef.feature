Feature: Extraction ATK/DEF

  En tant qu'utilisateur je veux verifier la fiabilité de l'extraction des données

  Scenario: Vérification A
    Given Image de départ 'testA.png'
    When  Je passe le fichier à la reconnaissance de texte
    Then  J ai les bons scores pour l image 'testA.png'

  Scenario: Vérification B
    Given Image de départ 'testB.png'
    When  Je passe le fichier à la reconnaissance de texte
    Then  J ai les bons scores pour l image 'testB.png'

  Scenario: Vérification C
    Given Image de départ 'testC.png'
    When  Je passe le fichier à la reconnaissance de texte
    Then  J ai les bons scores pour l image 'testC.png'

  Scenario: Vérification D
    Given Image de départ 'testD.png'
    When  Je passe le fichier à la reconnaissance de texte de contribution
    Then  J ai les bons scores de contribution pour l image