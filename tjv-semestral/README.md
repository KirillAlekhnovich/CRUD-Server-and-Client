# CRUD server

Server part is a three-layer application that provides CRUD operations (plus some additional ones) on 3 entities and uses Spring Framework.

## Requirements

* Java version: 11
* Database type: Any

## Usage

### via Client
Client usage manual is written in the **client repository**.

### via Postman

To work with specific columns use `/players`, `/teams` or `/sponsor` postfix.

#### Postman commands:
* **Creating** a new entity is just a post-query on a required column (for example `http://localhost:8080/players`).
* To **get** information about all entities of that column use get-query. If you want to get information about some specific entity add `/{id}` to the end.
* To **update** any entity add `/update/{id}` to the address. It's a put-query and body is required.
* To **delete** any entity add `/delete/{id}`.
* To add players to a team use `http://localhost:8080/teams/{teamId}/add_players`. For this operation required body is a list of players' id's that you want to add in this team.
* Same goes for sponsors, but instead of `add_players` use `add_sponsors`.
* To remove players from a team use `http://localhost:8080/teams/{teamId}/remove_players`. Required body is list of players' id's.
* You can also remove sponsors this way but change `remove_players` to `remove_sponsors`.