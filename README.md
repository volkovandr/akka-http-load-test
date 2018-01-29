# akka-http-load-test

The application, or to be preciese, applications, are supposed to
show how akka-http work and how fast it can be.

## Server

The server listens on the port 8000 and has one resource /count
Simply start it with `sbt run` and then try `curl http://localhoat:8000/count`

## Client

There are several clients doing the same thing but in different way:

- FutureClient: runs each request in it's own Future and onComplete starts
another request
- SyncClient: using the library scalaj-http runs the requests synchronously.
But this process is executed in several threads.
- StreamClient: using Akka Stream opens one HTTP connection and sends many
requests via a single connection. Also runs in parallel using several threads.

## Usage

Start the server and any of the clients and then check the client's and server's
metrics on ports (by default) 8001 and 8002 repectively.