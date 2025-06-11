describe("Authenticated Tests", () => {
  it("should not see library content when not logged in", () => {
    cy.visit("/library");
    cy.contains("Словники").should("not.exist");
    cy.url().should("include", "/sign-in");
  });

  describe("when logged in", () => {
    beforeEach(() => {
      cy.loginByApi(
        Cypress.env("testUserEmail"),
        Cypress.env("testUserPassword")
      );
    });

    it("should access library and see content", () => {
      cy.visit("/library");
      cy.contains("Словники").should("be.visible");
      cy.url().should("include", "/library");
    });

    it("should show user profile", () => {
      cy.visit("/account");
      cy.contains("Налаштування профілю").should("be.visible");
      cy.url().should("include", "/account");
    });
  });
});
