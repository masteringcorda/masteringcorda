package com.cordabook.complexstates.states;

import net.corda.core.contracts.Amount;
import net.corda.core.contracts.FungibleState;
import net.corda.core.identity.AbstractParty;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ExampleFungibleState implements FungibleState {
    @NotNull
    @Override
    public Amount getAmount() {
        return null;
    }

    @NotNull
    @Override
    public List<AbstractParty> getParticipants() {
        return null;
    }
}
