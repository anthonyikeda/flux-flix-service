Services:

MongoDB

```bash
$ docker service create  \
  --name mongo \
  --network traefik-network \
  --network reactive-network \
  --mount type=volume,src=mongodb,dst=/data/db \
  --label traefik.backend=mongo \
  --label traefik.frontend.rule=Host:mongo.local \
  --label traefik.docker.network=traefik-network \
  --label traefik.port=27017 \
  mongo:3.5

```

Portainer

```bash
$ docker service create --mount=type=bind,src=/var/run/docker.sock,dst=/var/run/docker.sock \
      --name portainer \
      --network traefik-network \
      --label traefik.backend=portainer \
      --label traefik.frontend.rule=Host:portainer.local \
      --label traefik.docker.network=traefik-network \
      --label traefik.port=9000 \
      portainer/portainer
```


Application

```bash
$ docker service create --name flux-flix-service \
    --network traefik-network \
    --label traefik-backend=flux-flix-service
    --label traefik-fronetend.rule=Host:flux.local \
    --label traefik.docker.network=traefik-network \
    --label traefik.port=8080 \
    --env SPRING_PROFILES_ACTIVE=swarm \
    localhost:5000/anthonyikeda/flux-flix-service:0.0.1-SNAPSHOT

```