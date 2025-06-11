describe("Account Management Journey", () => {
  beforeEach(() => {
    cy.loginByUi(Cypress.env("testUserEmail"), Cypress.env("testUserPassword"));
  });

  it("should allow user to edit account settings and persist changes", () => {
    cy.get('[data-testid="account-button"]').click();
    cy.url().should("include", "/account");

    const randomUsername = `test-${Cypress._.random(0, 99999999)
      .toString()
      .padStart(8, "0")}`;

    cy.get('[data-testid="username-input"]').clear().type(randomUsername);

    cy.get(".option-circle").eq(2).click();

    cy.get('[data-testid="save-button"]').click();

    cy.contains("Дані успішно збережені").should("be.visible");

    cy.reload();
    cy.get('[data-testid="username-input"]').should(
      "have.value",
      randomUsername
    );
    cy.get(".option-circle")
      .eq(2)
      .should("have.css", "background-color", "rgb(51, 87, 255)"); 
    cy.get('[data-testid="account-button"]').click();
    cy.get('[data-testid="logout-button"]').click();
    cy.url().should("include", "/sign-in");
  });
});
