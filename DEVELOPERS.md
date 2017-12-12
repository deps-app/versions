# Developers

## Running

```console
$ docker-compose up
$ lein repl
user> (go)
```

## Styles

```console
$ grunt build # one off build
$ grunt watch # watch for changes and rebuild
```

## Heroku

Heroku Redis maxmemory-policy is `volatile-lru`

```console
$ heroku redis:maxmemory --policy volatile-lru
```
