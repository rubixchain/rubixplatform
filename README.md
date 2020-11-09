# Rubix API Calls

## DID Creation

Creates a unique Decentralized IDentity 
> $ curl --location --request POST 'http://localhost:1898/create' --form 'image=@<"image path"
>' --form 'data={ "data": "9876543333,user@rubix.network"}'

***Input***:     data(String) and image(Multipart File)
***Returns***: DID (String)


## Initial Setup

Does the initial setup of IPFS and syncing the network nodes
*Make sure to make this call before any other calls*
>$ curl --header "Content-Type: application/json" --request GET http://localhost:1898/setup

***Input***:     nill
***Returns***: nill


## Transfer Tokens

Transfers token(s) from one wallet address to another 
> $ curl --header "Content-Type: application/json" --request POST http://localhost:1898/initiateTransaction --data '{ "receiver": "445f59c3d71c6769124470cf4b82ca0b9b1626aec4f14f50a8f1e6a13e1fc70d", "tokenCount":1, "comment":"transaction comments"}' 

***Input***:     receiver (String), tokenCount (Integer), comment (String)
***Returns***: Transaction ID (String), Success / Failure (Boolean), DID (String)

## Account Information
Retrieves the user account details 

>$ curl --header "Content-Type: application/json" --request GET http://localhost:1898/getAccountInfo

***Input***:  nill
***Returns***: DID (String), WalletID (String), TransactionAsSender (Integer), TransactionAsReceiver (Integer), Balance (Integer)

## Account Balance
Check the balance in user's wallet

>$ curl --header "Content-Type: application/json" --request GET http://localhost:1898/getBalance

***Input***:  nill
***Returns***: Account Balance(Integer)

## Get Transaction details with Transaction ID

Details of a particular transaction like Quorum involved, token transferred, receiver details, time and more
> $ curl --header "Content-Type: application/json" --request POST  http://localhost:1898/getTxnDetails --data '{"transactionID": "82A03ABB70495A2241D7FD51079A635040A145E4C701B2C1B0C2DC92CB79AFC A"}'

***Input***:    transactionID (String)
***Returns***:  senderDID(String), role(String), totalTime(Integer), tokens(ArrayList), comment(String), txn(String), receiverDID(String), Date(Date)


## Get Transaction details with Date

Retrieves the details of all the transactions committed during the specified period
> $ curl --header "Content-Type: application/json" --request POST http://localhost:1898/getTxnByDate --data '{"eDate":"2020-04-21", "sDate":"2020-04-02"}' 
 
***Input***:  sDate (String),  eDate (String)
***Returns***: senderDID(String), role(String), totalTime(Integer), tokens(ArrayList), comment(String), txn(String), receiverDID(String), Date(Date)

## Get Transaction details with Comment

Retrieves the details of all the transactions committed with the specified comment
> $ curl --header "Content-Type: application/json" --request POST http://localhost:1898/getTxnByComment
 --data '{"comment":"10,000Rs"}' 
 
***Input***:  comment (String)
***Returns***: senderDID(String), role(String), totalTime(Integer), tokens(ArrayList), comment(String), txn(String), receiverDID(String), Date(Date)
## Get Transaction details with Count

Retrieves the last specified count of transactions committed 
> $ curl --header "Content-Type: application/json" --request POST http://localhost:1898/getTxnByCount
 --data '{"count" : 3}' 
 
***Input***:  count (Integer)
***Returns***: senderDID(String), role(String), totalTime(Integer), tokens(ArrayList), comment(String), txn(String), receiverDID(String), Date(Date)

## View Proof Chains

Views proofchain of the input token.
>$ curl --header "Content-Type: application/json" --request POST http://localhost:1898/viewProofs --data '{"token": "Qma1dRiJYdHHx4zCFxKz8LEoNERwoqXYzSHFePkhhewbjE"}'

***Input***:   token(String)
***Returns***: ProofChain (String)


## List of Tokens
Lists out all the tokens available in the user's wallet
>$ curl --header "Content-Type: application/json" --request GET http://localhost:1898/viewTokens

***Input***:   nill
***Returns***: List of Tokens (JSONObject)


## Close Streams

To close all the streams open
>$ curl --header "Content-Type: application/json" --request GET http://localhost:1898/p2pClose

***Input***:   nill
***Returns***: nill

## Synchronise Network Nodes

To synchronize the DIDs of the systems, so that the node will have an updated list of all the DIDs in the network.
>$ curl --header "Content-Type: application/json" --request GET http://localhost:1898/sync

***Input***:   nill
***Returns***: nill


## Shutdown

Closes all application processes and exits 
>$ curl --header "Content-Type: application/json" --request GET http://localhost:1898/shutdown


***Input***:   nill
***Returns***: nill
