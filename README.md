## Introduction
This project is an example of security interactions with Spring Boot.

The project named `jwt-api` will work as JWT and JWKS provider.

## Run the project
### Certificate
Before starting the projects, you need to generate a private key / public certificate pair.
```
openssl req -nodes -new -x509 -keyout jwt-api/src/main/resources/certificates/private.key -out jwt-api/src/main/resources/certificates/public.cert
```

The private key will be used by the `jwt-api` to sign tokens.
The public certificate will be available on the `/authentication/jwks` endpoint on the `jwt-api`.
This way, the `security-example` project can retrieve the certificate to verify incoming JWTs.

### Application
Start both Spring boot projects, then you can try out the HTTP api.

#### JWT api
Generate a JWT with claims (body)
```
curl --request POST \
  --url http://localhost:8091/jwt-api/authentication \
  --header 'Content-Type: application/json' \
  --data '{
	"first_name": "John",
	"last_name": "Doe",
	"job": "developer",
	"roles": [
		"admin"
	]
}'
```

Retrieve the JWKS
```
curl --request GET \
  --url http://localhost:8091/jwt-api/authentication/jwks
```

#### Security api

Test unauthenticated endpoint
```
curl --request GET \
  --url http://localhost:8090/security-example/unauthenticated
```

Test health endpoint
```
curl --request GET \
  --url http://localhost:8090/security-example/actuator/health
```

Test authenticated endpoint
```
curl --request GET \
  --url http://localhost:8090/security-example/authenticated \
  --header 'Authorization: Bearer <JWT>'
```

Test admin endpoint
```
curl --request GET \
  --url http://localhost:8090/security-example/admin \
  --header 'Authorization: Bearer <JWT>'
```

Test authorized job endpoint
```
curl --request GET \
  --url http://localhost:8090/security-example/authorized_job \
  --header 'Authorization: Bearer <JWT>'
```

Test UUID endpoint (decode the JWT to retrieve the current uuid)
```
curl --request GET \
  --url http://localhost:8090/security-example/uuid/<uuid> \
  --header 'Authorization: Bearer <JWT>'
```
