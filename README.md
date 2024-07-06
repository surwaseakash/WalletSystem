Wallet System API Documentation
Introduction
Welcome to the API documentation for the Wallet System. This document provides details on the endpoints available, their request and response formats, error handling, testing strategies, and deployment instructions.

Endpoints
**POST /wallet/topup**

Description
Add funds to a user's wallet.

Request Body:
{
"user_id": "string",
"amount": float
}
user_id (required): The ID of the user.
amount (required): The amount to add to the wallet.

Response:
{
"status": boolean,
"new_balance": float,
"transaction_id": string
}
status: Indicates if the top-up was successful.
new_balance: The updated balance after top-up.
transaction_id: Unique ID of the transaction.

**POST /wallet/deduct**
Description
Deduct funds from a user's wallet.

Request Body:
{
"user_id": "string",
"amount": float
}
user_id (required): The ID of the user.
amount (required): The amount to deduct from the wallet.

Response:
{
"status": boolean,
"new_balance": float,
"transaction_id": string
}
status: Indicates if the deduction was successful.
new_balance: The updated balance after deduction.
transaction_id: Unique ID of the transaction.

**GET /wallet/balance**
Description
Retrieve the current balance of a user's wallet.

Request Params:
user_id (required): The ID of the user.

Response:
{
"balance": float
}
balance: The current balance of the user's wallet.

**Error Handling**
The Wallet System API returns appropriate HTTP status codes and error messages in response to invalid requests or errors encountered during processing. Common status codes include:

404 Not Found: When a resource (user or wallet) is not found.
400 Bad Request: For invalid requests or insufficient funds.
500 Internal Server Error: For unexpected server-side errors.

**Testing**
Testing strategies include unit tests for individual components and integration tests to ensure proper interaction between components. Tests cover scenarios such as top-up, deduction, balance retrieval, and error handling.

**Deployment**
Local Setup
Clone the repository from https://github.com/surwaseakash/WalletSystem.git.
Configure application properties for database connection.(In postgres db, just create db named walletdb & add these details along with the creds in application properties file)
Build and run the application using Maven or your preferred IDE.

Production Deployment
Set environment-specific configurations (database URL, credentials).
Build a deployable artifact (JAR file) using Maven.
Deploy the artifact to your production environment.