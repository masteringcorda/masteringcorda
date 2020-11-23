package com.cordabook.complexstates.contracts;

import net.corda.core.contracts.CommandData;

public interface Commands extends CommandData {
    class CreateParentStateCommand implements CommandData{}
    class CreateAutoIncrementStateCommand implements CommandData{}
    class AddChildState implements CommandData {}
    class AddStockSymbol implements CommandData {}
    class Trade implements CommandData {}

}
