describe('User Profile', () => {
    beforeEach(()=>{
        cy.visit('/home');
    })

    it('Validate the User Profile page', () => {
        cy.get('.mdc-button__label > .mat-mdc-tooltip-trigger').contains('User').click()
        cy.get('.mat-mdc-menu-content > :nth-child(1)').contains('Profile').click()
        cy.get(':nth-child(1) > h2').contains('Personal Details').should('exist')
    })

    it('Validate the logout functionality', () => {
        cy.get('.mdc-button__label > .mat-mdc-tooltip-trigger').contains('User').click()
        cy.get('.mat-mdc-menu-content > :nth-child(2)').contains('Logout').click()
        cy.get('.auth-section > h2').contains(' Let us help you secure your future! ')
    })
})
    
