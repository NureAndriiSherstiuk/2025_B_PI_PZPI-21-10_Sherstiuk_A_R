describe("Dictionary Management Journey", () => {
  const randomSuffix = Cypress._.random(1000, 9999);
  const testDictionary = {
    name: `Test Dictionary ${randomSuffix}`,
    description: "This is a test dictionary",
    sourceLanguage: "en",
    targetLanguage: "uk",
    level: "A1",
    label: "Освіта",
    isPublic: "private",
    cards: [
      {
        source: "Hello",
        target: "Привіт",
        description: "A greeting",
      },
      {
        source: "World",
        target: "Світ",
        description: "The earth and all life upon it",
      },
      {
        source: "Language",
        target: "Мова",
        description: "A system of communication",
      },
    ],
  };

  beforeEach(() => {
    cy.loginByUi(Cypress.env("testUserEmail"), Cypress.env("testUserPassword"));
  });

  it("should complete the create-edit-logout journey", () => {
    cy.get('[data-testid="plus-button"]').click();

    cy.get('[data-testid="create-dictionary-button"]').click();
    cy.url().should("include", "/add-vocabulary");

    cy.get('input[name="name"]').type(testDictionary.name);
    cy.get('textarea[name="description"]').type(testDictionary.description);
    cy.get('select[name="sourceLanguage"]').select(
      testDictionary.sourceLanguage
    );
    cy.get('select[name="targetLanguage"]').select(
      testDictionary.targetLanguage
    );
    cy.get('select[name="level"]').select(testDictionary.level);
    cy.get('select[name="label"]').select(testDictionary.label);
    cy.get('select[name="isPublic"]').select(testDictionary.isPublic);

    testDictionary.cards.forEach((card, index) => {
      if (index > 1) {
        cy.get(".add-card").click();
      }
      cy.get(".terms-entrance")
        .eq(index)
        .within(() => {
          cy.get(".added-terms__input").first().type(card.source);
          cy.get(".added-terms__input").eq(1).type(card.target);
          cy.get(".added-terms__input").eq(2).type(card.description);
        });
    });

    cy.get('[data-testid="dictionary-submit"]').click();
    cy.contains("Словник успішно додано").should("be.visible");

    cy.contains(testDictionary.name).click();
    cy.get('[data-testid="more-info-button"]').click();
    cy.get('[data-testid="edit-dictionary-button"]').click();

    const updatedName = `${testDictionary.name} - Updated`;
    cy.get('input[name="name"]').clear().type(updatedName);
    cy.get('[data-testid="dictionary-submit"]').click();
    cy.contains(updatedName).should("be.visible");

    cy.get('[data-testid="account-button"]').click();
    cy.get('[data-testid="logout-button"]').click();
    cy.url().should("include", "/sign-in");
  });

  it("should complete the create-delete-logout journey", () => {
    cy.get('[data-testid="plus-button"]').click();

    cy.get('[data-testid="create-dictionary-button"]').click();
    cy.url().should("include", "/add-vocabulary");

    cy.get('input[name="name"]').type(testDictionary.name);
    cy.get('textarea[name="description"]').type(testDictionary.description);
    cy.get('select[name="sourceLanguage"]').select(
      testDictionary.sourceLanguage
    );
    cy.get('select[name="targetLanguage"]').select(
      testDictionary.targetLanguage
    );
    cy.get('select[name="level"]').select(testDictionary.level);
    cy.get('select[name="label"]').select(testDictionary.label);
    cy.get('select[name="isPublic"]').select(testDictionary.isPublic);

    testDictionary.cards.forEach((card, index) => {
      if (index > 1) {
        cy.get(".add-card").click();
      }
      cy.get(".terms-entrance")
        .eq(index)
        .within(() => {
          cy.get(".added-terms__input").first().type(card.source);
          cy.get(".added-terms__input").eq(1).type(card.target);
          cy.get(".added-terms__input").eq(2).type(card.description);
        });
    });

    cy.get('[data-testid="dictionary-submit"]').click();
    cy.contains("Словник успішно додано").should("be.visible");

    cy.contains(testDictionary.name).click();
    cy.get('[data-testid="more-info-button"]').click();
    cy.get('[data-testid="delete-dictionary-button"]').click();

    cy.url().should("include", "/library");

    cy.contains(testDictionary.name).should("not.exist");

    cy.get('[data-testid="account-button"]').click();
    cy.get('[data-testid="logout-button"]').click();
    cy.url().should("include", "/sign-in");
  });
});
