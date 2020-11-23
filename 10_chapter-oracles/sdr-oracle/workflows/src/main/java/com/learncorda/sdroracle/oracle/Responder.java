package com.learncorda.sdroracle.oracle;

import co.paralleluniverse.fibers.Suspendable;
import com.learncorda.sdroracle.client.Initiator;
import net.corda.core.crypto.TransactionSignature;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.FlowSession;
import net.corda.core.flows.InitiatedBy;
import net.corda.core.node.ServiceHub;
import net.corda.core.transactions.FilteredTransaction;

// ******************
// * Responder flow *
// ******************
@InitiatedBy(Initiator.class)
public class Responder extends FlowLogic<Void> {
    private FlowSession counterpartySession;

    public Responder(FlowSession counterpartySession) {
        this.counterpartySession = counterpartySession;
    }

    @Suspendable
    @Override
    public Void call() throws FlowException {
        System.out.println("Responding");
        ServiceHub sb = getServiceHub();
        FilteredTransaction ftx = counterpartySession.receive(FilteredTransaction.class).unwrap(s -> s);
        SDROracle sdrOracle = sb.cordaService(SDROracle.class);
        final TransactionSignature signature = sdrOracle.sign(ftx);
        counterpartySession.send(signature);
        return null;
    }
}
