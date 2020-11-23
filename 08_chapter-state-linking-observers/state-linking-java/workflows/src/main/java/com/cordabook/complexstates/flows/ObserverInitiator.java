package com.cordabook.complexstates.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.cordabook.complexstates.contracts.Commands;
import com.cordabook.complexstates.states.EquityCUSIPReferenceState;
import com.cordabook.complexstates.states.TradeState;
import com.google.common.collect.ImmutableList;
import net.corda.core.contracts.ReferencedStateAndRef;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.messaging.FlowHandle;
import net.corda.core.node.ServiceHub;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;

import java.security.PublicKey;
import java.util.Collections;
import java.util.stream.Collectors;

@InitiatingFlow
@StartableByRPC
public class ObserverInitiator extends FlowLogic<Void> {

    public ObserverInitiator(SignedTransaction stx) { this.stx = stx;   }

    private SignedTransaction stx;


    @Suspendable
    @Override
    public Void call() throws FlowException {
        ServiceHub serviceHub = getServiceHub();
        Party observer = serviceHub.getIdentityService().partiesFromName("Observer", true).iterator().next();
        FlowSession observerSession = initiateFlow(observer);
        subFlow(new SendTransactionFlow(observerSession,stx));


        return null;
    }

}
