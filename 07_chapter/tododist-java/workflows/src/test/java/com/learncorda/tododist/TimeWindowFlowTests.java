package com.learncorda.tododist;

import com.learncorda.tododist.flows.Responder;
import com.learncorda.tododist.flows.TimeWindowExampleFlow;
import com.google.common.collect.ImmutableList;
import net.corda.core.concurrent.CordaFuture;
import net.corda.testing.node.MockNetwork;
import net.corda.testing.node.MockNetworkParameters;
import net.corda.testing.node.StartedMockNode;
import net.corda.testing.node.TestCordapp;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TimeWindowFlowTests {
    private final MockNetwork network = new MockNetwork(new MockNetworkParameters(ImmutableList.of(
            TestCordapp.findCordapp("com.cordabook.tododist.contracts"),
            TestCordapp.findCordapp("com.cordabook.tododist.flows")
    )));
    private final StartedMockNode a = network.createNode();
    private final StartedMockNode b = network.createNode();
    public TimeWindowFlowTests() {
        a.registerInitiatedFlow(Responder.class);
        b.registerInitiatedFlow(Responder.class);
    }
    @Before
    public void setup() {
        network.runNetwork();
    }
    @After
    public void tearDown() {
        network.stopNodes();
    }
    @Test
    public void dummyTest() throws Exception {
        //CreateToDoFlow createToDoFlow = new CreateToDoFlow("Complete me.");
        TimeWindowExampleFlow twf = new TimeWindowExampleFlow();
        CordaFuture future = a.startFlow(twf);
        future.get();
    }
}
