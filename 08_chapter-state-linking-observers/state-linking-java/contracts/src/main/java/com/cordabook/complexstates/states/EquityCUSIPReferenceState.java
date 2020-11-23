package com.cordabook.complexstates.states;

import com.cordabook.complexstates.contracts.TemplateContract;
import com.google.common.collect.ImmutableList;
import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.LinearState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@BelongsToContract(TemplateContract.class)
public class EquityCUSIPReferenceState implements LinearState {
    private String symbol;
    private String cusip;
    private Party dataCustodian;
    private UniqueIdentifier linearId;

    public EquityCUSIPReferenceState(String symbol, String cusip, Party dataCustodian) {
        this.symbol = symbol;
        this.cusip = cusip;
        this.dataCustodian = dataCustodian;
        this.linearId =  new UniqueIdentifier(cusip);
    }

    public String getSymbol() {
        return symbol;
    }

    public Party getDataCustodian() {
        return dataCustodian;
    }


    @NotNull
    @Override
    public List<AbstractParty> getParticipants() {
        return ImmutableList.of(dataCustodian);
    }

    @NotNull
    @Override
    public UniqueIdentifier getLinearId() {
        return linearId;
    }

    public String getCusip() {
        return cusip;
    }
}
