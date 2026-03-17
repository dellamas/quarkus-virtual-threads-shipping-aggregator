# quarkus-virtual-threads-shipping-aggregator-lab

Lab Quarkus que simula uma API de cotacao de frete para e-commerce agregando tres parceiros com I/O bloqueante em paralelo usando virtual threads.

## Stack

- Java 21
- Quarkus 3.32.3
- REST Jackson
- CDI com Arc
- SmallRye OpenAPI
- Virtual Threads
- JUnit 5 e Rest Assured

## Cenario

O endpoint principal recebe os dados do pedido e consulta `FASTBOX`, `ECONOSHIP` e `PICKNPACK` em paralelo. Cada parceiro simula latencia bloqueante com `Thread.sleep`, enquanto a agregacao usa `Executors.newVirtualThreadPerTaskExecutor()` para manter o codigo simples e escalavel.

O retorno final vem ordenado por menor preco, o que facilita a comparacao imediata para um checkout de e-commerce.

## Endpoints

- `POST /api/shipping-options/quotes`
- `GET /api/shipping-options/partners`
- `GET /api/shipping-options/diagnostics`
- `GET /q/swagger-ui`
- `GET /q/openapi`

## Exemplo de requisicao

```json
{
  "orderId": "order-1001",
  "destinationZipCode": "04538-132",
  "weightKg": 3.75,
  "declaredValue": 899.90,
  "itemsCount": 4
}
```

## Diagnosticos

O endpoint de diagnostico expone:

- total de cotacoes recebidas
- chamadas realizadas por parceiro
- tempo medio em milissegundos por parceiro

## Executar localmente

```bash
./mvnw quarkus:dev
```

Swagger UI: `http://localhost:8080/q/swagger-ui`

## Testes

Os testes cobrem:

- ordenacao das cotacoes por menor preco
- catalogo de parceiros
- diagnosticos agregados por parceiro

## CTA

Veja o repositório completo e publique sua variacao deste lab em: https://github.com/dellamas/quarkus-virtual-threads-shipping-aggregator-lab

Para conversar sobre Quarkus, Java e arquitetura, conecte-se no LinkedIn: https://www.linkedin.com/in/luisfabriciodellamas/

Mais artigos e labs em: https://dev.to/dellamas
