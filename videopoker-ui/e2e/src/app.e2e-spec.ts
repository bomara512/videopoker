import {AppPage} from './app.po';
import {browser, logging} from 'protractor';

describe('Poker', () => {
  let page: AppPage;

  beforeAll(async () => {
    page = new AppPage();
    await page.navigateTo();
  });

  describe('New Game', () => {
    it('should display credits', async () => {
      expect(await page.getCreditsText()).toEqual('Total Credits: 50');
    });

    it('should display default current bet', async () => {
      expect(await page.getBetText()).toEqual('Bet: 1');
    });

    it('should display payout schedule', async () => {
      const payoutScheduleRows = await page.getPayoutSchedule();
      expect(payoutScheduleRows.length).toEqual(9);

      const payoutScheduleForRoyalFlush: string[] = await page.getPayoutScheduleFor('ROYAL_FLUSH');
      expect(payoutScheduleForRoyalFlush).toEqual(['ROYAL_FLUSH', '250', '500', '750', '1000', '4000']);

      const payoutScheduleForJacksOrBetter: string[] = await page.getPayoutScheduleFor('JACKS_OR_BETTER');
      expect(payoutScheduleForJacksOrBetter).toEqual(['JACKS_OR_BETTER', '1', '2', '3', '4', '5']);
    });

    it('should display undealt hand', async () => {
      expect(await page.getHand()).toEqual(['CARD_BACK', 'CARD_BACK', 'CARD_BACK', 'CARD_BACK', 'CARD_BACK']);
    });

    it('should let user deal', async () => {
      expect(await page.getDealButton().isPresent()).toBeTruthy();
      expect(await page.getDrawButton().isPresent()).toBeFalsy();
    });
  });

  describe('Deal', () => {
    it('should not allow user to hold cards', async () => {
      expect(await page.getHoldButtons().isPresent()).toBeFalsy();
    });

    it('should not indicate best hand', async () => {
      expect(await page.getBestHand().getText()).toBeFalsy();
    });

    it('should allow user to increase current bet by 1', async () => {
      expect(await page.getBetOneButton().isPresent()).toBeTruthy();
      await page.getBetOneButton().click();
      expect(await page.getBetText()).toEqual('Bet: 2');
    });

    it('should allow user to increase current bet to max', async () => {
      expect(await page.getBetMaxButton().isPresent()).toBeTruthy();
      await page.getBetMaxButton().click();
      expect(await page.getBetText()).toEqual('Bet: 5');
    });

    it('should display fresh hand when deal button is clicked', async () => {
        const dealButton = page.getDealButton();
        await dealButton.click();
        expect(await page.getHand()).not.toEqual(['CARD_BACK', 'CARD_BACK', 'CARD_BACK', 'CARD_BACK', 'CARD_BACK']);
    });

    it('should decrease credits by amount of bet', async () => {
      expect(await page.getCreditsText()).toEqual('Total Credits: 45');
    });
  });

  describe('Draw', () => {
    let originalHand: string[];

    beforeAll(async () => {
      originalHand = await page.getHand();
    });

    it('should let user draw new cards', async () => {
      expect(await page.getDealButton().isPresent()).toBeFalsy();
      expect(await page.getDrawButton().isPresent()).toBeTruthy();
    });

    it('should allow user to hold cards', async () => {
      expect(await page.getHoldButtons().isPresent()).toBeTruthy();
      await page.getHoldButtons().get(1).click();
      await page.getHoldButtons().get(3).click();
    });

    it('should display new hand with held cards', async () => {
      const drawButton = page.getDrawButton();
      await drawButton.click();
      const newHand = await page.getHand();
      expect(newHand[0]).not.toEqual(originalHand[0]);
      expect(newHand[1]).toEqual(originalHand[1]);
      expect(newHand[2]).not.toEqual(originalHand[2]);
      expect(newHand[3]).toEqual(originalHand[3]);
      expect(newHand[4]).not.toEqual(originalHand[4]);
    });

    it('should indicate best hand out of drawn cards', async () => {
      expect(await page.getBestHand().isPresent()).toBeTruthy();
    });

    it('should let user deal new hand', async () => {
      expect(await page.getDealButton().isPresent()).toBeTruthy();
      expect(await page.getDrawButton().isPresent()).toBeFalsy();
    });
  });

  afterEach(async () => {
    // Assert that there are no errors emitted from the browser
    const logs = await browser.manage().logs().get(logging.Type.BROWSER);
    expect(logs).not.toContain(jasmine.objectContaining({
      level: logging.Level.SEVERE,
    } as logging.Entry));
  });
});
