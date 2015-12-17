/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.pti.myatm;

import junit.framework.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mockito;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author andrii
 */


public class ATMTest {

    @Test(expected = IllegalArgumentException.class)
    public void testNegativeMoneyATM() {
        ATM atm = new ATM(-5);
    }

    @Test
    public void testGetATMMoney() {
        ATM atm = new ATM(100.0);
        Assert.assertEquals(atm.getMoneyInATM(), 100.0);
    }

    @Test(expected = NullPointerException.class)
    public void testCardValidationNullPointerException() {
        ATM atm = new ATM(1000);
        atm.validateCard(null, 777);
    }

    @Test
    public void testCardValidationBlockedCard() {
        ATM atm = new ATM(1000);
        Card card = mock(Card.class);
        when(card.isBlocked()).thenReturn(true);
        boolean result = atm.validateCard(card, 777);
        Assert.assertFalse(result);
    }

    @Test
    public void testCardAcceptation() {
        ATM atm = new ATM(1000);
        int pin = 777;
        Card card = mock(Card.class);
        when(card.isBlocked()).thenReturn(false);
        when(card.checkPin(pin)).thenReturn(true);

        boolean result = atm.validateCard(card, pin);
        Assert.assertTrue(result);
    }

    @Test(expected = NoCardException.class)
    public void testCheckBalanceNoCard() throws NoCardException, BlockedCardException {
        ATM atm = new ATM(1000);
        atm.checkBalance();
    }

    @Test
    public void testCheckBalance() throws NoCardException, BlockedCardException {
        ATM atm = new ATM(1000.0);
        int pin = 777;
        double balance = 1000.0;

        Account account = mock(Account.class);
        when(account.getBalance()).thenReturn(balance);

        Card card = mock(Card.class);
        when(card.getAccount()).thenReturn(account);
        when(card.isBlocked()).thenReturn(false);
        when(card.checkPin(pin)).thenReturn(true);
        atm.validateCard(card, pin);

        Assert.assertEquals(atm.checkBalance(), 1000.0);
    }

    @Test(expected = NoCardException.class)
    public void testGetCashNoCard() throws NotEnoughMoneyInAccountException, NoCardException, NotEnoughMoneyInATMException, BlockedCardException {
        ATM atm = new ATM(1000.0);
        atm.getCash(1000.0);
    }

    @Test(expected = NotEnoughMoneyInAccountException.class)
    public void testGetCashNoEnoughMoney() throws NotEnoughMoneyInAccountException, NoCardException, NotEnoughMoneyInATMException, BlockedCardException {
        double amount = 1001;
        ATM atm = new ATM(100);
        double actual = 1000;
        int pin = 777;

        Account account = mock(Account.class);
        when(account.getBalance()).thenReturn(actual);

        Card card = mock(Card.class);
        when(card.getAccount()).thenReturn(account);
        when(card.isBlocked()).thenReturn(false);
        when(card.checkPin(pin)).thenReturn(true);

        atm.validateCard(card, pin);
        atm.getCash(amount);
    }

    @Test(expected = NotEnoughMoneyInATMException.class)
    public void testGetCashNoEnoughMoneyInATM() throws NotEnoughMoneyInAccountException, NoCardException, NotEnoughMoneyInATMException, BlockedCardException {
        double amount = 1001;
        ATM atm = new ATM(100);
        int pin = 777;
        double actual = 1005;

        Account account = mock(Account.class);
        when(account.getBalance()).thenReturn(actual);

        Card card = mock(Card.class);
        when(card.getAccount()).thenReturn(account);
        when(card.isBlocked()).thenReturn(false);
        when(card.checkPin(pin)).thenReturn(true);

        atm.validateCard(card, pin);
        atm.getCash(amount);
    }

    @Test
    public void testGetCashBalanceOrderGetBalanceBeforeWithdraw() throws NotEnoughMoneyInAccountException, NoCardException, NotEnoughMoneyInATMException, BlockedCardException {
        double amount = 1000;
        ATM atm = new ATM(10000);
        double actual = 10000;
        int pin = 777;

        Account account = mock(Account.class);
        when(account.getBalance()).thenReturn(actual);
        when(account.withdrow(amount)).thenReturn(amount);

        Card card = mock(Card.class);
        when(card.getAccount()).thenReturn(account);
        when(card.isBlocked()).thenReturn(false);
        when(card.checkPin(pin)).thenReturn(true);
        atm.validateCard(card, pin);
        atm.getCash(amount);

        InOrder order = inOrder(account);
        order.verify(account).getBalance();
        order.verify(account).withdrow(amount);
    }

    @Test
    public void testGetCash() throws NotEnoughMoneyInAccountException, NoCardException, NotEnoughMoneyInATMException, BlockedCardException {
        double amount = 1000.0;
        ATM atm = new ATM(1000.0);
        int pin = 777;
        double actual = 1000.0;

        
        Account account = mock(Account.class);
        when(account.getBalance()).thenReturn(actual);

        Card card = mock(Card.class);
        when(card.getAccount()).thenReturn(account);
        when(card.isBlocked()).thenReturn(false);
        when(card.checkPin(pin)).thenReturn(true);

        Assert.assertTrue(atm.validateCard(card, pin));
        atm.getCash(amount);
        Assert.assertEquals(0.0, atm.getMoneyInATM());
    }

    @Test
    public void testResolveGetMoneyInATM() throws BlockedCardException, NoCardException, NotEnoughMoneyInAccountException, NotEnoughMoneyInATMException {
        double amount = 1000.0;
        ATM atm = new ATM(1000.0);
        int pin = 777;
        double actual = 1000.0;

        ATM spy = Mockito.spy(new ATM(1000.0));
        Account account = mock(Account.class);
        when(account.getBalance()).thenReturn(actual);
        Card card = mock(Card.class);
        when(card.getAccount()).thenReturn(account);
        when(card.isBlocked()).thenReturn(false);
        when(card.checkPin(pin)).thenReturn(true);
        Assert.assertTrue(spy.validateCard(card, pin));

        spy.getCash(amount);

        verify(spy, times(1)).getMoneyInATM();

        }

    @Test
    public void testCorrectCashWithdraw() throws BlockedCardException, NoCardException, NotEnoughMoneyInATMException, NotEnoughMoneyInAccountException {
        double amount = 1000.0;
        ATM atm = new ATM(1000.0);
        int pin = 777;
        double actual = 1000.0;


        Account account = mock(Account.class);
        when(account.getBalance()).thenReturn(actual);

        Card card = mock(Card.class);
        when(card.getAccount()).thenReturn(account);
        when(card.isBlocked()).thenReturn(false);
        when(card.checkPin(pin)).thenReturn(true);

        Assert.assertTrue(atm.validateCard(card, pin));

        atm.getCash(amount);

        verify(account, times(1)).withdrow(anyDouble());
    }


}
