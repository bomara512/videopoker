import {browser, by, element} from 'protractor';

export class AppPage {
  async navigateTo(): Promise<unknown> {
    return browser.get(browser.baseUrl);
  }

  async getCreditsText(): Promise<string> {
    return element(by.css('app-root .game h1')).getText();
  }

  async getBetText() {
    return element(by.css('app-root .game h2')).getText();
  }

  async getPayoutSchedule() {
    return element.all(by.css('.payout table tr'));
  }

  async getPayoutScheduleFor(hand: string) {
    return element.all(by.cssContainingText('.payout table tr', hand))
      .all(by.css('td'))
      .map(e => e!.getText()) as Promise<string[]>;
  }

  async getHand() {
    return element.all(by.css('.hand tr td'))
      .map(e => e!.getText()) as Promise<string[]>;
  }

  getDealButton() {
    return element(by.css('.hand .dealButton'));
  }

  getDrawButton() {
    return element(by.css('.hand .drawButton'));
  }

  getBestHand() {
    return element(by.css('.hand .bestHand'));
  }

  getHoldButtons() {
    return element.all(by.css('.hand .holdButton'));
  }

  getBetOneButton() {
    return element.all(by.css('.hand .betOneButton'));
  }

  getBetMaxButton() {
    return element.all(by.css('.hand .betMaxButton'));
  }
}
