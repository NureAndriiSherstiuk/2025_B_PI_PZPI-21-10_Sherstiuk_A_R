describe("Dictionary Testing", () => {
  beforeEach(() => {
    cy.loginByApi(
      Cypress.env("testUserEmail"),
      Cypress.env("testUserPassword")
    );
  });

  it("should create and complete a test", () => {
    const dictionaryName = `test-dict-${Cypress._.random(0, 99999999)
      .toString()
      .padStart(8, "0")}`;

    cy.createDictionaryByApi({
      name: dictionaryName,
      description: "Test dictionary for testing",
    }).then(() => {
      cy.visit("/library");

      cy.contains(dictionaryName).click();

      cy.get(".vocabulary-test").click();

      cy.url().should("include", "/create-test");

      cy.get('[data-testid="questions-number-input"]').type("2");

      cy.get('[data-testid="test-type-grammar"]')
        .parent()
        .find(".custom-checkbox")
        .click();

      cy.get('[data-testid="start-test-button"]').click();

      cy.url().should("include", "/test/");

      cy.get('[data-testid="test-title"]').should("contain", dictionaryName);
      cy.get('[data-testid="current-question-number"]').should(
        "have.text",
        "1"
      );
      cy.get('[data-testid="total-questions"]').should("have.text", "2");
      cy.get('[data-testid="progress-bar"]')
        .should("have.attr", "style")
        .and("include", "50%");
      cy.get('[data-testid="test-timer"]').should("be.visible");

      cy.get('[data-testid="question-term"]').should("be.visible");
      cy.get('[data-testid="question-translation"]').should("be.visible");

      cy.get('[data-testid="answer-option-0"]').click();

      cy.wait(2000);
      cy.get('[data-testid="current-question-number"]').should(
        "have.text",
        "2"
      );
      cy.get('[data-testid="progress-bar"]')
        .should("have.attr", "style")
        .and("include", "100%");

      cy.get('[data-testid="answer-option-0"]').click();

      cy.wait(2000);
      cy.url().should("include", "/test-result");
    });
  });
});
