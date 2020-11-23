package com.cordabook.complexstates.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.cordabook.complexstates.states.EquityCUSIPReferenceState;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.node.ServiceHub;
import net.corda.core.node.StatesToRecord;
import net.corda.core.transactions.SignedTransaction;
import org.jetbrains.annotations.NotNull;

import java.security.PublicKey;

// ******************
// * TradeResponder flow *
// ******************
@InitiatedBy(ObserverInitiator.class)
public class ObserverResponder extends FlowLogic<Void> {
    private FlowSession counterpartySession;

    public ObserverResponder(FlowSession counterpartySession) {
        this.counterpartySession = counterpartySession;
    }

    @Suspendable
    @Override
    public Void call() throws FlowException {

        subFlow(new ReceiveTransactionFlow(counterpartySession, true, StatesToRecord.ALL_VISIBLE));
        return null;
    }
}
