import {test, expect, Page} from '@playwright/test';

test.describe.configure({mode: 'serial'});

test.describe('Poker', () => {
  let page: Page;
  const consoleErrors: string[] = [];

  const creditsText = () => page.locator('.game h1');
  const betText = () => page.locator('.game h2');
  const handImages = () => page.locator('.hand tr td img');
  const holdButtons = () => page.locator('.holdButton');
  const dealButton = () => page.locator('.dealButton');
  const drawButton = () => page.locator('.drawButton');
  const bestHand = () => page.locator('.bestHand');

  const handSrcs = async () => handImages().evaluateAll(
    imgs => imgs.map(img => img.getAttribute('src')));

  test.beforeAll(async ({browser}) => {
    page = await browser.newPage();
    page.on('console', msg => {
      if (msg.type() === 'error') consoleErrors.push(msg.text());
    });
    await page.goto('/');
  });

  test.afterEach(() => {
    expect(consoleErrors).toEqual([]);
  });

  test.describe('New Game', () => {
    test('displays credits', async () => {
      await expect(creditsText()).toHaveText('Total Credits: 50');
    });

    test('displays default current bet', async () => {
      await expect(betText()).toHaveText('Bet: 1');
    });

    test('displays payout schedule', async () => {
      await expect(page.locator('.payout table tbody tr')).toHaveCount(9);

      const royalFlushRow = page.locator('.payout table tbody tr', {hasText: 'ROYAL_FLUSH'});
      await expect(royalFlushRow.locator('td')).toHaveText(['ROYAL_FLUSH', '250', '500', '750', '1000', '4000']);

      const jacksRow = page.locator('.payout table tbody tr', {hasText: 'JACKS_OR_BETTER'});
      await expect(jacksRow.locator('td')).toHaveText(['JACKS_OR_BETTER', '1', '2', '3', '4', '5']);
    });

    test('does not display undealt hand', async () => {
      await expect(handImages()).toHaveCount(0);
    });

    test('lets user deal', async () => {
      await expect(dealButton()).toBeVisible();
      await expect(drawButton()).toHaveCount(0);
    });
  });

  test.describe('Deal', () => {
    test('does not allow user to hold cards', async () => {
      await expect(holdButtons()).toHaveCount(0);
    });

    test('does not indicate best hand', async () => {
      await expect(bestHand()).toHaveText('');
    });

    test('allows user to increase current bet by 1', async () => {
      await page.locator('.betOneButton').click();
      await expect(betText()).toHaveText('Bet: 2');
    });

    test('allows user to increase current bet to max', async () => {
      await page.locator('.betMaxButton').click();
      await expect(betText()).toHaveText('Bet: 5');
    });

    test('displays fresh hand when deal button is clicked', async () => {
      await dealButton().click();
      await expect(handImages()).toHaveCount(5);
      for (const src of await handSrcs()) {
        expect(src).toMatch(/^assets\/cards\/[A-Z]+_[A-Z]+\.png$/);
      }
    });

    test('decreases credits by amount of bet', async () => {
      await expect(creditsText()).toHaveText('Total Credits: 45');
    });
  });

  test.describe('Draw', () => {
    let originalHand: (string | null)[];

    test('lets user draw new cards', async () => {
      await expect(dealButton()).toHaveCount(0);
      await expect(drawButton()).toBeVisible();
    });

    test('allows user to hold cards', async () => {
      await expect(holdButtons()).toHaveCount(5);
      originalHand = await handSrcs();
      await holdButtons().nth(1).click();
      await holdButtons().nth(3).click();
    });

    test('displays new hand with held cards', async () => {
      await drawButton().click();
      await expect(drawButton()).toHaveCount(0);

      const newHand = await handSrcs();
      expect(newHand[0]).not.toEqual(originalHand[0]);
      expect(newHand[1]).toEqual(originalHand[1]);
      expect(newHand[2]).not.toEqual(originalHand[2]);
      expect(newHand[3]).toEqual(originalHand[3]);
      expect(newHand[4]).not.toEqual(originalHand[4]);
    });

    test('indicates best hand out of drawn cards', async () => {
      await expect(bestHand()).toBeVisible();
    });

    test('lets user deal new hand', async () => {
      await expect(dealButton()).toBeVisible();
      await expect(drawButton()).toHaveCount(0);
    });
  });
});
