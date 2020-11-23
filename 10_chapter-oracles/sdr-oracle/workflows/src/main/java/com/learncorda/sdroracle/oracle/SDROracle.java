package com.learncorda.sdroracle.oracle;

import com.learncorda.sdroracle.contracts.RetrieveSDRCommand;
import kotlin.jvm.functions.Function1;
import net.corda.core.contracts.Command;
import net.corda.core.crypto.TransactionSignature;
import net.corda.core.node.AppServiceHub;
import net.corda.core.node.ServiceHub;
import net.corda.core.node.services.CordaService;
import net.corda.core.serialization.SingletonSerializeAsToken;
import net.corda.core.transactions.FilteredComponentGroup;
import net.corda.core.transactions.FilteredTransaction;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CordaService
public class SDROracle extends SingletonSerializeAsToken {

    private ServiceHub serviceHub;
    private Map<String,Float> rates;

    public SDROracle(AppServiceHub sb) {
        this.serviceHub = sb;
        if(isOracleNode()) {
            System.out.println("This is an oracle node");
            String fileURL = "https://raw.githubusercontent.com/masteringcorda/masteringcorda/main/10_chapter-oracles/data/SDRV.csv";
            BufferedReader br = null;
            String r = null;
            rates = new HashMap();
            try {

                br = new BufferedReader(new InputStreamReader(new URL(fileURL).openStream()));
                while ((r = br.readLine()) != null) {
                    String[] line = r.split(",");
                    rates.put(line[0], Float.valueOf(line[2]));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("SDROracle service loaded with " + rates.size() + " rates.");
        }
    }


    public TransactionSignature sign(FilteredTransaction ftx) {
        PublicKey mykey = serviceHub.getMyInfo().getLegalIdentities().get(0).getOwningKey();
        if (ftx.checkWithFun(this::isSignableTransaction))
            return serviceHub.createSignature(ftx,mykey);
        else
            throw new IllegalArgumentException("The SDR Oracle cannot sign this transaction");
    }

    private boolean isSignableTransaction(Object o) {
        PublicKey mykey = serviceHub.getMyInfo().getLegalIdentities().get(0).getOwningKey();
        if (o instanceof Command
                && ((Command) o).getValue() instanceof RetrieveSDRCommand
                && ((Command) o).getSigners().contains(mykey)) {
            final String date = ((RetrieveSDRCommand) ((Command) o).getValue()).getDate();
            final Float rate = ((RetrieveSDRCommand) ((Command) o).getValue()).getRate();

            if(rates.get(date).equals(rate))
                return true;
        }
        return false;

    }

    private boolean isOracleNode() {
        return serviceHub
                .getMyInfo()
                .getLegalIdentities()
                .get(0)
                .getName()
                .getOrganisation()
                .equalsIgnoreCase("sdroracle");

    }

}
