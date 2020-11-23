package com.cordabook.complexstates.states;

import net.corda.core.contracts.Amount;
import net.corda.core.contracts.CommandAndState;
import net.corda.core.contracts.FungibleAsset;
import net.corda.core.contracts.Issued;
import net.corda.core.identity.AbstractParty;
import org.jetbrains.annotations.NotNull;

import java.security.PublicKey;
import java.util.Collection;
import java.util.List;

public class ExampleFungibleAsset implements FungibleAsset {
    @NotNull
    @Override
    public Amount<Issued> getAmount() {
        return null;
    }

    @NotNull
    @Override
    public Collection<PublicKey> getExitKeys() {
        return null;
    }

    @NotNull
    @Override
    public FungibleAsset withNewOwnerAndAmount(@NotNull Amount newAmount, @NotNull AbstractParty newOwner) {
        return null;
    }

    @NotNull
    @Override
    public AbstractParty getOwner() {
        return null;
    }

    @NotNull
    @Override
    public CommandAndState withNewOwner(@NotNull AbstractParty newOwner) {
        return null;
    }

    @NotNull
    @Override
    public List<AbstractParty> getParticipants() {
        return null;
    }
}
