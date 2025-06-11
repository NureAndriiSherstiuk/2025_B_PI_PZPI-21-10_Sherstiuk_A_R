// eslint-disable-next-line @typescript-eslint/no-namespace
declare namespace Cypress {
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  interface Chainable<Subject> {
    login(email: string, password: string): Chainable<void>;
    loginByApi(email: string, password: string): Chainable<void>;
    createDictionaryByApi(options?: DictionaryOptions): Chainable<string>;
    loginByUi(email: string, password: string): Chainable<void>;
  }
}

interface DictionaryOptions {
  name?: string;
  description?: string;
  sourceLanguage?: string;
  targetLanguage?: string;
  level?: string;
  label?: string;
  isPublic?: boolean;
  cards?: Array<{
    term: string;
    translation: string;
    definition: string;
  }>;
}

Cypress.Commands.add("login", (email: string, password: string) => {
  cy.visit("/sign-in");
  cy.get('input[name="email"]').type(email);
  cy.get('input[name="password"]').type(password);
  cy.get('button[type="submit"]').click();
  cy.url().should("include", "/library");
});

Cypress.Commands.add("loginByApi", (email: string, password: string) => {
  cy.request({
    method: "POST",
    url: "https://localhost:7288/login-user",
    body: {
      email,
      password,
    },
    form: true,
    headers: {
      "Content-Type": "multipart/form-data",
    },
    failOnStatusCode: false,
  }).then((response) => {
    if (response.status === 200 && response.body) {
      window.localStorage.setItem("token", response.body.token);
      cy.log("Token stored:", response.body);
    } else {
      cy.log("Login failed:", JSON.stringify(response.body));
      throw new Error(`Login failed with status ${response.status}`);
    }
  });
});

Cypress.Commands.add(
  "createDictionaryByApi",
  (options: DictionaryOptions = {}) => {
    const defaultOptions: Required<DictionaryOptions> = {
      name: "Test Dictionary",
      description: "This is a test dictionary",
      sourceLanguage: "en",
      targetLanguage: "uk",
      level: "A1",
      label: "Освіта",
      isPublic: false,
      cards: [
        {
          term: "Hello",
          translation: "Привіт",
          definition: "A greeting",
        },
        {
          term: "World",
          translation: "Світ",
          definition: "The earth and all life upon it",
        },
        {
          term: "Language",
          translation: "Мова",
          definition: "A system of communication",
        },
      ],
    };

    const finalOptions = { ...defaultOptions, ...options };

    const formattedData = {
      title: finalOptions.name,
      description: finalOptions.description,
      isPublic: finalOptions.isPublic,
      fromLang: finalOptions.sourceLanguage,
      toLang: finalOptions.targetLanguage,
      cefr: finalOptions.level,
      label: finalOptions.label,
      cards: finalOptions.cards.map((card) => ({
        term: card.term,
        meaning: card.definition || null,
        translation: card.translation,
      })),
    };

    return cy
      .request({
        method: "POST",
        url: "https://localhost:7288/Dictionary",
        body: formattedData,
        headers: {
          Authorization: `Bearer ${window.localStorage.getItem("token")}`,
        },
      })
      .then((response) => {
        if (response.status === 200) {
          return finalOptions.name;
        }
        throw new Error(`Failed to create dictionary: ${response.status}`);
      });
  }
);

Cypress.Commands.add("loginByUi", (email: string, password: string) => {
  cy.visit("/sign-in");
  cy.get('input[name="email"]').type(email);
  cy.get('input[name="password"]').type(password);
  cy.get('button[type="submit"]').click();
  cy.url().should("not.include", "/sign-in");
});
