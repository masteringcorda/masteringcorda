package com.learncorda.tododist.flows;

import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.fibers.Suspendable;
import co.paralleluniverse.strands.Strand;
import net.corda.core.flows.*;
import net.corda.core.transactions.SignedTransaction;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;


@InitiatedBy(AssignToDoInitiator.class)
public class AssignToDoResponder extends FlowLogic<SignedTransaction> {
    private FlowSession counterpartySession;

    public AssignToDoResponder(FlowSession counterpartySession) {
        this.counterpartySession = counterpartySession;
    }

    @Suspendable
    @Override
    public SignedTransaction call() throws FlowException {
        System.out.println("responder called");
        final SignTransactionFlow signTransactionFlow = new SignTransactionFlow(counterpartySession) {
            @Override
            protected void checkTransaction(@NotNull SignedTransaction stx) throws FlowException {
                System.out.println("check!!");
            }
        };

        try {
            Strand.sleep(20, TimeUnit.SECONDS); // Force a time window expiration - can also use FlowLogic.sleep()
        } catch (SuspendExecution suspendExecution) {
            suspendExecution.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    SignedTransaction stx = subFlow(signTransactionFlow);
    return subFlow(new ReceiveFinalityFlow(counterpartySession, stx.getId()));
    }

}