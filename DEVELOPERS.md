# Developers

## Running

```console
$ lein ring server
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
