package com.learncorda.tododist.contracts;

import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.Contract;
import net.corda.core.transactions.LedgerTransaction;

// ************
// * Contract *
// ************
public class TemplateContract implements Contract {
    // This is used to identify our contract when building a transaction.

    // commenting this string allowed app to run just fine, why is this needed?
    //public static final String ID = "com.cordabook.tododist.contracts.TemplateContract";

    // A transaction is valid if the verify() function of the contract of all the transaction's input and output states
    // does not throw an exception.
    @Override
    public void verify(LedgerTransaction tx) {
        System.out.println("TemplateContract fired");
    }

    /// https://stackoverflow.com/questions/2400828/inner-class-within-interface

    // Used to indicate the transaction's intent.
    public interface Commands extends CommandData {
        class Action implements Commands {}
        class Action2 implements Commands {}
    }

}