describe('Home page', () => {
  beforeEach(()=>{
    cy.visit('/home');
  })

  it('Validate the components of the Home page', () => {
    cy.get(':nth-child(5) > .mdc-button__label').contains('Home')
    cy.get('h1').contains('Welcome')
    cy.get('.mat-mdc-nav-list > :nth-child(2)').should('exist')
    cy.get('.mat-mdc-nav-list > :nth-child(4)').should('exist')
    cy.get('.mat-mdc-nav-list > :nth-child(6)').should('exist')
  })

  it('Validate Portfolio holding page', () =>{
    cy.get('.mat-mdc-nav-list > :nth-child(4)').click()
    cy.get('.title').contains('Current Balance')
    cy.get('.home-content > .ng-star-inserted > :nth-child(2)').contains('My Holdings')
    cy.get('.ag-center-cols-viewport').should('exist')
  })

  it('Validate Trade History page', () =>{
    cy.get('.mat-mdc-nav-list > :nth-child(6)').click()
    cy.get('h2').contains('Trade History')
    cy.get('.ag-center-cols-viewport').should('exist')
  })

  it('Validate Client Preferences page', () =>{
    cy.get('.mat-mdc-nav-list > :nth-child(2)').click()
    cy.get('h2').contains('CLIENT PREFERENCES')
    cy.get('#mat-select-value-1').click()
    cy.get('#mat-option-0').click()
    cy.get('#mat-select-value-3').click()
    cy.get('#mat-option-4').click()
    cy.get('#mat-select-value-5').click()
    cy.get('#mat-option-8').click()
    cy.get('#mat-select-value-7').click()
    cy.get('#mat-option-11').click()
    cy.get('.acceptAdvisor').check()
    cy.get('.center-buttons > .mdc-button > .mdc-button__label').contains('Save').click()
    cy.get('h1').contains('Welcome')
  })
})
  