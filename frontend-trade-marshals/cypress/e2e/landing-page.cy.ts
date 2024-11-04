describe('Landing page', () => {
  beforeEach(() => {
    cy.visit('/');
  })

  it('Validate the Landing page of the application', () => {
    cy.get('.nav-title').contains('Trade Marshals');
    cy.get('.mdc-button__label > a').contains('Features')
    cy.get(':nth-child(6) > .mdc-button__label').contains('Login')
  })

  it("Validate the login of user", () =>{
    cy.get(':nth-child(6) > .mdc-button__label').contains('Login').click();
    cy.get('#mat-input-0').type('john.doe@gmail.com')
    cy.get('#mat-input-1').click().type('Marsh2024')
    cy.get('form > button').contains('Login').click()
  })
})
