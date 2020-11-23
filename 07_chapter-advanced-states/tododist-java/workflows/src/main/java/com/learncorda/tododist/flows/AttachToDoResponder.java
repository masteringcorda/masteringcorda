package com.learncorda.tododist.flows;

import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.flows.*;
import net.corda.core.transactions.SignedTransaction;
import org.jetbrains.annotations.NotNull;



public class AttachToDoResponder extends FlowLogic<SignedTransaction> {
    private FlowSession counterpartySession;

    public AttachToDoResponder(FlowSession counterpartySession) {
        this.counterpartySession = counterpartySession;
    }

    @Suspendable
    @Override
    public SignedTransaction call() throws FlowException {
        System.out.println("responder called");
        final SignTransactionFlow signTransactionFlow = new SignTransactionFlow(counterpartySession) {
            @Override
            protected void checkTransaction(@NotNull SignedTransaction stx) throws FlowException { // when is this called? doesnt seem to be called
                System.out.println("check!!");
            }
        };
        SignedTransaction stx = subFlow(signTransactionFlow);
        return subFlow(new ReceiveFinalityFlow(counterpartySession, stx.getId()));
    }

}