# KtorBackend

Welcome to Ktor Backend! This is basically a sample project for using Kotlin as Backend and taking advantage of Ktor,
MongoDB and hashing.

## How to run

First of all, you need to register on [MongoDb](https://www.mongodb.com/cloud/atlas/register) website, and create a
database.
Then, you will need to define the following environment variables in your IDE:

- dbName
- JWT_SECRET
- mongoPassword

After that, if you already created the account on MongoDb, you can use the already available option called "Connect" on
that website and getting the connection string.

- Use the "Connect your application" item, then it will show you the connection string.

As a note, you will need to replace the `<password>` with the password you defined on the environment
variable `$mongoPassword`, and at the end of the connection url (`mongodb.net/`), you will need to add the database name you defined on
the environment variable `$dbName`.

After that, you can run the project or use the following command to watch the changes on the code:

```bash
./gradlew -t build
```

This will basically rebuild the project every time you change something on the code.
