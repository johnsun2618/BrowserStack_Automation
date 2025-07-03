Feature: Validate the first five Opinion articles on ElPais

  Background:
    Given User opens ElPais homepage in Spanish

  Scenario: Scrape, translate, and analyse the first five Opinion articles
    When User navigates to the Opinion section
    And User fetches the first five Opinion articles
    Then User prints each article's Spanish title and content and download images
    When User translates all article titles to English
    And User prints every word that appears more than twice across all translated titles
    

