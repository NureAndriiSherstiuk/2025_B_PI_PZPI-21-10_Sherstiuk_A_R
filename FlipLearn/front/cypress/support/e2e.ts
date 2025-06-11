/* eslint-disable @typescript-eslint/no-namespace */
import "./commands";

declare global {
  namespace Cypress {
    interface Chainable {
      login(email: string, password: string): Chainable<void>;
      loginByApi(email: string, password: string): Chainable<void>;
      loginByUi(email: string, password: string): Chainable<void>;
      createDictionaryByApi(options?: {
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
      }): Chainable<string>;
    }
  }
}
