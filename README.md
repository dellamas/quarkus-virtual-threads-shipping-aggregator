# quarkus-virtual-threads-shipping-aggregator

Aplicação Quarkus que simula uma API de cotação de frete para e-commerce, agregando três parceiros com I/O bloqueante em paralelo usando virtual threads.

## Stack

- Java 21
- Quarkus 3.32.3
- REST Jackson
- CDI com Arc
- SmallRye OpenAPI
- Virtual Threads
- JUnit 5 e Rest Assured

## Cenário

O endpoint principal recebe os dados do pedido e consulta `FASTBOX`, `ECONOSHIP` e `PICKNPACK` em paralelo. Cada parceiro simula latência bloqueante com `Thread.sleep`, enquanto a agregação usa `Executors.newVirtualThreadPerTaskExecutor()` para manter o código simples e escalável.

O retorno final vem ordenado por menor preço, o que facilita a comparação imediata em um checkout de e-commerce.

## Endpoints

- `POST /api/shipping-options/quotes`
- `GET /api/shipping-options/partners`
- `GET /api/shipping-options/diagnostics`
- `GET /q/swagger-ui`
- `GET /q/openapi`

## Exemplo de requisição

```json
{
  "origin": "sao-paulo-sp",
  "destination": "belo-horizonte-mg",
  "sku": "SKU-9001",
  "quantity": 3,
  "orderValue": 799.90
}
```

## Diagnósticos

O endpoint de diagnóstico expõe:

- total de cotações recebidas
- chamadas realizadas por parceiro
- tempo médio em milissegundos por parceiro

## Executar localmente

```bash
./mvnw quarkus:dev
```

Swagger UI: `http://localhost:8080/q/swagger-ui`

## Testes

Os testes cobrem:

- ordenação das cotações por menor preço
- catálogo de parceiros
- diagnósticos agregados por parceiro

## Links

Se quiser ver a implementação completa, o repositório está aqui:
https://github.com/dellamas/quarkus-virtual-threads-shipping-aggregator

Para conversar sobre Quarkus, Java e arquitetura:
https://www.linkedin.com/in/luisfabriciodellamas/

Mais artigos em:
https://dev.to/dellamas
