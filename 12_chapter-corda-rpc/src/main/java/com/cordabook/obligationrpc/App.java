/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package com.cordabook.obligationrpc;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.corda.client.rpc.CordaRPCClient;
import net.corda.client.rpc.RPCConnection;
import net.corda.client.rpc.internal.ReconnectingCordaRPCOps;
import net.corda.core.concurrent.CordaFuture;
import net.corda.core.contracts.*;
import net.corda.core.crypto.SecureHash;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.core.identity.PartyAndCertificate;
import net.corda.core.messaging.CordaRPCOps;
import static net.corda.core.messaging.CordaRPCOpsKt.*;
import net.corda.core.messaging.CordaRPCOpsKt;
import net.corda.core.messaging.FlowHandle;
import net.corda.core.node.NetworkParameters;
import net.corda.core.node.NodeDiagnosticInfo;
import net.corda.core.node.NodeInfo;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.*;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.utilities.NetworkHostAndPort;
import net.corda.core.utilities.OpaqueBytes;
import net.corda.examples.obligation.Obligation;
import net.corda.examples.obligation.flows.IssueObligation;
import net.corda.examples.obligation.flows.SettleObligation;
import net.corda.examples.obligation.flows.TransferObligation;
import net.corda.finance.contracts.asset.Cash;
import net.corda.finance.flows.CashIssueFlow;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.FileAlreadyExistsException;
import java.util.*;
import java.util.concurrent.*;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import


public class App {
    private static CordaRPCOps rpc;
    private static CordaRPCOps rpcB;
    private static CordaRPCOps rpcC;
    public static void main(String[] args) {
        final String user = "user1";
        final String password = "test";

        // replace IP address
        prints("Connecting to 3.87.185.112");
        NetworkHostAndPort portPartyA = new NetworkHostAndPort("3.87.185.112",10006);
        CordaRPCClient clientPartyA = new CordaRPCClient(portPartyA);
        RPCConnection<CordaRPCOps> connPartyA = clientPartyA.start(user,password);
        rpc = connPartyA.getProxy();


//        rpcB = new CordaRPCClient(new NetworkHostAndPort("localhost",10009)).start(user,password).getProxy();
//        rpcC = new CordaRPCClient(new NetworkHostAndPort("localhost",10012)).start(user,password).getProxy();




        // comment / uncomment below as needed

        doNodeInfo();
        doGetNetworkParameters();
        doNodeDiagnosticInfo();
        doCurrentNodeTime();
        doNetworkSnapshot();
        doClearNetworkMapCache();
        doNetworkMapFeed();
        doRegisteredFlows();
        doStartFlowDynamic();
        doKillFlow();
        doPartyAndKey();
        doAttachments();
        doStartTrackedFlowDynamic();
        doKillFlow();
        doVaultQuery();
        generateRandomCashState();
        doVaultQueryCriteria();
        doVaultQueryWithSort();
        doVaultQueryWithPagination();
        doVaultTrackBy();
        doTransactionNotes();
        doNotary();
        doStateMachine();
        doAttachments();
        doDrainOff();

    }

    public static void doNode() throws Exception{
        Executor executor = Executors.newFixedThreadPool(4);
        rpc.setFlowsDrainingModeEnabled(false);
        prints("Is waiting for shutdown: " + rpc.isWaitingForShutdown());
        prints("Flow drain mode enabled: " + rpc.isFlowsDrainingModeEnabled());


        rpc.setFlowsDrainingModeEnabled(true);
        prints("Flow drain mode enabled: " + rpc.isFlowsDrainingModeEnabled());

        prints("Issuing 1 obligation");
        executor.execute(() -> issueObligation());
        Thread.sleep(5000);

        prints("Disable drain mode");
        rpc.setFlowsDrainingModeEnabled(false);
        prints("Issuing 1 obligation again");
        executor.execute(() -> issueObligation());
        Thread.sleep(5000);


        prints("Add more flows");
        for(int i =0; i<30;i++) {
            prints("Launching flow: " + i);
            executor.execute(() -> issueObligation());
        }

        Thread.sleep(10000);
        prints("Terminate with draining enabled");
        // We lose attempt terminate
//        rpc.terminate(true);
//        prints("Is waiting for shutdown: " + rpc.isWaitingForShutdown());
//        prints("Flow drain mode enabled: " + rpc.isFlowsDrainingModeEnabled());

//
//        prints("To hell with it");
//        // Bonkers
        rpc.terminate(false);
//        rpc.shutdown();

    }

    public static void doDrainOff() {
        rpc.setFlowsDrainingModeEnabled(false);
    }



//    private static List<Callable<SignedTransaction>> callableObligation(int n) {
//
//        List callables = new ArrayList<Callable<SignedTransaction>>(n);
//        for (int i=0;i<n;i++)
//            callables.add(new CallableObligation());
//        return callables;
//}

    public static void doAttachments() {

        String url = "https://github.com/masteringcorda/masteringcorda/raw/main/07_chapter-advanced-states/sample.jar.zip";
        SecureHash hash = SecureHash.sha256("378D571FE9CFE205CA15516FF10FDB34F603B9C2F4FE3A337D1193E487FB2A26");

        try {
//            SecureHash attachmentHash = null;
//            if(!rpc.attachmentExists(hash))
//                attachmentHash = rpc.uploadAttachment(new BufferedInputStream(new URL(url).openStream()));
//            prints(attachmentHash.toString());
//            prints("The attachment exists? " + rpc.attachmentExists(attachmentHash));
//            attachmentHash = rpc.uploadAttachment(new BufferedInputStream(new URL(url).openStream()));
//            prints(attachmentHash.toString());
//
//            final BufferedInputStream attachment = new BufferedInputStream(new URL(url).openStream());
//            new BufferedInputStream(new URL(url).openStream());
//
//            SecureHash attachmentHashA = rpc.uploadAttachmentWithMetadata(fakeBytes(), "RPCClient", "FileA.jar.zip");
//            SecureHash attachmentHashB = rpc.uploadAttachmentWithMetadata(fakeBytes(),"Web app","FileB.jar.zip");
//            SecureHash attachmentHashC = rpc.uploadAttachmentWithMetadata(fakeBytes(),"Mobile","FileC.jar.zip");
//            SecureHash attachmentHashD = rpc.uploadAttachmentWithMetadata(fakeBytes(),"RPCClient","FileD.jar.zip");

            ImmutableList.of(
                    rpc.uploadAttachmentWithMetadata(randomJarZip(),
                            "RPCClient", "FileA.jar.zip"),
                    rpc.uploadAttachmentWithMetadata(randomJarZip(),
                            "Web", "FileB.jar.zip"),
                    rpc.uploadAttachmentWithMetadata(randomJarZip(),
                            "RPCClient", "FileC.jar.zip"),
                    rpc.uploadAttachmentWithMetadata(randomJarZip(),
                            "Mobile", "FileD.jar.zip")
            ).forEach(secureHash ->
                    prints(secureHash.toString()));


            final ColumnPredicate.EqualityComparison<String> predicate = QueryCriteriaUtils.builder(builder -> builder.equal("RPCClient"));
            final AttachmentQueryCriteria.AttachmentsQueryCriteria attachmentsQueryCriteria = new AttachmentQueryCriteria.AttachmentsQueryCriteria(predicate);
            rpc.queryAttachments(attachmentsQueryCriteria, null).forEach(secureHash -> {
                prints("Matching attachment: " + secureHash);
            });



        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static InputStream randomJarZip() throws IOException {
        byte[] fakeBytes = new byte[50];
        new Random().nextBytes(fakeBytes);
        PipedOutputStream pot = new PipedOutputStream();
        PipedInputStream pin = new PipedInputStream(pot);
        ZipOutputStream zos = new ZipOutputStream(pot);
        zos.putNextEntry(new ZipEntry(new Random().ints(97,122)
                .limit(10)
                .collect(StringBuilder::new,StringBuilder::appendCodePoint,StringBuilder::append)
                .toString() +".jar"));
        zos.write(fakeBytes);
        zos.closeEntry();
        zos.flush();
        zos.close();
        pot.close();
        return pin;
    }

    public static void doStateMachine() {
        final ExecutorService executorService = Executors.newFixedThreadPool(5);
        for(int i=0;i<5;i++)
            executorService.submit(() -> issueObligation());
        try {Thread.sleep(1000); } catch (Exception e) {};
        rpc.stateMachinesSnapshot().forEach(stateMachineInfo -> {
            prints("Running flow: " + stateMachineInfo.getFlowLogicClassName() + " State machine ID: " + stateMachineInfo.getId());
        });

//        rpc.stateMachinesFeed().getUpdates().toBlocking().forEach(stateMachineUpdate -> {
//            prints("Running state machine: " + stateMachineUpdate.getId());
//
//        });


    }

    public static void doNotary() {
        rpc.notaryIdentities().forEach(party ->
                prints("Notary: " + party.getName().getOrganisation()));
        final Party notaryForPartyC = rpc.notaryPartyFromX500Name(new CordaX500Name("Notary", "London", "GB"));
        prints("Notary found: " + notaryForPartyC.getName().getOrganisation());
    }
    public static void doTransactionNotes() {
        final SecureHash id = issueObligation().getId();
        rpc.addVaultTransactionNote(id,"We did it.");
        rpc.addVaultTransactionNote(id,"Yes we did.");
        rpc.getVaultTransactionNotes(id).forEach(App::prints);
    }

    public static void doVaultTrackBy() {
        Executors.newFixedThreadPool(1).submit(() -> {
            for(int i=0;i<5;i++) {
                issueObligation();
            }

        });

        QueryCriteria queryCriteria = new QueryCriteria.VaultQueryCriteria(Vault.StateStatus.CONSUMED)
                .and(new QueryCriteria.VaultQueryCriteria(Vault.StateStatus.UNCONSUMED));
        rpc.vaultTrackByCriteria(Obligation.class,queryCriteria).getUpdates().toBlocking().forEach(obligationUpdate -> {
            prints("Obligation generated: Linear ID: " + obligationUpdate.getProduced().iterator().next().getState().getData().getLinearId());
        });
    }

    public static void generateRandomCashState() {
        Amount<Currency> cash = null;
        Party notary = rpc.notaryIdentities().get(0);
        for (int i = 0; i < 20 ; i++) {
            cash = new Amount<>(new Random().nextInt(100), Currency.getInstance("USD"));
            rpc.startFlowDynamic(CashIssueFlow.class, cash, OpaqueBytes.of("1".getBytes()), notary);
        }
    }

    public static void doVaultQueryCriteria() {
        QueryCriteria.LinearStateQueryCriteria linearStateQueryCriteria = new QueryCriteria.LinearStateQueryCriteria(null, ImmutableList.of(UUID.fromString("e7d8c582-4ba7-4617-8587-faa3ecdf058a")));
        SortAttribute.Standard standard = new SortAttribute.Standard(Sort.CommonStateAttribute.STATE_REF_TXN_ID);
        Vault.Page<Obligation> obligationPage = rpc.vaultQueryBy(linearStateQueryCriteria, new PageSpecification(), new Sort(ImmutableList.of(new Sort.SortColumn(standard, Sort.Direction.DESC))), Obligation.class);
        obligationPage.getStates().forEach(obligationStateAndRef -> {
            prints("Obligation amount: " + obligationStateAndRef.getState().getData().getAmount());
            prints("Linear id: " + obligationStateAndRef.getState().getData().getLinearId());
        });


        // Replace with your own linear Id
        final UUID linearId = UUID.fromString("e7d8c582-4ba7-4617-8587-faa3ecdf058a");
        linearStateQueryCriteria = new QueryCriteria.LinearStateQueryCriteria(null, ImmutableList.of(linearId));
        obligationPage = rpc.vaultQueryByCriteria(linearStateQueryCriteria, Obligation.class);
        printObligationPage(obligationPage.getStates());

        // Fungible Asset

        final QueryCriteria.FungibleStateQueryCriteria fungibleStateQueryCriteria =
                new QueryCriteria.FungibleStateQueryCriteria(null, predicate, Vault.StateStatus.ALL,ImmutableSet.of(Cash.State.class), Vault.RelevancyStatus.ALL);
        Vault.Page<Cash.State> cashStates = rpc.vaultQueryByCriteria(fungibleStateQueryCriteria,Cash.State.class);
        printCashStates(cashStates.getStates());


        final ColumnPredicate.Between<Long> predicate = QueryCriteriaUtils.builder(builder -> builder.between(0L, 5L));
        printCashStates(
                rpc.vaultQueryByCriteria(
                        new QueryCriteria.FungibleAssetQueryCriteria(
                                null,
                                null,
                                predicate),
                        Cash.State.class).getStates()
        );


    }




    public static void doVaultQueryBy() {
        rpc.vaultQueryBy(fungibleAssetQueryCriteria,
                new PageSpecification(3,20),
                new Sort(ImmutableList.of(new Sort.SortColumn(standard, Sort.Direction.ASC))),
                Cash.State.class);
    }

    public static void doVaultQueryWithPagination() {
        Party partyA = rpc.partiesFromName("PartyA",true).iterator().next();
        printCashStates(
                rpc.vaultQueryByWithPagingSpec(
                        Cash.State.class,
                        new QueryCriteria.FungibleAssetQueryCriteria(ImmutableList.of(partyA)),
                        new PageSpecification(1,10))
                        .getStates()
        );

    }

    public static void doVaultQueryWithSort() {
        final SortAttribute.Standard standard = new SortAttribute.Standard(Sort.FungibleStateAttribute.QUANTITY);
        Party partyA = rpc.partiesFromName("PartyA",true).iterator().next();
        final QueryCriteria.FungibleAssetQueryCriteria fungibleAssetQueryCriteria = new QueryCriteria.FungibleAssetQueryCriteria(ImmutableList.of(partyA));
        final Vault.Page<Cash.State> statePage = rpc.vaultQueryByWithSorting(Cash.State.class,fungibleAssetQueryCriteria,
                new Sort(ImmutableList.of(new Sort.SortColumn(standard, Sort.Direction.DESC))));
        printCashStates(statePage.getStates());

    }



    private static void printStateMetaData(List<Vault.StateMetadata> vms) {
        System.out.println("----------------------------------------------------------------");
        System.out.printf("%11s %5s %10s %10s %10s", "Tx Hash", "Index", "Relevancy", "Status","Recorded");
        System.out.println();
        System.out.println("---------------------------------------------------------------");
        vms.forEach(smd -> {
            System.out.format("%11s %5s %10s %10s %10s",
                    smd.getRef().getTxhash().toString().substring(0,10) + "..",smd.getRef().getIndex(),smd.getRelevancyStatus(),smd.getStatus(),smd.getRecordedTime().toString());
            System.out.println();
        });
    }

    private static void printObligationPage(List<StateAndRef<Obligation>> obligations) {
        System.out.println("---------------------------------------------------------");
        System.out.printf("%11s %10s %10s %10s %10s", "Linear ID", "Amount", "Lender", "Borrower", "Paid");
        System.out.println();
        System.out.println("---------------------------------------------------------");
        obligations.forEach(ob -> {
            Obligation o = ob.getState().getData();
            System.out.format("%11s %10s %10s %10s %10s",
                    o.getLinearId().toString().substring(0,10) + "..", o.getAmount(), o.getLender().nameOrNull().getOrganisation(), o.getBorrower().nameOrNull().getOrganisation(), o.getPaid());
            System.out.println();
        });
    }

    public static void doVaultQuery() {
        prints("MAX " + Integer.MAX_VALUE);
        final Vault.Page<Obligation> obligationPage = rpc.vaultQuery(Obligation.class);
        printObligationPage(obligationPage.getStates());
        printStateMetaData(obligationPage.getStatesMetadata());

        obligationPage.getOtherResults().forEach(o -> {
            prints("Other: " + o);
        });

        // 0th item is same state
        prints(obligationPage.getStates().get(0).getRef().getTxhash().toString());
        prints(obligationPage.getStatesMetadata().get(0).getRef().getTxhash().toString());

        prints("Total states available: " + obligationPage.getTotalStatesAvailable());

        prints("State types: " + obligationPage.getStateTypes().name());

        final Vault.Page<Cash.State> cashPage = rpc.vaultQuery(Cash.State.class);
        printCashStates(cashPage.getStates());



    }

    private static void printCashStates(List<StateAndRef<Cash.State>> cash) {
        System.out.println("---------------------------------------------");
        System.out.printf("%11s %8s %8s %12s", "Owner", "Amount", "USD", "Tx Hash");
        System.out.println();
        System.out.println("---------------------------------------------");
        cash.forEach(c -> {
            Cash.State cs = c.getState().getData();
            System.out.format("%11s %8s %8s %12s",
                    cs.getOwner().nameOrNull().getOrganisation(),cs.getAmount().getQuantity(), "$ " + cs.getAmount().getQuantity() * cs.getAmount().getDisplayTokenSize().floatValue(),c.getRef().getTxhash().toString().substring(0,10) + "..");
            System.out.println();
        });
    }

//    public static void doAttachments() {
//        rpc.uploadAttachment()
//
//    }
    public static void doPartyAndKey() {
        Party someParty = rpc.partiesFromName("PartyC",true).iterator().next();
        CordaX500Name someName = someParty.getName();
        someParty = rpc.partyFromKey(someParty.getOwningKey());
        someParty = rpc.wellKnownPartyFromX500Name(someName);
        final NodeInfo nodeInfo = rpc.nodeInfoFromParty(someParty);
    }

    public static void doKillFlow() {
        Party lender = rpc.partiesFromName("PartyB",true).iterator().next();
        Amount<Currency> amt = new Amount<>(300, Currency.getInstance("USD"));
        FlowHandle<SignedTransaction> flowHandle = rpc.startFlowDynamic(IssueObligation.Initiator.class, amt, lender, false);
        rpc.killFlow(flowHandle.getId());
        prints("Killed state machine id: " + flowHandle.getId());
        // Next line waits indefinitely
        //CordaFuture<SignedTransaction> returnValue = flowHandle.getReturnValue();
    }

    public static void doRegisteredFlows() {
        rpc.registeredFlows().forEach(App::prints);
    }

    public static void doStartTrackedFlowDynamic() {
        Party lender = rpc.partiesFromName("PartyB",true).iterator().next();
        Amount<Currency> amt = new Amount<>(200, Currency.getInstance("USD"));
//        rpc.startTrackedFlowDynamic(IssueObligation.Initiator.class, amt, lender, false)
//                .getProgress()
//                .toBlocking()
//                .forEach(s -> prints("Emit: " + s););

//        rpc.startTrackedFlowDynamic(IssueObligation.Initiator.class, amt, lender, false)
//                .getProgress();
        rpc.startTrackedFlowDynamic(IssueObligation.Initiator.class, amt, lender, false)
                .getProgress().subscribe();

//                .getProgress();
                //.filter(s -> s.length()>30).toBlocking().forEach(App::prints);

//                .doOnEach(App::print);

//        rpc.startTrackedFlowDynamic(IssueObligation.Initiator.class, amt, lender, false)
//                .getProgress()
//                .debounce(5, TimeUnit.MILLISECONDS)
//                .doOnEach(App::print);


    }

    public static SignedTransaction issueObligation() {
        prints("Issuing obligation");
        Party lender = rpc.partiesFromName("PartyB", true).iterator().next();
        Amount<Currency> amt = new Amount<>(100, Currency.getInstance("USD"));
        FlowHandle<SignedTransaction> flowHandle = rpc.startFlowDynamic(IssueObligation.Initiator.class, amt, lender, false);
        CordaFuture<SignedTransaction> returnValue = flowHandle.getReturnValue();
        SignedTransaction signedTransaction = null;
        try {
            signedTransaction = returnValue.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        prints("Obligation issued. Tx ID: " + signedTransaction.getTx());

        return signedTransaction;
    }


    public static void doStartFlowDynamic() {

        Party lender = rpc.partiesFromName("PartyB",true).iterator().next();
        Amount<Currency> amt = new Amount<>(100, Currency.getInstance("USD"));
        FlowHandle<SignedTransaction> flowHandle = rpc.startFlowDynamic(IssueObligation.Initiator.class, amt, lender, false);
        CordaFuture<SignedTransaction> returnValue = flowHandle.getReturnValue();
        SignedTransaction signedTransaction = null;
        try {
            signedTransaction = returnValue.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        //flowHandle.close();// fixxxxxxxxxxxxxxxxxxxxxxxxxx
        prints(signedTransaction.getTx().toString());

        final Obligation o = (Obligation) signedTransaction.getTx().getOutputs().get(0).getData();
        prints("Linear Id: " + o.getLinearId());
        final UniqueIdentifier linearId = o.getLinearId();

        prints("Transferring Obligation");
        Party newLender = rpc.partiesFromName("PartyC",true).iterator().next();
        flowHandle= rpcB.startFlowDynamic(TransferObligation.Initiator.class, linearId, newLender, false);
        returnValue = flowHandle.getReturnValue();
        try {
            signedTransaction = returnValue.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        prints(signedTransaction.getTx().toString());

        // Issue cash

        Amount<Currency> cash = cash = new Amount<>(1000, Currency.getInstance("USD"));
        Party notary = rpc.notaryIdentities().get(0);
        rpc.startFlowDynamic(CashIssueFlow.class, cash, OpaqueBytes.of("1".getBytes()), notary);
        prints("Cash issued");

        // Settle
        flowHandle = rpc.startFlowDynamic(SettleObligation.Initiator.class, linearId, cash, false);
        returnValue = flowHandle.getReturnValue();
        try {
            signedTransaction = returnValue.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        prints(signedTransaction.getTx().toString());



    }

    public static void doNetworkMapFeed() {

        rpc.networkMapFeed().getUpdates().toBlocking().subscribe(
                mapChange ->  prints("onNext Fired"),
                z -> prints("onError Fired"), //
                () -> prints("onCompleted Fired"));

                rpc.waitUntilNetworkReady();
    }

    public static void doNetworkSnapshot() {
        rpc.networkMapSnapshot()
                .forEach(nodeInfo ->
                        nodeInfo.getLegalIdentities()
                                .forEach(party ->
                                        prints(party.getName().getOrganisation())));
    }

    public static void doClearNetworkMapCache() {

        prints(rpc.networkMapSnapshot().get(0).getLegalIdentities().get(0).getName().getOrganisation());
        rpc.clearNetworkMapCache();
        prints(rpc.networkMapSnapshot().get(0).getLegalIdentities().get(0).getName().getOrganisation());
        rpc.refreshNetworkMapCache();


        System.out.println(rpc.networkMapSnapshot().get(1).getLegalIdentities().get(0).getName().getOrganisation());
        rpc.clearNetworkMapCache();
        System.out.println(rpc.networkMapSnapshot().get(1).getLegalIdentities().get(0).getName().getOrganisation());
        rpc.refreshNetworkMapCache();
        System.out.println(rpc.networkMapSnapshot().get(1).getLegalIdentities().get(0).getName().getOrganisation());
        prints("before");
        doNodeDiagnosticInfo();
        doNodeInfo();
        rpc.clearNetworkMapCache();
        prints("cleared");
        doNodeDiagnosticInfo();
        doNodeInfo();
        prints("refreshing");
        rpc.refreshNetworkMapCache();
        prints("refreshed");
        doNodeDiagnosticInfo();
        doNodeInfo();
    }

    public static void doCurrentNodeTime() {
        prints("Node's clocks: " + rpc.currentNodeTime().toString());
    }

    public static void doNodeDiagnosticInfo() {
        final NodeDiagnosticInfo ndi = rpc.nodeDiagnosticInfo();
        ndi.getCordapps().forEach(ci -> prints("CorDapp: " + ci.getName()));
        prints("Corda Platform version: " + ndi.getPlatformVersion());
        prints("Corda Version: " + ndi.getVersion());
        prints("Revision: " + ndi.getRevision());
        prints("Vendor: " + ndi.getVendor());

    }

    public static void doNodeInfo() {
        final NodeInfo ni = rpc.nodeInfo();
        ni.getLegalIdentities().forEach(p -> prints(p.getName().getOrganisation()));
        ni.getAddresses().forEach(p -> prints(p.toString()));
        ni.getLegalIdentitiesAndCerts().forEach(pc -> {
            prints(pc.getCertificate().getIssuerDN().getName());
        });
        prints("Platform version: " + ni.getPlatformVersion());
        prints("Always true: " + ni.isLegalIdentity(ni.getLegalIdentities().get(0)));
        CordaX500Name name = ni.getLegalIdentities().get(0).getName();
        Party party = ni.identityFromX500Name(name);
        prints("Party: " + party.getName().getOrganisation());
        final PartyAndCertificate partyAndCertificate = ni.identityAndCertFromX500Name(name);
        prints("Party and Cert: " + partyAndCertificate.getCertificate().getNotAfter());
    }

    public static void doGetNetworkParameters() {
        final NetworkParameters np = rpc.getNetworkParameters();
        np.getPackageOwnership().forEach(App::print);
        np.getWhitelistedContractImplementations().forEach(App::print);
        prints("Event horizon in days: " + (np.getEventHorizon().getSeconds() / 86400 ));
        prints("Max message size: " + np.getMaxTransactionSize());
        prints("Epoch: " + np.getEpoch());
    }

    public static void print(Object k, Object v) {
            System.out.println("Key: " + k + ", Value: " + v);
    }

    public static void print(Object e) {
            System.out.println("Element: " + e);
    }

    public static void prints(String s) {
            System.out.println(s);
    }

    public class CallableObligation implements Runnable {

        @Override
        public void run() {
            final String user = "user1";
            final String password = "test";
            NetworkHostAndPort portPartyA = new NetworkHostAndPort("localhost",10006);
            CordaRPCClient clientPartyA = new CordaRPCClient(portPartyA);
            RPCConnection<CordaRPCOps> connPartyA = clientPartyA.start(user,password);
            CordaRPCOps rpc = connPartyA.getProxy();
            prints("Issuing obligation");
            Party lender = rpc.partiesFromName("PartyB", true).iterator().next();
            Amount<Currency> amt = new Amount<>(100, Currency.getInstance("USD"));
            FlowHandle<SignedTransaction> flowHandle = rpc.startFlowDynamic(IssueObligation.Initiator.class, amt, lender, false);
            CordaFuture<SignedTransaction> returnValue = flowHandle.getReturnValue();
            SignedTransaction signedTransaction = null;
            try {
                signedTransaction = returnValue.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            prints("Tx ID: " + signedTransaction.getTx());
        }
    }
}
