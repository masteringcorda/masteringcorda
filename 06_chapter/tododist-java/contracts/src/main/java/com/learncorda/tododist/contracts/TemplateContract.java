package com.learncorda.tododist.contracts;

import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.Contract;
import net.corda.core.contracts.ContractState;
import net.corda.core.transactions.LedgerTransaction;

import java.util.List;

// ************
// * Contract *
// ************
public class TemplateContract implements Contract {

    @Override
    public void verify(LedgerTransaction tx) {

    }

    /// https://stackoverflow.com/questions/2400828/inner-class-within-interface

    // Used to indicate the transaction's intent.
    public interface Commands extends CommandData {
        class Action implements Commands {}

    }
}