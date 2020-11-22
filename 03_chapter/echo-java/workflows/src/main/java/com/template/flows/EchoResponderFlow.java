package com.template.flows;

import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.FlowSession;
import net.corda.core.flows.InitiatedBy;

// ******************
// * EchoResponderFlow flow *
// ******************
@InitiatedBy(EchoInitiatorFlow.class)
public class EchoResponderFlow extends FlowLogic<Void> {
    private FlowSession counterpartySession;

    public EchoResponderFlow(FlowSession counterpartySession) {
        this.counterpartySession = counterpartySession;
    }

    @Suspendable
    @Override
    public Void call() throws FlowException {
        System.out.println("Counterparty: " + counterpartySession.getCounterparty().getName());
        String inboundMessage = counterpartySession.receive(String.class).unwrap(s -> s);
        System.out.println("Inbound message: " + inboundMessage);
        String reversed = new StringBuilder(inboundMessage).reverse().toString();
        System.out.println("Reversed: " + reversed);
        counterpartySession.send(reversed);
        return null;
    }
}
