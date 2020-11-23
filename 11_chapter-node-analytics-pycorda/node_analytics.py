# To install PyCorda use pip install pycorda==0.61
# The book uses version .61

import pycorda as pyc

# Quick start


url = 'jdbc:h2:tcp://localhost:55555/node'
partyA = pyc.Node(url,’sa’,’’)
print(partyA.get_node_infos())

# Download H2 JDBC driver
pyc.H2Tools().download_h2jar()

# Exploring a node

username = 'sa'
password = ''
url = 'jdbc:h2:tcp://localhost:55555/node'
partyA = pyc.Node(url,username,password)

node_infos = partyA.get_node_infos()
print(node_infos)

type(node_infos)

print(node_infos.shape)

messaging_ports = partyA.get_node_info_hosts()
print(messaging_ports)
print(messaging_ports['host_name'])

print(node_infos.columns)

print(messaging_ports[messaging_ports['PORT'] > 10006])

messaging_ports.sort_values('PORT')
messaging_ports.sort_values('PORT',ascending=False)

infos_and_ports = node_infos.merge(messaging_ports)

infos_and_ports.columns

n = partyA.get_node_names_identities()['NAME']

for n in names:
    print(n)

partyA.close()

# Java Keystore
partyA = pyc.Node(url, username, password, node_root = 'D:/corda-samples/obligation-cordapp/java-source/build/nodes/PartyA')
partyA.set_node_root('D:/corda-samples/obligation-cordapp/java-source/build/nodes/PartyA')
partyA.display_keys_from_jks()

# Node and Vault snapshots to file
partyA = pyc.Node(url, username, password,name='PartyA')
partyA.set_name('PartyA')
partyA.generate_snapshot()
partyA.generate_snapshot(‘somefile.txt’)

# Exploring the Vault and CorDapp

partyA.get_node_attachments_contracts()['CONTRACT_CLASS_NAME']
partyA.get_vault_linear_states()
partyA.get_node_transactions()
linear_tx = partyA.find_transactions_by_linear_id('9e5cb14f-2743-4eb8-acc5-b5a0179e34a4')
tx_id = linear_tx.iloc[0]['TRANSACTION_ID'] 
tx_state = partyA.find_vault_states_by_transaction_id(tx_id)
tx_state.columns 
tx_state[['RECORDED_TIMESTAMP','CONTRACT_STATE_CLASS_NAME']]

unconsumed = partyA.find_unconsumed_states_by_contract_state('net.corda.examples.obligation.Obligation')
unconsumed.columns
unconsumed['TRANSACTION_ID']

partyA.get_node_transactions()

fungible_states = partyA.get_vault_fungible_states()
fungible_states.columns
fungible_states[['OWNER_NAME','QUANTITY']]
partyA.find_unconsumed_states_by_contract_state('net.corda.finance.contracts.asset.Cash$State')
  
partyA.find_unconsumed_states_by_contract_state('net.corda.finance.contracts.asset.Cash$State')['RECORDED_TIMESTAMP']

partyA.get_vault_fungible_states()[['ISSUER_NAME','QUANTITY']]

url = 'jdbc:h2:tcp://localhost:55556/node'
partyB= pyc.Node(url, username, password,name='PartyB')
partyB.get_vault_fungible_states()[['ISSUER_NAME','QUANTITY']]

url = 'jdbc:h2:tcp://localhost:55557/node'
partyC= pyc.Node(url, username, password,name='PartyC')
partyC.get_vault_linear_states()

partyA.get_vault_fungible_states()[['ISSUER_NAME','QUANTITY']]

partyA.get_vault_states()[['CONTRACT_STATE_CLASS_NAME','RECORDED_TIMESTAMP']]

partyA = pyc.Node(url, username, password, name = 'PartyA' , web_server_url='http://localhost:10007')

partyA.set_web_server_url('http://localhost:10007')

partyA.send_api_request_get('/api/obligation/issue-obligation?amount=9595&currency=USD&party=PartyC')


# Generate transactions

url = 'jdbc:h2:tcp://localhost:55555/node'
username = 'sa'
password = ''
partyA = pyc.Node(url, username, password, name = 'PartyA' , web_server_url='http://localhost:10007')
rest_url = '/api/obligation/issue-obligation?amount={}&currency=USD&party=PartyC'
for amount in range(5000,10000,500):
    print('Generating obligation for amount of $',amount)
    response = partyA.send_api_request_get(rest_url.format(amount))
    print(response)
    print()

# Settle transactions

url = 'jdbc:h2:tcp://localhost:55555/node'
username = 'sa'
password = ''
partyA = pyc.Node(url, username, password, name = 'PartyA' , web_server_url='http://localhost:10007')

rest_url = '/api/obligation/settle-obligation?id={}&amount={}&currency=USD'
linear_ids =['2d2e2219-0074-4d8d-a0d6-4bbdf0b47b34',
                'b267fc0d-8e2a-46a8-91bf-3cf5858af7ea',
                '43973661-4be4-4723-8c6e-3ef6ecb702bb',
                '5456b9c5-4e33-441a-b89f-e1b9047c68a8',
                'bd619ab7-297a-4d32-9830-c0d51a378edc',
                '1f6950dc-fcce-4598-8e85-37daae1e6e88',
                '259922b9-dd06-4475-8451-4786c8373072',
                'a5a6cdf2-347b-457d-8359-5ee9272fa4e3',
                '7d61d473-f677-4027-86e8-12266528560c',
                'a96a84ea-3e61-4a93-b637-29adc77e7f6c']
linear_index = 0

# Loop through each linear ID and settle the obligation
for amount in range(5000,10000,500):
    linear_id = linear_ids[linear_index]
    linear_index += 1
    print('Settling obligation for',amount,'with linear ID',linear_id)
    response = partyA.send_api_request_get(rest_url.format(linear_id,amount))
    print(response)
    print()


# Charts

plotter = pyc.Plotter(partyA)
plotter.plot_timeseries_fungible_qty('net.corda.finance.contracts.asset.Cash$State')
plotter.show()

partyA.send_api_request_get('/api/obligation/self-issue-cash?amount=1000000&currency=USD')

plotter.plot_timeseries_fungible_qty('net.corda.finance.contracts.asset.Cash$State')
plotter.show()

# Plotly

plotter.publish_timeseries_fungible_qty_plotly('net.corda.finance.contracts.asset.Cash$State',user='jamielsheikh',api_key='***')