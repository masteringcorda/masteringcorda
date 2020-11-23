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
public class TradeState implements LinearState {
    private String symbol;
    private Integer quantity;
    private UniqueIdentifier linearId;
    private Party buyer;
    private Party seller;

    public TradeState(String symbol, Integer quantity, Party buyer, Party seller) {
        this.symbol = symbol;
        this.quantity = quantity;
        this.buyer = buyer;
        this.seller = seller;
        this.linearId = new UniqueIdentifier();
    }

    @NotNull
    @Override
    public UniqueIdentifier getLinearId() {
        return linearId;
    }

    @NotNull
    @Override
    public List<AbstractParty> getParticipants() {
        return ImmutableList.of(buyer,seller);
    }

    public String getSymbol() {
        return symbol;
    }
}
