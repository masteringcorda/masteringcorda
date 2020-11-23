package com.template.flows;

import net.corda.core.contracts.ContractState;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.StateRef;
import net.corda.core.crypto.SecureHash;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import org.hibernate.criterion.Example;
import org.jetbrains.annotations.NotNull;

import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.List;


public class CordaHashExamples {
    public static void main(String[] args) {
        String hello = "Hi there";
        SecureHash hash = SecureHash.SHA256.sha256(hello);
        System.out.println(hash.toString());
        ExampleState state = new ExampleState("Level 1");
        StateRef rf = new StateRef(hash,0);
        //StateAndRef<ExampleState>
        KeyPairGenerator keyGen = null;
        try {
            keyGen = KeyPairGenerator.getInstance("EC");
            keyGen.initialize(new ECGenParameterSpec("secp256r1"), new SecureRandom());
        } catch (Exception e) {
            e.printStackTrace();
        }

        KeyPair pair = keyGen.generateKeyPair();
        PrivateKey priv = pair.getPrivate();
        PublicKey pub = pair.getPublic();
        CordaX500Name name = new CordaX500Name("PartyA","PartyA","New York","US");
        Party partyA = new Party(name,pub);
        System.out.println(partyA.toString());

    }
}

class ExampleState implements ContractState {
    private String arbitraryString;

    public ExampleState(String arbitraryString) {
        this.arbitraryString = arbitraryString;
    }

    @NotNull
    @Override
    public List<AbstractParty> getParticipants() {
        return null;
    }
}