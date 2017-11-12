# Developers

## Running

```console
$ lein ring server
```

## Heroku

Heroku Redis maxmemory-policy is `volatile-lru`

```console
$ heroku redis:maxmemory --policy volatile-lru
```
